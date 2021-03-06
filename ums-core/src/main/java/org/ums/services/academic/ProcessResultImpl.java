package org.ums.services.academic;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.ums.configuration.UMSConfiguration;
import org.ums.domain.model.immutable.Semester;
import org.ums.domain.model.immutable.StudentRecord;
import org.ums.domain.model.immutable.TaskStatus;
import org.ums.domain.model.immutable.UGRegistrationResult;
import org.ums.domain.model.mutable.MutableStudentRecord;
import org.ums.domain.model.mutable.MutableTaskStatus;
import org.ums.enums.CourseRegType;
import org.ums.enums.CourseType;
import org.ums.manager.*;
import org.ums.persistent.model.PersistentTaskStatus;
import org.ums.response.type.TaskStatusResponse;
import org.ums.util.UmsUtils;

@Component
public class ProcessResultImpl implements ProcessResult {
  @Autowired
  UGRegistrationResultManager mResultManager;
  @Autowired
  TaskStatusManager mTaskStatusManager;
  @Autowired
  StudentRecordManager mStudentRecordManager;
  @Autowired
  ResultPublishManager mResultPublishManager;
  @Autowired
  UMSConfiguration mUmsConfiguration;
  @Autowired
  RemarksBuilder mRemarksBuilder;
  @Autowired
  SemesterManager mSemesterManager;

  public final static String PROCESS_GRADES = "process_grades";
  public final static String PROCESS_GPA_CGPA_PROMOTION = "process_gpa_cgpa_promotion";
  public final static String PUBLISH_RESULT = "publish_result";
  public final static Integer UPDATE_NOTIFICATION_AFTER = 20;

  private Map<String, Double> GPA_MAP = null;
  private final List<String> EXCLUDE_GRADES = new ArrayList<>();

  public ProcessResultImpl() {
    GPA_MAP = UmsUtils.getGPAMap();

    EXCLUDE_GRADES.add("E");
    EXCLUDE_GRADES.add("W");
    EXCLUDE_GRADES.add("P");
    EXCLUDE_GRADES.add("F");
  }

  @Override
  public void process(int pProgramId, int pSemesterId) {
    processResult(pProgramId, pSemesterId);
  }

  @Override
  public void process(int pProgramId, int pSemesterId, int pYear, int pSemester) {
    processResult(pProgramId, pSemesterId, pYear, pSemester);
  }

  @Async
  private void processResult(int pProgramId, int pSemesterId, int pYear, int pSemester) {
    MutableTaskStatus processResultStatus = new PersistentTaskStatus();
    String taskId
        = mTaskStatusManager.buildTaskId(pProgramId, pSemesterId, pYear, pSemester, PROCESS_GPA_CGPA_PROMOTION);
    processResultStatus.setId(taskId);
    processResultStatus.setStatus(TaskStatus.Status.INPROGRESS);
    processResultStatus.create();

    List<UGRegistrationResult> resultList = mResultManager.getResults(pProgramId, pSemesterId, pYear, pSemester);
    List<StudentRecord> studentRecords =
        mStudentRecordManager.getStudentRecords(pProgramId, pSemesterId, pYear, pSemester);

    processResult(pSemesterId,
        resultList.stream().collect(Collectors.groupingBy(UGRegistrationResult::getStudentId)), studentRecords, taskId);
  }

  @Async
  private void processResult(int pProgramId, int pSemesterId) {
    List<UGRegistrationResult> resultList = mResultManager.getResults(pProgramId, pSemesterId);

    String taskId = mTaskStatusManager.buildTaskId(pProgramId, pSemesterId, PROCESS_GPA_CGPA_PROMOTION);
    MutableTaskStatus processResultStatus = new PersistentTaskStatus();
    processResultStatus.setId(taskId);
    processResultStatus.setStatus(TaskStatus.Status.INPROGRESS);
    processResultStatus.create();

    List<StudentRecord> studentRecords = mStudentRecordManager.getStudentRecords(pProgramId, pSemesterId);
    processResult(pSemesterId,
        resultList.stream().collect(Collectors.groupingBy(UGRegistrationResult::getStudentId)), studentRecords, taskId);
  }

