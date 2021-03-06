package org.ums.domain.model.immutable;

import org.ums.domain.model.common.EditType;
import org.ums.domain.model.common.Identifier;
import org.ums.domain.model.common.LastModifier;
import org.ums.domain.model.mutable.MutableExpelledInformation;

import java.io.Serializable;

/**
 * Created by Monjur-E-Morshed on 5/27/2018.
 */
public interface ExpelledInformation extends Serializable, LastModifier, EditType<MutableExpelledInformation>,
    Identifier<Long> {
  String getStudentId();

  String getStudentName();

  Integer getSemesterId();

  String getSemesterName();

  String getCourseId();

  String getCourseNo();

  String getCourseTitle();

  Integer getExamType();

  Integer getRegType();

  String getExamTypeName();

  Department getDepartment();

  String getProgramName();

  String getExpelledReason();

  String getInsertionDate();

  Integer getStatus();

  String getExamDate();

  String getDeptId();

  String getDeptName();

}
