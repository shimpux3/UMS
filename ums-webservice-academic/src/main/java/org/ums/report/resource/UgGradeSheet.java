package org.ums.report.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ums.report.generator.UgGradeSheetGenerator;
import org.ums.enums.ExamType;
import org.ums.resource.Resource;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Ifti on 07-Jun-16.
 */

@Component
@Path("/gradeReport/pdf/")
@Produces({"application/pdf"})
public class UgGradeSheet extends Resource {
  @Autowired
  UgGradeSheetGenerator mUgGradeSheetGenerator;

  @GET
  @Path("/semester/{semester-id}/courseid/{course-id}/examtype/{exam-type}/role/{role}")
  public StreamingOutput get(final @Context Request pRequest, final @PathParam("semester-id") Integer pSemesterId,
      final @PathParam("course-id") String pCourseId, final @PathParam("exam-type") Integer pExamTypeId,
      final @PathParam("role") String pRequestedRole) {
    return new StreamingOutput() {
      public void write(OutputStream output) throws IOException, WebApplicationException {
        try {
          mUgGradeSheetGenerator.createPdf(pSemesterId, pCourseId, ExamType.get(pExamTypeId), pRequestedRole, output);
        } catch(Exception e) {
          throw new WebApplicationException(e);
        }
      }
    };
  }

}