  private void processResult(final int pSemesterId,
      final Map<String, List<UGRegistrationResult>> studentCourseGradeMap,
      final List<StudentRecord> studentRecords,
      final String pTaskId) {
    int totalStudents = studentCourseGradeMap.keySet().size();
    int i = 0;

    try {
      // Temoporary code to find duplicate student record
      Set<String> allItems = new HashSet<>();
      Set<String> duplicates = studentRecords.stream().map(StudentRecord::getStudentId)
          .filter(pStudentId -> !allItems.add(pStudentId)).collect(Collectors.toSet());
      System.out.println(duplicates);

      Map<String, StudentRecord> studentRecordMap =
          studentRecords.stream().collect(Collectors.toMap(StudentRecord::getStudentId, Function.identity()));

      List<MutableStudentRecord> updatedStudentRecords = new ArrayList<>();

      for(String studentId : studentCourseGradeMap.keySet()) {
        MutableStudentRecord studentRecord = studentRecordMap.get(studentId).edit();

        StudentRecordParams studentRecordParams = calculateGPA(studentCourseGradeMap.get(studentId).stream()
            .filter(pResult -> pResult.getSemesterId() == pSemesterId).collect(Collectors.toList()));
        if(studentRecordParams != null) {
          studentRecord.setGPA(studentRecordParams.getGpa());
          studentRecord.setCompletedCrHr(studentRecordParams.getCompletedCrHr());
          studentRecord.setCompletedGradePoints(studentRecordParams.getCompletedGradePoints());
        }
        if(!mUmsConfiguration.isProcessGPAOnly()) {
          List<Semester> previousSemesters = mSemesterManager.getPreviousSemesters(pSemesterId,
              studentRecord.getStudent().getProgram().getProgramTypeId());
          List<Integer> previousSemesterIds =
              previousSemesters.stream().map(Semester::getId).collect(Collectors.toList());
          List<UGRegistrationResult> courseResults = studentCourseGradeMap.get(studentId).stream()
              .filter(pResult -> previousSemesterIds.contains(pResult.getSemesterId())).collect(Collectors.toList());
          StudentRecordParams cgpa = calculateCGPA(courseResults);
          boolean isPassed = isPassed(pSemesterId, courseResults);
          if(cgpa != null) {
            studentRecord.setCGPA(cgpa.getGpa());
            studentRecord.setTotalCompletedCrHr(cgpa.getCompletedCrHr());
            studentRecord.setTotalCompletedGradePoints(cgpa.getCompletedGradePoints());
          }
          studentRecord.setStatus(isPassed ? StudentRecord.Status.PASSED : StudentRecord.Status.FAILED);
          studentRecord.setGradesheetRemarks(mRemarksBuilder.getGradeSheetRemarks(courseResults,
              isPassed ? StudentRecord.Status.PASSED : StudentRecord.Status.FAILED, pSemesterId));
          studentRecord.setTabulationSheetRemarks(
              mRemarksBuilder.getTabulationSheetRemarks(courseResults, studentRecord, pSemesterId));
        }
        updatedStudentRecords.add(studentRecord);

        i++;

        if((i % UPDATE_NOTIFICATION_AFTER) == 0 || (i == totalStudents)) {
          mStudentRecordManager.update(updatedStudentRecords);
          updatedStudentRecords.clear();
          TaskStatus taskStatus = mTaskStatusManager.get(pTaskId);
          MutableTaskStatus mutableTaskStatus = taskStatus.edit();
          mutableTaskStatus.setProgressDescription(UmsUtils.getPercentageString(i, totalStudents));
          mutableTaskStatus.update();
        }
      }
      TaskStatus taskStatus = mTaskStatusManager.get(pTaskId);
      MutableTaskStatus mutableTaskStatus = taskStatus.edit();
      mutableTaskStatus.setProgressDescription("100");
      mutableTaskStatus.setStatus(TaskStatus.Status.COMPLETED);
      mutableTaskStatus.update();
    } catch(Exception e) {
      TaskStatus taskStatus = mTaskStatusManager.get(pTaskId);
      MutableTaskStatus mutableTaskStatus = taskStatus.edit();
      mutableTaskStatus.setProgressDescription("0");
      mutableTaskStatus.setStatus(TaskStatus.Status.FAILED);
      mutableTaskStatus.update();

      throw e;
    }

  }

  private StudentRecordParams calculateCGPA(List<UGRegistrationResult> pResults) {
    return calculateGPA(pResults);
  }

