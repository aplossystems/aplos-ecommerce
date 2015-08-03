package com.aplos.ecommerce.developermodulebacking.frontend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.enums.SubscriptionHook;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.InfoPackRequest;
import com.aplos.ecommerce.beans.NewsEntry;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.developercmsmodules.NewsModule;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
public class NewsFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 5459117519152209503L;

	private NewsModule newsAtom;
	private String firstName;
	private String surname;
	private String email;

	private List<NewsEntry> entries = new ArrayList<NewsEntry>();
	private NewsEntry selectedNewsEntry = null;
	private final String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	private final int[] month_days = { 31,28,31,30,31,30,31,31,30,31,30,31 };

	private boolean infoPackSent = false;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		boolean continueLoad = super.responsePageLoad(developerCmsAtom);
		setNewsAtom((NewsModule) developerCmsAtom);
		createNewsEntryRotatorList();
		getLatestPostList(); //populate
		setSelectedNewsEntry(null);
		String articleIdStr = JSFUtil.getRequestParameter("article_id");
		if( !CommonUtil.isNullOrEmpty( articleIdStr ) ) {
			Long id = null;
			try {
				id = Long.parseLong(articleIdStr);
			} catch (NumberFormatException nfe) {
				ApplicationUtil.getAplosContextListener().handleError(nfe);
			}
			
			setSelectedNewsEntry((NewsEntry) new BeanDao( NewsEntry.class ).get( id ));
		}
		
		return continueLoad;
	}

	public void setNewsAtom(NewsModule newsAtom) {
		this.newsAtom = newsAtom;
	}

	public NewsModule getNewsAtom() {
		return newsAtom;
	}

	public void redirectToNewsEntry() {
		NewsEntry entry = (NewsEntry) JSFUtil.getRequest().getAttribute("entry");
		JSFUtil.redirect(new CmsPageUrl(EcommerceConfiguration.getEcommerceCprsStatic().getNewsCpr(),"article_id=" + entry.getId()),true);
	}

	public String getProductMapping( RealizedProduct realizedProduct ) {
		return EcommerceUtil.getEcommerceUtil().getProductMapping(realizedProduct);
	}

	public String sendInfoPack() {
		//validate
		if (firstName == null || firstName.equals("")) {
			JSFUtil.addMessageForError("You must enter your first name to request an info pack");
		} else if (surname == null || surname.equals("")) {
			JSFUtil.addMessageForError("You must enter your surname to request an info pack");
		} else if (email == null || email.equals("") || !CommonUtil.validateEmailAddressFormat(email)) {
			JSFUtil.addMessageForError("You must enter your email, in a valid format, to request an info pack");
		} else {
			//subscribe
			Subscriber subs = null;
			//new or existing subscriber...
			Customer customer = JSFUtil.getBeanFromScope(Customer.class);
			if (customer != null && customer.getId() != null) {
				subs = customer.getSubscriber();
			} else {
				BeanDao subscriberDao = new BeanDao( Subscriber.class ).addWhereCriteria( "emailAddress=:emailAddress" );
				subscriberDao.setNamedParameter( "emailAddress", email );
				List<Subscriber> subscriberList = subscriberDao.getAll();
				if( subscriberList.size() > 0 ) {
					subs = subscriberList.get(0);
				}
			}
			if (subs == null) {
				subs = new Subscriber();
				subs.setFirstName(firstName);
				subs.setSurname(surname);
				subs.setEmailAddress(email);
				subs.setSubscriptionHook(SubscriptionHook.INFO_PACK);
			}
			subs.setSubscribed(true);
			subs.saveDetails();
			//attach and send
			sendInfoPack(subs);
		}
		return null;
	}

	private void sendInfoPack(Subscriber subs) {
		InfoPackRequest infoPackRequest = new InfoPackRequest();
		infoPackRequest.setNewsEntry(getSelectedNewsEntry());
		infoPackRequest.setFirstName(getFirstName());
		infoPackRequest.setSurname(getSurname());
		infoPackRequest.setEmailAddress(getEmail());
		infoPackRequest.saveDetails();
		
		AplosEmail mail = new AplosEmail(EcommerceEmailTemplateEnum.INFO_PACK,infoPackRequest,infoPackRequest);
		mail.sendAplosEmailToQueue();

		mail = new AplosEmail(EcommerceEmailTemplateEnum.INFO_PACK_ADMIN,CommonConfiguration.getCommonConfiguration().getDefaultAdminUser(),infoPackRequest);
		mail.sendAplosEmailToQueue();
		
		setInfoPackSent(true);
		JSFUtil.addMessage("An information pack has been sent to the address you provided.",FacesMessage.SEVERITY_INFO);
	}

	public List<NewsEntry> getNewsEntryList() {
		return getEntries();
	}

	public List<NewsEntry> createNewsEntryRotatorList() {
		BeanDao newsBeanDao = new BeanDao( NewsEntry.class ).addWhereCriteria( "bean.showsInNewsRotator = true" );
		newsBeanDao.setOrderBy( "bean.dateCreated DESC" ).setMaxResults( 10 );
		List<NewsEntry> dbEntries = newsBeanDao.setIsReturningActiveBeans(true).getAll();
		setEntries(dbEntries);
		return getEntries();
	}

	public void getLatestPostList() {
		BeanDao newsBeanDao = new BeanDao( NewsEntry.class ).addWhereCriteria( "bean.showsInNewsPage = true" );
		newsBeanDao.setOrderBy( "bean.dateCreated DESC" );
		List<NewsEntry> dbEntries = newsBeanDao.setIsReturningActiveBeans(true).getAll();
		setEntries(dbEntries);
	}

	//common with blog btu dont wantto extend blogfedmb beacause the supertype will load another module

	public List<String> getArchiveMonthList() {
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String sql = "SELECT dateCreated FROM " + NewsEntry.class.getSimpleName()
		+ " WHERE dateCreated < '" + sdf.format(cal.getTimeInMillis()) + "-01' GROUP BY Year(dateCreated), Month(dateCreated) ORDER BY Year(dateCreated), Month(dateCreated) DESC";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = ApplicationUtil.getResults(sql);
		sdf = new SimpleDateFormat("MMMM yyyy");
		List<String> archiveList = new ArrayList<String>();
		for (int i=0; i < resultList.size(); i++) {
			archiveList.add(sdf.format((String)resultList.get(i)[0]));
		}
		return archiveList;
	}

	public int monthNumberFromName(String monthName) {
		for (int i=0; i<12; i++) {
			if (months[i].equals(monthName)) {
				if (i < 10) {
					return i;
				} else {
					return i;
				}
			}
		}
		return 1;
	}

	public String fetchArchive() {
		//get archive var (June 2010 etc)
		String[] archiveName = ((String)JSFUtil.getRequest().getAttribute("archive")).split(" ");
		int month = monthNumberFromName(archiveName[0]);
		String minDateStr = archiveName[1] + "-" + (((month+1)<10)?"0":"") + (month+1) + "-01";
		String maxDateStr = archiveName[1] + "-" + (((month+1)<10)?"0":"") + (month+1) + "-" + month_days[month];

		@SuppressWarnings("unchecked")
		BeanDao newsEntryDao = new BeanDao( NewsEntry.class );
		newsEntryDao.addWhereCriteria( "dateCreated <= '" + maxDateStr + "' AND dateCreated >= '" + minDateStr + "'" );
		newsEntryDao.setOrderBy( "be.dateCreated DESC" );
		newsEntryDao.setMaxResults( 10 );
		List<NewsEntry> dbEntries = newsEntryDao.getAll();
		setEntries(dbEntries);
		return null;
	}

	public String getEntryDateString( NewsEntry newsEntry ) {
		Date created = newsEntry.getDateCreated();
		if (created == null) {
			return "No Date Specified";
		}
		//return created.getDate() + " " + months[created.getMonth()] + " " + (created.getYear()+1900);
		return FormatUtil.formatDate(created);
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSurname() {
		return surname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setInfoPackSent(boolean infoPackSent) {
		this.infoPackSent = infoPackSent;
	}

	public boolean isInfoPackSent() {
		return infoPackSent;
	}

	public void setEntries(List<NewsEntry> entries) {
		this.entries = entries;
	}

	public List<NewsEntry> getEntries() {
		return entries;
	}

	public NewsEntry getSelectedNewsEntry() {
		return selectedNewsEntry;
	}

	public void setSelectedNewsEntry(NewsEntry selectedNewsEntry) {
		this.selectedNewsEntry = selectedNewsEntry;
	}

}
