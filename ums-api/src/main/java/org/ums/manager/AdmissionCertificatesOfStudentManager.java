package org.ums.manager;

import org.ums.domain.model.immutable.AdmissionCertificatesOfStudent;
import org.ums.domain.model.mutable.MutableAdmissionCertificatesOfStudent;

import java.util.List;

/**
 * Created by kawsu on 1/11/2017.
 */
public interface AdmissionCertificatesOfStudentManager extends
    ContentManager<AdmissionCertificatesOfStudent, MutableAdmissionCertificatesOfStudent, Integer> {

  int saveAdmissionStudentsCertificates(
      final List<MutableAdmissionCertificatesOfStudent> pCertificateHistorys);

  List<AdmissionCertificatesOfStudent> getStudentsSavedCertificateLists(final int pSemesterId,
      final String pReceiptId);
}
