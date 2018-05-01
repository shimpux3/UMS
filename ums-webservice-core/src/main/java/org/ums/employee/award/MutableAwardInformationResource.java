package org.ums.employee.award;

import org.springframework.beans.factory.annotation.Autowired;
import org.ums.logs.PostLog;
import org.ums.resource.Resource;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

public class MutableAwardInformationResource extends Resource {

  @Autowired
  AwardInformationResourceHelper mAwardInformationResourceHelper;

  @POST
  @Path("/save")
  @PostLog(message = "Post employee information (Award data)")
  public Response saveServiceInformation(@Context HttpServletRequest pHttpServletRequest, final JsonObject pJsonObject) {
    return mAwardInformationResourceHelper.saveAwardInformation(pJsonObject, mUriInfo);
  }
}
