package com.aplos.ecommerce.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.SystemUser;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.developercmsmodules.MyDetailsModule;

@ManagedBean
@ViewScoped
public class MyDetailsBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -8833466427889384812L;
	private MyDetailsModule myDetailsModule;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setMyDetailsAtom((MyDetailsModule) developerCmsAtom);
		return true;
	}

	public void setMyDetailsAtom(MyDetailsModule myDetailsModule) {
		this.myDetailsModule = myDetailsModule;
	}

	public MyDetailsModule getMyDetailsAtom() {
		return myDetailsModule;
	}

	public SelectItem[] getCustomerSelectItemBeans() {
		BeanDao dao = new BeanDao(Customer.class);
		dao.setSelectCriteria("bean.id, bean.username, bean.subscriber.firstName, bean.subscriber.surname");
		return SystemUser.getSelectItemBeansWithNotSelected(dao.getAll(), "The Session User");
	}

}
