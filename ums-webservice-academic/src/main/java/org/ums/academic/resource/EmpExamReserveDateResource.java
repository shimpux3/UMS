package org.ums.academic.resource;

import org.springframework.stereotype.Component;
import org.ums.resource.Resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by Monjur-E-Morshed on 7/27/2018.
 */
@Component
@Path("academic/empExamInvigilatorDate")
@Produces(Resource.MIME_TYPE_JSON)
@Consumes(Resource.MIME_TYPE_JSON)
public class EmpExamReserveDateResource extends MutableEmpExamReserveDateResource {
}
