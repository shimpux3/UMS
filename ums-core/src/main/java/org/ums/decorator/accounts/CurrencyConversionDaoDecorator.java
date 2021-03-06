package org.ums.decorator.accounts;

import org.ums.decorator.ContentDaoDecorator;
import org.ums.domain.model.immutable.Company;
import org.ums.domain.model.immutable.accounts.CurrencyConversion;
import org.ums.domain.model.mutable.accounts.MutableCurrencyConversion;
import org.ums.manager.accounts.CurrencyConversionManager;

import java.util.List;

/**
 * Created by Monjur-E-Morshed on 29-Jan-18.
 */
public class CurrencyConversionDaoDecorator extends
    ContentDaoDecorator<CurrencyConversion, MutableCurrencyConversion, Long, CurrencyConversionManager> implements
    CurrencyConversionManager {
  @Override
  public List<CurrencyConversion> getAll(Company pCompany) {
    return getManager().getAll(pCompany);
  }
}
