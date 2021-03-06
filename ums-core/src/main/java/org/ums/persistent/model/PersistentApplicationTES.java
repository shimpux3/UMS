package org.ums.persistent.model;

import org.springframework.context.ApplicationContext;
import org.ums.context.AppContext;
import org.ums.domain.model.mutable.MutableApplicationTES;
import org.ums.manager.*;

import java.math.BigDecimal;

/**
 * Created by Rumi on 2/20/2018.
 */
public class PersistentApplicationTES implements MutableApplicationTES {
  private static StudentManager sStudentManager;
  private static SemesterManager sSemesterManager;
  private static CourseManager sCourseManager;
  private static ApplicationTESManager sApplicationTESManager;
  static {
    ApplicationContext applicationContext = AppContext.getApplicationContext();
    sStudentManager = applicationContext.getBean("studentManager", StudentManager.class);
    sSemesterManager = applicationContext.getBean("semesterManager", SemesterManager.class);
    sCourseManager = applicationContext.getBean("courseManager", CourseManager.class);
    sApplicationTESManager = applicationContext.getBean("applicationTESManager", ApplicationTESManager.class);
  }
  private Long mId;
  private String mApplicationDate;
  private Long mQuestionID;
  private String mQuestionDetails;
  private String mReviewEligibleCoures;
  private String mSemesterName;
  private String mCourseTitle;
  private String mCourseNo;
  private String mTeacherId;
  private String mSection;
  private String mDeptId;
  private String mDeptShortName;
  private String mFullName;
  private Integer mObservationType;
  private Integer mPoint;
  private String mComment;
  private String mStudentId;
  private Integer mSemesterId;
  private String mDesignation;
  private Integer mStatus;
  private String mAppliedDate;
  private String mProgramShortName;
  private String mSemesterStartDate;
  private String mSemesterEndDate;
  private Boolean mDeadLineStatus;

  public PersistentApplicationTES() {

  }

  public PersistentApplicationTES(final PersistentApplicationTES persistentApplicationTES) {
    mId = persistentApplicationTES.getId();
    mApplicationDate = persistentApplicationTES.getApplicationDate();
    mQuestionID = persistentApplicationTES.getQuestionId();
    mQuestionDetails = persistentApplicationTES.getQuestionDetails();
    mReviewEligibleCoures = persistentApplicationTES.getReviewEligibleCourseId();
    mSemesterName = persistentApplicationTES.getSemesterName();
    mCourseTitle = persistentApplicationTES.getCourseTitle();
    mCourseNo = persistentApplicationTES.getCourseNo();
    mTeacherId = persistentApplicationTES.getTeacherId();
    mSection = persistentApplicationTES.getSection();
    mDeptId = persistentApplicationTES.getDeptId();
    mDeptShortName = persistentApplicationTES.getDeptShortName();
    mFullName = persistentApplicationTES.getFullName();
    mObservationType = persistentApplicationTES.getObservationType();
    mPoint = persistentApplicationTES.getPoint();
    mComment = persistentApplicationTES.getComment();
    mStudentId = persistentApplicationTES.getStudentId();
    mSemesterId = persistentApplicationTES.getSemester();
    mDesignation = persistentApplicationTES.getDesignation();
    mStatus = persistentApplicationTES.getStatus();
    mAppliedDate = persistentApplicationTES.getAppliedDate();
    mProgramShortName = persistentApplicationTES.getProgramShortName();
    mSemesterStartDate = persistentApplicationTES.getSemesterStartDate();
    mSemesterEndDate = persistentApplicationTES.getSemesterEndDate();
    mDeadLineStatus = persistentApplicationTES.getDeadLineStatus();
  }

  @Override
  public void setDeadLineStatus(Boolean pDeadLineStatus) {
    mDeadLineStatus = pDeadLineStatus;
  }

  @Override
  public Boolean getDeadLineStatus() {
    return mDeadLineStatus;
  }

  @Override
  public void setSemesterStartDate(String pSemesterStartDate) {
    mSemesterStartDate = pSemesterStartDate;
  }

  @Override
  public void setSemesterEndDate(String pSemesterEndDate) {
    mSemesterEndDate = pSemesterEndDate;
  }

  @Override
  public String getSemesterStartDate() {
    return mSemesterStartDate;
  }

  @Override
  public String getSemesterEndDate() {
    return mSemesterEndDate;
  }

  @Override
  public void setProgramShortName(String pProgramShortName) {
    this.mProgramShortName = pProgramShortName;
  }

