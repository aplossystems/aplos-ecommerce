package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.ecommerce.beans.InfoPackRequest;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class InfoPackEmail extends SourceGeneratedEmailTemplate<InfoPackRequest> {
	private static final long serialVersionUID = -939101865265005129L;

	public InfoPackEmail() {
	}
	
	public String getDefaultName() {
		return "Info pack";
	}

	@Override
	public String getDefaultSubject() {
		return "Info pack";
	}
	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "infoPack.html" );
	}
	
	@Override
	public List<FileDetails> getAttachments(InfoPackRequest bulkEmailSource) {
		List<FileDetails> fileDetailsList = super.getAttachments(bulkEmailSource);
		fileDetailsList.add( bulkEmailSource.getNewsEntry().getPdfDetails() );
		return fileDetailsList;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.INFO_PACK;
	}
	
	@Override
	public String postProcessSubject(InfoPackRequest bulkEmailRecipient, String subject) {
		return bulkEmailRecipient.getNewsEntry().getEmailSubject();
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			InfoPackRequest infoPackRequest) {
		super.addContentJDynamiTeValues(jDynamiTe, infoPackRequest);
		jDynamiTe.setVariable("FIRST_NAME", infoPackRequest.getFirstName() );
		jDynamiTe.setVariable("FULLNAME", infoPackRequest.getFullName() );
		jDynamiTe.setVariable("EMAIL_BODY", infoPackRequest.getNewsEntry().getEmailBody() );
		jDynamiTe.setVariable("PDF_NAME", infoPackRequest.getNewsEntry().getPdfTitle() );
	}
}
