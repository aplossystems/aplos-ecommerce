package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.SubscriptionChannel;
import com.aplos.common.enums.SubscriptionHook;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class NewsletterSignupFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -8817302417873897324L;
	private Subscriber subscriberSignup;
	private String thankYouMessage = "";
	private SubscriptionChannel channel;
	private List<SubscriptionChannel> channelsCache;

	public NewsletterSignupFeDmb() {
		subscriberSignup = new Subscriber();
		subscriberSignup.setEmailAddress("Email Address"); //so we can have a field that clears when clicked
		subscriberSignup.setSubscriptionHook(SubscriptionHook.NEWSLETTER);
		JSFUtil.addToTabSession( "subscriberSignup", subscriberSignup );
	}

	@SuppressWarnings("unchecked")
	public void signupSubscriber() {
		if (CommonUtil.validateEmailAddressFormat(subscriberSignup.getEmailAddress())) {
			BeanDao customerDao = new BeanDao( Customer.class ).addWhereCriteria( "subscriber.emailAddress=:emailAddress" );
			customerDao.setNamedParameter( "emailAddress", subscriberSignup.getEmailAddress() );
			List<Customer> customerList = customerDao.getAll();
			if ( customerList.size() > 0 ) {

				JSFUtil.addMessage("The email address entered belongs to a registered customer. Please sign in to continue.");
				String thisSafeUrl = JSFUtil.getAplosContextOriginalUrl();
				if (thisSafeUrl.startsWith("/")) {
					thisSafeUrl = thisSafeUrl.substring(1);
				}
				JSFUtil.redirect(new CmsPageUrl(EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutSignInOrSignUpCpr().getCmsPage(),"user=" + subscriberSignup.getEmailAddress() + "&location=" + thisSafeUrl), true);

			} else {

				BeanDao subscriberDao = new BeanDao( Subscriber.class ).addWhereCriteria( "emailAddress=:emailAddress" );
				subscriberDao.setNamedParameter( "emailAddress", subscriberSignup.getEmailAddress() );
				List<Subscriber> subscriberList = subscriberDao.getAll();
				if ( subscriberList.size() > 0 ) {

					subscriberList.get(0).setSubscribed(true);
					if (channel != null) {
						subscriberList.get(0).addToChannel(channel);
					} else if (channelsCache != null && channelsCache.size() > 0) {
						for (SubscriptionChannel subChannel : channelsCache) {
							subscriberList.get(0).addToChannel(subChannel);
						}
					}
					subscriberList.get(0).saveDetails();
					thankYouMessage = "Thank you for subscribing, we will now keep you up to date with our latest news.";

				} else {

					if (channel != null) {
						getSubscriberSignup().addToChannel(channel);
					} else if (channelsCache != null && channelsCache.size() > 0) {
						for (SubscriptionChannel subChannel : channelsCache) {
							getSubscriberSignup().addToChannel(subChannel);
						}
					}
					getSubscriberSignup().saveDetails();
					thankYouMessage = "Thank you for signing up, we will now keep you up to date with our latest news.";

				}

			}
		} else {
			thankYouMessage = "The email address entered is not in a valid format.";
		}
	}

	public void signupSubscriberOrCurrentUser() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if (customer != null && customer.getId() != null && customer.isLoggedIn()) {
			customer.getSubscriber().setSubscribed(true);
			customer.getSubscriber().setSubscriptionHook(SubscriptionHook.NEWSLETTER);
			customer.saveDetails();
			customer.login(); //just in case;
			thankYouMessage = "Thank you for subscribing, we will now keep you up to date with our latest news.";
		} else {
			signupSubscriber();
		}
	}

	public void unsubscribe() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if (customer != null && customer.getId() != null) {
			customer.getSubscriber().setSubscribed(false);
			customer.getSubscriber().setSubscriptionHook(null);
			customer.saveDetails();
			customer.login(); //just in case;
		}
	}

	public boolean isAlreadySubscribedToNewsletter() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if (customer != null && customer.getId() != null) {
			if (customer.getSubscriber() != null) {
				if (customer.getSubscriber().isSubscribed()) {
					return true;
				}
			}
		}
		return false;
	}

	public String resetSignup() {
		thankYouMessage = "";
		channelsCache = null;
		return null;
	}

	public void setThankYouMessage(String thankYouMessage) {
		this.thankYouMessage = thankYouMessage;
	}

	public String getThankYouMessage() {
		return thankYouMessage;
	}

	public void setSubscriberSignup(Subscriber subscriberSignup) {
		this.subscriberSignup = subscriberSignup;
	}

	public Subscriber getSubscriberSignup() {
		return subscriberSignup;
	}

	public String redirectToNewsletterSignup() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToNewsletterSignUpCpr();
		return null;
	}

	public void setChannel(SubscriptionChannel channel) {
		this.channel = channel;
	}

	public SubscriptionChannel getChannel() {
		return channel;
	}

	public List<SelectItem> getChannelSelectItems() {
		BeanDao dao = new BeanDao(SubscriptionChannel.class);
//		if (subscriberSignup != null && !subscriberSignup.isNew()) {
//			StringBuffer buff = new StringBuffer("-1");
//			for (int i=0; i < subscriberSignup.getSubscriptions().size(); i++) {
//				buff.append(",");
//				buff.append(subscriberSignup.getSubscriptions().get(i).getId());
//			}
//			dao.addWhereCriteria("bean.id NOT IN (" + buff.toString() + ")");
//		}
		if (channelsCache == null) {
			channelsCache = dao.setIsReturningActiveBeans(true).getAll();
		}
		List<SelectItem> items = Arrays.asList(AplosAbstractBean.getSelectItemBeansWithNotSelected(channelsCache, "All"));
		return items;
	}

	public boolean isMultipleChannelsAvailable() {
		return getChannelSelectItems().size() > 1; //1 for not selected
	}
}
