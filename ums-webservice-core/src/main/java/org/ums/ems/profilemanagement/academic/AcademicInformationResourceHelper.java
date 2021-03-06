package org.ums.ems.profilemanagement.academic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ums.builder.Builder;
import org.ums.cache.LocalCache;
import org.ums.manager.ContentManager;
import org.ums.resource.ResourceHelper;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Component
public class AcademicInformationResourceHelper extends
    ResourceHelper<AcademicInformation, MutableAcademicInformation, Long> {

  @Autowired
  private AcademicInformationManager mManager;

  @Autowired
  private AcademicInformationBuilder mBuilder;

  @Override
  public Response post(JsonObject pJsonObject, final UriInfo pUriInfo) {
    LocalCache localCache = new LocalCache();
    MutableAcademicInformation mutableAcademicInformation = new PersistentAcademicInformation();
    mBuilder.build(mutableAcademicInformation, pJsonObject.getJsonObject("entries"), localCache);
    Long id = mManager.create(mutableAcademicInformation);
    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
    mBuilder.build(objectBuilder, mManager.get(id), pUriInfo, localCache);
    localCache.invalidate();
    return Response.ok(objectBuilder.build()).build();
  }

  public JsonObject get(final String pEmployeeId, final UriInfo pUriInfo) {
    if(mManager.exists(pEmployeeId)) {
      List<AcademicInformation> academicInformationList = mManager.get(pEmployeeId);
      return buildJsonResponse(academicInformationList, pUriInfo);
    }
    return null;
  }

  public Response update(JsonObject pJsonObject, final UriInfo pUriInfo) {
    LocalCache localCache = new LocalCache();
    MutableAcademicInformation mutableAcademicInformation = new PersistentAcademicInformation();
    mBuilder.build(mutableAcademicInformation, pJsonObject.getJsonObject("entries"), localCache);
    mManager.update(mutableAcademicInformation);
    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
    mBuilder.build(objectBuilder, mManager.get(mutableAcademicInformation.getId()), pUriInfo, localCache);
    localCache.invalidate();
    return Response.ok(objectBuilder.build()).build();
  }

  public Response delete(Long id, UriInfo pUriInfo) {
    LocalCache localCache = new LocalCache();
    MutableAcademicInformation mutableAcademicInformation = (MutableAcademicInformation) mManager.get(id);
    mManager.delete(mutableAcademicInformation);
    localCache.invalidate();
    return Response.noContent().build();
  }

  @Override
  protected ContentManager<AcademicInformation, MutableAcademicInformation, Long> getContentManager() {
    return mManager;
  }

  @Override
  protected Builder<AcademicInformation, MutableAcademicInformation> getBuilder() {
    return mBuilder;
  }

  @Override
  protected String getETag(AcademicInformation pReadonly) {
    return pReadonly.getLastModified();
  }
}
