package com.aplos.ecommerce.backingpage.marketing;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.Promotion;
import com.aplos.ecommerce.beans.PromotionConstraint;
import com.aplos.ecommerce.beans.listbeans.CustomerListBean;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Promotion.class)
public class PromotionEditPage extends EditPage {

	private static final long serialVersionUID = -7534805207675931752L;
	private static final int PT_PERC_DISCOUNT = 7771;
	private static final int PT_FREE_POSTAGE = 7772;
	private static final int PT_FREE_POSTAGE_PLUS_DISCOUNT = 7773;

	private int promotionType = PT_PERC_DISCOUNT;
	private boolean generateCodeAutomatically = true;
	private Customer acCustomer = null;
	private ProductInfo acProductInfoRange = null;
	
	private PromotionConstraint newPromotionConstraint = new PromotionConstraint();

	@Override
	public void okBtnAction() {
		Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
		resetHiddenFields(promotion);
		if (generateCodeAutomatically) {
			promotion.generatePromoCode();
		}
		if (!promotion.isCodeRequired() || promotion.ensurePromotionalCodeUnique()) {
			super.okBtnAction();
		} else {
			JSFUtil.addMessageForError("This promotional code has been used already, please select another code.");
		}
	}
	
	public void setCodeNotRequired( boolean codeNotRequired ) {
		Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
		promotion.setCodeRequired(!codeNotRequired);
	}
	
	public boolean getCodeNotRequired() {
		Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
		return !promotion.isCodeRequired();
	}

	public String getGeneratedDescription() {
		Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
		String returnStr = promotion.getGeneratedDescription();
		if (promotion.isOneUsePerCustomer()) {
			if (promotion.getMaxUseCount() >= 1) {
				returnStr += ", for the first " + promotion.getMaxUseCount() + " customers to use it";
			} else {
				returnStr += ", good for one use per customer";
			}
		} else {
			if (promotion.getMaxUseCount() == 1) {
				returnStr += ", good for a single use";
			} else if (promotion.getMaxUseCount() > 1) {
				returnStr += ", good for " + promotion.getMaxUseCount() + " uses";
			}
		}
		return returnStr;
	}

	public void addConstraint() {
		if (getNewPromotionConstraint().getProductBrand() != null || 
				getNewPromotionConstraint().getProductInfo() != null || 
				getNewPromotionConstraint().getProductType() != null) {
			Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
			promotion.getConstraints().add(getNewPromotionConstraint());
			setNewPromotionConstraint(new PromotionConstraint());
		} else {
			JSFUtil.addMessageForError("You must select a constraint before you try to add it to the list.");
		}
	}

	public void removeConstraint() {
		Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
		PromotionConstraint promotionConstraint = (PromotionConstraint) JSFUtil.getRequest().getAttribute("constraint");
		promotion.getConstraints().remove(promotionConstraint);
	}

	public String getMaxUseLabel() {
		Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
		if (promotion != null && promotion.isOneUsePerCustomer()) {
			return "Maximum Number of Customers to Use This";
		}
		return "Maximum Number of Uses";
	}

	public void resetHiddenFields() {
		Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
		resetHiddenFields(promotion);
	}

	/**
	 * Used to set fields which aren't on the interface, based on the promotion type
	 * @param promotion
	 */
	private void resetHiddenFields(Promotion promotion) {
		switch (promotionType) {
			case PT_PERC_DISCOUNT:
				promotion.setFreePostage(false);
			break;
			case PT_FREE_POSTAGE:
				promotion.setFreePostage(true);
				promotion.setPercentage(null);
			break;
			case PT_FREE_POSTAGE_PLUS_DISCOUNT:
				promotion.setFreePostage(true);
				if (promotion.getPercentage() == null) {
					promotion.setPercentage(new BigDecimal(1));
				}
			break;
		}
		if (promotion.getCustomer() != null) {
			//can be handled by the max-uses field
			promotion.setOneUsePerCustomer(false);
		}
	}

	public String getActiveText() {
		Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
		StringBuffer activeText = new StringBuffer();
		if (promotion.isValidForDate()) {
			activeText.append("<span style='color:green;'>");
		} else {
			activeText.append("<span style='color:darkred;'>");
		}
		if (promotion.getStarts() != null) {
			activeText.append(FormatUtil.formatDate(promotion.getStarts()));
		} else if (promotion.getDateCreated() == null){
			activeText.append("Immediately");
		} else {
			activeText.append(FormatUtil.formatDate(promotion.getDateCreated()));
		}
		if (promotion.getExpires() != null) {
			activeText.append(" - ");
			activeText.append(FormatUtil.formatDate(promotion.getExpires()));
		} else {
			activeText.append(" onwards");
		}
		activeText.append("</span>");
		return activeText.toString();
	}

