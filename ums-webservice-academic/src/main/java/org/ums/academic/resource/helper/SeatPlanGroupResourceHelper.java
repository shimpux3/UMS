package org.ums.academic.resource.helper;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ums.builder.Builder;
import org.ums.cache.LocalCache;
import org.ums.builder.SeatPlanGroupBuilder;
import org.ums.domain.model.immutable.SeatPlan;
import org.ums.domain.model.immutable.SeatPlanGroup;
import org.ums.domain.model.mutable.MutableSeatPlanGroup;
import org.ums.manager.CourseManager;
import org.ums.manager.ProgramManager;
import org.ums.manager.SeatPlanGroupManager;
import org.ums.manager.SemesterManager;
import org.ums.resource.ResourceHelper;
import org.ums.response.type.GenericResponse;
import org.ums.services.academic.SeatPlanService;
import org.ums.services.academic.SeatPlanSubGroupService;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Monjur-E-Morshed on 4/21/2016.
 */
@Component
public class SeatPlanGroupResourceHelper extends ResourceHelper<SeatPlanGroup, MutableSeatPlanGroup, Integer> {

  @Autowired
  SeatPlanGroupManager mSeatPlanGroupManager;

  @Autowired
  SemesterManager mSemesterManager;

  @Autowired
  ProgramManager mProgramManager;

  @Autowired
  CourseManager mCourseManager;

  @Autowired
  SeatPlanGroupBuilder mBuilder;

  @Autowired
  SeatPlanService mSeatPlanService;

  @Autowired
  SeatPlanSubGroupService mSeatPlanSubGroupService;

  public JsonObject getSeatPlanGroupBySemester(final int pSemesterId, int type, final int update,
      final Request pRequest, final UriInfo pUriInfo) {

    GenericResponse<Map> genericResponse = null, previousResponse = null;

    /*
     * First, we will check whethere there is value or not in the database. Because, if there is no
     * value and we search by a semesterId, then, our program will be shown error or exception.
     */
    int seatPlanGroupListForCheckingIfThereIsValueOrNot =
        mSeatPlanGroupManager.checkSeatPlanGroupDataSize(pSemesterId, type);

    /*
     * variable seatPlanGroupForSemester will be used to sent the group information based on the
     * semesterId
     */
    List<SeatPlanGroup> seatPlanGroupForSemester;
    List<SeatPlanGroup> seatPlanGroupForSemesterTmp = new ArrayList<>();

    JsonObjectBuilder object = Json.createObjectBuilder();
    JsonArrayBuilder children = Json.createArrayBuilder();
    LocalCache localCache = new LocalCache();
    Boolean seatPlanGroupExistForTheSemesterAndType = false;

    // if (seatPlanGroupListForCheckingIfThereIsValueOrNot > 0) {
    // seatPlanGroupListBySemesterAndType = mSeatPlanGroupManager.getGroupBySemester(pSemesterId,
    // type);
    if(seatPlanGroupListForCheckingIfThereIsValueOrNot > 0) {
      seatPlanGroupExistForTheSemesterAndType = true;
    }

    if(seatPlanGroupExistForTheSemesterAndType == true && update == 0) {

      seatPlanGroupForSemester = mSeatPlanGroupManager.getGroupBySemesterTypeFromDb(pSemesterId, type);
      seatPlanGroupForSemesterTmp = seatPlanGroupForSemester;
      mSeatPlanGroupManager.deleteBySemesterAndExamType(pSemesterId, type);
    }

    mSeatPlanGroupManager.createSeatPlanGroup(pSemesterId, type);

    seatPlanGroupForSemester = mSeatPlanGroupManager.getGroupBySemesterTypeFromDb(pSemesterId, type);
    for(SeatPlanGroup seatPlanGroup : seatPlanGroupForSemester) {
      children.add(toJson(seatPlanGroup, pUriInfo, localCache));
    }
    if(seatPlanGroupForSemesterTmp.size() > 0)
      mSeatPlanSubGroupService.updateSeatPlanSubGroup(seatPlanGroupForSemesterTmp, seatPlanGroupForSemester,
          pSemesterId, type);

    object.add("entries", children);
    localCache.invalidate();
    return object.build();
  }

  @Override
  public Response post(JsonObject pJsonObject, UriInfo pUriInfo) {
    throw new NotImplementedException("Post method not implemented for SeatPlanGroupResourceHelper");
  }

  @Override
  protected SeatPlanGroupManager getContentManager() {
    return mSeatPlanGroupManager;
  }

  @Override
  protected Builder<SeatPlanGroup, MutableSeatPlanGroup> getBuilder() {
    return mBuilder;
  }

  @Override
  protected String getETag(SeatPlanGroup pReadonly) {
    return pReadonly.getLastUpdateDate();
  }
}
