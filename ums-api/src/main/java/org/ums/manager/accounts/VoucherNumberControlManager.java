package org.ums.manager.accounts;

import org.ums.domain.model.immutable.Company;
import org.ums.domain.model.immutable.accounts.Voucher;
import org.ums.domain.model.immutable.accounts.VoucherNumberControl;
import org.ums.domain.model.mutable.accounts.MutableVoucherNumberControl;
import org.ums.manager.ContentManager;

import java.util.List;

/**
 * Created by Monjur-E-Morshed on 08-Jan-18.
 */
public interface VoucherNumberControlManager extends
    ContentManager<VoucherNumberControl, MutableVoucherNumberControl, Long> {

  List<VoucherNumberControl> getByCurrentFinancialYear(Company pCompany);

  VoucherNumberControl getByVoucher(Voucher pVoucher, Company pCompany);

  int[] updateVoucherNumberControls(List<MutableVoucherNumberControl> voucherNumberControls);

}
