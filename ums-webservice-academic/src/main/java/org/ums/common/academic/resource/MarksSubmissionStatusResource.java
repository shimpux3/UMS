package org.ums.common.academic.resource;

import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ums.common.academic.resource.helper.MarksSubmissionStatusResourceHelper;
import org.ums.domain.model.immutable.TaskStatus;
import org.ums.resource.Resource;
import org.ums.response.type.GenericResponse;
import org.ums.response.type.TaskStatusResponse;
import org.ums.services.academic.ProcessResult;

@Component
@Path("/academic/marksSubmissionStatus")
@Produces(Resource.MIME_TYPE_JSON)
@Consumes(Resource.MIME_TYPE_JSON)
public class MarksSubmissionStatusResource extends Resource {
  @Autowired
  MarksSubmissionStatusResourceHelper mResourceHelper;

  @GET
  @Path("/programType/{programType-id}/semester/{semester-id}/")
  public JsonObject getBySemesterProgramType(final @Context Request pRequest,
      final @PathParam("programType-id") Integer pProgramTypeId,
      final @PathParam("semester-id") Integer pSemesterId) throws Exception {
    return mResourceHelper.getMarksSubmissionStatus(pProgramTypeId, pSemesterId, mUriInfo);
  }

  @GET
  @Path("/program/{program-id}/semester/{semester-id}/")
  public JsonObject getBySemesterProgram(final @Context Request pRequest,
      final @PathParam("program-id") Integer pProgramId,
      final @PathParam("semester-id") Integer pSemesterId) throws Exception {
    return mResourceHelper.getMarksSubmissionStatusByProgram(pProgramId, pSemesterId, mUriInfo);
  }
}
