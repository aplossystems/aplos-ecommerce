package com.aplos.ecommerce.templates.printtemplates;

import java.util.List;
import java.util.Map;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.AplosUrl;
import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;


public class VoidStickersTemplate extends PrintTemplate {
	private static final long serialVersionUID = -431143167074937047L;
	private int firstSerialNumberId;
	private int lastSerialNumberId;
	
	@Override
	public String getName() {
		return "Void stickers";
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return EcommerceWorkingDirectory.SERIAL_NUMBERS_PDFS_DIR;
	}
	
	@Override
	public void initialise(Map<String, String[]> params) {
		setFirstSerialNumberId( Integer.valueOf( params.get( "firstSerialNumber" )[ 0 ] ) );
		setLastSerialNumberId( Integer.valueOf( params.get( "lastSerialNumber" )[ 0 ] ) );
	}

	public static String getTemplateUrl( Long firstSerialNumberId, Long lastSerialNumberId ) {
		AplosUrl aplosUrl = getBaseTemplateUrl( VoidStickersTemplate.class );
		aplosUrl.addQueryParameter("firstSerialNumber", firstSerialNumberId);
		aplosUrl.addQueryParameter("lastSerialNumber", lastSerialNumberId);
		aplosUrl.addQueryParameter(AplosAppConstants.CREATE_PDF, "true");
		return aplosUrl.toString();
	}

	public String getTemplateContent() {
		try {
			JDynamiTe dynamiTe;

			if ((dynamiTe = CommonUtil.loadContentInfoJDynamiTe( "voidStickers.html", PrintTemplate.printTemplatePath, ApplicationUtil.getAplosContextListener() )) != null) {
				BeanDao aqlBeanDao = new BeanDao(SerialNumber.class);
				aqlBeanDao.addWhereCriteria("bean.id >= " + getFirstSerialNumberId());
				aqlBeanDao.addWhereCriteria("bean.id <= " + getLastSerialNumberId());
				aqlBeanDao.setOrderBy("bean.id asc");
				List<SerialNumber> serialNumberAssignments = aqlBeanDao.getAll();

//				for (int i=0, n=serialNumberAssignments.size(); i<n ; i+=5) {
//					dynamiTe.setVariable("SERIAL_NUMBER_TABLE_NUMBER1", serialNumberAssignments.get(i).getSerialNumber());
//					if (i+1 < n) {
//						dynamiTe.setVariable("SERIAL_NUMBER_TABLE_NUMBER2", serialNumberAssignments.get(i+1).getSerialNumber());
//					}
//					if (i+2 < n) {
//						dynamiTe.setVariable("SERIAL_NUMBER_TABLE_NUMBER3", serialNumberAssignments.get(i+2).getSerialNumber());
//					}
//					if (i+3 < n) {
//						dynamiTe.setVariable("SERIAL_NUMBER_TABLE_NUMBER4", serialNumberAssignments.get(i+3).getSerialNumber());
//					}
//					if (i+5 < n) {
//						dynamiTe.setVariable("SERIAL_NUMBER_TABLE_NUMBER5", serialNumberAssignments.get(i+5).getSerialNumber());
//					}
//					dynamiTe.parseDynElem( "serialNumberAssignmentListTable" );
//				}

				StringBuffer tableContent = new StringBuffer();

				boolean isLastRow = false;
				for (int i=0, n=serialNumberAssignments.size(); i<n ; i++) {
					if (i % 5 == 0) {
						tableContent.append("<tr>");
						if( (i/5) == 12 ) {
							isLastRow = true;
						}
					}
					tableContent.append("<td style='" );
					if( !isLastRow ) {
						tableContent.append("padding-bottom:8.9pt");
					}
					tableContent.append(";'>");
					tableContent.append("<div class='innerDiv'>");
					tableContent.append("<p style='font-size:10pt;padding-right:4px;'>Serial #</p>");
					tableContent.append("<p style='font-size:10pt;padding-right:3px;font-weight:bold'>TSN" + serialNumberAssignments.get(i).getId() + "</p>");
					tableContent.append("<p style='font-size:7pt;padding-right:9px'>Opening this unit voids warranty</p>");
					tableContent.append("<img src='http://www.teletest.tv/images/voidStickerLogo.jpg' style='padding-right:2px;padding-top:2px' />");
					tableContent.append("</div>");
					tableContent.append("</td>");
					if ((i+1) % 5 == 0 ) {
						tableContent.append("</tr>");
					}
				}

				//style='width:96pt;min-width:96pt;max-width:96pt'

				dynamiTe.setVariable("TABLE_CONTENT", tableContent.toString());

			}
			dynamiTe.parse();

			return dynamiTe.toString();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

	public int getFirstSerialNumberId() {
		return firstSerialNumberId;
	}

	public void setFirstSerialNumberId(int firstSerialNumberId) {
		this.firstSerialNumberId = firstSerialNumberId;
	}

	public int getLastSerialNumberId() {
		return lastSerialNumberId;
	}

	public void setLastSerialNumberId(int lastSerialNumberId) {
		this.lastSerialNumberId = lastSerialNumberId;
	}
}