  @Override
  public String getProgramShortName() {
    return mProgramShortName;
  }

  @Override
  public void setAppliedDate(String pAppliedDate) {
    mAppliedDate = pAppliedDate;
  }

  @Override
  public String getAppliedDate() {
    return mAppliedDate;
  }

  @Override
  public void setStatus(Integer pStatus) {
    mStatus = pStatus;
  }

  @Override
  public Integer getStatus() {
    return mStatus;
  }

  @Override
  public void setDesignation(String pDesignation) {
    mDesignation = pDesignation;
  }

  @Override
  public String getDesignation() {
    return mDesignation;
  }

  @Override
  public void setStudentId(String pStudentId) {
    mStudentId = pStudentId;
  }

  @Override
  public void setSemester(Integer pSemesterId) {
    mSemesterId = pSemesterId;
  }

  @Override
  public String getStudentId() {
    return mStudentId;
  }

  @Override
  public Integer getSemester() {
    return mSemesterId;
  }

  @Override
  public void setPoint(Integer pPoint) {
    mPoint = pPoint;
  }

  @Override
  public void setComment(String pComment) {
    mComment = pComment;
  }

  @Override
  public Integer getPoint() {
    return mPoint;
  }

  @Override
  public String getComment() {
    return mComment;
  }

  @Override
  public void setObservationType(Integer pObservationType) {
    mObservationType = pObservationType;
  }

  @Override
  public Integer getObservationType() {
    return mObservationType;
  }

  @Override
  public void setTeacherId(String pTeacherId) {
    mTeacherId = pTeacherId;
  }

  @Override
  public void setSection(String pSection) {
    mSection = pSection;
  }

  @Override
  public void setDeptId(String pDeptId) {
    mDeptId = pDeptId;
  }

  @Override
  public void setDeptShortName(String pDeptShortName) {
    mDeptShortName = pDeptShortName;
  }

  @Override
  public void setFullName(String pFirstName) {
    mFullName = pFirstName;
  }

  @Override
  public String getTeacherId() {
    return mTeacherId;
  }

  @Override
  public String getSection() {
    return mSection;
  }

  @Override
  public String getDeptId() {
    return mDeptId;
  }

  @Override
  public String getDeptShortName() {
    return mDeptShortName;
  }

  @Override
  public String getFullName() {
    return mFullName;
  }

  @Override
  public void setCourseTitle(String pCourseTitle) {
    mCourseTitle = pCourseTitle;
  }

  @Override
  public void setCourseNo(String pCourseNo) {
    mCourseNo = pCourseNo;
  }

  @Override
  public String getCourseTitle() {
    return mCourseTitle;
  }

  @Override
  public String getCourseNo() {
    return mCourseNo;
  }

  @Override
  public void setReviewEligibleCourses(String pReviewEligibleCourses) {
    mReviewEligibleCoures = pReviewEligibleCourses;
  }

  @Override
  public void setSemesterName(String pSemesterName) {
    mSemesterName = pSemesterName;
  }

  @Override
  public String getReviewEligibleCourseId() {
    return mReviewEligibleCoures;
  }

  @Override
  public String getSemesterName() {
    return mSemesterName;
  }

  @Override
  public void setQuestionId(Long pQuestionId) {
    mQuestionID = pQuestionId;
  }

  @Override
  public void setQuestionDetails(String pQuestionDetails) {
    mQuestionDetails = pQuestionDetails;
  }

  @Override
  public Long getQuestionId() {
    return mQuestionID;
  }

  @Override
  public String getQuestionDetails() {
    return mQuestionDetails;
  }

  @Override
  public void setApplicationDate(String pApplicationDate) {
    mApplicationDate = pApplicationDate;
  }

  @Override
  public String getApplicationDate() {
    return mApplicationDate;
  }

  @Override
  public MutableApplicationTES edit() {
    return new PersistentApplicationTES(this);
  }

  @Override
  public Long create() {
    return sApplicationTESManager.create(this);
  }

  @Override
  public void update() {
    sApplicationTESManager.update(this);
  }

  @Override
  public void delete() {
    sApplicationTESManager.delete(this);
  }

  @Override
  public Long getId() {
    return mId;
  }

  @Override
  public String getLastModified() {
    return null;
  }

  @Override
  public void setId(Long pId) {
    mId = pId;
  }

  @Override
  public void setLastModified(String pLastModified) {

  }
}
