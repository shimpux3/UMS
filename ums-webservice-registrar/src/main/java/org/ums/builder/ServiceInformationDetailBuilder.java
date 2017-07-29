package org.ums.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ums.cache.LocalCache;
import org.ums.domain.model.immutable.registrar.ServiceInformationDetail;
import org.ums.domain.model.mutable.registrar.MutableServiceInformation;
import org.ums.domain.model.mutable.registrar.MutableServiceInformationDetail;
import org.ums.enums.common.EmploymentPeriod;
import org.ums.formatter.DateFormat;
import org.ums.manager.registrar.ServiceInformationDetailManager;
import org.ums.manager.registrar.ServiceInformationManager;

import javax.json.*;
import javax.print.attribute.standard.JobSheets;
import javax.validation.constraints.Null;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceInformationDetailBuilder implements
    Builder<ServiceInformationDetail, MutableServiceInformationDetail> {

  @Autowired
  DateFormat mDateFormat;

  @Autowired
  ServiceInformationManager mServiceInformationManager;

  private EmploymentPeriod mEmploymentPeriod;

  @Override
  public void build(JsonObjectBuilder pBuilder, ServiceInformationDetail pReadOnly, UriInfo pUriInfo,
      LocalCache pLocalCache) {
    pBuilder.add("id", pReadOnly.getId());
    pBuilder.add("intervalId", pReadOnly.getEmploymentPeriodId());
    pBuilder.add("startDate", mDateFormat.format(pReadOnly.getStartDate()));
    pBuilder.add("endDate", pReadOnly.getEndDate() == null ? "" : mDateFormat.format(pReadOnly.getEndDate()));
    pBuilder.add("serviceId", pReadOnly.getServiceId().toString());
    pBuilder.add("dbAction", "");
  }

  @Override
  public void build(MutableServiceInformationDetail pMutable, JsonObject pJsonObject, LocalCache pLocalCache) {
    pMutable.setEmploymentPeriod(mEmploymentPeriod.get(pJsonObject.getJsonObject("interval").getInt("id")));
    pMutable.setStartDate(mDateFormat.parse(pJsonObject.getString("startDate")));
    pMutable.setEndDate(pJsonObject.containsKey("endDate") ? pJsonObject.getString("endDate").isEmpty() ? null
        : mDateFormat.parse(pJsonObject.getString("endDate")) : null);
    pMutable.setServiceId(Long.parseLong(pJsonObject.getString("serviceId")));
  }

  public void serviceInformationDetailBuilder(MutableServiceInformationDetail pMutable, JsonObject pJsonObject,
      LocalCache pLocalCache, Long pServiceId) {
    if(pJsonObject.getString("dbAction").equals("Update") || pJsonObject.getString("dbAction").equals("Delete")) {
      pMutable.setId(pJsonObject.getInt("id"));
    }
    pMutable.setEmploymentPeriod(mEmploymentPeriod.get(pJsonObject.getJsonObject("interval").getInt("id")));
    pMutable.setStartDate(mDateFormat.parse(pJsonObject.getString("startDate")));
    pMutable.setEndDate(pJsonObject.containsKey("endDate") ? pJsonObject.getString("endDate").equals("") ? null
        : mDateFormat.parse(pJsonObject.getString("endDate")) : null);
    pMutable.setServiceId(pServiceId);
  }
}
