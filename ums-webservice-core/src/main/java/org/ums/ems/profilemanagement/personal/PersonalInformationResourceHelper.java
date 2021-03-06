package org.ums.ems.profilemanagement.personal;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ums.cache.LocalCache;
import org.ums.domain.model.immutable.Employee;
import org.ums.manager.EmployeeManager;
import org.ums.resource.ResourceHelper;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PersonalInformationResourceHelper extends
    ResourceHelper<PersonalInformation, MutablePersonalInformation, String> {

  @Autowired
  private PersonalInformationManager mManager;

  @Autowired
  private PersonalInformationBuilder mBuilder;

  @Autowired
  private EmployeeManager mEmployeeManager;

  public JsonObject get(final String pEmployeeId, final UriInfo pUriInfo) {
    LocalCache localCache = new LocalCache();
    if(mManager.exists(pEmployeeId)) {
      PersonalInformation personalInformation = mManager.get(pEmployeeId);
      return toJson(personalInformation, pUriInfo, localCache);
    }
    return null;
  }

  public JsonObject getSimilarUsers(final String pName, UriInfo pUriInfo) {
        LocalCache localCache = new LocalCache();
        Map<PersonalInformation, Double> similarUsers = new HashMap<>();
        List<PersonalInformation> personalInformationList = mManager.getAll();
        CharSequence target = pName.trim().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
        for (PersonalInformation personalInformation : personalInformationList) {
          CharSequence source = personalInformation.getFullName().trim().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
            JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();
            Double score = jaroWinklerDistance.apply(target, source);
            if (score > 0.9) {
                similarUsers.put(personalInformation, score);
            }
        }
        Map sortedSimilarUsersMap = similarUsers.entrySet().stream()
                .sorted(Map.Entry.<PersonalInformation, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return buildJson(sortedSimilarUsersMap, pUriInfo);
    }

  public Response update(JsonObject pJsonObject, UriInfo pUriInfo) {
    if(pJsonObject.getJsonObject("entries").containsKey("type")
        && pJsonObject.getJsonObject("entries").containsKey("employeeId")
        && !pJsonObject.getJsonObject("entries").getString("type").isEmpty()
        && !pJsonObject.getJsonObject("entries").getString("employeeId").isEmpty()
        && mManager.exists(pJsonObject.getJsonObject("entries").getString("employeeId"))) {

      LocalCache localeCache = new LocalCache();
      MutablePersonalInformation mutablePersonalInformation = new PersistentPersonalInformation();
      mBuilder.build(mutablePersonalInformation, pJsonObject.getJsonObject("entries"), localeCache);
      mManager.update(mutablePersonalInformation);
      localeCache.invalidate();
      Response.ResponseBuilder builder = Response.created(null);
      builder.status(Response.Status.CREATED);
      return builder.build();
    }
    Response.ResponseBuilder builder = Response.created(null);
    builder.status(Response.Status.INTERNAL_SERVER_ERROR);
    return builder.build();

  }

  /*
   * public Response create(JsonObject pJsonObject, UriInfo pUriInfo) { MutablePersonalInformation
   * personalInformation = new PersistentPersonalInformation(); LocalCache localeCache = new
   * LocalCache(); JsonArray entries = pJsonObject.getJsonArray("entries"); JsonObject jsonObject =
   * entries.getJsonObject(0).getJsonObject("personal"); mBuilder.build(personalInformation,
   * jsonObject, localeCache); mManager.create(personalInformation); localeCache.invalidate();
   * Response.ResponseBuilder builder = Response.created(null);
   * builder.status(Response.Status.CREATED); return builder.build(); }
   */

  @Override
  public Response post(final JsonObject pJsonObject, final UriInfo pUriInfo) throws Exception {
    throw new NotImplementedException();
  }

  /*
   * private JsonObject buildJson(PersonalInformation pPersonalInformation, UriInfo pUriInfo,
   * LocalCache localCache) { JsonObjectBuilder jsonObject = Json.createObjectBuilder();
   * JsonArrayBuilder children = Json.createArrayBuilder(); if(pPersonalInformation.getId() != null)
   * { children.add(toJson(pPersonalInformation, pUriInfo, localCache)); } jsonObject.add("entries",
   * children); localCache.invalidate(); return jsonObject.build(); }
   */

  private JsonObject buildJson(Map<PersonalInformation, Double> similarUsers, UriInfo pUriInfo) {
    JsonObjectBuilder object = Json.createObjectBuilder();
    JsonArrayBuilder children = Json.createArrayBuilder();
    LocalCache localCache = new LocalCache();
    for(Map.Entry<PersonalInformation, Double> entry : similarUsers.entrySet()) {
      JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
      mBuilder.build(jsonObjectBuilder, entry.getKey(), pUriInfo, localCache);
      jsonObjectBuilder.add("score", Math.round(entry.getValue() * 100));
      Employee employee = mEmployeeManager.get(entry.getKey().getId());
      jsonObjectBuilder.add("deptName", employee.getDepartment().getLongName());
      jsonObjectBuilder.add("designation", employee.getDesignation().getDesignationName());
      children.add(jsonObjectBuilder.build());
    }
    object.add("entries", children);
    localCache.invalidate();

    return object.build();
  }

  @Override
  protected PersonalInformationManager getContentManager() {
    return mManager;
  }

  @Override
  protected PersonalInformationBuilder getBuilder() {
    return mBuilder;
  }

  @Override
  protected String getETag(PersonalInformation pReadonly) {
    return pReadonly.getLastModified();
  }
}
