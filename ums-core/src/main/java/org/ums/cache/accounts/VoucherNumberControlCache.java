package org.ums.cache.accounts;

import org.ums.cache.ContentCache;
import org.ums.domain.model.immutable.Company;
import org.ums.domain.model.immutable.accounts.Voucher;
import org.ums.domain.model.immutable.accounts.VoucherNumberControl;
import org.ums.domain.model.mutable.accounts.MutableVoucherNumberControl;
import org.ums.manager.CacheManager;
import org.ums.manager.accounts.VoucherNumberControlManager;

import java.util.List;

/**
 * Created by Monjur-E-Morshed on 08-Jan-18.
 */
public class VoucherNumberControlCache extends
    ContentCache<VoucherNumberControl, MutableVoucherNumberControl, Long, VoucherNumberControlManager> implements
    VoucherNumberControlManager {

  private CacheManager<VoucherNumberControl, Long> mVoucherNumberControlLongCacheManager;

  public VoucherNumberControlCache(CacheManager<VoucherNumberControl, Long> pVoucherNumberControlLongCacheManager) {
    mVoucherNumberControlLongCacheManager = pVoucherNumberControlLongCacheManager;
  }

  @Override
  protected CacheManager<VoucherNumberControl, Long> getCacheManager() {
    return mVoucherNumberControlLongCacheManager;
  }

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
