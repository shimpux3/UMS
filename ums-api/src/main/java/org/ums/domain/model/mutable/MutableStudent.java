package org.ums.domain.model.mutable;

import org.ums.domain.model.common.Editable;
import org.ums.domain.model.common.MutableIdentifier;
import org.ums.domain.model.immutable.*;
import org.ums.usermanagement.user.User;

import java.util.Date;

public interface MutableStudent extends Student, Editable<String>, MutableIdentifier<String>, MutableLastModifier {
  void setUser(final User pUser);

  void setFullName(final String pFullName);

  void setDepartmentId(final String pDepartmentId);

  void setDepartment(final Department pDepartment);

  void setSemesterId(final Integer pSemesterId);

  void setSemester(final Semester pSemester);

  void setProgramId(final Integer pProgramId);

  void setProgram(final Program pProgram);

  void setFatherName(final String pFatherName);

  void setMotherName(final String pMotherName);

  void setDateOfBirth(final Date pDateOfBirth);

  void setGender(final String pGender);

  void setPresentAddress(final String pPresentAddress);

  void setPermanentAddress(final String pPermanentAddress);

  void setMobileNo(final String pMobileNo);

  void setPhoneNo(final String pPhoneNo);

  void setBloodGroup(final String pBloodGroup);

  void setEmail(final String pEmail);

  void setGuardianName(final String pGuardianName);

  void setGuardianMobileNo(final String pGuardianMobileNo);

  void setGuardianPhoneNo(final String pGuardianPhoneNo);

  void setGuardianEmail(final String pGuardianEmail);

  void setEnrollmentType(final EnrollmentType pEnrollmentType);

  void setCurrentYear(final Integer pCurrentYear);

  void setCurrentAcademicSemester(final Integer pAcademicSemester);

  void setCurrentEnrolledSemesterId(final Integer pCurrentEnrolledSemesterId);

  void setCurrentEnrolledSemester(final Semester pCurrentEnrolledSemester);

  void setTheorySection(final String pTheorySection);

  void setSessionalSection(final String pSessionalSection);

  void setApplicationType(final Integer pApplicationType);

  void setProgramShortName(final String pProgramShortName);

  void setAdviser(final Teacher pTeacher);

  void setStatus(final int pStatus);
}
