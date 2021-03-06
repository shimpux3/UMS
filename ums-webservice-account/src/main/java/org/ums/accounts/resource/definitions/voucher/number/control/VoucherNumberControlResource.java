package org.ums.accounts.resource.definitions.voucher.number.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.ums.domain.model.immutable.Semester;
import org.ums.domain.model.immutable.accounts.VoucherNumberControl;
import org.ums.domain.model.mutable.accounts.MutableVoucherNumberControl;
import org.ums.manager.SemesterManager;
import org.ums.persistent.model.accounts.PersistentVoucherNumberControl;
import org.ums.resource.Resource;
import org.ums.util.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monjur-E-Morshed on 08-Jan-18.
 */

@Component
@Path("account/definition/voucher-number-control")
@Produces(Resource.MIME_TYPE_JSON)
@Consumes(Resource.MIME_TYPE_JSON)
public class VoucherNumberControlResource extends MutableVoucherNumberControlResource {

  @Autowired
  SemesterManager semesterManager;

  @GET
  @Path("/based-on-curr-fin-year")
  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String getVoucherNumberControlBasedOnCurrentFinancialYear(final @Context HttpServletRequest pRequest)
      throws Exception {
    System.out.println(pRequest.getRequestURL());
    List<VoucherNumberControl> numberControls =
        mHelper.getContentManager().getByCurrentFinancialYear(Utils.getCompany());
    List<Semester> semesters = semesterManager.getAll();
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(numberControls);
  }

  @POST
  @Path("/save")
  public List<VoucherNumberControl> saveAndReturn(List<PersistentVoucherNumberControl> pVoucherNumberControls,
      final @Context Request pRequest) {
    List<MutableVoucherNumberControl> voucherNumberControls = new ArrayList<>(pVoucherNumberControls);
    return mHelper.createOrUpdate(voucherNumberControls);
  }

}
