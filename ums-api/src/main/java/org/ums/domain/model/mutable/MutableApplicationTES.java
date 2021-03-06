package org.ums.domain.model.mutable;

import org.ums.domain.model.common.Editable;
import org.ums.domain.model.common.MutableIdentifier;
import org.ums.domain.model.immutable.ApplicationTES;

import java.math.BigDecimal;

/**
 * Created by Rumi on 2/20/2018.
 */
public interface MutableApplicationTES extends ApplicationTES, Editable<Long>, MutableLastModifier,
    MutableIdentifier<Long> {

  void setApplicationDate(final String pApplicationDate);

  void setQuestionId(final Long pQuestionId);

  void setQuestionDetails(final String pQuestionDetails);

  void setReviewEligibleCourses(final String pReviewEligibleCourses);

  void setSemesterName(final String pSemesterName);

  void setCourseTitle(final String pCourseTitle);

  void setCourseNo(final String pCourseNo);

  void setTeacherId(final String pTeacherId);

  void setSection(final String pSection);

  void setDeptId(final String pDeptId);

  void setDeptShortName(final String pDeptShortName);

  void setFullName(final String pFirstName);

  void setObservationType(final Integer pObservationType);

  void setPoint(final Integer pPoint);

  void setComment(final String pComment);

  void setStudentId(final String pStudentId);

  void setSemester(final Integer pSemesterId);

  void setDesignation(final String pDesignation);

  void setStatus(final Integer pStatus);

  void setAppliedDate(final String pAppliedDate);

  void setProgramShortName(final String pProgramShortName);

  void setSemesterStartDate(final String pSemesterStartDate);

  void setSemesterEndDate(final String setSemesterEndDate);

  void setDeadLineStatus(final Boolean pDeadLineStatus);

}
