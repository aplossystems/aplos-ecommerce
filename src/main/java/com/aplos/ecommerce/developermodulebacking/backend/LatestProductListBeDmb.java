package com.aplos.ecommerce.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;

@ManagedBean
@ViewScoped
public class LatestProductListBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 4443657124207624019L;

	public LatestProductListBeDmb() {}

	public SelectItem[] getDaysToDisplaySelectItems() {
		int weeksToDisplay = 8;
		SelectItem[] items = new SelectItem[ weeksToDisplay*7 ];
		for (int day=1; day <= weeksToDisplay*7; day++) {
			int weeks = (int) Math.floor(day/7);
			int mod7 = day%7;
			//Prints: x day[s] || [y week[s]][, x day[s]] (z days)
			//1 day
			//2 days
			//1 week (7 days)
			//2 weeks (14 days)
			//2 weeks, 1 day (15 days)
			//2 weeks, 2 days (16 days)
			String display = ((day > 6)? weeks + " week" + ((weeks>1)?"s":"") + (((mod7)>0)? ", " + mod7 + " day" + ((mod7 > 1)?"s":""):"") : day + " day" + ((day > 1)?"s":"")) + ((weeks>=1)? " (" + day + " days)":"");
			items[day-1] = new SelectItem(day, display);
		}
	    return items;
	}

}
