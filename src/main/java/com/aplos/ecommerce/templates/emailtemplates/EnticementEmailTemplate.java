package com.aplos.ecommerce.templates.emailtemplates;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.Inheritance;
import com.aplos.common.annotations.persistence.InheritanceType;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.ecommerce.beans.EnticementEmailPromotion;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class EnticementEmailTemplate extends SourceGeneratedEmailTemplate<EnticementEmailPromotion> {

	private static final long serialVersionUID = 5595130625212920374L;

	//TODO: we dont need any methods since the change to using BulkEmailSource, consider removing

}