  private StudentRecordParams calculateGPA(List<UGRegistrationResult> pResults) {
    Double totalCrHr = 0D;
    Double totalGPA = 0D;
    if(pResults.size() == 0) {
      return null;
    }
    for(UGRegistrationResult result : pResults) {
      if(!EXCLUDE_GRADES.contains(result.getGradeLetter())) {
        totalCrHr += result.getCourse().getCrHr();
        totalGPA += GPA_MAP.get(result.getGradeLetter()) * result.getCourse().getCrHr();
      }
    }
    Double toBeTruncated = totalGPA / totalCrHr;
    toBeTruncated = Double.isNaN(toBeTruncated) ? 0D : toBeTruncated;
    return new StudentRecordParams(toBeTruncated, totalCrHr, totalGPA);
  }

  private Boolean isPassed(final int pSemesterId, List<UGRegistrationResult> pResults) {
    int totalFailedCourse = 0, failedInCurrentSemester = 0;
    for(UGRegistrationResult result : pResults) {
      if(result.getGradeLetter().equalsIgnoreCase("F")) {
        if(result.getCourse().getCourseType() == CourseType.SESSIONAL) {
          return false;
        }
        totalFailedCourse++;
        if(result.getSemesterId() == pSemesterId && result.getType() != CourseRegType.CARRY) {
          failedInCurrentSemester++;
        }
      }
    }
    return (totalFailedCourse <= MAX_NO_FAILED_COURSE)
        && (failedInCurrentSemester <= MAX_NO_FAILED_COURSE_CURRENT_SEMESTER);
  }

  @Override
  public TaskStatusResponse status(Object... pKeys) {
    String publishResult = mTaskStatusManager.buildTaskId(appendToArray(pKeys, PUBLISH_RESULT));
    String processCGPA = mTaskStatusManager.buildTaskId(appendToArray(pKeys, PROCESS_GPA_CGPA_PROMOTION));
    String processGrades = mTaskStatusManager.buildTaskId(appendToArray(pKeys, PROCESS_GRADES));

    if(mTaskStatusManager.exists(publishResult)) {
      TaskStatus status = mTaskStatusManager.get(publishResult);
      return new TaskStatusResponse(status);
    }

    if(mTaskStatusManager.exists(processGrades)) {
      TaskStatus status = mTaskStatusManager.get(processGrades);
      if(status.getStatus() == TaskStatus.Status.INPROGRESS) {
        return new TaskStatusResponse(status);
      }
      else {
        if(mTaskStatusManager.exists(processCGPA)) {
          return new TaskStatusResponse(mTaskStatusManager.get(processCGPA));
        }
        else {
          return new TaskStatusResponse(status);
        }
      }
    }

    MutableTaskStatus taskStatus = new PersistentTaskStatus();
    taskStatus.setId(processCGPA);
    taskStatus.setStatus(TaskStatus.Status.NONE);

    return new TaskStatusResponse(taskStatus);
  }

  private Object[] appendToArray(Object[] pObjects, Object append) {
    Object[] objects = new Object[pObjects.length + 1];
    System.arraycopy(pObjects, 0, objects, 0, objects.length - 1);
    objects[objects.length - 1] = append;
    return objects;
  }

  @Transactional
  @Override
  public void publishResult(int pProgramId, int pSemesterId) {
    String publishResult = mTaskStatusManager.buildTaskId(pProgramId, pSemesterId, PUBLISH_RESULT);

    MutableTaskStatus taskStatus = new PersistentTaskStatus();
    taskStatus.setId(publishResult);
    taskStatus.setStatus(TaskStatus.Status.INPROGRESS);
    taskStatus.create();

    mResultPublishManager.publishResult(pProgramId, pSemesterId);

    TaskStatus status = mTaskStatusManager.get(publishResult);
    MutableTaskStatus mutableTaskStatus = status.edit();
    mutableTaskStatus.setStatus(TaskStatus.Status.COMPLETED);
    mutableTaskStatus.update();
  }

  private class StudentRecordParams {
    private double gpa;
    private double completedCrHr;
    private double completedGradePoints;

    StudentRecordParams(double pGpa, double pCompletedCrHr, double pCompletedGradePoints) {
      gpa = pGpa;
      completedCrHr = pCompletedCrHr;
      completedGradePoints = pCompletedGradePoints;
    }

    double getGpa() {
      return gpa;
    }

    double getCompletedCrHr() {
      return completedCrHr;
    }

    double getCompletedGradePoints() {
      return completedGradePoints;
    }
  }
}
