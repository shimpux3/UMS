package org.ums.ems.profilemanagement.training;

import org.ums.domain.model.common.Editable;
import org.ums.domain.model.common.MutableIdentifier;
import org.ums.domain.model.mutable.MutableLastModifier;
import org.ums.enums.registrar.TrainingCategory;

import java.util.Date;

public interface MutableTrainingInformation extends TrainingInformation, Editable<Long>, MutableIdentifier<Long>,
    MutableLastModifier {

  void setEmployeeId(final String pEmployeeId);

  void setTrainingName(final String pTrainingName);

  void setTrainingInstitute(final String pTrainingInstitute);

  void setTrainingFromDate(final Date pTrainingFromDate);

  void setTrainingToDate(final Date pTrainingToDate);

  void setTrainingDuration(final int pTrainingDuration);

  void setTrainingDurationString(final String pTrainingDurationString);

  void setTrainingCategory(final TrainingCategory pTrainingCategory);

  void setTrainingCategoryId(final int pTrainingCategoryId);
}
