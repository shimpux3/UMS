package org.ums.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.ums.resource.helper.AdmissionStudentResourceHelper;
import org.ums.enums.DepartmentSelectionType;
import org.ums.enums.ProgramType;

import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

/**
 * Created by Monjur-E-Morshed on 17-Dec-16.
 */
public class MutableAdmissionStudentResource extends Resource {

  @Autowired
  AdmissionStudentResourceHelper mHelper;

  @POST
  @Path("/taletalkData/semester/{semester-id}/programType/{program-type}")
  public Response saveTaletalkData(@PathParam("semester-id") int pSemesterId,
      @PathParam("program-type") int pProgramType, final JsonObject pJsonObject) throws Exception {
    return mHelper.postTaletalkData(pJsonObject, pSemesterId, ProgramType.get(pProgramType), mUriInfo);
  }

  @PUT
  @Path("/meritListUpload/quota/{quota}")
  public Response saveMeritList(final JsonObject pJsonObject, @PathParam("quota") String pQuota) throws Exception {
    return mHelper.saveMeritListData(pJsonObject, pQuota, mUriInfo);
  }

  @PUT
  @Path("/migrationList")
  public Response saveMigrationList(final JsonObject pJsonObject) throws Exception {
    return mHelper.saveMigrationData(pJsonObject, mUriInfo);
  }

  @PUT
  @Path("/departmentSelectionStatus/{department-selection-status}")
  public JsonObject saveAndGetNextStudent(@PathParam("department-selection-status") int pDepartmentSelectionStatus,
      final JsonObject pJsonObject, final @Context Request pRequest) throws Exception {
    return mHelper.saveDepartmentSelectionInfoAndRetrieveNextStudent(pJsonObject,
        DepartmentSelectionType.get(pDepartmentSelectionStatus), mUriInfo);
  }

}
