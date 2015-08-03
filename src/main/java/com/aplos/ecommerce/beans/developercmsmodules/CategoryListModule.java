package com.aplos.ecommerce.beans.developercmsmodules;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;

@Entity
@DynamicMetaValueKey(oldKey="CATEGORY_LIST_MODULE")
public class CategoryListModule extends ConfigurableDeveloperCmsAtom implements PlaceholderContent {

	private static final long serialVersionUID = -2323713296848079498L;
	private Long menuNodeId;

	public CategoryListModule() {
		super();
	}

	@Override
	public String getName() {
		return "Category list";
	}

	public void setMenuNodeId(Long menuNodeId) {
		this.menuNodeId = menuNodeId;
	}

	public Long getMenuNodeId() {
		return menuNodeId;
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		CategoryListModule categoryListModule = new CategoryListModule();
		categoryListModule.setMenuNodeId(menuNodeId);
		return categoryListModule;
	}

}
