package org.ums.manager;

import java.util.List;
import java.util.Map;

import org.ums.domain.model.immutable.StudentsExamAttendantInfo;
import org.ums.domain.model.immutable.UGRegistrationResult;
import org.ums.domain.model.mutable.MutableUGRegistrationResult;
import org.ums.enums.CourseRegType;
import org.ums.tabulation.TabulationCourseModel;

public interface UGRegistrationResultManager extends
    ContentManager<UGRegistrationResult, MutableUGRegistrationResult, Long> {
  List<UGRegistrationResult> getBySemesterAndExamTYpeAndGrade(int pSemesterId, int pExamType, String pGrade);

  List<UGRegistrationResult> getBySemesterAndExamTypeAndGradeAndStudent(int pSemesterId, int pExamType, String pGrade,
      String pStudentId);

  List<UGRegistrationResult> getImprovementCoursesBySemesterAndStudent(int pSemesterId, String pStudentId);

  List<UGRegistrationResult> getCarryCoursesBySemesterAndStudent(int pSemesterId, String pStudentId);

  List<UGRegistrationResult> getCarryClearanceImprovementCoursesByStudent(int pSemesterId, String pStudentId);

  List<UGRegistrationResult> getCCI(int pSemesterId, String pExamDate);

  List<UGRegistrationResult> getByCourseSemester(int semesterId, String courseId, CourseRegType pCourseRegType);

  List<UGRegistrationResult> getRegisteredCourseByStudent(int pSemesterId, String pStudentId,
      CourseRegType pCourseRegType);

  List<UGRegistrationResult> getRegisteredTheoryCourseByStudent(String pStudentId, int pSemesterId, int pExamType,
      int pRegType);

  List<UGRegistrationResult> getRegisteredCoursesBySemesterAndExamTypeAndRegTpe(int pSemesterId, int pExamType,
      int pRegType);

  List<UGRegistrationResult> getResults(String pStudentId, Integer pSemesterId);

  List<UGRegistrationResult> getSemesterResult(String pStudentId, Integer pSemesterId);

  List<UGRegistrationResult> getResultUpToSemester(String pStudentId, Integer pSemesterId, Integer pProgramTypeId);

  List<UGRegistrationResult> getResults(Integer pProgramId, Integer pSemesterId);

  List<UGRegistrationResult> getResults(Integer pProgramId, Integer pSemesterId, Integer pYear, Integer pSemester);

  Map<String, TabulationCourseModel> getResultForTabulation(Integer pProgramId, Integer pSemesterId, Integer pYear,
      Integer pSemester);

  List<StudentsExamAttendantInfo> getExamAttendantInfo(Integer pSemesterId, String pExamDate, Integer pExamType);

  Integer getTotalRegisteredStudentForCourse(final String pCourseId, final List<String> pSection,
      final Integer pSemesterId);

}
