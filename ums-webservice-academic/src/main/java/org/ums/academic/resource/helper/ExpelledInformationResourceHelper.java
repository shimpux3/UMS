package org.ums.academic.resource.helper;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ums.builder.Builder;
import org.ums.builder.ExpelledInformationBuilder;
import org.ums.cache.LocalCache;
import org.ums.domain.model.dto.ExamRoutineDto;
import org.ums.domain.model.immutable.ExpelledInformation;
import org.ums.domain.model.immutable.UGRegistrationResult;
import org.ums.domain.model.mutable.MutableExpelledInformation;
import org.ums.enums.CourseRegType;
import org.ums.enums.ExamType;
import org.ums.enums.ProgramType;
import org.ums.manager.*;
import org.ums.persistent.model.PersistentExpelledInformation;
import org.ums.resource.ResourceHelper;

import javax.json.*;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Monjur-E-Morshed on 5/27/2018.
 */
@Component
public class ExpelledInformationResourceHelper extends
    ResourceHelper<ExpelledInformation, MutableExpelledInformation, Long> {
  @Autowired
  ExpelledInformationManager mManager;
  @Autowired
  ExpelledInformationBuilder mBuilder;
  @Autowired
  UGRegistrationResultManager mUGRegistrationResultManager;
  @Autowired
  SemesterManager mSemesterManager;
  @Autowired
  CourseManager mCourseManager;
  @Autowired
  ExamRoutineManager mExamRoutineManager;
  @Autowired
  StudentManager mStudentManager;
  @Autowired
  DepartmentManager mDepartmentManager;
  @Autowired
  ProgramManager mProgramManager;

  @Override
  public Response post(JsonObject pJsonObject, UriInfo pUriInfo) throws Exception {
    JsonArray entries = pJsonObject.getJsonArray("entries");
    LocalCache localCache = new LocalCache();
    JsonObject jsonObject = entries.getJsonObject(0);
    PersistentExpelledInformation application = new PersistentExpelledInformation();
    application.setSemesterId(mSemesterManager.getActiveSemester(ProgramType.UG.getValue()).getId());
    getBuilder().build(application, jsonObject, localCache);

    Long response = mManager.create(application);
    URI contextURI = null;
    Response.ResponseBuilder builder = Response.created(contextURI);
    if(response != null) {
      builder.status(Response.Status.CREATED);
    }
    else {
      builder.status(Response.Status.NOT_FOUND);
    }

    return builder.build();
  }

  public Response deleteExpelStudents(JsonObject pJsonObject, UriInfo pUriInfo) {
    List<MutableExpelledInformation> applications = new ArrayList<>();
    JsonArray entries = pJsonObject.getJsonArray("entries");
    for(int i = 0; i < entries.size(); i++) {
      LocalCache localCache = new LocalCache();
      JsonObject jsonObject = entries.getJsonObject(i);
      PersistentExpelledInformation application = new PersistentExpelledInformation();
      getBuilder().build(application, jsonObject, localCache);
      applications.add(application);
    }
    mManager.delete(applications);

    URI contextURI = null;
    Response.ResponseBuilder builder = Response.created(contextURI);
    builder.status(Response.Status.CREATED);
    return builder.build();
  }

  public JsonObject getCourseList(final String pStudentId, final Integer pRegType, final Request pRequest,
      final UriInfo pUriInfo) {
    Integer examType = getExamType(pRegType);
    List<MutableExpelledInformation> mutableExpelledInformationList =
        getExpelledInformation(pStudentId, pRegType, examType);
    JsonObjectBuilder object = Json.createObjectBuilder();
    JsonArrayBuilder children = Json.createArrayBuilder();
    LocalCache localCache = new LocalCache();
    for(MutableExpelledInformation app : mutableExpelledInformationList) {
      children.add(toJson(app, pUriInfo, localCache));
    }
    object.add("entries", children);
    localCache.invalidate();
    return object.build();
  }

  public List<MutableExpelledInformation> getExpelledInformation(String pStudentId, Integer pRegType, Integer examType) {
    List<ExamRoutineDto> examRoutineList = mExamRoutineManager.getExamRoutine(mSemesterManager.getActiveSemester(ProgramType.UG.getValue()).getId(), examType);
    Map<String, String> examRoutineMapWithCourseId = examRoutineList
            .stream()
            .collect(Collectors.toMap(e->e.getCourseId(), e->e.getExamDate()));
    List<ExpelledInformation> expelledInfo=mManager.getSemesterExamTyeRegTypeWiseRecords(
            mSemesterManager.getActiveSemester(ProgramType.UG.getValue()).getId(),examType,pRegType);
    List<UGRegistrationResult> registeredTheoryCourseList =
        mUGRegistrationResultManager.getRegisteredTheoryCourseByStudent(pStudentId, mSemesterManager.getActiveSemester(ProgramType.UG.getValue()).getId(), examType,pRegType);
    List<MutableExpelledInformation> mutableExpelledInformationList = addDataToList(pStudentId, examType, examRoutineMapWithCourseId, expelledInfo, registeredTheoryCourseList);
    return mutableExpelledInformationList;
  }

  public List<MutableExpelledInformation> addDataToList(String pStudentId, Integer examType, Map<String, String> examRoutineMapWithCourseId, List<ExpelledInformation> expelledInfo, List<UGRegistrationResult> registeredTheoryCourseList) {
    List<MutableExpelledInformation> mutableExpelledInformationList = new ArrayList<>();
    for(UGRegistrationResult registrationResult : registeredTheoryCourseList) {
      MutableExpelledInformation expelledInformation =new PersistentExpelledInformation();
      expelledInformation.setCourseId(registrationResult.getCourseId());
      expelledInformation.setCourseNo(mCourseManager.get(registrationResult.getCourseId()).getNo());
      expelledInformation.setCourseTitle(mCourseManager.get(registrationResult.getCourseId()).getTitle());
      expelledInformation.setExamDate(examRoutineMapWithCourseId.get(registrationResult.getCourseId()));
      expelledInformation.setRegType(registrationResult.getType().getId());
      expelledInformation.setStatus(expelledInfo.stream().filter(a->a.getSemesterId().equals(mSemesterManager.getActiveSemester(ProgramType.UG.getValue()).getId()) && a.getCourseId().equals(registrationResult.getCourseId()) && a.getStudentId().equals(pStudentId)
               && a.getExamType()==examType).collect(Collectors.toList()).size()==1 ? 1:0);
      mutableExpelledInformationList.add(expelledInformation);
    }
    return mutableExpelledInformationList;
  }

  public JsonObject getExpelInfoList(final Integer pSemesterId, final Integer pRegType, final Request pRequest,
                                  final UriInfo pUriInfo) {
    Integer examType = getExamType(pRegType);
    Integer semesterId = mSemesterManager.getActiveSemester(ProgramType.UG.getValue()).getId();
    Integer hideDeleteOption=0;
    if(pSemesterId.equals(semesterId)){
      hideDeleteOption=1;
    }
    List<ExamRoutineDto> examRoutineList = mExamRoutineManager.getExamRoutine(pSemesterId, examType);

    Map<String, String> examRoutineMapWithCourseId = examRoutineList
            .stream()
            .collect(Collectors.toMap(e->e.getCourseId(), e->e.getExamDate()));
    Map<String, String> examRoutineMapWithProgramId= examRoutineList
            .stream()
            .collect(Collectors.toMap(e->e.getCourseId(), e->e.getProgramName()));
    List<ExpelledInformation> expelledInfo=mManager.getSemesterExamTyeRegTypeWiseRecords(pSemesterId,examType,pRegType);

    List<MutableExpelledInformation> expelInfoList = addInfo(pRegType, hideDeleteOption, examRoutineMapWithCourseId, examRoutineMapWithProgramId, expelledInfo);

    JsonObjectBuilder object = Json.createObjectBuilder();
    JsonArrayBuilder children = Json.createArrayBuilder();
    LocalCache localCache = new LocalCache();
    for(MutableExpelledInformation app : expelInfoList) {
      children.add(toJson(app, pUriInfo, localCache));
    }
    object.add("entries", children);
    localCache.invalidate();
    return object.build();
  }

  public List<MutableExpelledInformation> addInfo(Integer pRegType, Integer hideDeleteOption,
      Map<String, String> examRoutineMapWithCourseId, Map<String, String> examRoutineMapWithProgramId,
      List<ExpelledInformation> expelledInfo) {
    List<MutableExpelledInformation> expelInfoList = new ArrayList<>();
    for(ExpelledInformation exp : expelledInfo) {
      MutableExpelledInformation expelledInformation = new PersistentExpelledInformation();
      expelledInformation.setStudentId(exp.getStudentId());
      expelledInformation.setStudentName(mStudentManager.get(exp.getStudentId()).getFullName());
      expelledInformation.setSemesterId(exp.getSemesterId());
      expelledInformation.setSemesterName(mSemesterManager.get(exp.getSemesterId()).getName());
      expelledInformation.setDeptId(mStudentManager.get(exp.getStudentId()).getDepartmentId());
      expelledInformation.setDeptName(mDepartmentManager.get(mStudentManager.get(exp.getStudentId()).getDepartmentId())
          .getShortName());
      expelledInformation.setExamType(exp.getExamType());
      expelledInformation.setExamTypeName(CourseRegType.get(pRegType).getLabel());
      expelledInformation.setStatus(hideDeleteOption);
      expelledInformation.setExpelledReason(exp.getExpelledReason());
      expelledInformation.setCourseId(exp.getCourseId());
      expelledInformation.setCourseNo(mCourseManager.get(exp.getCourseId()).getNo());
      expelledInformation.setCourseTitle(mCourseManager.get(exp.getCourseId()).getTitle());
      expelledInformation.setExamDate(examRoutineMapWithCourseId.get(exp.getCourseId()));
      expelledInformation.setProgramName(examRoutineMapWithProgramId.get(exp.getCourseId()));
      expelInfoList.add(expelledInformation);
    }
    return expelInfoList;
  }

  @NotNull
  public Integer getExamType(Integer pRegType) {
    return pRegType == CourseRegType.REGULAR.getId() ? ExamType.SEMESTER_FINAL.getId()
        : ExamType.CLEARANCE_CARRY_IMPROVEMENT.getId();
  }

  @Override
  protected ContentManager<ExpelledInformation, MutableExpelledInformation, Long> getContentManager() {
    return mManager;
  }

  @Override
  protected Builder<ExpelledInformation, MutableExpelledInformation> getBuilder() {
    return mBuilder;
  }

  @Override
  protected String getETag(ExpelledInformation pReadonly) {
    return null;
  }
}
