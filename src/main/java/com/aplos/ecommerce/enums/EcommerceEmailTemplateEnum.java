package com.aplos.ecommerce.enums;

import com.aplos.common.beans.communication.EmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.ecommerce.templates.emailtemplates.AbandonedTransactionEmail;
import com.aplos.ecommerce.templates.emailtemplates.AcknowledgementEmail;
import com.aplos.ecommerce.templates.emailtemplates.AwaitingAuthorisationEmail;
import com.aplos.ecommerce.templates.emailtemplates.CalibrationCertificateEmail;
import com.aplos.ecommerce.templates.emailtemplates.CancellationNoticeEmail;
import com.aplos.ecommerce.templates.emailtemplates.ConditionReceivedReportEmail;
import com.aplos.ecommerce.templates.emailtemplates.ConfirmPaymentEmail;
import com.aplos.ecommerce.templates.emailtemplates.CourierDetailsEmail;
import com.aplos.ecommerce.templates.emailtemplates.CustomerEmailConfirmationEmail;
import com.aplos.ecommerce.templates.emailtemplates.CustomerSignupEmail;
import com.aplos.ecommerce.templates.emailtemplates.DispatchInvoiceEmail;
import com.aplos.ecommerce.templates.emailtemplates.DispatchNoteEmail;
import com.aplos.ecommerce.templates.emailtemplates.DispatchNotificationEmail;
import com.aplos.ecommerce.templates.emailtemplates.EcommerceInvoiceEmail;
import com.aplos.ecommerce.templates.emailtemplates.EmailAFriendEmail;
import com.aplos.ecommerce.templates.emailtemplates.FirstHookEmail;
import com.aplos.ecommerce.templates.emailtemplates.ForgottenPasswordEmail;
import com.aplos.ecommerce.templates.emailtemplates.FriendReferralAdminEmail;
import com.aplos.ecommerce.templates.emailtemplates.FriendReferralEmail;
import com.aplos.ecommerce.templates.emailtemplates.GiftVoucherIssuedEmail;
import com.aplos.ecommerce.templates.emailtemplates.InfoPackAdminEmail;
import com.aplos.ecommerce.templates.emailtemplates.InfoPackEmail;
import com.aplos.ecommerce.templates.emailtemplates.LoanEmail;
import com.aplos.ecommerce.templates.emailtemplates.PaymentFailedEmail;
import com.aplos.ecommerce.templates.emailtemplates.PdfConfirmPaymentEmail;
import com.aplos.ecommerce.templates.emailtemplates.ProFormaEmail;
import com.aplos.ecommerce.templates.emailtemplates.QuoteEmail;
import com.aplos.ecommerce.templates.emailtemplates.ReferralPayoutIssuedEmail;
import com.aplos.ecommerce.templates.emailtemplates.RepairReportEmail;
import com.aplos.ecommerce.templates.emailtemplates.ResetPasswordEmail;
import com.aplos.ecommerce.templates.emailtemplates.ReturnsAuthorisationNoteEmail;
import com.aplos.ecommerce.templates.emailtemplates.SecondHookEmail;
import com.aplos.ecommerce.templates.emailtemplates.ThirdHookEmail;

public enum EcommerceEmailTemplateEnum implements EmailTemplateEnum {
	CONFIRM_PAYMENT ( ConfirmPaymentEmail.class ),
	GIFT_VOUCHER_ISSUED ( GiftVoucherIssuedEmail.class ),
	REFERRAL_PAYOUT_ISSUED ( ReferralPayoutIssuedEmail.class ),
	PDF_CONFIRM_PAYMENT ( PdfConfirmPaymentEmail.class ),
	CUSTOMER_SIGNUP ( CustomerSignupEmail.class ),
	DISPATCH_NOTIFICATION ( DispatchNotificationEmail.class ),
	COURIER_DETAILS ( CourierDetailsEmail.class ),
	EMAIL_A_FRIEND ( EmailAFriendEmail.class ),
	FORGOTTEN_PASSWORD ( ForgottenPasswordEmail.class ),
	PAYMENT_FAILED ( PaymentFailedEmail.class ),
	RESET_PASSWORD ( ResetPasswordEmail.class ),
	AWAITING_AUTHORISATION ( AwaitingAuthorisationEmail.class ),
	CUSTOMER_EMAIL_CONFIRMATION ( CustomerEmailConfirmationEmail.class ),
	FRIEND_REFERRAL ( FriendReferralEmail.class ),
	FIRST_HOOK ( FirstHookEmail.class ),
	SECOND_HOOK ( SecondHookEmail.class ),
	THIRD_HOOK ( ThirdHookEmail.class ),
	ACKNOWLEDGEMENT ( AcknowledgementEmail.class ),
	CALIBRATION ( CalibrationCertificateEmail.class ),
	CONDITION_RECEIVED ( ConditionReceivedReportEmail.class ),
	DISPATCH_INVOICE ( DispatchInvoiceEmail.class ),
	INVOICE ( EcommerceInvoiceEmail.class ),
	LOAN ( LoanEmail.class ),
	PRO_FORMA ( ProFormaEmail.class ),
	QUOTE ( QuoteEmail.class ),
	REPAIR_REPORT ( RepairReportEmail.class ),
	RETURNS_AUTH ( ReturnsAuthorisationNoteEmail.class ),
	FRIEND_REFER_ADMIN ( FriendReferralAdminEmail.class ),
	CANCELLATION_NOTICE ( CancellationNoticeEmail.class ),
	ABANDONED_TRANSACTION ( AbandonedTransactionEmail.class ),
	DISPATCH_NOTE ( DispatchNoteEmail.class ),
	INFO_PACK ( InfoPackEmail.class ),
	INFO_PACK_ADMIN ( InfoPackAdminEmail.class );

	private Class<? extends EmailTemplate> emailTemplateClass;
	boolean isActive = true;

	private EcommerceEmailTemplateEnum( Class<? extends EmailTemplate> emailTemplateClass ) {
		this.emailTemplateClass = emailTemplateClass;
	}

	@Override
	public Class<? extends EmailTemplate> getEmailTemplateClass() {
		return emailTemplateClass;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
