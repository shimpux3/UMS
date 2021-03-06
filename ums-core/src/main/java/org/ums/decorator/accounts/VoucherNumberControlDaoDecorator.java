package org.ums.decorator.accounts;

import org.ums.decorator.ContentDaoDecorator;
import org.ums.domain.model.immutable.Company;
import org.ums.domain.model.immutable.accounts.Voucher;
import org.ums.domain.model.immutable.accounts.VoucherNumberControl;
import org.ums.domain.model.mutable.accounts.MutableVoucherNumberControl;
import org.ums.manager.accounts.VoucherNumberControlManager;

import java.util.List;

/**
 * Created by Monjur-E-Morshed on 08-Jan-18.
 */
public class VoucherNumberControlDaoDecorator extends
    ContentDaoDecorator<VoucherNumberControl, MutableVoucherNumberControl, Long, VoucherNumberControlManager> implements
    VoucherNumberControlManager {

  @Override
  public List<VoucherNumberControl> getByCurrentFinancialYear(Company pCompany) {
    return getManager().getByCurrentFinancialYear(pCompany);
  }

  @Override
  public int[] updateVoucherNumberControls(List<MutableVoucherNumberControl> voucherNumberControls) {
    return getManager().updateVoucherNumberControls(voucherNumberControls);
  }

  @Override
  public VoucherNumberControl getByVoucher(Voucher pVoucher, Company pCompany) {
    return getManager().getByVoucher(pVoucher, pCompany);
  }
}
