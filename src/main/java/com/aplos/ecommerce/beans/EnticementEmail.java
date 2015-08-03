package com.aplos.ecommerce.beans;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;

import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.persistence.Any;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.templates.emailtemplates.EnticementEmailTemplate;

@Entity
@ManagedBean
public class EnticementEmail extends AplosBean {

	private static final long serialVersionUID = -2478197927989412191L;

	@Column(name="idx") // dont know if this annotation is really required but the example had it
	private Integer sendDay=3; // integer so we can use compareto
	private Integer positionIdx;
	@Transient
	private Integer oldPositionIdx;

	//@ManyToOne
	@Any( metaColumn = @Column( name = "emailTemplate_type" ) )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = { /* Meta Values added in at run-time */ } )
    @JoinColumn(name="emailTemplate_id")
    @Cascade({CascadeType.ALL})
	@DynamicMetaValues
	private EnticementEmailTemplate emailTemplate;

	public EnticementEmail() { }

	public void setEmailTemplate(EnticementEmailTemplate template) {
		this.emailTemplate = template;
	}

	public EnticementEmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	public void setSendDay(Integer sendDelayDays) {
		this.sendDay = sendDelayDays;
	}

	public Integer getSendDay() {
		return sendDay;
	}

	public void setOldPositionIdx(Integer oldPositionIdx) {
		this.oldPositionIdx = oldPositionIdx;
	}

	public Integer getOldPositionIdx() {
		return oldPositionIdx;
	}

	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	public Integer getPositionIdx() {
		return positionIdx;
	}

	@Override
	public void saveBean( SystemUser currentUser ) {
		if (oldPositionIdx!=positionIdx) {
			BeanDao enticementEmailDao = new BeanDao( EnticementEmail.class );
			enticementEmailDao.addWhereCriteria( "positionIdx=" + positionIdx );
			EnticementEmail dbEnticementEmail = enticementEmailDao.getFirstBeanResult();
			if (dbEnticementEmail != null && !dbEnticementEmail.equals(this)) {
				if (oldPositionIdx == null){
					ApplicationUtil.executeSql("UPDATE " + EnticementEmail.class.getSimpleName() + " SET positionIdx=(positionIdx+1) WHERE positionIdx >= " + positionIdx);
				} else if (oldPositionIdx > positionIdx) {
					ApplicationUtil.executeSql("UPDATE " + EnticementEmail.class.getSimpleName() + " SET positionIdx=(positionIdx+1) WHERE positionIdx >= " + positionIdx + " AND positionIdx < " + oldPositionIdx);
				} else {
					ApplicationUtil.executeSql("UPDATE " + EnticementEmail.class.getSimpleName() + " SET positionIdx=(positionIdx-1) WHERE positionIdx > " + oldPositionIdx + " AND positionIdx <= " + positionIdx);
				}
				setOldPositionIdx(getPositionIdx());
			}
		}
		super.saveBean(currentUser);
	}

	public static List<EnticementEmail> sortByDaysDelay( List<EnticementEmail> lookupBeanList ) {
		return sortByDaysDelay(lookupBeanList, false);
	}

	public static List<EnticementEmail> sortByDaysDelay( List<EnticementEmail> lookupBeanList, final Boolean reverseOrder ) {
		Collections.sort( lookupBeanList, new Comparator<EnticementEmail>() {
			@Override
			public int compare(EnticementEmail email1, EnticementEmail email2) {
				if (reverseOrder) {
					return email2.getSendDay().compareTo( email1.getSendDay() );
				} else {
					return email1.getSendDay().compareTo( email2.getSendDay() );
				}
			}
		});
		return lookupBeanList;
	}
}

