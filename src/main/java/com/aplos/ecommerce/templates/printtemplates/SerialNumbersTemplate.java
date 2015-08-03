package com.aplos.ecommerce.templates.printtemplates;

import java.util.List;
import java.util.Map;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;

public class SerialNumbersTemplate extends PrintTemplate {
	private static final long serialVersionUID = 2466169540894380686L;
	private int firstSerialNumberId;
	private int lastSerialNumberId;
	
	@Override
	public void initialise(Map<String, String[]> params) {
		setFirstSerialNumberId( Integer.valueOf( params.get( "firstSerialNumber" )[ 0 ] ) );
		setLastSerialNumberId( Integer.valueOf( params.get( "lastSerialNumber" )[ 0 ] ) );
	}
	
	@Override
	public String getName() {
		return "Serial numbers";
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return EcommerceWorkingDirectory.SERIAL_NUMBERS_PDFS_DIR;
	}

	public String getTemplateContent() {
		try {
			JDynamiTe dynamiTe;

			if ((dynamiTe = CommonUtil.loadContentInfoJDynamiTe( "newSerialNumbers.html", PrintTemplate.printTemplatePath, ApplicationUtil.getAplosContextListener() )) != null) {
				BeanDao aqlBeanDao = new BeanDao(SerialNumber.class);
				aqlBeanDao.addWhereCriteria("bean.id >= " + getFirstSerialNumberId());
				aqlBeanDao.addWhereCriteria("bean.id <= " + getLastSerialNumberId());
				aqlBeanDao.setOrderBy("bean.number asc");
				List<SerialNumber> serialNumberAssignments = aqlBeanDao.getAll();

				int firstTableMax = serialNumberAssignments.size()/3;
				int secondTableMax = (serialNumberAssignments.size()/3) *2;
				int thirdTableMax = serialNumberAssignments.size();

				for (int i=0 ; i<firstTableMax ; i+=3) {
					dynamiTe.setVariable("SERIAL_NUMBER_TABLE1_NUMBER1", String.valueOf( serialNumberAssignments.get(i).getId()));
					dynamiTe.setVariable("SERIAL_NUMBER_TABLE1_NUMBER2", String.valueOf( serialNumberAssignments.get(i+1).getId()));
					dynamiTe.setVariable("SERIAL_NUMBER_TABLE1_NUMBER3", String.valueOf( serialNumberAssignments.get(i+2).getId()));
					dynamiTe.parseDynElem( "serialNumberListTable1" );
				}

				for (int i=firstTableMax; i<secondTableMax ; i+=3) {
					dynamiTe.setVariable("SERIAL_NUMBER_TABLE2_NUMBER1", String.valueOf( serialNumberAssignments.get(i).getId()));
					dynamiTe.setVariable("SERIAL_NUMBER_TABLE2_NUMBER2", String.valueOf( serialNumberAssignments.get(i+1).getId()));
					dynamiTe.setVariable("SERIAL_NUMBER_TABLE2_NUMBER3", String.valueOf( serialNumberAssignments.get(i+2).getId()));
					dynamiTe.parseDynElem( "serialNumberListTable2" );
				}

				for (int i=secondTableMax ; i<thirdTableMax ; i+=3) {
					dynamiTe.setVariable("SERIAL_NUMBER_TABLE3_NUMBER1", String.valueOf( serialNumberAssignments.get(i).getId()));
					dynamiTe.setVariable("SERIAL_NUMBER_TABLE3_NUMBER2", String.valueOf( serialNumberAssignments.get(i+1).getId()));
					dynamiTe.setVariable("SERIAL_NUMBER_TABLE3_NUMBER3", String.valueOf( serialNumberAssignments.get(i+2).getId()));
					dynamiTe.parseDynElem( "serialNumberListTable3" );
				}

			}
			dynamiTe.parse();

			try {
				return dynamiTe.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
