package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;

@Entity
public class Promotion extends AplosBean {

	private static final long serialVersionUID = -2540030459757291182L;
	private String name;
	private String code;
	private BigDecimal percentage;
	@ManyToOne(fetch=FetchType.LAZY)
	private Customer customer;
	private Date starts;
	private Date expires;
	private int useCount=0;
	private int maxUseCount=-1;
	private boolean oneUsePerCustomer = false;
	private boolean freePostage = false;
	private boolean isCodeRequired = true;
	@OneToMany
	private List<PromotionConstraint> constraints = new ArrayList<PromotionConstraint>();

	public Promotion() {}

	public String getUsedCountText() {
		if (maxUseCount > 0) {
			return useCount + "/" + maxUseCount;
		} else {
			return String.valueOf(useCount);
		}
	}
	
	@Override
	public void saveBean( SystemUser currentUser ) {
		if (getConstraints() != null) {
			for (PromotionConstraint constraint : getConstraints()) {
				constraint.saveDetails();
			}
		}
		if (customer != null) {
			customer.saveDetails();
		}
		super.saveBean(currentUser);
	}
	
	public void registerUse() {
		setUseCount( getUseCount() + 1 );
	}

	public String getGeneratedDescription() {
		StringBuffer returnBuff = new StringBuffer();
		if (isFreePostage()) {
			if (getPercentage() == null) {
				returnBuff.append("Free postage");
			} else {
				returnBuff.append(percentage.doubleValue());
				returnBuff.append("% discount and free postage");
			}
		} else if (percentage != null) {
			returnBuff.append(percentage.doubleValue());
			returnBuff.append("% Off");
		} else {
			returnBuff.append("---");
		}
		boolean containsConstraintInfo = false;
		if (constraints.size() > 0) {
			returnBuff.append(" when you purchase");
		}
		for (PromotionConstraint constraint : constraints) {
		
			if (constraint.getProductInfo() != null) {

				if (containsConstraintInfo) {
					returnBuff.append(" and");
				}
				returnBuff.append(" ");
				returnBuff.append(constraint.getProductInfo().getDisplayName());
				containsConstraintInfo=true;
				
			} else if (constraint.getProductBrand() != null) {
				
				if (containsConstraintInfo) {
					returnBuff.append(" and");
				}
				returnBuff.append(" ");
				returnBuff.append(constraint.getProductBrand().getDisplayName());
				containsConstraintInfo=true;
				
			}
			if (constraint.getProductType() != null) {
				
				if (containsConstraintInfo && constraint.getProductInfo() == null && constraint.getProductBrand() == null) {
					returnBuff.append(" and");
				}
				returnBuff.append(" ");
				returnBuff.append(constraint.getProductType().getDisplayName());
				containsConstraintInfo=true;
			}
		
		}
		if (customer != null) {
			returnBuff.append(" for " + customer.getFullName());
		}
		return returnBuff.toString();
	}

	@Override
	public String getDisplayName() {
		return name + ": " + getGeneratedDescription();
	}

	public boolean isValid() {
		EcommerceShoppingCart ecommerceShoppingCart = JSFUtil.getBeanFromScope(ShoppingCart.class);
		return isValidFor(ecommerceShoppingCart, true);
	}

	public boolean isValidForDate(){
		return (expires == null || expires.compareTo(new Date()) >= 0) && (starts == null || starts.compareTo(new Date()) <= 0);
	}

	/**
	 * Checks if this promotion is valid for the cart passed in.
	 * @return
	 */
	public boolean isValidFor(EcommerceShoppingCart ecommerceShoppingCart,boolean addMessages) {

		//TODO: account for free postage
		if (ecommerceShoppingCart == null) {
			return false;
		}
		boolean containsRequiredCustomer = false;
		boolean satisfiesAllConstraints = false;
		boolean hasValidUses = false;
		boolean isntExpired = isValidForDate();
		if (!isntExpired) {
			if( addMessages ) {
				JSFUtil.addMessageForError("The " + code + " promotional code has expired.");
			}
		} else {
			
			if (maxUseCount > 0) {
				if (useCount < maxUseCount) {
					hasValidUses = true;
				} else {
					if( addMessages ) {
						JSFUtil.addMessageForError("The " + code + " promotional code has been used the maximum number of times.");
					}
				}
			} else {
				hasValidUses = true;
			}
			if (isValidForCustomer(ecommerceShoppingCart.getCustomer())) {
				containsRequiredCustomer = true;
			} else {
				if( addMessages ) {
					JSFUtil.addMessageForError("You cannot use the " + code + " promotional code.");
				}
			}
			
			satisfiesAllConstraints = true;
			
			if (constraints.size() > 0) {
				List<EcommerceShoppingCartItem> availableItemList = ecommerceShoppingCart.getEcommerceShoppingCartItems();
				
				for (PromotionConstraint constraint : constraints) {
					EcommerceShoppingCartItem usedItem = constraint.getCartItemWhichSatisfiesConstraint(availableItemList, getCode(), addMessages);
					if (usedItem == null) {
						satisfiesAllConstraints = false;
						break;
					} else {
						availableItemList.remove(usedItem);
					}
				}
			
			}

		}
		return satisfiesAllConstraints && containsRequiredCustomer && hasValidUses && isntExpired;
	}

	public boolean isUsedPreviouslyForCustomer(Customer customer) {
		BeanDao aqlBeanDao = new BeanDao(Transaction.class);
		/*
		 * Need to select at least too things otherwise
		 */
		aqlBeanDao.setSelectCriteria("bean.id, bean.active");
		aqlBeanDao.setMaxResults(1);
		aqlBeanDao.setWhereCriteria("bean.ecommerceShoppingCart.promotion.id=" + this.getId());
		aqlBeanDao.addWhereCriteria("bean.ecommerceShoppingCart.customer.id=" + customer.getId());
		aqlBeanDao.addWhereCriteria("bean.transactionStatus !=" + TransactionStatus.CANCELLED.ordinal());
		List<Transaction> existingCartList = aqlBeanDao.getAll();
		return existingCartList.size() > 0;
	}

