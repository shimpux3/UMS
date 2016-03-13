package org.ums.common.builder;

import org.springframework.stereotype.Component;
import org.ums.persistent.model.PersistentCourse;
import org.ums.domain.model.mutable.MutableCourse;
import org.ums.domain.model.immutable.Course;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.List;
@Component
public class OptionalCourseApplicationBuilder {

  public void build(List<Course> pCourseList, JsonObject pJsonObject,String courseType) throws Exception {

    JsonArray entries=pJsonObject.getJsonArray(courseType);
    MutableCourse course;
    //List<Course> courseList=new ArrayList<>(entries.size());
    for (int i = 0; i < entries.size(); i++) {
      JsonObject jsonObject = entries.getJsonObject(i);
      course = new PersistentCourse();
      course.setId(jsonObject.getString("id"));
      pCourseList.add(course);
    }
  }

}
