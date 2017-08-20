package org.ums.microservice.instance.paymentvalidator;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.ums.configuration.UMSConfiguration;
import org.ums.fee.payment.MutableStudentPayment;
import org.ums.fee.payment.StudentPayment;
import org.ums.fee.payment.StudentPaymentManager;
import org.ums.microservice.AbstractService;

public class PaymentValidatorJob extends AbstractService implements PaymentValidator {
  private static final Logger mLogger = LoggerFactory.getLogger(PaymentValidatorJob.class);
  private StudentPaymentManager mStudentPaymentManager;
  private SecurityManager mSecurityManager;
  private UMSConfiguration mUMSConfiguration;

  public PaymentValidatorJob(StudentPaymentManager pStudentPaymentManager, SecurityManager pSecurityManager,
      UMSConfiguration pUMSConfiguration) {
    mStudentPaymentManager = pStudentPaymentManager;
    mSecurityManager = pSecurityManager;
    mUMSConfiguration = pUMSConfiguration;
  }

  @Scheduled(fixedDelay = 120000, initialDelay = 0)
  @Transactional
  public void validatePayments() {
    if(login()) {
      List<StudentPayment> payments = mStudentPaymentManager.getToExpirePayments();
      if(mLogger.isDebugEnabled()) {
        mLogger.debug(String.format("Found total %d payments to expire", payments.size()));
      }
      List<MutableStudentPayment> mutablePayments =
          payments.stream().map(payment -> payment.edit()).collect(Collectors.toList());
      mutablePayments.forEach(payment -> payment.setStatus(StudentPayment.Status.EXPIRED));
      if(!mutablePayments.isEmpty()) {
        mStudentPaymentManager.update(mutablePayments);
      }
    }
  }

  @Override
  public void start() {
    // Do Nothing for now
  }

  @Override
  public SecurityManager getSecurityManager() {
    return mSecurityManager;
  }

  @Override
  public UMSConfiguration getUMSConfiguration() {
    return mUMSConfiguration;
  }
}
