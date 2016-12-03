package org.ums.domain.model.immutable;

import org.ums.domain.model.common.EditType;
import org.ums.domain.model.common.Identifier;
import org.ums.domain.model.common.LastModifier;
import org.ums.domain.model.mutable.MutableCourse;
import org.ums.enums.CourseCategory;
import org.ums.enums.CourseType;

import java.io.Serializable;

public interface Course extends Serializable, LastModifier, EditType<MutableCourse>,
    Identifier<String> {

  String getNo();

  String getTitle();

  float getCrHr();

  String getOfferedDepartmentId();

  Department getOfferedBy();

  Department getOfferedTo();

  int getYear();

  int getSemester();

  int getViewOrder();

  int getCourseGroupId();

  CourseGroup getCourseGroup(final String pSyllabusId);

  String getSyllabusId();

  Syllabus getSyllabus();

  CourseType getCourseType();

  CourseCategory getCourseCategory();

  String getPairCourseId();

  int getTotalApplied();
}
