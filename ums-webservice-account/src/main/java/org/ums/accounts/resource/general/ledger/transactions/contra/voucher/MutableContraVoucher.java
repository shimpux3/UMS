package org.ums.accounts.resource.general.ledger.transactions.contra.voucher;

import org.springframework.beans.factory.annotation.Autowired;
import org.ums.domain.model.immutable.accounts.AccountTransaction;
import org.ums.manager.accounts.AccountTransactionManager;

import javax.json.JsonArray;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Monjur-E-Morshed on 22-Feb-18.
 */
public class MutableContraVoucher {
  @Autowired
  protected ContraVoucherResourceHelper mHelper;
  @Autowired
  protected AccountTransactionManager mAccountTransactionManager;

  @POST
  @Path("/save")
  public List<AccountTransaction> save(JsonArray pJsonArray) throws Exception {
    return mHelper.save(pJsonArray);
  }

  @PUT
  @Path("/delete")
  public Response delete(final String pTransactionId) {
    AccountTransaction pMutableAccountTransaction = mAccountTransactionManager.get(Long.parseLong(pTransactionId));
    return mHelper.delete(pMutableAccountTransaction);
  }

  @POST
  @Path("/post")
  public List<AccountTransaction> post(JsonArray pJsonArray) throws Exception {
    return mHelper.postTransactions(pJsonArray);
  }
}