	@Override
	public boolean responsePageLoad() {
		Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
		if (promotion != null) {
			if (promotion.isFreePostage()) {
				if (promotion.getPercentage() == null) {
					promotionType = PT_FREE_POSTAGE;
				} else {
					promotionType = PT_FREE_POSTAGE_PLUS_DISCOUNT;
				}
			} else {
				promotionType = PT_PERC_DISCOUNT;
			}
			if (promotion.getCode() != null && !promotion.getCode().equals("")) {
				generateCodeAutomatically = false;
			}
		}
		return super.responsePageLoad();
	}

	public SelectItem[] getProductInfoSelectItemBeansWithNotSelected() {
		return AplosAbstractBean.getSelectItemBeansWithNotSelected( ProductInfo.class );
	}

	public SelectItem[] getProductTypeSelectItemBeansWithNotSelected() {
		return AplosAbstractBean.getSelectItemBeansWithNotSelected( ProductType.class );
	}

    public SelectItem[] getPromotionTypeSelectItems() {
    	SelectItem[] items = new SelectItem[3];
    	items[0] = new SelectItem(PT_PERC_DISCOUNT, "Percentage Discount");
    	items[1] = new SelectItem(PT_FREE_POSTAGE, "Free Postage");
    	items[2] = new SelectItem(PT_FREE_POSTAGE_PLUS_DISCOUNT, "Free Postage and Percentage Discount");
    	return items;
    }

	public boolean getIsPercentageDiscountType() {
		return promotionType == PT_PERC_DISCOUNT || promotionType == PT_FREE_POSTAGE_PLUS_DISCOUNT;
	}

	public void setPromotionType(int promotionType) {
		this.promotionType = promotionType;
		resetHiddenFields((Promotion)JSFUtil.getBeanFromScope(Promotion.class));
	}

	public int getPromotionType() {
		return promotionType;
	}

	public void setGenerateCodeAutomatically(boolean generateCodeAutomatically) {
		this.generateCodeAutomatically = generateCodeAutomatically;
	}

	public boolean isGenerateCodeAutomatically() {
		return generateCodeAutomatically;
	}

	public List<Customer> suggestCustomers(String searchStr) {
		BeanDao customerDao = new BeanDao(Customer.class);
		customerDao.setSelectCriteria( "bean.id, s.firstName, s.surname, s.emailAddress, bean.active" );
		customerDao.addQueryTable( "s", "bean.subscriber" );
		customerDao.setListBeanClass( CustomerListBean.class );
		customerDao.setWhereCriteria("LOWER(CONCAT(s.firstName,' ',s.surname)) LIKE '%" + searchStr.toLowerCase() + "%'");
		//Query query = custoemrDao.setIsReturningActiveBeans(true).getAllQuery();
		//query.setParameter("similarSearchText", "%" + searchStr.toLowerCase() + "%");
		List<Customer> custoemrs = customerDao.setIsReturningActiveBeans(true).getAll(); //query.list();
		custoemrs = (List<Customer>) Customer.sortByDisplayName(custoemrs);
		return custoemrs;
	}
	
	public void selectCustomer( SelectEvent event ) {
		
		Customer customerListBean = (Customer) event.getObject();
		if (customerListBean != null) {
			Customer customer = new BeanDao(Customer.class).get(customerListBean.getId());
			Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
			promotion.setCustomer(customer);
		}
		setAcCustomer(null);
		
	}

	public Customer getAcCustomer() {
		return acCustomer;
	}

	public void setAcCustomer(Customer acCustomer) {
		this.acCustomer = acCustomer;
	}

	public ProductInfo getAcProductInfoRange() {
		return acProductInfoRange;
	}

	public void setAcProductInfoRange(ProductInfo acProductInfoRange) {
		this.acProductInfoRange = acProductInfoRange;
	}
	
	public List<ProductInfo> suggestProductInfoRanges(String searchStr) {
		BeanDao productInfoDao = new BeanDao(ProductInfo.class);
		productInfoDao.setWhereCriteria("LOWER(bean.product.name) LIKE '%" + searchStr.toLowerCase() + "%'");
		List<ProductInfo> productInfoRanges = productInfoDao.setIsReturningActiveBeans(true).getAll();
		productInfoRanges = (List<ProductInfo>) ProductInfo.sortByDisplayName(productInfoRanges);
		return productInfoRanges;
	}
	
	public void selectProductInfoRange( SelectEvent event ) {
		
		ProductInfo productInfoRange = (ProductInfo) event.getObject();
		if (productInfoRange != null) {
			getNewPromotionConstraint().setProductInfo(productInfoRange);
			getNewPromotionConstraint().setProductBrand(null);
			getNewPromotionConstraint().setProductType(null);
		}
		setAcProductInfoRange(null);
		
	}
	
	public void removeCustomer() {
		Promotion promotion = JSFUtil.getBeanFromScope(Promotion.class);
		promotion.setCustomer(null);
	}
	
	public void removeProductInfoRange() {
		getNewPromotionConstraint().setProductInfo(null);
	}

	public PromotionConstraint getNewPromotionConstraint() {
		return newPromotionConstraint;
	}

	public void setNewPromotionConstraint(PromotionConstraint newPromotionConstraint) {
		this.newPromotionConstraint = newPromotionConstraint;
	}
	
}





