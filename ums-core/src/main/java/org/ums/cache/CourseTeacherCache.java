package org.ums.cache;

import org.ums.domain.model.immutable.ApplicationTES;
import org.ums.domain.model.immutable.CourseTeacher;
import org.ums.domain.model.mutable.MutableCourseTeacher;
import org.ums.manager.CacheManager;
import org.ums.manager.CourseTeacherManager;
import org.ums.util.CacheUtil;

import java.util.List;

public class CourseTeacherCache extends
    AssignedTeacherCache<CourseTeacher, MutableCourseTeacher, Long, CourseTeacherManager> implements
    CourseTeacherManager {
  @Override
  public List<ApplicationTES> getAllSectionForSelectedCourse(String pCourseId, String pTeacherId, Integer pSemesterId) {
    return getManager().getAllSectionForSelectedCourse(pCourseId, pTeacherId, pSemesterId);
  }

  public CourseTeacherCache(final CacheManager<CourseTeacher, Long> pCacheManager) {
    super(pCacheManager);
  }

  @Override
  public List<CourseTeacher> getAssignedSections(Integer pSemesterId, String pCourseId, String pTeacherId) {
    return getManager().getAssignedSections(pSemesterId, pCourseId, pTeacherId);
  }

  @Override
  public List<CourseTeacher> getCourseTeacher(int pSemesterId, String pCourseId) {
    return getManager().getCourseTeacher(pSemesterId, pCourseId);
  }

  @Override
  public List<CourseTeacher> getCourseTeacher(int pSemesterId) {
    return getManager().getCourseTeacher(pSemesterId);
  }

  @Override
  public List<CourseTeacher> getCourseTeacher(int pSemesterId, List<String> pCourseIdList) {
    return getManager().getCourseTeacher(pSemesterId, pCourseIdList);
  }

  @Override
  public List<CourseTeacher> getDistinctCourseTeacher(int pSemesterId) {
    return getManager().getDistinctCourseTeacher(pSemesterId);
  }

  @Override
  public List<CourseTeacher> getCourseTeacher(int pSemesterId, String pCourseId, String pSection) {
    return getManager().getCourseTeacher(pSemesterId, pCourseId, pSection);
  }

  @Override
  protected String getCacheKey(Long pId) {
    return CacheUtil.getCacheKey(CourseTeacher.class, pId);
  }

  @Override
  public List<CourseTeacher> getCourseTeacher(int pProgramId, int pSemesterId, String pSection, int pYear, int pSemester) {
    return getManager().getCourseTeacher(pProgramId, pSemesterId, pSection, pYear, pSemester);
  }

  @Override
  public List<CourseTeacher> getCourseTeacher(int pSemesterId, Integer pProgramId) {
    return getManager().getCourseTeacher(pSemesterId, pProgramId);
  }
}
