package org.ums.common.academic.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.ums.common.Resource;
import org.ums.common.academic.resource.helper.SeatPlanGroupResourceHelper;

/**
 * Created by My Pc on 4/21/2016.
 */
public class MutableSeatPlanGroupResource extends Resource {

  @Autowired
  SeatPlanGroupResourceHelper mResourceHelper;

}