	public boolean isValidForCustomer(Customer customer) {
		if (this.customer == null) {
			if (oneUsePerCustomer && customer != null && !this.isNew()) {
				//check the database to see that the customer has never
				//used this code before
				return !isUsedPreviouslyForCustomer(customer);
			} else {
				return true;
			}
		} else if (this.customer.equals(customer)) {
			if (oneUsePerCustomer && customer != null && !this.isNew()) {
				return !isUsedPreviouslyForCustomer(customer);
			} else {
				return true;
			}
		}
		return false;
	}

//	public boolean isValidForBrand(ProductBrand productBrand) {
//		if (this.productBrand == null) {
//			return true;
//		} else if (this.productBrand.equals(productBrand)) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean isValidForProduct(ProductInfo productInfo) {
//		if (this.productInfo == null) {
//			return true;
//		} else if (this.productInfo.equals(productInfo)) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean isValidForType(ProductType productType) {
//		if (this.productType == null) {
//			return true;
//		} else if (this.productType.equals(productType)) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean isValidForType(List<ProductType> productTypes) {
//		for (ProductType tempType : productTypes) {
//			if (isValidForType(tempType)) {
//				return true;
//			}
//		}
//		return false;
//	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String newCode) {
		this.code = newCode;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal newPercentage) {
		this.percentage = newPercentage;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
		if (customer != null) {
			//use max-uses instead when we have a customer
			setOneUsePerCustomer(false);
		}
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setMaxUseCount(int maxUseCount) {
		this.maxUseCount = maxUseCount;
	}

	public int getMaxUseCount() {
		return maxUseCount;
	}

	public void setUseCount(int useCount) {
		this.useCount = useCount;
	}

	public int getUseCount() {
		return useCount;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public Date getExpires() {
		return expires;
	}

	public void setOneUsePerCustomer(boolean oneUsePerCustomer) {
		this.oneUsePerCustomer = oneUsePerCustomer;
	}

	public boolean isOneUsePerCustomer() {
		return oneUsePerCustomer;
	}

	//Unique code methods

	public String generatePromoCode() {
		if (getName() != null) {
			String code = "";
			if (isFreePostage()) {
				code += "F";
			}
			if (getCustomer() != null) {
				code += "C";
			}
			if (getConstraints().size() > 0 && getConstraints().get(0).getProductInfo() != null) {
				code += "P";
			}
			if (getConstraints().size() > 0 && getConstraints().get(0).getProductType() != null) {
				code += "T";
			}
			if (getConstraints().size() > 0 && getConstraints().get(0).getProductBrand() != null) {
				code += "X";
			}
			if (getMaxUseCount() > 0) {
				code += "L";
			}
			if (getCustomer() != null) {
				String temp = getCustomer().getId().toString();
				if (temp.length() > 2) {
					temp = temp.substring(0, 2);
				}
				code += temp;
			}
			if (getConstraints().size() > 0 && getConstraints().get(0).getProductInfo() != null) {
				String temp = getConstraints().get(0).getProductInfo().getId().toString();
				if (temp.length() > 2) {
					temp = temp.substring(0, 2);
				}
				code += temp;
			}
			if (getConstraints().size() > 0 && getConstraints().get(0).getProductBrand() != null) {
				String temp = getConstraints().get(0).getProductBrand().getId().toString();
				if (temp.length() > 2) {
					temp = temp.substring(0, 2);
				}
				code += temp;
			}
			if (getMaxUseCount() > 0) {
				String temp = String.valueOf(getMaxUseCount());
				if (temp.length() > 2) {
					temp = temp.substring(0, 2);
				}
				code += temp;
			}
			//try to make it 9 chars so with the suffix we (hopefully) have a 10 char code
			if (code.length() < 9) {
				code += CommonUtil.md5(new Date()).substring(0, 9 - code.length());
			} else if (code.length() > 9) {
				code = code.substring(0, 9);
			}
			int suffix = 0;
			while (true) {
				if (ensurePromotionalCodeUnique(code + suffix)) {
					break;
				} else {
					suffix++;
				}
			}
			setCode(code.toUpperCase() + suffix);
		}
		return null;
	}

	private static boolean ensurePromotionalCodeUnique(String code) {
		BeanDao dao = new BeanDao(Promotion.class);
		dao.setWhereCriteria("bean.code LIKE :exactCode ");
		dao.setNamedParameter( "exactCode", code );
		return dao.getCountAll() < 1;
	}

	public boolean ensurePromotionalCodeUnique() {
		BeanDao dao = new BeanDao(Promotion.class);
		dao.setWhereCriteria("bean.code LIKE :exactCode ");
		if (!isNew()) {
			dao.addWhereCriteria("bean.id != " + getId());
		}
		dao.setNamedParameter( "exactCode", code );
		return dao.getCountAll() < 1;
	}

	public void setStarts(Date starts) {
		this.starts = starts;
	}

	public Date getStarts() {
		return starts;
	}

	public void setFreePostage(boolean freePostage) {
		this.freePostage = freePostage;
	}

	public boolean isFreePostage() {
		return freePostage;
	}

	public List<PromotionConstraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<PromotionConstraint> constraints) {
		this.constraints = constraints;
	}

	public boolean isCodeRequired() {
		return isCodeRequired;
	}

	public void setCodeRequired(boolean isCodeRequired) {
		this.isCodeRequired = isCodeRequired;
	}

}
