package com.aplos.ecommerce.templates.printtemplates;

import java.util.Map;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.beans.Mailout;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;

public class MailoutTemplate extends PrintTemplate {
	private static final long serialVersionUID = 8608482034989552939L;
	private Mailout mailout;
	
	@Override
	public void initialise(Map<String, String[]> params) {

		Long mailoutId = Long.valueOf( params.get( "mailoutId" )[ 0 ] );
		setMailout( (Mailout) new BeanDao( Mailout.class ).get( mailoutId ) );
	}
	
	@Override
	public String getName() {
		return "Mailout";
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return EcommerceWorkingDirectory.REPORTING_PDFS_DIR;
	}

	public String getTemplateContent() {
		try {
			JDynamiTe dynamiTe = new JDynamiTe();
			StringBuffer mailoutContent = new StringBuffer();
			mailoutContent.append( getMailout().getMailoutTemplate().getMailoutHeader() );
			mailoutContent.append( getMailout().getContent() );
			mailoutContent.append( getMailout().getMailoutTemplate().getMailoutFooter() );
			dynamiTe.setInput( mailoutContent.toString() );
			dynamiTe.setVariable("CUSTOMER_NAME", "Frank" );

			dynamiTe.parse();

			try {
				return dynamiTe.toString();
			} catch (Exception e) {
				ApplicationUtil.getAplosContextListener().handleError(e);
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

	public Mailout getMailout() {
		return mailout;
	}

	public void setMailout(Mailout mailout) {
		this.mailout = mailout;
	}
}
