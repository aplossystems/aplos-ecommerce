package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.developercmsmodules.CategoryListModule;

@ManagedBean
@ViewScoped
public class CategoryListFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -5185784209575844057L;

	public CategoryListFeDmb() {}

	//Lists children of selected node
	//This is used for Womanswear etc (top-level menu items)
	//Also handles designers a-z as it will be a list of the designers listed as submenu itmes
	public List<MenuNode> getNodeList() {

		MenuNode mainMenu = ((CmsWebsite) Website.getCurrentWebsiteFromTabSession()).getMainMenu();
		List<MenuNode> nodes = mainMenu.getChildren();

		for (MenuNode node : nodes) {
			if (node.getCmsPage() != null && node.getCmsPage().getMapping() != null
					&& node.getCmsPage().getStatus().equals(CmsPageRevision.PageStatus.PUBLISHED) ) {

				if (node.getId().equals(((CategoryListModule) getDeveloperCmsAtom()).getMenuNodeId())) {
					return node.getChildren();
				}

			}
		}

		return new ArrayList<MenuNode>();
	}

	public List<String> getGroups() {
		String[] groupsArr = { "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z" };
		return Arrays.asList(groupsArr);
	}

	public List<MenuNode> getNodeListByGroup() {
		String group = (String) JSFUtil.getRequest().getAttribute("group");
		List<MenuNode> groupNodes = new ArrayList<MenuNode>();
		for (MenuNode node : this.getNodeList()) {
			if (node.getCmsPage().getName().toUpperCase().startsWith(group.toUpperCase())) {
				groupNodes.add(node);
			}
		}
		/*if (groupNodes.size() < 1) {
			CmsPage cmsp = new CmsPage();
			cmsp.setName("- none -");
			cmsp.setMapping("#");
			CmsPageRevision cmspr = new CmsPageRevision();
			cmspr.setCmsPage(cmsp);
			MenuNode mn = new MenuNode();
			mn.setCmsPageRevision(cmspr);
			groupNodes.add(mn);
		}*/
		return groupNodes;
	}

}



















