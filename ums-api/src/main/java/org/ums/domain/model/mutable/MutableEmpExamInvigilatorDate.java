package org.ums.domain.model.mutable;

import org.ums.domain.model.common.Editable;
import org.ums.domain.model.common.MutableIdentifier;
import org.ums.domain.model.immutable.EmpExamInvigilatorDate;

/**
 * Created by Monjur-E-Morshed on 7/27/2018.
 */
public interface MutableEmpExamInvigilatorDate extends EmpExamInvigilatorDate, Editable<Long>, MutableLastModifier,
    MutableIdentifier<Long> {
  void setId(final Long pId);

  void setExamDate(final String pExamDate);

  void setAttendantId(final Long pAttendantId);
}
