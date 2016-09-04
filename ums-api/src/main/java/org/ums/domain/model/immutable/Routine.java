package org.ums.domain.model.immutable;

import org.ums.domain.model.common.EditType;
import org.ums.domain.model.common.Identifier;
import org.ums.domain.model.common.LastModifier;
import org.ums.domain.model.mutable.MutableRoutine;

import java.io.Serializable;

/**
 * Created by My Pc on 3/5/2016.
 */
public interface Routine extends Serializable,LastModifier,EditType<MutableRoutine>,Identifier<String>{
  Semester getSemester() throws Exception;
  Program getProgram() throws Exception;
  String getCourseId();
  String getCourseNo();
  int getDay();
  String getSection();
  int getAcademicYear();
  int getAcademicSemester();
  String getStartTime();
  String getEndTime();
  int getDuration()throws Exception;
  String getRoomNo();
}

