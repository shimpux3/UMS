package org.ums.persistent.dao;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.ums.decorator.UGRegistrationResultDaoDecorator;
import org.ums.domain.model.immutable.UGRegistrationResult;
import org.ums.domain.model.mutable.MutableUGRegistrationResult;
import org.ums.enums.ExamType;
import org.ums.persistent.model.PersistentUGRegistrationResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersistentUGRegistrationResultDao extends UGRegistrationResultDaoDecorator {
  String INSERT_ALL = "INSERT INTO UG_REGISTRATION_RESULT(STUDENT_ID, SEMESTER_ID, COURSE_ID, GL, EXAM_TYPE, STATUS, LAST_MODIFIED)" +
      " VALUES(?, ?, ?, ?, ?, ?, " + getLastModifiedSql() + ")";
  String DELETE_BY_STUDENT_SEMESTER = "DELETE FROM UG_REGISTRATION_RESULT WHERE STUDENT_ID = ? AND SEMESTER_ID = ? AND EXAM_TYPE = ? AND STATUS = ?";

  String SELECT_ALL_CCI="Select CCI.*,MST_COURSE.COURSE_NO,COURSE_TITLE,to_char(exam_routine.exam_date,'DD-MM-YYYY') exam_date FROM  " +
      "(  " +
      "Select student_id,course_id,GRADE_LETTER,exam_type,'Clearance' type from UG_REGISTRATION_RESULT where GRADE_LETTER='F' and  " +
      "Semester_Id=? and Student_id=? And Exam_Type=1  " +
      "Union  " +
      "Select student_id,course_id,GRADE_LETTER,exam_type,'Improvement' type from UG_REGISTRATION_RESULT where   " +
      "Semester_Id=? and Student_id=? and GRADE_LETTER not in ('F','A+','A','A-','B+') And Exam_Type=1  " +
      "Union  " +
      "Select student_id,course_id, 'F' GRADE_LETTER,1 Exam_Type,'Carry' type from   " +
      "(  " +
      " Select student_id,course_id from UG_REGISTRATION_RESULT where Student_id=? and Semester_Id!=? and Exam_Type=1 and GRADE_LETTER='F'   " +
      " Minus  " +
      " Select student_id,course_id from UG_REGISTRATION_RESULT where Student_id=? and GRADE_LETTER!='F' " +
      " ) " +
      " )CCI,DB_IUMS.MST_COURSE,EXAM_ROUTINE " +
      " WHERE MST_COURSE.COURSE_ID=CCI.COURSE_ID and cci.course_id=exam_routine.course_id  and exam_routine.exam_type=2 ORDER BY Type,COURSE_NO,EXAM_ROUTINE.EXAM_DATE";

  private JdbcTemplate mJdbcTemplate;

  public PersistentUGRegistrationResultDao(JdbcTemplate pJdbcTemplate) {
    mJdbcTemplate = pJdbcTemplate;
  }


  @Override
  public List<UGRegistrationResult> getBySemesterAndExamTYpeAndGrade(int pSemesterId, int pExamType, String pGrade) {
    return super.getBySemesterAndExamTYpeAndGrade(pSemesterId, pExamType, pGrade);
  }

  @Override
  public List<UGRegistrationResult> getBySemesterAndExamTypeAndGradeAndStudent(int pSemesterId, int pExamType, String pGrade, String pStudentId) {
    return super.getBySemesterAndExamTypeAndGradeAndStudent(pSemesterId, pExamType, pGrade, pStudentId);
  }

  @Override
  public List<UGRegistrationResult> getImprovementCoursesBySemesterAndStudent(int pSemesterId, String pStudentId) {
    return super.getImprovementCoursesBySemesterAndStudent(pSemesterId, pStudentId);
  }

  @Override
  public List<UGRegistrationResult> getCarryCoursesBySemesterAndStudent(int pSemesterId, String pStudentId) {
    return super.getCarryCoursesBySemesterAndStudent(pSemesterId, pStudentId);
  }

  @Override
  public List<UGRegistrationResult> getCarryClearanceImprovementCoursesByStudent(int pSemesterId, String pStudentId) {
    String query = SELECT_ALL_CCI;
    return mJdbcTemplate.query(query,new Object[]{pSemesterId,pStudentId,pSemesterId,pStudentId,pStudentId,pSemesterId,pStudentId},
        new UGRegistrationResultCCIRowMapper());
  }

  @Override
  public int create(List<MutableUGRegistrationResult> pMutableList) throws Exception {
    return mJdbcTemplate.batchUpdate(INSERT_ALL, getInsertParamList(pMutableList)).length;
  }

  private List<Object[]> getInsertParamList(List<MutableUGRegistrationResult> pRegistrationResults) throws Exception {
    List<Object[]> params = new ArrayList<>();
    for (UGRegistrationResult registrationResult : pRegistrationResults) {
      params.add(new Object[]{
          registrationResult.getStudent().getId(),
          registrationResult.getSemester().getId(),
          registrationResult.getCourse().getId(),
          registrationResult.getGradeLetter(),
          registrationResult.getExamType().getValue(),
          registrationResult.getStatus().getValue()
      });
    }

    return params;
  }

  @Override
  public int delete(List<MutableUGRegistrationResult> pMutableList) throws Exception {
    return mJdbcTemplate.batchUpdate(DELETE_BY_STUDENT_SEMESTER, getDeleteParamList(pMutableList)).length;
  }

  private List<Object[]> getDeleteParamList(List<MutableUGRegistrationResult> pRegistrationResults) throws Exception {
    List<Object[]> params = new ArrayList<>();
    for (UGRegistrationResult registrationResult : pRegistrationResults) {
      params.add(new Object[]{
          registrationResult.getStudent().getId(),
          registrationResult.getSemester().getId(),
          registrationResult.getExamType().getValue(),
          registrationResult.getStatus().getValue()
      });
    }

    return params;
  }


  //this will only work with Carry, Clearance and Improvement applications.
  class UGRegistrationResultCCIRowMapper implements RowMapper<UGRegistrationResult>{
    @Override
    public UGRegistrationResult mapRow(ResultSet pResultSet, int pI) throws SQLException {
      PersistentUGRegistrationResult result = new PersistentUGRegistrationResult();
      result.setStudentId(pResultSet.getString("STUDENT_ID"));
      result.setCourseId(pResultSet.getString("COURSE_ID"));
      result.setGradeLetter(pResultSet.getString("GRADE_LETTER"));
      result.setExamType(ExamType.get(pResultSet.getInt("EXAM_TYPE")));
      result.setType(pResultSet.getString("TYPE"));
      result.setCourseNo(pResultSet.getString("COURSE_NO"));
      result.setCourseTitle(pResultSet.getString("COURSE_TITLE"));
      result.setExamDate(pResultSet.getString("EXAM_DATE"));
      return result;
    }
  }

  //Todo: make another row mapper which will implement all the methods.



}
