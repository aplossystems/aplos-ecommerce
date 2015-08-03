package com.aplos.ecommerce.beans.developercmsmodules;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;

@Entity
@DynamicMetaValueKey(oldKey="NEWS_MODULE")
public class NewsModule extends ConfigurableDeveloperCmsAtom {

	private static final long serialVersionUID = -7600785224671741847L;

	private Boolean includeRecentPostsMenu=true;
	private Boolean includeRecentPostsTitle=true;
	private Boolean includeArchivedPostsMenu=true;
	private Boolean includeArchivedPostsTitle=true;

	public NewsModule() {
		super();
		setIncludeArchivedPostsMenu(false);
		setIncludeArchivedPostsTitle(false);
		setIncludeRecentPostsMenu(false);
		setIncludeRecentPostsTitle(false);
	}

	@Override
	public String getName() {
		return "News Feed";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		NewsModule copiedAtom = new NewsModule();
		copiedAtom.setIncludeRecentPostsMenu(getIncludeRecentPostsMenu());
		copiedAtom.setIncludeArchivedPostsMenu(getIncludeArchivedPostsMenu());
		copiedAtom.setIncludeRecentPostsTitle(getIncludeRecentPostsTitle());
		copiedAtom.setIncludeArchivedPostsTitle(getIncludeArchivedPostsTitle());
		return copiedAtom;
	}

	public void setIncludeRecentPostsMenu(Boolean includeRecentPostsMenu) {
		this.includeRecentPostsMenu = includeRecentPostsMenu;
	}

	public Boolean getIncludeRecentPostsMenu() {
		return includeRecentPostsMenu;
	}

	public void setIncludeRecentPostsTitle(Boolean includeRecentPostsTitle) {
		this.includeRecentPostsTitle = includeRecentPostsTitle;
	}

	public Boolean getIncludeRecentPostsTitle() {
		return includeRecentPostsTitle;
	}

	public void setIncludeArchivedPostsTitle(Boolean includeArchivedPostsTitle) {
		this.includeArchivedPostsTitle = includeArchivedPostsTitle;
	}

	public Boolean getIncludeArchivedPostsTitle() {
		return includeArchivedPostsTitle;
	}

	public void setIncludeArchivedPostsMenu(Boolean includeArchivedPostsMenu) {
		this.includeArchivedPostsMenu = includeArchivedPostsMenu;
	}

	public Boolean getIncludeArchivedPostsMenu() {
		return includeArchivedPostsMenu;
	}

}
