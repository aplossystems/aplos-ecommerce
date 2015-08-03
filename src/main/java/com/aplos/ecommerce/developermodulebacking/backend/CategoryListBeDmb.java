package com.aplos.ecommerce.developermodulebacking.backend;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.Website;
import com.aplos.ecommerce.beans.developercmsmodules.CategoryListModule;

@ManagedBean
@ViewScoped
public class CategoryListBeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = 2876413249367636256L;

	public CategoryListBeDmb() {}

	//Lists top level menu items to select
	public SelectItem[] getMenuNodeSelectItems() {
	    SelectItem[] items;
		MenuNode mainMenu = ((CmsWebsite) Website.getCurrentWebsiteFromTabSession()).getMainMenu();
		List<MenuNode> nodes = mainMenu.getChildren();
		int itemCount=0;
		for (MenuNode node : nodes) {
			if (node.getCmsPage() != null && node.getCmsPage().getMapping() != null
					&& node.getCmsPage().getStatus().equals(CmsPageRevision.PageStatus.PUBLISHED)
					&& node.getChildren().size() > 0) {
					itemCount++;
					for (MenuNode nodeChild : node.getChildren()) {
						if (nodeChild.getCmsPage() != null && nodeChild.getCmsPage().getMapping() != null
								&& nodeChild.getCmsPage().getStatus().equals(CmsPageRevision.PageStatus.PUBLISHED)
								&& nodeChild.getChildren().size() > 0) {
								itemCount++;
						}
					}
			}
		}
		items = new SelectItem[itemCount];
		for (MenuNode node : nodes) {
			if (node.getCmsPage() != null && node.getCmsPage().getMapping() != null
					&& node.getCmsPage().getStatus().equals(CmsPageRevision.PageStatus.PUBLISHED)
					&& node.getChildren().size() > 0) {
				itemCount--;
				items[itemCount] = new SelectItem(node.getId(),node.getCmsPage().getName());
				for (MenuNode nodeChild : node.getChildren()) {
					if (nodeChild.getCmsPage() != null && nodeChild.getCmsPage().getMapping() != null
							&& nodeChild.getCmsPage().getStatus().equals(CmsPageRevision.PageStatus.PUBLISHED)
							&& nodeChild.getChildren().size() > 0) {
							itemCount--;
							items[itemCount] = new SelectItem(nodeChild.getId(),nodeChild.getCmsPage().getName());
					}
				}
			}
		}
		if (items.length == 0) {
			items = new SelectItem[1];
	    	items[0] = new SelectItem(-1l,"No Options Available");
		}
	    return items;
	}

	public void setMenuNodeId(Long menuNodeId) {
		CategoryListModule categoryListDefinition = (CategoryListModule) getDeveloperCmsAtom();
		categoryListDefinition.setMenuNodeId(menuNodeId);
	}

	public Long getMenuNodeId() {
		CategoryListModule categoryListDefinition = (CategoryListModule) getDeveloperCmsAtom();
		return categoryListDefinition.getMenuNodeId();
	}
}
