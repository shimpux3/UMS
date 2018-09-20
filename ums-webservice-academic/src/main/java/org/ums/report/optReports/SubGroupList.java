package org.ums.report.optReports;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.ums.logs.PutLog;

import java.util.List;

/**
 * Created by Monjur-E-Morshed on 9/20/2018.
 */
public class SubGroupList {
  public Integer groupId;
  public String groupName;
  public Integer totalApplied;
  public Integer totalSeats;
  public boolean isMandatory;
  public List<CourseList> courses;

  public SubGroupList() {}

  public SubGroupList(Integer subGroupId, String subGroupName, Integer totalApplied, Integer totalSeats,
      boolean isMandatory, List<CourseList> courses) {
    this.groupId = subGroupId;
    this.groupName = subGroupName;
    this.totalApplied = totalApplied;
    this.totalSeats = totalSeats;
    this.isMandatory = isMandatory;
    this.courses = courses;
  }

  public boolean isMandatory() {
    return isMandatory;
  }

  public void setMandatory(boolean mandatory) {
    isMandatory = mandatory;
  }

  public Integer getSubGroupId() {
    return groupId;
  }

  public void setSubGroupId(Integer subGroupId) {
    this.groupId = subGroupId;
  }

  public String getSubGroupName() {
    return groupName;
  }

  public void setSubGroupName(String subGroupName) {
    this.groupName = subGroupName;
  }

  public Integer getTotalApplied() {
    return totalApplied;
  }

  public void setTotalApplied(Integer totalApplied) {
    this.totalApplied = totalApplied;
  }

  public Integer getTotalSeats() {
    return totalSeats;
  }

  public void setTotalSeats(Integer totalSeats) {
    this.totalSeats = totalSeats;
  }

  public List<CourseList> getCourses() {
    return courses;
  }

  public void setCourses(List<CourseList> courses) {
    this.courses = courses;
  }
}
