package org.ums.employee.training;

import org.springframework.beans.factory.annotation.Autowired;
import org.ums.logs.UmsLogMessage;
import org.ums.resource.Resource;

import javax.json.JsonObject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class MutableTrainingInformationResource extends Resource {

  @Autowired
  TrainingInformationResourceHelper mTrainingInformationResourceHelper;

  @POST
  @Path("/save")
  @UmsLogMessage(message = "Post employee information (training data)")
  public Response saveServiceInformation(final JsonObject pJsonObject) {
    return mTrainingInformationResourceHelper.saveTrainingInformation(pJsonObject, mUriInfo);
  }
}
