package com.aplos.ecommerce.scheduledjobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.ScheduledJob;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.module.EcommerceConfiguration;


@Entity
public class AbandonedOrderEmailerJob extends ScheduledJob<Boolean> {
	
	@Override
	public Boolean executeCall() throws Exception {
		BeanDao abandonedTransactionDao = new BeanDao(Transaction.class);
		int timeout = EcommerceConfiguration.getEcommerceSettingsStatic().getAbandonedOrderAlertTimeout();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -1 * timeout);
		Date startTime = cal.getTime();
		abandonedTransactionDao.setWhereCriteria("bean.transactionStatus=" + TransactionStatus.INCOMPLETE.ordinal() + " AND bean.abandonedEmailSent = 0 AND bean.dateLastModified <= '" + FormatUtil.formatDateTimeForDB(startTime, false) + "'");
		List<Transaction> abandonedTransactions = abandonedTransactionDao.setIsReturningActiveBeans(true).getAll();
		for (Transaction abandonedTransaction : abandonedTransactions) {

			abandonedTransaction = abandonedTransaction.getSaveableBean();
			JDynamiTe subjectDynamiTe = new JDynamiTe();
			try {
				subjectDynamiTe.setInput(new ByteArrayInputStream( "Order Abandoned - {ABANDONMENT_DATETIME} - {CUSTOMER_NAME} £{TRANSACTION_VALUE}".getBytes() ));
				subjectDynamiTe.setVariable("TRANSACTION_VALUE", abandonedTransaction.getGrandTotal(false).toPlainString());
				subjectDynamiTe.setVariable("ABANDONMENT_DATETIME", FormatUtil.formatDateTime(abandonedTransaction.getDateLastModified(), true));
				if (abandonedTransaction.getCustomer() != null) {
					subjectDynamiTe.setVariable("CUSTOMER_NAME", CommonUtil.getStringOrEmpty(abandonedTransaction.getCustomer().getFullName()));
				} else {
					subjectDynamiTe.setVariable("CUSTOMER_NAME", "No customer");
				}
				subjectDynamiTe.parse();
				String parsedSubject = subjectDynamiTe.toString();
				// For some reason the parser adds a newline character to the end
				if ( parsedSubject.endsWith( "\n" ) ) {
					parsedSubject = parsedSubject.substring( 0, parsedSubject.length() - 1 );
				}
				
				JDynamiTe mainTextDynamiTe = new JDynamiTe();
				URL url = JSFUtil.checkFileLocations("abandonedTransactionEmailBody.html", "resources/templates/emailtemplates/", true );
				String templateContent = new String(CommonUtil.readEntireFile( url.openStream() ));
				mainTextDynamiTe.setInput(new ByteArrayInputStream( XmlEntityUtil.replaceEntitiesWith( templateContent, XmlEntityUtil.EncodingType.UNICODE ).getBytes() ));
				mainTextDynamiTe.setVariable("ADMIN_NAME", CommonUtil.getStringOrEmpty( CommonUtil.getAdminUser().getFirstName() ));
				mainTextDynamiTe.setVariable("TRANSACTION_VALUE", abandonedTransaction.getGrandTotal(false).toPlainString());
				mainTextDynamiTe.setVariable("ABANDONMENT_DATETIME", FormatUtil.formatDateTime(abandonedTransaction.getDateLastModified(), true));
				if (abandonedTransaction.getCustomer() != null) {
					mainTextDynamiTe.setVariable("CUSTOMER_NAME", CommonUtil.getStringOrEmpty(abandonedTransaction.getCustomer().getFullName()));
				} else {
					mainTextDynamiTe.setVariable("CUSTOMER_NAME", "No customer");
				}
				mainTextDynamiTe.setVariable("TRANSACTION_ID", abandonedTransaction.getId().toString());
				mainTextDynamiTe.parse();
	
				AplosEmail aplosEmail = new AplosEmail(parsedSubject, mainTextDynamiTe.toString());
				aplosEmail.addToAddress(CommonUtil.getAdminUser().getEmail());
				aplosEmail.setFromAddress(CommonUtil.getAdminUser().getEmail());
				aplosEmail.sendAplosEmailToQueue();
				abandonedTransaction.setAbandonedEmailSent( true );
				abandonedTransaction.saveDetails();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return true;
	}

	@Override
	public Calendar getFirstExecutionTime() { // first execution
		return null;
	}

	@Override
	public Long getIntervalQuantity(Date previousExecutionDate) {
		return 15L; // 15 Minutes
	}

	@Override
	public Integer getIntervalUnit() {
		return Calendar.MINUTE;
	}
	
}
