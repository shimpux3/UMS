package org.ums.report.generator.seatPlan.support;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ums.domain.model.dto.ExamRoutineDto;
import org.ums.domain.model.immutable.*;
import org.ums.enums.CourseRegType;
import org.ums.enums.ExamType;
import org.ums.manager.*;
import org.ums.itext.UmsPdfPageEventHelper;
import org.ums.util.UmsUtils;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by Monjur-E-Morshed on 13-May-17.
 */
@Component
public class SeatChartReport {

  @Autowired
  private ClassRoomManager mRoomManager;

  @Autowired
  SemesterManager semesterManager;

  @Autowired
  private SeatPlanGroupManager mSeatPlanGroupManager;

  @Autowired
  private ExamRoutineManager mExamRoutineManager;

  @Autowired
  private SeatPlanManager mSeatPlanManager;

  @Autowired
  private UGRegistrationResultManager mUGRegistrationResultManager;

  public static final String DEST = "seat_plan_report.pdf";

  public void createPdf(String dest, boolean noSeatPlanInfo, int pSemesterId, int groupNo, int type, String examDate,
      OutputStream pOutputStream) throws IOException, DocumentException, WebApplicationException {

    Document document = new Document();
    document.addTitle("Seat Plan");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfWriter writer = PdfWriter.getInstance(document, baos);
    MyFooter event = new MyFooter();
    writer.setPageEvent(event);

    document.setPageSize(PageSize.A4.rotate());
    document.open();

    Font f = new Font(Font.FontFamily.TIMES_ROMAN, 10.0f, Font.BOLD, BaseColor.BLACK);

    // to do , semesterManager is not working.
    Semester mSemester = semesterManager.get(pSemesterId);

    String semesterName = mSemester.getName();

    String examDates = "Date: ";
    java.util.List<SeatPlanGroup> seatPlanGroup =
        mSeatPlanGroupManager.getBySemesterGroupAndType(pSemesterId, groupNo, type);
    java.util.List<ExamRoutineDto> examRoutines = mExamRoutineManager.getExamRoutine(pSemesterId, type);
    java.util.List<SeatPlan> seatPlans;
    if(groupNo <= 0) {
      seatPlans = mSeatPlanManager.getBySemesterAndGroupAndExamTypeAndExamDate(pSemesterId, groupNo, type, examDate);
    }
    else {
      seatPlans = mSeatPlanManager.getBySemesterAndGroupAndExamType(pSemesterId, groupNo, type);

    }
    java.util.List<UGRegistrationResult> ugRegistrationResults = new ArrayList<>();
    Map<String, Course> studentCourseMap = new HashMap<>();
    Map<String, CourseRegType> studentCourseRegType = new HashMap<>();
    if(groupNo <= 0) {
      ugRegistrationResults = mUGRegistrationResultManager.getCCI(pSemesterId, examDate);
      for(UGRegistrationResult ug : ugRegistrationResults) {
        studentCourseMap.put(ug.getStudentId() + ug.getCourse().getId(), ug.getCourse());
        studentCourseRegType.put(ug.getStudentId() + ug.getCourse().getId(), ug.getType());
      }
    }
    java.util.List<Student> students;
    java.util.List<Long> roomsOfTheSeatPlan = new ArrayList<>();
    // Map<String, Student> studentIdWIthStuddentInfoMap = new HashMap<>();
    Map<String, SeatPlan> roomRowColWithSeatPlanMap = new HashMap<>();
    // Map<Integer, Program> programIdWithProgramInfoMap = new HashMap<>();
    /*
     * if (groupNo != 0) { //students = mSpStudentManager.getRegisteredStudents(groupNo,
     * pSemesterId, type); studentIdWIthStuddentInfoMap =
     * mSpStudentManager.getRegisteredStudents(groupNo, pSemesterId, type) .parallelStream()
     * .collect(Collectors.toMap(Student::getId, Function.identity())); } else { //students =
     * mSpStudentManager.getStudentBySemesterIdAndExamDateForCCI(pSemesterId, examDate);
     * studentIdWIthStuddentInfoMap =
     * mSpStudentManager.getStudentBySemesterIdAndExamDateForCCI(pSemesterId, examDate)
     * .parallelStream() .collect(Collectors.toMap(Student::getId, Function.identity())); }
     * programIdWithProgramInfoMap = mProgramManager.getAll().stream()
     * .collect(Collectors.toMap(Program::getId, Function.identity()));
     */

    long startTime = System.currentTimeMillis();
    for(SeatPlan seatPlan : seatPlans) {
      roomsOfTheSeatPlan.add(seatPlan.getClassRoomId());
      /*
       * StringBuilder sb = new StringBuilder();
       * sb.append(seatPlan.getClassRoom().getId()).append(seatPlan
       * .getRowNo()).append(seatPlan.getColumnNo());
       */
      /* String roomRowCol =sb.toString(); */
      roomRowColWithSeatPlanMap.put(seatPlan.getClassRoomId() + "" + seatPlan.getRowNo() + "" + seatPlan.getColumnNo(),
          seatPlan);
    }
    long endTime = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    /*
     * for(Student student : students) { studentIdWIthStuddentInfoMap.put(student.getId(), student);
     * }
     */
    /*
     * for(Program program : programs) { programIdWithProgramInfoMap.put(program.getId(), program);
     * }
     */

    int routineCounter = 0;
    if(examDate.equals("null")) {
      for(ExamRoutineDto routine : examRoutines) {
        SeatPlanGroup group = seatPlanGroup.get(0);

        if(routine.getProgramId() == group.getProgramId() && routine.getCourseYear() == group.getAcademicYear()
            && routine.getCourseSemester() == group.getAcademicSemester()) {

          if(routineCounter == examRoutines.size()) {
            examDates = examDates + routine.getExamDate();
          }
          else {
            if(examDates.equals("Date: ")) {
              examDates = examDates + routine.getExamDate();
            }
            else {
              examDates = examDates + ", " + routine.getExamDate();
            }

          }
        }
        routineCounter += 1;

      }
    }
    else {
      // examDate="";
      if(type == ExamType.CLEARANCE_CARRY_IMPROVEMENT.getId()) {
        String testExamDate = UmsUtils.formatDate(examDate, "MM-dd-yyyy", "dd/MM/yyyy");
        examDates = "Date: " + testExamDate;
      }
      else {
        examDates = "Date: " + examDate;
      }
    }

    long startTimeOfTheMainAlgorithm = System.currentTimeMillis();

    if(noSeatPlanInfo) {
      Chunk c =
          new Chunk(
              "No SubGroup and No Seat Plan Information in the database, createOrUpdate one and then come back again!",
              f);
      c.setBackground(BaseColor.WHITE);
      Paragraph p = new Paragraph(c);
      p.setAlignment(Element.ALIGN_CENTER);
      document.add(p);
    }
    else {
      java.util.List<ClassRoom> rooms = mRoomManager.getAll();

      int roomCounter = 0;
      int seatPlanEnabledRoomCounter = 0;
      for(ClassRoom room : rooms) {
        float fontSize;
        roomCounter += 1;
        boolean checkIfRoomExistsInSeatPlan = roomsOfTheSeatPlan.contains(room.getId());
        // int checkIfRoomExists =
        // mSeatPlanManager.checkIfExistsByRoomSemesterGroupExamType(room.getId(),pSemesterId,groupNo,type);

        if(checkIfRoomExistsInSeatPlan && room.getId() != 284) {
          if(seatPlanEnabledRoomCounter > 0)
            document.newPage();
          seatPlanEnabledRoomCounter += 1;
          // document.add(UmsUtils.getIUMSHeaderParagraph());

          long startTimeInRoom = System.currentTimeMillis();
          String roomHeader = "Room No: " + room.getRoomNo();
          Paragraph pRoomHeader = new Paragraph(roomHeader, FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
          pRoomHeader.setAlignment(Element.ALIGN_CENTER);
          document.add(pRoomHeader);
          String semesterInfo = "";
          if(type == 1) {
            semesterInfo =
                "Semester Final Examination " + semesterName + ". Capacity: " + (room.getCapacity() + 2) + ".";
          }
          if(type == 2) {
            semesterInfo =
                "Clearance/Improvement/Carryover Examination " + semesterName + ". Capacity: "
                    + (room.getCapacity() + 2) + ".";
          }
          Paragraph pSemesterInfo = new Paragraph(semesterInfo, FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
          pSemesterInfo.setAlignment(Element.ALIGN_CENTER);
          document.add(pSemesterInfo);

          Paragraph pExamDates = new Paragraph(examDates, FontFactory.getFont(FontFactory.TIMES_BOLD, 10));
          pExamDates.setAlignment(Element.ALIGN_CENTER);
          pExamDates.setSpacingAfter(6f);
          document.add(pExamDates);

          // for getting the summery

          java.util.List<String> deptList = new ArrayList<>();
          Map<String, java.util.List<String>> deptStudentListMap = new HashMap<>();
          Map<String, String> deptWithDeptNameMap = new HashMap<>();
          Map<String, String> deptWithYearSemesterMap = new HashMap<>();

          int ccCounter = 0;
          for(int i = 1; i <= (room.getTotalRow() + 1); i++) {
            ccCounter += 1;
            for(int j = 1; j <= room.getTotalColumn(); j++) {

              /*
               * if(j<room.getTotalColumn()-2){ if(i==1) i=2; }
               */

              SeatPlan seatPlanOfTheRowAndCol = roomRowColWithSeatPlanMap.get(room.getId() + "" + i + "" + j);
              int ifSeatPlanExist;
              /*
               * if(groupNo == 0) { ifSeatPlanExist =
               * mSeatPlanManager.checkIfExistsBySemesterGroupTypeExamDateRoomRowAndCol(
               * pSemesterId, groupNo, type, examDate, room.getId(), i, j); } else { ifSeatPlanExist
               * = mSeatPlanManager.checkIfExistsBySemesterGroupTypeRoomRowAndCol(pSemesterId,
               * groupNo, type, room.getId(), i, j);
               * 
               * }
               */
              if(seatPlanOfTheRowAndCol != null) {
                SeatPlan seatPlan = seatPlanOfTheRowAndCol; // roomRowColWithSeatPlanMap.get(room.getId()+""+i+""+j)
                // ;
                Student student = seatPlan.getStudent();
                Program program;
                String dept;
                String deptName;
                if(groupNo <= 0) {
                  int year = studentCourseMap.get(student.getId() + seatPlan.getCourse().getId()).getYear();
                  int semester = studentCourseMap.get(student.getId() + seatPlan.getCourse().getId()).getSemester();
                  dept =
                      student.getProgram().getShortName().replace("B.Sc in ", "") + " "
                          + studentCourseMap.get(student.getId() + seatPlan.getCourse().getId()).getYear() + "/"
                          + studentCourseMap.get(student.getId() + seatPlan.getCourse().getId()).getSemester();
                  deptName = student.getProgram().getShortName().replace("B.Sc in ", "");
                }
                else {
                  program = student.getProgram();
                  dept =
                      program.getShortName().replace("B.Sc in ", "") + " " + student.getCurrentYear() + "/"
                          + student.getCurrentAcademicSemester();
                  deptName = program.getShortName().replace("B.Sc in ", "");

                }
                String yearSemester = "";
                if(groupNo <= 0) {
                  yearSemester =
                      studentCourseMap.get(student.getId() + seatPlan.getCourse().getId()).getYear() + "/"
                          + studentCourseMap.get(student.getId() + seatPlan.getCourse().getId()).getSemester();
                }
                else {
                  yearSemester = student.getCurrentYear() + "/" + student.getCurrentAcademicSemester();
                }
                if(deptList.size() == 0) {
                  deptList.add(dept);
                  java.util.List<String> studentList = new ArrayList<>();
                  if(groupNo <= 0) {
                    if(studentCourseRegType.get(student.getId() + seatPlan.getCourse().getId()).getId() == 3) {
                      studentList.add(student.getId() + "(C)");
                    }
                    else if(studentCourseRegType.get(student.getId() + seatPlan.getCourse().getId()).getId() == 4) {
                      studentList.add(student.getId() + "(SC)");
                    }
                    else if(studentCourseRegType.get(student.getId() + seatPlan.getCourse().getId()).getId() == 5) {
                      studentList.add(student.getId() + "(I)");

                    }
                    else {
                      studentList.add(student.getId());

                    }
                  }
                  else {
                    studentList.add(student.getId());

                  }
                  deptStudentListMap.put(dept, studentList);
                  deptWithDeptNameMap.put(dept, deptName);
                  deptWithYearSemesterMap.put(dept, yearSemester);
                }
                else {
                  boolean foundInTheList = false;
                  for(String deptOfTheList : deptList) {
                    if(deptOfTheList.equals(dept)) {
                      java.util.List<String> studentList = deptStudentListMap.get(dept);
                      if(groupNo <= 0) {
                        if(studentCourseRegType.get(student.getId() + seatPlan.getCourse().getId()).getId() == 3) {
                          studentList.add(student.getId() + "(C)");
                        }
                        else if(studentCourseRegType.get(student.getId() + seatPlan.getCourse().getId()).getId() == 4) {
                          studentList.add(student.getId() + "(SC)");
                        }
                        else if(studentCourseRegType.get(student.getId() + seatPlan.getCourse().getId()).getId() == 5) {
                          studentList.add(student.getId() + "(I)");

                        }
                        else {
                          studentList.add(student.getId());

                        }
                      }
                      else {
                        studentList.add(student.getId());

                      }
                      deptStudentListMap.put(dept, studentList);
                      foundInTheList = true;
                      break;
                    }
                  }
                  if(foundInTheList == false) {
                    deptList.add(dept);
                    java.util.List<String> studentList = new ArrayList<>();
                    if(groupNo <= 0) {
                      if(studentCourseRegType.get(student.getId() + seatPlan.getCourse().getId()).getId() == 3) {
                        studentList.add(student.getId() + "(C)");
                      }
                      else if(studentCourseRegType.get(student.getId() + seatPlan.getCourse().getId()).getId() == 4) {
                        studentList.add(student.getId() + "(SC)");
                      }
                      else if(studentCourseRegType.get(student.getId() + seatPlan.getCourse().getId()).getId() == 5) {
                        studentList.add(student.getId() + "(I)");

                      }
                      else {
                        studentList.add(student.getId());

                      }
                    }
                    else {
                      studentList.add(student.getId());

                    }
                    deptStudentListMap.put(dept, studentList);
                    deptStudentListMap.put(dept, studentList);
                    deptWithDeptNameMap.put(dept, deptName);
                    deptWithYearSemesterMap.put(dept, yearSemester);
                  }
                }
              }
            }
          }
          float summaryFontSize = 10.0f;
          if(deptList.size() < 6 && room.getCapacity() < 60) {
            fontSize = 11.0f;
            summaryFontSize = 11.0f;
          }
          else if(deptList.size() >= 6 && deptList.size() < 9 && room.getCapacity() < 60) {
            fontSize = 10.0f;
            summaryFontSize = 10.0f;

          }
          else if(deptList.size() == 9 && room.getCapacity() < 60) {
            fontSize = 10.0f;
            summaryFontSize = 10.0f;
          }
          else {
            if(room.getCapacity() <= 40) {
              fontSize = 9.0f;
              summaryFontSize = 9.0f;
            }
            else if(room.getCapacity() < 60) {
              fontSize = 8.5f;
              summaryFontSize = 8.5f;
            }
            else {
              fontSize = 8.0f;
              summaryFontSize = 8.0f;

            }
          }

          int totalStudent = 0;
          float[] tableWithForSummery = new float[] {1, 1, 8, 1};
          float[] columnWiths = new float[] {0.5f, 0.4f, 9f, 0.6f};

          PdfPTable summaryTable = new PdfPTable(tableWithForSummery);
          summaryTable.setWidthPercentage(100);
          summaryTable.setWidths(columnWiths);
          PdfPCell deptLabelCell = new PdfPCell();
          Paragraph deptLabel = new Paragraph("DEPT", FontFactory.getFont(FontFactory.TIMES_BOLD, summaryFontSize));
          deptLabelCell.addElement(deptLabel);
          deptLabelCell.setPaddingRight(-0.5f);
          deptLabelCell.setColspan(1);
          deptLabelCell.setPaddingTop(-2f);
          summaryTable.addCell(deptLabelCell);

          PdfPCell yearSemesterLabelCell = new PdfPCell();
          Paragraph yearSemesterLabel =
              new Paragraph("Y/S", FontFactory.getFont(FontFactory.TIMES_BOLD, summaryFontSize));
          yearSemesterLabelCell.addElement(yearSemesterLabel);
          yearSemesterLabelCell.setPaddingRight(-0.5f);
          yearSemesterLabelCell.setColspan(1);
          yearSemesterLabelCell.setPaddingTop(-2f);
          summaryTable.addCell(yearSemesterLabelCell);

          PdfPCell studentLabelCell = new PdfPCell();
          Paragraph studentLabel =
              new Paragraph("STUDENT ID", FontFactory.getFont(FontFactory.TIMES_BOLD, summaryFontSize));
          studentLabel.setAlignment(Element.ALIGN_CENTER);
          studentLabelCell.addElement(studentLabel);
          studentLabelCell.setPaddingTop(-2f);
          summaryTable.addCell(studentLabelCell);

          PdfPCell totalCellLabel = new PdfPCell();
          Paragraph totalLabel = new Paragraph("TOTAL", FontFactory.getFont(FontFactory.TIMES_BOLD, summaryFontSize));
          totalCellLabel.addElement(totalLabel);
          totalCellLabel.setPaddingTop(-2f);
          summaryTable.addCell(totalCellLabel);

          int deptListCounter = 0;
          for(String deptOfTheList : deptList) {
            PdfPCell deptCell = new PdfPCell();
            Paragraph deptParagraph =
                new Paragraph(deptWithDeptNameMap.get(deptOfTheList), FontFactory.getFont(FontFactory.TIMES_ROMAN,
                    summaryFontSize));
            deptCell.addElement(deptParagraph);
            deptCell.setPaddingTop(-2f);
            summaryTable.addCell(deptCell);
            PdfPCell yearSemesterCell = new PdfPCell();
            Paragraph yearSemesterCellParagraph =
                new Paragraph(deptWithYearSemesterMap.get(deptOfTheList), FontFactory.getFont(FontFactory.TIMES_ROMAN,
                    summaryFontSize));
            yearSemesterCell.addElement(yearSemesterCellParagraph);
            yearSemesterCell.setPaddingTop(-2f);
            summaryTable.addCell(yearSemesterCell);
            PdfPCell studentCell = new PdfPCell();
            String studentListInString = "";
            java.util.List<String> studentList = deptStudentListMap.get(deptOfTheList);
            Collections.sort(studentList);
            totalStudent += studentList.size();
            int studentCounter = 0;
            for(String studentOfTheList : studentList) {
              if(studentCounter == 0) {
                studentListInString = studentListInString + studentOfTheList;
                studentCounter += 1;
              }
              else {
                if(studentList.size() > 10) {
                  studentListInString = studentListInString + ", " + studentOfTheList;

                }
                else {
                  studentListInString = studentListInString + ", " + studentOfTheList;

                }

              }
            }

            deptListCounter += 1;
            studentListInString = studentListInString + " = " + studentList.size();

            if(studentList.size() > 10) {
              summaryFontSize = 9.0f;
            }

            Paragraph studentCellParagraph =
                new Paragraph(studentListInString, FontFactory.getFont(FontFactory.TIMES_ROMAN, summaryFontSize));
            studentCell.addElement(studentCellParagraph);
            studentCell.setPaddingTop(-2f);
            summaryTable.addCell(studentCell);

            if(deptListCounter == deptList.size()) {
              PdfPCell totalCell = new PdfPCell();
              Paragraph totalLabels =
                  new Paragraph("" + totalStudent, FontFactory.getFont(FontFactory.TIMES_BOLD, summaryFontSize));
              totalLabels.setAlignment(Element.ALIGN_CENTER);
              totalCell.addElement(totalLabels);
              totalCell.setPaddingTop(-2f);
              summaryTable.addCell(totalCell);
            }
            else {
              PdfPCell totalCell = new PdfPCell();
              Paragraph totalLabels = new Paragraph("");
              totalCell.addElement(totalLabels);
              summaryTable.addCell(totalCell);
            }

          }
          summaryTable.setSpacingAfter(8f);
          document.add(summaryTable);
          // end of getting the summary

          for(int i = 1; i <= room.getTotalRow() + 1; i++) {
            Paragraph tableRow = new Paragraph();
            PdfPTable mainTable = new PdfPTable(room.getTotalColumn() / 2);
            mainTable.setWidthPercentage(100);
            PdfPCell[] cellsForMainTable = new PdfPCell[room.getTotalColumn() / 2];
            int cellCounter = 0;
            for(int j = 1; j <= room.getTotalColumn(); j++) {
              cellsForMainTable[cellCounter] = new PdfPCell();
              PdfPTable table = new PdfPTable(2);
              SeatPlan seatPlan = roomRowColWithSeatPlanMap.get(room.getId() + "" + i + "" + j);
              // int ifSeatPlanExists =
              // mSeatPlanManager.checkIfExistsBySemesterGroupTypeRoomRowAndCol(pSemesterId,groupNo,type,room.getId(),i,j);
              if(seatPlan == null) {
                PdfPCell emptyCell = new PdfPCell();
                String emptyString = "  ";
                Paragraph emptyParagraph = new Paragraph(emptyString);
                Paragraph emptyParagraph2 = new Paragraph("  ");
                emptyParagraph.setPaddingTop(-5f);
                emptyParagraph2.setPaddingTop(-10f);
                emptyCell.addElement(emptyParagraph);
                emptyCell.addElement(emptyParagraph2);
                if(i == 1 && j <= room.getTotalColumn() - 2)
                  emptyCell.setBorder(Rectangle.NO_BORDER);
                emptyCell.setPaddingTop(-5f);
                table.addCell(emptyCell);
                // cellsForMainTable[cellCounter].addElement(emptyCell);
              }
              else {
                /*
                 * java.util.List<SeatPlan> seatPlanLists =
                 * mSeatPlanManager.getBySemesterGroupTypeRoomRowAndCol
                 * (pSemesterId,groupNo,type,room.getId(),i,j);
                 * 
                 * SeatPlan seatPlan = seatPlanLists.get(0);
                 */

                Student student = seatPlan.getStudent();
                Program program = student.getProgram();

                PdfPCell upperCell = new PdfPCell();

                PdfPCell lowerCell = new PdfPCell();

                String upperPart = "";
                if(groupNo <= 0) {
                  upperPart =
                      program.getShortName().replace("B.Sc in ", "") + " "
                          + studentCourseMap.get(student.getId() + seatPlan.getCourse().getId()).getYear() + "/"
                          + studentCourseMap.get(student.getId() + seatPlan.getCourse().getId()).getSemester();
                }
                else {
                  upperPart =
                      program.getShortName().replace("B.Sc in ", "") + " " + student.getCurrentYear() + "/"
                          + student.getCurrentAcademicSemester();
                }

                Paragraph upperParagraph =
                    new Paragraph(upperPart, FontFactory.getFont(FontFactory.TIMES_ROMAN, fontSize));
                upperParagraph.setPaddingTop(-5f);
                String lowerPart = student.getId();
                Paragraph lowerParagraph =
                    new Paragraph(lowerPart, FontFactory.getFont(FontFactory.TIMES_ROMAN, fontSize));
                lowerParagraph.setPaddingTop(-10f);
                upperCell.addElement(upperParagraph);
                upperCell.addElement(lowerParagraph);
                upperCell.setPaddingTop(-5f);

                table.addCell(upperCell);
              }

              j = j + 1;
              SeatPlan seatPlan2 = roomRowColWithSeatPlanMap.get(room.getId() + "" + i + "" + j);

              if(seatPlan2 == null) {
                PdfPCell emptyCell = new PdfPCell();
                String emptyString = "  ";
                Paragraph emptyParagraph = new Paragraph(emptyString);
                Paragraph emptyParagraph2 = new Paragraph("  ");
                emptyParagraph.setPaddingTop(-5f);
                emptyParagraph2.setPaddingTop(-10f);
                emptyCell.addElement(emptyParagraph);
                emptyCell.addElement(emptyParagraph2);
                if(i == 1 && j <= room.getTotalColumn() - 2)
                  emptyCell.setBorder(Rectangle.NO_BORDER);
                emptyCell.setPaddingTop(-5f);
                table.addCell(emptyCell);

              }
              else {

                Student student2 = seatPlan2.getStudent();
                Program program2 = student2.getProgram();

                PdfPCell upperCell = new PdfPCell();
                upperCell.setColspan(10);
                PdfPCell lowerCell = new PdfPCell();
                lowerCell.setColspan(10);
                String upperPart = "";
                if(groupNo <= 0) {
                  upperPart =
                      program2.getShortName().replace("B.Sc in ", "") + " "
                          + studentCourseMap.get(student2.getId() + seatPlan2.getCourse().getId()).getYear() + "/"
                          + studentCourseMap.get(student2.getId() + seatPlan2.getCourse().getId()).getSemester();
                }
                else {
                  upperPart =
                      program2.getShortName().replace("B.Sc in ", "") + " " + student2.getCurrentYear() + "/"
                          + student2.getCurrentAcademicSemester();
                  /*
                   * upperPart = program2.getShortName().replace("B.Sc in ", "") + " " +
                   * studentCourseMap.get(student2.getId() + student2.getCurrentYear() + "/" +
                   * studentCourseMap.get(student2.getId() +
                   * seatPlan2.getCourse().getId()).getSemester();
                   */
                }

                Paragraph upperParagraph =
                    new Paragraph(upperPart, FontFactory.getFont(FontFactory.TIMES_ROMAN, fontSize));
                upperParagraph.setPaddingTop(-5f);
                String lowerPart = student2.getId();
                Paragraph lowerParagraph =
                    new Paragraph(lowerPart, FontFactory.getFont(FontFactory.TIMES_ROMAN, fontSize));
                lowerParagraph.setPaddingTop(-10f);
                upperCell.addElement(upperParagraph);
                upperCell.addElement(lowerParagraph);
                upperCell.setPaddingTop(-5f);
                table.addCell(upperCell);
              }
              table.setPaddingTop(-2f);
              cellsForMainTable[cellCounter].addElement(table);
              cellsForMainTable[cellCounter].setBorder(PdfPCell.NO_BORDER);
              mainTable.addCell(cellsForMainTable[cellCounter]);

              cellCounter += 1;
            }

            tableRow.add(mainTable);
            tableRow.setSpacingAfter(3f);

            document.add(tableRow);
          }

          PdfPTable footer = new PdfPTable(3);
          float footerFontSize;
          if(deptList.size() >= 9) {
            footerFontSize = 10.0f;
          }
          else {
            footerFontSize = 12.0f;

          }

          PdfPCell dateAndPreparedByCell = new PdfPCell();
          dateAndPreparedByCell.addElement(new Phrase("Date:", FontFactory.getFont(FontFactory.TIMES_BOLD,
              footerFontSize)));
          dateAndPreparedByCell.addElement(new Phrase("Prepared By", FontFactory.getFont(FontFactory.TIMES_BOLD,
              footerFontSize)));
          dateAndPreparedByCell.setBorder(Rectangle.NO_BORDER);
          footer.addCell(dateAndPreparedByCell);
          // todo: remove border
          PdfPCell spaceCell = new PdfPCell();
          Paragraph spaceParagraph = new Paragraph(" ");
          spaceCell.addElement(spaceParagraph);
          spaceCell.setBorder(PdfPCell.NO_BORDER);
          PdfPCell checkedByCell = new PdfPCell();
          Paragraph pCheckedBy =
              new Paragraph("Checked By", FontFactory.getFont(FontFactory.TIMES_BOLD, footerFontSize));
          checkedByCell.addElement(spaceParagraph);
          checkedByCell.addElement(pCheckedBy);
          checkedByCell.setBorder(PdfPCell.NO_BORDER);
          footer.addCell(checkedByCell);
          PdfPCell controllerCell = new PdfPCell();
          Paragraph pController =
              new Paragraph("Controller of Examinations", FontFactory.getFont(FontFactory.TIMES_BOLD, footerFontSize));
          controllerCell.addElement(spaceParagraph);
          controllerCell.addElement(pController);
          controllerCell.setBorder(PdfPCell.NO_BORDER);
          footer.addCell(controllerCell);

          if(room.getCapacity() <= 40) {
            footer.setSpacingBefore(40f);

          }
          else {
            footer.setSpacingBefore(15.0f);

          }

          /* document.add(footer); */
          long endTimeInRoom = System.currentTimeMillis();
          long totalTimeInRoom = endTimeInRoom - startTimeInRoom;
          // document.newPage();

        }

        // just for debug purpose
        /*
         * if(roomCounter==26){ break; }
         */

      }
    }

    long endTimeOfTheMainAlgorithm = System.currentTimeMillis();
    long totalTimeOfTHeMainAlgorithm = endTimeOfTheMainAlgorithm - startTimeOfTheMainAlgorithm;

    double totalTimeTaken = totalTimeOfTHeMainAlgorithm / 60000;
    long exp = totalTimeOfTHeMainAlgorithm;
    document.close();
    baos.writeTo(pOutputStream);
  }

  class MyFooter extends UmsPdfPageEventHelper {
    Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
      super.onStartPage(writer, document);
    }

    public void onEndPage(PdfWriter writer, Document document) {
      PdfContentByte cb = writer.getDirectContent();
      Paragraph pDate = new Paragraph("Date:", FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
      Paragraph pPreparedBy = new Paragraph("Prepared by:", FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
      Paragraph pCheckedBy = new Paragraph("Checked by:", FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
      Paragraph pController =
          new Paragraph("Controller of Examinations", FontFactory.getFont(FontFactory.TIMES_BOLD, 12));

      PdfPCell dateAndPreparedByCell = new PdfPCell();
      dateAndPreparedByCell.addElement(new Phrase("Date:", FontFactory.getFont(FontFactory.TIMES_BOLD, 12)));
      dateAndPreparedByCell.addElement(new Phrase("Prepared By", FontFactory.getFont(FontFactory.TIMES_BOLD, 12)));
      dateAndPreparedByCell.setBorder(Rectangle.NO_BORDER);
      Phrase leftPhrase = new Phrase();
      leftPhrase.add(pDate);
      Phrase leftPhraseBottom = new Phrase();
      leftPhraseBottom.add(pPreparedBy);

      Phrase middlePhraseBottom = new Phrase();
      middlePhraseBottom.add(pCheckedBy);

      Phrase rightPhraseButtom = new Phrase();
      rightPhraseButtom.add(pController);

      ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, leftPhrase, (document.left()) / 3, document.bottom() - 1, 0);

      ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, leftPhraseBottom, (document.left()) / 3,
          document.bottom() - 20, 0);

      ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, middlePhraseBottom, (document.right()) / 2,
          document.bottom() - 20, 0);

      ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, rightPhraseButtom, (document.right()) / 1,
          document.bottom() - 20, 0);

    }
  }

}
