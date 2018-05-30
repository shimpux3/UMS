package org.ums.academic.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.ums.academic.resource.helper.ExpelledInformationResourceHelper;
import org.ums.resource.Resource;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

/**
 * Created by Monjur-E-Morshed on 5/27/2018.
 */
public class MutableExpelledInformationResource extends Resource {

  @Autowired
  ExpelledInformationResourceHelper mHelper;

  @POST
  @Path("/addRecords")
  @Produces({MediaType.APPLICATION_JSON})
  public Response createApplicationCCI(final JsonObject pJsonObject) throws Exception {
    return mHelper.post(pJsonObject, mUriInfo);
  }
}
