package com.aplos.ecommerce.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.module.AplosModule;
import com.aplos.common.module.ModuleUpgrader;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.amazon.AmazonBrowseNode;
import com.aplos.ecommerce.beans.product.Product;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductSize;

public class EcommerceModuleUpgrader extends ModuleUpgrader {
//	ModuleConfiguration moduleConfiguration;

	public EcommerceModuleUpgrader(AplosModule aplosModule) {
		super(aplosModule, EcommerceConfiguration.class);
	}

	@Override
	protected void upgradeModule() {

		//don't use break, allow the rules to cascade
		switch (getMajorVersionNumber()) {
			case 1:

				switch (getMinorVersionNumber()) {
//					case 6:
//						switch (getPatchVersionNumber()) {
//						    case 0:
//						    	upgradeTo1_6_1();
//						    case 1:
//						    	upgradeTo1_6_2();
//						    case 2:
//						    	upgradeTo1_6_3();
//						    case 3:
//						    	upgradeTo1_6_4();
//						    case 4:
//						    	upgradeTo1_6_5();
//						    case 5:
//						    	upgradeTo1_7_0();
//						}
//					case 7:
//						switch (getPatchVersionNumber()) {
//						    case 0:
//						    	upgradeTo1_7_1();
//						    case 1:
//						    	upgradeTo1_7_2();
//						    case 2:
//						    	upgradeTo1_7_3();
//						    case 3:
//						    	upgradeTo1_7_4();
//						    case 4:
//						    	upgradeTo1_7_5();
//						    case 5:
//						    	upgradeTo1_7_6();
//						    case 6:
//						    	upgradeTo1_7_7();
//						    case 7:
//						    	upgradeTo1_7_8();
//						    case 8:
//						    	upgradeTo1_7_9();
//						    case 9:
//						    	upgradeTo1_7_10();
//						    case 10:
//						    	upgradeTo1_7_11();
//						    case 11:
//						    	upgradeTo1_7_12();
//						    case 12:
//						    	upgradeTo1_7_13();
//						    case 13:
//						    	upgradeTo1_7_14();
//						    case 14:
//						    	upgradeTo1_7_15();
//						    case 15:
//						    	upgradeTo1_7_16();
//						    case 16:
//						    	upgradeTo1_7_17();
//						    case 17:
//						    	upgradeTo1_7_18();
//						    case 18:
//						    	upgradeTo1_7_19();
//						    case 19:
//						    	upgradeTo1_7_20();
//						    case 20:
//						    	upgradeTo1_7_21();
//						    case 21:
//						    	upgradeTo1_7_22();
//						    case 22:
//						    	upgradeTo1_7_23();
//						    case 23:
//						    	upgradeTo1_7_24();
//						    case 24:
//						    	upgradeTo1_7_25();
//						    case 25:
//						    	upgradeTo1_7_26();
//						    case 26:
//						    	upgradeTo1_7_27();
//						    case 27:
//						    	upgradeTo1_7_28();
//						    case 28:
//						    	upgradeTo1_7_29();
//						    case 29:
//						    	upgradeTo1_7_30();
//						    case 30:
//						    	upgradeTo1_7_31();
//						    case 31:
//						    	upgradeTo1_7_32();
//						    case 32:
//						    	upgradeTo1_7_33();
//						    case 33:
//						    	upgradeTo1_7_34();
//						    case 34:
//						    	upgradeTo1_7_35();
//						    case 35:
//						    	upgradeTo1_7_36();
//						    case 36:
//						    	upgradeTo1_7_37();
//						    case 37:
//						    	upgradeTo1_7_38();
//						    case 38:
//						    	upgradeTo1_7_39();
//						    case 39:
//						    	upgradeTo1_7_40();
//						    case 40:
//						    	upgradeTo1_7_41();
//						    case 41:
//						    	upgradeTo1_7_42();
//						    case 42:
//						    	upgradeTo1_7_43();
//						    case 43:
//						    	upgradeTo1_7_44();
//						    case 44:
//						    	upgradeTo1_7_45();
//						    case 45:
//						    	upgradeTo1_7_46();
//						    case 46:
//						    	upgradeTo1_7_47();
//						    case 47:
//						    	upgradeTo1_7_48();
//						    case 48:
//						    	upgradeTo1_7_49();
//						    case 49:
//						    	upgradeTo1_7_50();
//						    case 50:
//						    	upgradeTo1_7_51();
//						    case 51:
//						    	upgradeTo1_7_52();
//						    case 52:
//						    	upgradeTo1_8_0();
//						}
					case 8:
						switch (getPatchVersionNumber()) {
//						    case 0:
//						    	upgradeTo1_8_1();
//						    case 1:
//						    	upgradeTo1_8_2();
//						    case 2:
//						    	upgradeTo1_8_3();
//						    case 3:
//						    	upgradeTo1_8_4();
//						    case 4:
//						    	upgradeTo1_8_5();
//						    case 5:
//						    	upgradeTo1_8_6();
//						    case 6:
//						    	upgradeTo1_8_7();
//						    case 7:
//						    	upgradeTo1_8_8();
//						    case 8:
//						    	upgradeTo1_8_9();
//						    case 9:
//						    	upgradeTo1_8_10();
//						    case 10:
//						    	upgradeTo1_8_11();
//						    case 11:
//						    	upgradeTo1_8_12();
//						    case 12:
//						    	upgradeTo1_8_13();
//						    case 13:
//						    case 14:
//						    	upgradeTo1_8_15();
//						    case 15:
//						    	upgradeTo1_8_16();
//						    case 16:
//						    	upgradeTo1_8_17();
//						    case 17:
//						    	upgradeTo1_8_18();
//						    case 18:
//						    	upgradeTo1_8_19();
//						    case 19:
//						    	upgradeTo1_8_20();
//						    case 20:
//						    	upgradeTo1_8_21();
//						    case 21:
//						    	upgradeTo1_8_22();
//						    case 22:
//						    	upgradeTo1_8_23();
//						    case 23:
//						    	upgradeTo1_8_24();
//						    case 24:
//						    	upgradeTo1_8_25();
//						    case 25:
//						    	upgradeTo1_8_26();
//						    case 26:
//						    	upgradeTo1_8_27();
//						    case 27:
//						    	upgradeTo1_8_28();
//						    case 28:
//						    	upgradeTo1_8_29();
//						    case 29:
//						    	upgradeTo1_8_30();
//						    case 30:
//						    	upgradeTo1_8_31();
//						    case 31:
//						    	upgradeTo1_8_32();
//						    case 32:
//						    	upgradeTo1_8_33();
//						    case 33:
//						    	upgradeTo1_8_34();
//						    case 34:
//						    	upgradeTo1_8_35();
//						    case 35:
//						    	upgradeTo1_8_36();
//						    case 36:
//						    	upgradeTo1_8_37();
//						    case 37:
//						    	upgradeTo1_8_38();
//						    case 38:
//						    	upgradeTo1_8_39();
//						    case 39:
//						    	upgradeTo1_8_40();
//						    case 40:
//						    	upgradeTo1_8_41();
//						    case 41:
//						    	upgradeTo1_8_42();
//						    case 42:
//						    	upgradeTo1_8_43();
//						    case 43:
//						    	upgradeTo1_8_44();
//						    case 44:
//						    	upgradeTo1_8_45();
//						    case 45:
//						    	upgradeTo1_8_46();
//						    case 46:
//						    	upgradeTo1_8_47();
//						    case 47:
//						    	upgradeTo1_8_48();
//						    case 48:
//						    	upgradeTo1_8_49();
//						    case 49:
//						    	upgradeTo1_8_50();
//						    case 50:
//						    	upgradeTo1_8_51();
//						    case 51:
//						    	upgradeTo1_8_52();
						    case 52:
						    	upgradeTo1_8_53();
						    case 53:
						    	upgradeTo1_8_54();
						    case 54:
						    	upgradeTo1_8_55();
						    case 55:
						    	upgradeTo1_8_56();
						    case 56:
						    	upgradeTo1_8_57();
						    case 57:
						    	upgradeTo1_8_58();
						    case 58:
						    	upgradeTo1_8_59();
						    case 59:
						    	upgradeTo1_8_60();
						    case 60:
						    	upgradeTo1_8_61();
						    case 61:
						    	upgradeTo1_8_62();
						    case 62:
						    	upgradeTo1_8_63();
						    case 63:
						    	upgradeTo1_8_64();
						    case 64:
						    	upgradeTo1_8_65();
						    case 65:
						    	upgradeTo1_8_66();
						    case 66:
						    	upgradeTo1_8_67();
						    case 67:
						    	upgradeTo1_8_68();
						    case 68:
						    	upgradeTo1_8_69();
						    case 69:
						    	upgradeTo1_8_70();
						    case 70:
						    	upgradeTo1_8_71();
						    case 71:
						    	upgradeTo1_8_72();
						    case 72:
						    	upgradeTo1_8_73();
						    case 73:
						    	upgradeTo1_8_74();
						    case 74:
						    	upgradeTo1_8_75();
						    case 75:
						    	upgradeTo1_8_76();
						    case 76:
						    	upgradeTo1_8_77();
						    case 77:
						    	upgradeTo1_8_78();
						    case 78:
						    	upgradeTo1_8_79();
						    case 79:
						    	upgradeTo1_8_80();
						    case 80:
						    	upgradeTo1_8_81();
						    case 81:
						    	upgradeTo1_9_0();
						}
					case 9:
						switch (getPatchVersionNumber()) {
						
						}

				}
		}
	}
	
	private void upgradeTo1_9_0() {
		setMinorVersionNumber(0);
		setPatchVersionNumber(0);
	}
	
	private void upgradeTo1_8_81() {
		dropColumn( Product.class, "discontinued" );
		setPatchVersionNumber(81);
	}
	
	private void upgradeTo1_8_80() {
		setDefault( EcommerceSettings.class, "isAllowingKitItems", "true" );
		setPatchVersionNumber(80);
	}
	
	private void upgradeTo1_8_79() {
		setDefault( EcommerceSettings.class, "isShowingDiscontinuedProducts", "false" );
		setPatchVersionNumber(79);
	}
	
	private void upgradeTo1_8_78() {
		dropColumn( ProductSize.class, "amazonSize" );
		dropColumn( SerialNumber.class, "antenna" );
		dropTable( "realizedproductreturn_aplosemail", true );
		dropColumn( ProductInfo.class, "searchKeywords" );
		dropTable( "transaction_aplosemail", true );
		setPatchVersionNumber(78);
	}
	
	private void upgradeTo1_8_77() {
		List<Object[]> searchKeywordResults = ApplicationUtil.getResults( "SELECT id, searchKeywords FROM ProductInfo WHERE searchKeywords IS NOT NULL" );
		BeanDao productInfoDao = new BeanDao( ProductInfo.class );
		String joinedSearchKeywords;
		for( int i = 0, n = searchKeywordResults.size(); i < n; i++ ) {
			joinedSearchKeywords = (String) searchKeywordResults.get( i )[ 1 ];
			if( !CommonUtil.isNullOrEmpty( joinedSearchKeywords ) ) {
				ProductInfo productInfo = productInfoDao.get( (Long) searchKeywordResults.get(i)[ 0 ] );
				String[] splitKeywords = StringUtils.split( joinedSearchKeywords, "," );
				for( int j = 0, p = splitKeywords.length; j < p; j++ ) {
					if( !CommonUtil.isNullOrEmpty( splitKeywords[ j ].trim() ) && splitKeywords[ j ].trim().length() < 180 ) {
						productInfo.getSearchKeywordList().add( splitKeywords[ j ].trim() );
					}
				}
				productInfo.saveDetails();
			}
		}
		setPatchVersionNumber(77);
	}
	
	private void upgradeTo1_8_76() {
		List<AmazonBrowseNode> amazonBrowseNodes = new BeanDao( AmazonBrowseNode.class ).getAll();
		for( int i = 0, n = amazonBrowseNodes.size(); i < n; i++ ) {
			if( amazonBrowseNodes.get( i ).getParentNode() != null ) {
				amazonBrowseNodes.get( i ).getParentNode().setAParentNode( true );
				amazonBrowseNodes.get( i ).getParentNode().saveDetails();
			}
		}
		setPatchVersionNumber(76);
	}
	
	private void upgradeTo1_8_75() {
		List<AmazonBrowseNode> amazonBrowseNodes = new BeanDao( AmazonBrowseNode.class ).getAll();
		for( int i = 0, n = amazonBrowseNodes.size(); i < n; i++ ) {
			if( amazonBrowseNodes.get( i ).getParentNode() != null ) {
				amazonBrowseNodes.get( i ).getParentNode().setAParentNode( true );
				amazonBrowseNodes.get( i ).getParentNode().saveDetails();
			}
		}
	}
	
	private void upgradeTo1_8_74() {
		List<AmazonBrowseNode> amazonBrowseNodes = new BeanDao( AmazonBrowseNode.class ).getAll();
		Map<String,AmazonBrowseNode> amazonBrowseNodeMap = new HashMap<String,AmazonBrowseNode>();
		for( int i = 0, n = amazonBrowseNodes.size(); i < n; i++ ) {
			amazonBrowseNodeMap.put( amazonBrowseNodes.get( i ).getName(), amazonBrowseNodes.get( i ));
		}
		
		String tempBrowseNodeName;
		String tempParentNodeName;
		for( int i = 0, n = amazonBrowseNodes.size(); i < n; i++ ) {
			tempBrowseNodeName = amazonBrowseNodes.get( i ).getName();
			if( !CommonUtil.isNullOrEmpty( tempBrowseNodeName ) && tempBrowseNodeName.lastIndexOf( "/" ) > -1 ) {
				tempParentNodeName = tempBrowseNodeName.substring( 0, tempBrowseNodeName.lastIndexOf( "/" ) );
				if( amazonBrowseNodeMap.get( tempParentNodeName ) != null ) {
					amazonBrowseNodes.get( i ).setParentNode( amazonBrowseNodeMap.get( tempParentNodeName ) );
					amazonBrowseNodes.get( i ).saveDetails();
				}
			}
		}
	}
	
	private void upgradeTo1_8_73() {
		ApplicationUtil.executeSql(  "UPDATE generatormenucmsatom SET generatorItemClass = 'com.aplos.ecommerce.beans.product.ProductType' WHERE generatorItemClass = 'com.aplos.ecommerce.beans.ProductType'" );
		ApplicationUtil.executeSql(  "UPDATE generatormenucmsatom SET generatorItemClass = 'com.aplos.ecommerce.beans.product.ProductBrand' WHERE generatorItemClass = 'com.aplos.ecommerce.beans.ProductBrand'" );
		setPatchVersionNumber(73);
	}
	
	private void upgradeTo1_8_72() {
//		CommonModuleUpgrader.renameMetaValueKeys();
		setPatchVersionNumber(72);
	}
	
	private void upgradeTo1_8_71() {
		dropColumn( EcommerceCmsPageRevisions.class, "cardSaveCheckoutThreeDAuthCpr_id" );
		dropColumn( EcommerceCmsPageRevisions.class, "checkoutThreeDAuthCpr_id" );
		dropColumn( Transaction.class, "configurationDetails_id" );
		dropColumn( Transaction.class, "configurationDetails_type" );
		setPatchVersionNumber(71);
	}
	
	private void upgradeTo1_8_70() {
		setDefault( EcommerceSettings.class, "isUsingAmazon", "false" );
		setPatchVersionNumber(70);
	}
	
	private void upgradeTo1_8_69() {
		ApplicationUtil.executeSql(  "UPDATE cmsconfiguration SET threeDAuthCpr_id = (SELECT Max(cardSaveCheckoutThreeDAuthCpr_id) FROM EcommerceCmsPageRevisions)" );
		setPatchVersionNumber(69);
	}
	
	private void upgradeTo1_8_68() {
		setDefault( ProductInfo.class, "isAddedToAmazon", "false" );
		setPatchVersionNumber(68);
	}

	private void upgradeTo1_8_67() {
		dropColumn( EcommerceCmsPageRevisions.class, "newsCpr_id" );
		setPatchVersionNumber(67);
	}

	private void upgradeTo1_8_66() {
		ApplicationUtil.executeSql(  "Update ecommerceSettings set isUsingBarcodes = false where isUsingBarcodes IS NULL" );
		setPatchVersionNumber(66);
	}

	private void upgradeTo1_8_65() {
		try {
			ApplicationUtil.executeSql(  "Update ecommerceSettings set isUsingBarcodes = false where isUsingBarcodes IS NULL" );
		} catch( Exception ex ) {}
		setPatchVersionNumber(65);
	}

	private void upgradeTo1_8_64() {
		try {
			ApplicationUtil.executeSql(  "Update additionalshippingoption set DTYPE = 'PostalZoneAso' where DTYPE = 'RoyalMailAddShipOpt'" );
			ApplicationUtil.executeSql(  "Update additionalshippingoption set DTYPE = 'PostalZoneAso' where DTYPE = 'RoyalMailAdditionalShippingOpti'" );
			
		} catch( Exception ex ) {}
		setPatchVersionNumber(64);
	}

	private void upgradeTo1_8_63() {
		try {
			ApplicationUtil.executeSql(  "UPDATE GiftVoucher SET DTYPE = 'GiftVoucher' WHERE DTYPE IS NULL" );
		} catch( Exception ex ) {}
		setPatchVersionNumber(63);
	}

	private void upgradeTo1_8_62() {
		ApplicationUtil.executeSql(  "UPDATE PaymentMethod SET isVisibleFrontend = true WHERE isVisibleFrontend IS NULL" );
		ApplicationUtil.executeSql(  "UPDATE PaymentMethod SET isVisibleFrontend = false WHERE name = 'Cash'" );
		setPatchVersionNumber(62);
	}

	private void upgradeTo1_8_61() {
		ApplicationUtil.executeSql(  "UPDATE PaymentMethod SET isSystemPaymentRequired = true WHERE isSystemPaymentRequired IS NULL" );
		ApplicationUtil.executeSql(  "UPDATE ecommerceConfiguration ec, PaymentMethod pm SET isSystemPaymentRequired = false WHERE pm.id in (ec.notRequiredPaymentMethod_id, ec.bankTransferPaymentMethod_id, chequePaymentMethod_id, accountPaymentMethod_id, cashPaymentMethod_id) OR name = 'Loan' OR name = 'Cheque' OR name = 'Credit note' OR name = 'Bank Transfer'" );
		setPatchVersionNumber(61);
	}

	private void upgradeTo1_8_60() {
		ApplicationUtil.executeSql(  "UPDATE EcommerceSettings SET isUsingRealizedProductReturns = false WHERE isUsingRealizedProductReturns IS NULL" );
		setPatchVersionNumber(60);
	}

	private void upgradeTo1_8_59() {
		try {
			ApplicationUtil.executeSql(  "ALTER TABLE `transaction_aplosemail` DROP INDEX `aplosEmailList_id` ;"  );
			ApplicationUtil.executeSql(  "ALTER TABLE `realizedproductreturn_aplosemail` DROP INDEX `aplosEmailList_id` ;"  );
		} catch( Exception ex ) {
			// do nothing
		}
		setPatchVersionNumber(59);
	}

	private void upgradeTo1_8_58() {
		ApplicationUtil.executeSql(  "UPDATE RealizedProduct SET isSerialNumberRequired = false WHERE isSerialNumberRequired IS NULL" );
		setPatchVersionNumber(58);
	}

	private void upgradeTo1_8_57() {
		ApplicationUtil.executeSql(  "UPDATE Transaction SET dateCreated = transactionDate WHERE duplicateTransaction_id IS NOT NULL" );
		setPatchVersionNumber(57);
	}

	private void upgradeTo1_8_56() {
		ApplicationUtil.executeSql(  "UPDATE EcommerceSettings SET isUsingPlayCom = false WHERE isUsingPlayCom IS NULL" );
		setPatchVersionNumber(56);
	}
	
	private void upgradeTo1_8_55() {
		ApplicationUtil.executeSql(  "UPDATE ProductInfo SET isOnStock = false WHERE isOnStock IS NULL"  );
		setPatchVersionNumber(55);
	}
	
	private void upgradeTo1_8_54() {
		try {
			ApplicationUtil.executeSql(  "ALTER TABLE promotion CHANGE COLUMN `percentage` `percentage` DOUBLE NULL;"  );
		} catch( Exception ex ) {
			// do nothing
		}
		setPatchVersionNumber(54);
	}
	
	private void upgradeTo1_8_53() {
		ApplicationUtil.executeSql(  "UPDATE CreatedPrintTemplate SET printTemplate_type = 'ProFormaTemplate' WHERE printTemplate_type IS NULL"  );
		setPatchVersionNumber(53);
	}
	
//	private void upgradeTo1_8_52() {
//		List<Object[]> aplosEmailDataList = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT pss.id, sentEmailList_id, createdPrintTemplate_id FROM printedsentstage pss LEFT OUTER JOIN printedsentstage_aplosemail pss_ae ON pss.id = pss_ae.printedsentstage_id" ).list();
//		Map<String, EmailTemplateEnum> emailTemplateEnumMap = new HashMap<String, EmailTemplateEnum>();
//		emailTemplateEnumMap.put( "Dispatch Invoice", EcommerceEmailTemplateEnum.DISPATCH_INVOICE );
//		emailTemplateEnumMap.put( "Ecommerce order", EcommerceEmailTemplateEnum.INVOICE );
//		emailTemplateEnumMap.put( "Dispatch note", EcommerceEmailTemplateEnum.DISPATCH_NOTE );
//		emailTemplateEnumMap.put( "Acknowledgement", EcommerceEmailTemplateEnum.ACKNOWLEDGEMENT );
//		emailTemplateEnumMap.put( "Pro forma", EcommerceEmailTemplateEnum.PRO_FORMA );
//		emailTemplateEnumMap.put( "Invoice", EcommerceEmailTemplateEnum.INVOICE );
//		emailTemplateEnumMap.put( "Condition Received Report", EcommerceEmailTemplateEnum.CONDITION_RECEIVED );
//		emailTemplateEnumMap.put( "Return Quote", EcommerceEmailTemplateEnum.QUOTE );
//		emailTemplateEnumMap.put( "Courier Details", EcommerceEmailTemplateEnum.COURIER_DETAILS );
//		emailTemplateEnumMap.put( "Repair Report", EcommerceEmailTemplateEnum.REPAIR_REPORT );
//		emailTemplateEnumMap.put( "Quote", EcommerceEmailTemplateEnum.QUOTE );
//		emailTemplateEnumMap.put( "Returns Authorisation Note", EcommerceEmailTemplateEnum.RETURNS_AUTH );
//		emailTemplateEnumMap.put( "Loan form", EcommerceEmailTemplateEnum.LOAN );
//		emailTemplateEnumMap.put( "Calibration Certificate", EcommerceEmailTemplateEnum.CALIBRATION );
//		
//		Object dispatchNoteEmailId = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id FROM emailTemplate WHERE DTYPE = 'DispatchNoteEmail'" ).uniqueResult();
//		if( dispatchNoteEmailId == null ) {
//			DispatchNoteEmail emailTemplate = new DispatchNoteEmail();
//			emailTemplate.loadDefaultValuesAndSave();
//			emailTemplate.saveDetails();
//			HibernateUtil.getCurrentSession().flush();
//		}
//
//		Map<EmailTemplateEnum, EmailTemplate> emailTemplateMap = new HashMap<EmailTemplateEnum, EmailTemplate>();
//		CreatedPrintTemplate tempCreatedPrintTemplate;
//		boolean isTransaction = false;
//		for( int i = 0, n = aplosEmailDataList.size(); i < n; i++ ) {
//			Long aplosEmailId = null;
//			AplosEmail aplosEmail;
//			if( aplosEmailDataList.get( i )[ 1 ] == null ) {
//				aplosEmail = new AplosEmail();
//			} else {
//				aplosEmailId = ((BigInteger) aplosEmailDataList.get( i )[ 1 ]).longValue();
//				aplosEmail = new AqlBeanDao( AplosEmail.class ).get( aplosEmailId );
//			}
//
//			Long printedSentStageId = ((BigInteger) aplosEmailDataList.get( i )[ 0 ]).longValue();
//			List<Object[]> printedSentStageDataList = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT stageName, createdPrintTemplate_id FROM printedsentstage WHERE id = " + printedSentStageId ).list();
//			if( printedSentStageDataList.size() > 0 && printedSentStageDataList.get( 0 )[ 0 ] != null ) {
//				EmailTemplateEnum emailTemplateEnum = emailTemplateEnumMap.get( printedSentStageDataList.get( 0 )[ 0 ] );
//				EmailTemplate emailTemplate = emailTemplateMap.get( emailTemplateEnum );
//				if( emailTemplate == null ) {
//					Long emailTemplateId = ((BigInteger) HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id FROM emailTemplate WHERE DTYPE = '" + emailTemplateEnum.getEmailTemplateClass().getSimpleName() + "'" ).uniqueResult()).longValue();
//					emailTemplate = new AqlBeanDao( EmailTemplate.class ).get( emailTemplateId );
//					emailTemplateMap.put( emailTemplateEnum, emailTemplate );
//				}
//				aplosEmail.setEmailTemplate(emailTemplate);
//
//				Object transactionId = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT transaction_id FROM transaction_printedSentStage WHERE printedSentStageList_id = " + printedSentStageId ).uniqueResult();
//				if( transactionId != null ) {
//					isTransaction = true;
//					Transaction transaction = new AqlBeanDao( Transaction.class ).get( ((BigInteger) transactionId).longValue() );
//					aplosEmail.getMessageSourceList().clear();
//					aplosEmail.getMessageSourceList().add( transaction );
//					aplosEmail.setEmailGenerationType(MessageGenerationType.SINGLE_SOURCE);
//				} else {
//					isTransaction = false;
//					Object realizedProductReturnObj = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT realizedProductReturn_id FROM realizedProductReturn_printedSentStage WHERE printedSentStageList_id = " + printedSentStageId ).uniqueResult();
//					if( realizedProductReturnObj != null ) {
//						Long realizedProductReturnId = ((BigInteger) realizedProductReturnObj).longValue();
//						RealizedProductReturn realizedProductReturn = new AqlBeanDao( RealizedProductReturn.class ).get( realizedProductReturnId );
//						aplosEmail.getMessageSourceList().clear();
//						aplosEmail.getMessageSourceList().add( realizedProductReturn );
//						aplosEmail.setEmailGenerationType(MessageGenerationType.SINGLE_SOURCE);
//					} else {
//						System.out.println( "Ignore" );
//					}
//				}
//			}
//			aplosEmail.saveDetails();
//			
//			if( aplosEmailId == null ) {
//				ApplicationUtil.executeSql(  "UPDATE aplosEmail ae, printedSentStage pss SET ae.active = pss.active, ae.dateCreated = pss.dateCreated, ae.dateLastModified = pss.dateLastModified, ae.userIdCreated = pss.userIdCreated, ae.userIdInactivated = pss.userIdInactivated, ae.userIdLastModified = pss.userIdLastModified, ae.owner_id = pss.owner_id WHERE ae.id = " + aplosEmail.getId() + " AND pss.id = " + printedSentStageId  );
//				Long createdPrintTemplateId = ((BigInteger) aplosEmailDataList.get( i )[ 2 ]).longValue();
//				ApplicationUtil.executeSql(  "INSERT INTO aplosEmail_fileDetails VALUES ( " + aplosEmail.getId() + ", " + createdPrintTemplateId + " )"  );
//			}
//
//			if( isTransaction ) {
//				ApplicationUtil.executeSql(  "INSERT INTO transaction_aplosEmail SELECT transaction_id, " + aplosEmail.getId() + " FROM transaction_printedsentstage WHERE printedSentStageList_id = " + printedSentStageId  );
//			} else {
//				ApplicationUtil.executeSql(  "INSERT INTO realizedProductReturn_aplosEmail SELECT realizedProductReturn_id, " + aplosEmail.getId() + " FROM realizedProductReturn_printedsentstage WHERE printedSentStageList_id = " + printedSentStageId  );
//			}
//			
//		}
//		setPatchVersionNumber(52);
//	}
//	
//	private void upgradeTo1_8_51() {
//		ApplicationUtil.executeSql(  "UPDATE Transaction SET isFullyPaid = false WHERE isFullyPaid IS NULL"  );
//		/*List<Transaction> transactionList = new AqlBeanDao( Transaction.class ).getAll();
//		for( int i = 0, n = transactionList.size(); i < n; i++ ) {
//			transactionList.get( i ).setFullyPaid( transactionList.get( i ).isAlreadyPaid() );
//			ApplicationUtil.executeSql(  "UPDATE Transaction SET isFullyPaid = true WHERE id = " + transactionList.get( i ).getId()  );
//		}*/
//		
//		AqlBeanDao sagePayDirectPostDao = new AqlBeanDao( SagePayDirectPost.class );
//		sagePayDirectPostDao.setSelectCriteria( "bean.id" );
//		sagePayDirectPostDao.addWhereCriteria( "bean.status = 'OK'" );
//		List<Long> transactionIds = sagePayDirectPostDao.getAll();
//		for( int i = 0, n = transactionIds.size(); i < n; i++ ) {
//			ApplicationUtil.executeSql(  "UPDATE Transaction SET isFullyPaid = true WHERE id = " + transactionIds.get( i )  );
//		}
//
//		AqlBeanDao cardSaveDirectPostDao = new AqlBeanDao( CardSaveDirectPost.class );
//		cardSaveDirectPostDao.setSelectCriteria( "bean.id" );
//		cardSaveDirectPostDao.addWhereCriteria( "bean.status = 'OK'" );
//		transactionIds = cardSaveDirectPostDao.getAll();
//		for( int i = 0, n = transactionIds.size(); i < n; i++ ) {
//			ApplicationUtil.executeSql(  "UPDATE Transaction SET isFullyPaid = true WHERE id = " + transactionIds.get( i )  );
//		}
//		
//		AqlBeanDao payPalDirectPostDao = new AqlBeanDao( PayPalDirectPost.class );
//		payPalDirectPostDao.setSelectCriteria( "bean.id" );
//		payPalDirectPostDao.addWhereCriteria( "bean.isPaid = true" );
//		transactionIds = payPalDirectPostDao.getAll();
//		for( int i = 0, n = transactionIds.size(); i < n; i++ ) {
//			ApplicationUtil.executeSql(  "UPDATE Transaction SET isFullyPaid = true WHERE id = " + transactionIds.get( i )  );
//		}
//		setPatchVersionNumber(51);
//	}
//	
//	private void upgradeTo1_8_50() {
//		ApplicationUtil.executeSql(  "INSERT INTO realizedproduct_filedetails SELECT * FROM realizedproduct_realizedproductimagedetails"  );
//		setPatchVersionNumber(50);
//	}
//	
//	private void upgradeTo1_8_49() {
////		List<Transaction> transactionList = new AqlBeanDao( Transaction.class ).addWhereCriteria( "bean.printedSentStageList.size > 0" ).getAll();
////		File tempFile;
////		Customer tempCustomer;
////		for( int i = 0, n = transactionList.size(); i < n; i++ ) {
////			for( int j = 0, p = transactionList.get( i ).getPrintedSentStageList().size(); j < p; j++ ) {
////				CreatedPrintTemplate createdPrintTemplate = transactionList.get( i ).getPrintedSentStageList().get( j ).getCreatedPrintTemplate();
////				if( createdPrintTemplate != null ) {
////					tempCustomer = transactionList.get( i ).getCustomer();
////					tempFile = new File( createdPrintTemplate.determineFileDetailsDirectory(true) + "/" + (tempCustomer.getId() + " - " + tempCustomer.getDisplayName()).trim() + "/" + createdPrintTemplate.getFilename() );
////					createdPrintTemplate.setName( createdPrintTemplate.getFilename() );
////					createdPrintTemplate.setFilename( createdPrintTemplate.getId() + ".pdf" );
////					tempFile.renameTo( createdPrintTemplate.getFile() );
////					createdPrintTemplate.saveDetails();
////				}
////			}
////		}
////		
////
////		List<RealizedProductReturn> realizedProductReturnList = new AqlBeanDao( RealizedProductReturn.class ).addWhereCriteria( "bean.printedSentStageList.size > 0" ).getAll();
////		for( int i = 0, n = realizedProductReturnList.size(); i < n; i++ ) {
////			for( int j = 0, p = realizedProductReturnList.get( i ).getPrintedSentStageList().size(); j < p; j++ ) {
////				CreatedPrintTemplate createdPrintTemplate = realizedProductReturnList.get( i ).getPrintedSentStageList().get( j ).getCreatedPrintTemplate();
////				if( createdPrintTemplate != null ) {
////					tempCustomer = realizedProductReturnList.get( i ).determineEndCustomer();
////					tempFile = new File( createdPrintTemplate.determineFileDetailsDirectory(true) + "/" + (tempCustomer.getId() + " - " + tempCustomer.getDisplayName()).trim() + "/" + createdPrintTemplate.getFilename() );
////					createdPrintTemplate.setName( createdPrintTemplate.getFilename() );
////					createdPrintTemplate.setFilename( createdPrintTemplate.getId() + ".pdf" );
////					tempFile.renameTo( createdPrintTemplate.getFile() );
////					createdPrintTemplate.saveDetails();
////				}
////			}
////		}
//		setPatchVersionNumber(49);
//	}
//	
//	private void upgradeTo1_8_48() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE tabclass SET classSerialUID = NULL where backingPageClass = 'com.aplos.ecommerce.backingpage.realizedProductReturn.RealizedProductReturnPrintSendPage'"  );
//		} catch( Exception ex ) {
//		}
//		setPatchVersionNumber(48);
//	}
//	
//	private void upgradeTo1_8_47() {
//		upgrade47J(); //NewsEntry PDF
//		setPatchVersionNumber(47);
//	}
//	
//	private void upgradeTo1_8_46() {
//		upgrade46I(); //BROCHURE PDF
//		setPatchVersionNumber(46);
//	}
//		
//	private void upgradeTo1_8_45() {
//		upgrade45A(); //BROCHURE
//		upgrade45B(); //NEWS
//		upgrade45C(); //RETURNS
//		upgrade45D(); //FAQ
//		upgrade45E();	 //BRAND
//		upgrade45F();	 //TYPE
//		upgrade45G(); //Associated product module
//		upgrade45H();	 //Company
//		//realized product seem to have been adapted already so doesnt need an upgrader
//		setPatchVersionNumber(45);
//	}
//	
//	private void upgrade47J() {
//		List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, pdf FROM newsEntry WHERE pdf IS NOT NULL" ).list();
//		NewsEntry tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (NewsEntry) new AqlBeanDao( NewsEntry.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], NewsImageKey.PDF.name());
//			tempBean.saveDetails();
//		}
//	}
//	
//	private void upgrade46I() {
//		List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, pdfUrl FROM brochure WHERE pdfUrl IS NOT NULL" ).list();
//		Brochure tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (Brochure) new AqlBeanDao( Brochure.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], BrochureImageKey.PDF.name());
//			tempBean.saveDetails();
//		}
//	}
//
//	private void upgrade45H() {
//		List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, imageUrl1, imageUrl2, photo, logo FROM company WHERE imageUrl1 IS NOT NULL OR imageUrl2 IS NOT NULL OR photo IS NOT NULL OR logo IS NOT NULL" ).list();
//		Company tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (Company) new AqlBeanDao( Company.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], CompanyImageKey.IMAGE_ONE.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 2 ], CompanyImageKey.IMAGE_TWO.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 3 ], CompanyImageKey.PHOTO.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 4 ], CompanyImageKey.LOGO.name());
//			tempBean.saveDetails();
//		}
//	}
//	
//	private void upgrade45G() {
//		List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, titleImageFilename FROM associatedproductsmodule WHERE titleImageFilename IS NOT NULL" ).list();
//		AssociatedProductsModule tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (AssociatedProductsModule) new AqlBeanDao( AssociatedProductsModule.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], AssociatedProductsImageKey.TITLE_IMAGE.name());
//			tempBean.saveDetails();
//		}
//	}
//	
//	private void upgrade45F() {
//		List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, smallPhotoUrl, mediumPhotoUrl, publicityPhoto1Url, publicityPhoto2Url FROM producttype WHERE smallPhotoUrl IS NOT NULL OR mediumPhotoUrl IS NOT NULL OR publicityPhoto1Url IS NOT NULL OR publicityPhoto2Url IS NOT NULL" ).list();
//		ProductType tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (ProductType) new AqlBeanDao( ProductType.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], ProductTypeImageKey.SMALL_IMAGE.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 2 ], ProductTypeImageKey.MEDIUM_IMAGE.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 3 ], ProductTypeImageKey.PUBLICITY_IMAGE_1.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 4 ], ProductTypeImageKey.PUBLICITY_IMAGE_2.name());
//			tempBean.saveDetails();
//		}
//	}
//	
//	private void upgrade45E() {
//		try {
//			List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, imageUrl, smallImageUrl FROM productBrand WHERE imageUrl IS NOT NULL OR smallImageUrl IS NOT NULL" ).list();
//			ProductBrand tempBean;
//			for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//				tempBean = (ProductBrand) new AqlBeanDao( ProductBrand.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//				putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], BrandImageKey.MEDIUM.name());
//				putFileDetails(tempBean, (String) fileUrls.get( i )[ 2 ], BrandImageKey.SMALL.name());
//				tempBean.saveDetails();
//			}
//		} catch (Exception e) {
//			//small image url doesnt exist on soem old projects
//			List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, imageUrl FROM productBrand WHERE imageUrl IS NOT NULL" ).list();
//			ProductBrand tempBean;
//			for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//				tempBean = (ProductBrand) new AqlBeanDao( ProductBrand.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//				putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], BrandImageKey.MEDIUM.name());
//				tempBean.saveDetails();
//			}
//		}
//	}
//	
//	private void upgrade45D() {
//		List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, imageUrl FROM productfaq WHERE imageUrl IS NOT NULL" ).list();
//		ProductFaq tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (ProductFaq) new AqlBeanDao( ProductFaq.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], FaqImageKey.IMAGE.name());
//			tempBean.saveDetails();
//		}
//	}
//	
//	private void upgrade45C() {
//		List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, photo1Url, photo2Url, photo3Url, photo4Url FROM realizedProductReturn WHERE photo1Url IS NOT NULL OR photo2Url IS NOT NULL OR photo3Url IS NOT NULL OR photo4Url IS NOT NULL" ).list();
//		RealizedProductReturn tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (RealizedProductReturn) new AqlBeanDao( RealizedProductReturn.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], ReturnsImageKey.PHOTO_ONE.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 2 ], ReturnsImageKey.PHOTO_TWO.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 3 ], ReturnsImageKey.PHOTO_THREE.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 4 ], ReturnsImageKey.PHOTO_FOUR.name());
//			tempBean.saveDetails();
//		}
//	}
//	
//	private void upgrade45B() {
//		List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, imageUrl, imageUrlMid, imageUrlBottom FROM newsEntry WHERE imageUrl IS NOT NULL OR imageUrlMid IS NOT NULL OR imageUrlBottom IS NOT NULL" ).list();
//		NewsEntry tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (NewsEntry) new AqlBeanDao( NewsEntry.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], NewsImageKey.INITIAL.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 2 ], NewsImageKey.MIDDLE.name());
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 3 ], NewsImageKey.FOOTER.name());
//			tempBean.saveDetails();
//		}
//	}
//	
//	private void upgrade45A() {
//		List<Object[]> fileUrls = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT id, imageUrl FROM brochure WHERE imageUrl IS NOT NULL" ).list();
//		Brochure tempBean;
//		for( int i = 0, n = fileUrls.size(); i < n; i++ ) {
//			tempBean = (Brochure) new AqlBeanDao( Brochure.class ).get( ((BigInteger) fileUrls.get( i )[ 0 ]).longValue() );
//			putFileDetails(tempBean, (String) fileUrls.get( i )[ 1 ], BrochureImageKey.IMAGE.name());
//			tempBean.saveDetails();
//		}
//	}
//	
//	private void putFileDetails(FileDetailsOwnerInter tempBean, String fileUrl, String enumvalue) {
//		if ( !CommonUtil.isNullOrEmpty( fileUrl ) ) {
//			FileDetails tempFileDetails = new FileDetails( tempBean, enumvalue );
//			tempFileDetails.setFilename( fileUrl );
//			tempFileDetails.saveDetails();
//			tempBean.getFileDetailsOwnerHelper().setFileDetails( tempFileDetails, enumvalue, null );
//		}
//	}
//	
//	
//
//	private void upgradeTo1_8_44() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE menutab SET tabActionClass_id = (select id FROM tabclass where backingPageClass = 'com.aplos.teletest.backingpage.transaction.InvoiceListPage') where tabActionClass_id = (select id FROM tabclass where backingPageClass = 'com.aplos.teletest.backingpage.InvoiceListPage')"  );
//			ApplicationUtil.executeSql(  "delete FROM tabclass where backingPageClass = 'com.aplos.teletest.backingpage.InvoiceListPage'"  );
//		} catch( Exception ex ) {
//		}
//		setPatchVersionNumber(44);
//	}	
//
//	private void upgradeTo1_8_43() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE realizedProduct rp LEFT OUTER JOIN realizedProduct_realizedproductimagedetails rp_rpid ON rp.id = rp_rpid.realizedProduct_id LEFT OUTER JOIN realizedproductimagedetails rpid ON rpid.id = rp_rpid.imageDetailsList_id LEFT OUTER JOIN fileDetails fd ON fd.id = rpid.id SET fileDetailsKey = rpImageKey, fileDetailsOwner_type = 'REALIZED_PRODUCT', fileDetailsOwner_id = rp.id"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(43);
//	}		
//	
//	private void upgradeTo1_8_42() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommerceSettings SET isVolumetricWeightAuthorisationRequired = false WHERE isVolumetricWeightAuthorisationRequired IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(42);
//	}
//	
//	private void upgradeTo1_8_41() {
//		try {
//			ApplicationUtil.executeSql(  "ALTER TABLE sizechartmodule DROP COLUMN `tolerance` , DROP COLUMN `columns`"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(41);
//	}
//	
//	private void upgradeTo1_8_40() {
//		ApplicationUtil.executeSql(  "UPDATE ecommerceSettings SET isUsingCommodityCodes = false WHERE isUsingCommodityCodes IS NULL"  );
//		setPatchVersionNumber(40);
//	}
//	
//	private void upgradeTo1_8_39() {
//		ApplicationUtil.executeSql(  "UPDATE ecommerceSettings SET isUsingSerialNumbers = false WHERE isUsingSerialNumbers IS NULL"  );
//		ApplicationUtil.executeSql(  "UPDATE ecommerceSettings SET isUsingCustomerReference = false WHERE isUsingCustomerReference IS NULL"  );
//		setPatchVersionNumber(39);
//	}
//	
//	private void upgradeTo1_8_38() {
//		ApplicationUtil.executeSql(  "UPDATE ecommerceSettings SET isUsingItemCodes = cartShowingItemCodes WHERE isUsingItemCodes IS NULL"  );
//		setPatchVersionNumber(38);
//	}
//	
//	private void upgradeTo1_8_37() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE sizeChartModule SET maxColumns = columns WHERE maxColumns IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE sizeChartModule SET columnTolerance = tolerance WHERE columnTolerance IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(37);
//	}
//	
//	private void upgradeTo1_8_36() {
//		ApplicationUtil.executeSql(  "UPDATE productSize SET productSizeType_id = NULL WHERE productSizeType_id = 1"  );
//		ApplicationUtil.executeSql(  "DELETE FROM productSizeChart WHERE id = 45"  );
//		List<ProductSizeChart> productSizeChartList = new AqlBeanDao( ProductSizeChart.class ).setIsReturningActiveBeans( null ).getAll();
//		ProductSizeChart tempProductSizeChart;
//		ProductSize tempProductSize;
//		HashMap<Long,ProductSize> productSizeMap = new HashMap<Long, ProductSize>();
//		for( int i = 0, n = productSizeChartList.size(); i < n; i++ ) {
//			tempProductSizeChart = productSizeChartList.get( i );
//			tempProductSizeChart.setRowAxisType( SizeChartAxisType.CUSTOM );
//			tempProductSizeChart.setColumnAxisType( SizeChartAxisType.SIZES );
//			List<Object[]> measurementsList = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT pscm.id, pscm.name FROM productSizeChartMeasurement pscm LEFT OUTER JOIN productsizechart_productsizechartmeasurement psc_pscm ON pscm.id = psc_pscm.measurements_id WHERE psc_pscm.productsizechart_id = " + tempProductSizeChart.getId() ).list();
//			CustomSizeChartAxisLabel customSizeChartAxisLabels[] = new CustomSizeChartAxisLabel[ measurementsList.size() ];
//			if( measurementsList.size() > 0 ) {
//				Long productSizeId = null;
//				measurementFor : for( int j = 0, p = measurementsList.size(); j < p; j++ ) {
//					List<Object[]> actualMeasurementsList = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT realMeasurementImperial, realMeasurementMetric, productSize_id FROM productSizeChartActualMeasurement pscam LEFT OUTER JOIN productsizechartmeasurement_productsizechartactualmeasurement pscm_pscam ON pscm_pscam.productsizeactualmeasurements_id = pscam.id WHERE productSizeChartMeasurement_id = " + measurementsList.get( j )[ 0 ] + " and productSize_id IS NOT NULL ORDER BY pscm_pscam.idx DESC" ).list();
//					for( int k = 0, q = actualMeasurementsList.size(); k < q; k++ ) {
//						if( actualMeasurementsList.get( k )[ 2 ] != null ) {
//							productSizeId = ((BigInteger) actualMeasurementsList.get( k )[ 2 ]).longValue();
//							break measurementFor;
//						}
//					}
//				}
//				if( productSizeId != null ) {
//					tempProductSize = productSizeMap.get( productSizeId ); 
//					if( tempProductSize == null ) {
//						tempProductSize = new AqlBeanDao( ProductSize.class ).get( productSizeId );
//						productSizeMap.put( productSizeId, tempProductSize ); 
//					}
//					tempProductSizeChart.setProductSizeType(tempProductSize.getProductSizeType());
//					
//				} else {
//					continue;
//				}
//			} else {
//				continue;
//			}
//			for( int j = 0, p = measurementsList.size(); j < p; j++ ) {
//				customSizeChartAxisLabels[ j ] = new CustomSizeChartAxisLabel();
//				customSizeChartAxisLabels[ j ].setName( (String) measurementsList.get( j )[ 1 ] );
//				tempProductSizeChart.addCustomAxisLabel( true, customSizeChartAxisLabels[ j ], true );
//				customSizeChartAxisLabels[ j ].saveDetails();
//			}
//			tempProductSizeChart.refreshLabels(false);
//			tempProductSizeChart.updateGridValues( true );
//			
//			for( int j = 0, p = measurementsList.size(); j < p; j++ ) {
//				List<Object[]> actualMeasurementsList = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT realMeasurementImperial, realMeasurementMetric, productSize_id FROM productSizeChartActualMeasurement pscam LEFT OUTER JOIN productsizechartmeasurement_productsizechartactualmeasurement pscm_pscam ON pscm_pscam.productsizeactualmeasurements_id = pscam.id WHERE productSizeChartMeasurement_id = " + measurementsList.get( j )[ 0 ] + " and productSize_id IS NOT NULL ORDER BY pscm_pscam.idx DESC" ).list();
//				for( int k = 0, q = actualMeasurementsList.size(); k < q; k++ ) {
//					Long productSizeId = ((BigInteger) actualMeasurementsList.get( k )[ 2 ]).longValue();
//					tempProductSize = productSizeMap.get( productSizeId ); 
//					if( tempProductSize == null ) {
//						tempProductSize = new AqlBeanDao( ProductSize.class ).get( productSizeId );
//						productSizeMap.put( productSizeId, tempProductSize ); 
//					}
//					
//					Integer columnIndex = -1;
//					for( SizeChartAxisLabel productSize : tempProductSizeChart.getColumnLabels() ) {
//						if( productSize.getSizeChartAxisLabelName().equals( tempProductSize.getName() ) ) {
//							columnIndex = tempProductSizeChart.getColumnLabels().indexOf( productSize );
//							break;
//						}
//					}
//					if( columnIndex != -1 ) {
//						tempProductSizeChart.setCellValue( MeasurementType.CENTIMETRES, columnIndex, j, (String) actualMeasurementsList.get( k )[ 1 ] );
//						tempProductSizeChart.setCellValue( MeasurementType.INCHES, columnIndex, j, (String) actualMeasurementsList.get( k )[ 0 ] );
//					} else {
//						System.out.println( tempProductSize.getName() );
//					}
//				}
//			}
//			tempProductSizeChart.saveDetails();
//		}
//		setPatchVersionNumber(36);
//	}
//	
//	private void upgradeTo1_8_35() {
//		ApplicationUtil.executeSql(  "UPDATE productSizeChart SET isUsingCentimetres = true WHERE isUsingCentimetres IS NULL"  );
//		ApplicationUtil.executeSql(  "UPDATE productSizeChart SET isUsingInches = true WHERE isUsingInches IS NULL"  );
//		setPatchVersionNumber(35);
//	}
//	
//	private void upgradeTo1_8_34() {
//		ApplicationUtil.executeSql(  "UPDATE ecommerceSettings SET isShowingVolumetricPricesFrontend = true WHERE isShowingVolumetricPricesFrontend IS NULL"  );
//		setPatchVersionNumber(34);
//	}
//	
//	private void upgradeTo1_8_33() {
//		ApplicationUtil.executeSql(  "UPDATE ecommerceSettings SET isEmailAlertAdminOnFrontendOrder = true WHERE isEmailAlertAdminOnFrontendOrder IS NULL"  );
//		setPatchVersionNumber(33);
//	}
//	
//	private void upgradeTo1_8_32() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommerceSettings SET isEmailAlertAdminOnFrontendOrder = isEmailAlertAdminOnOrder WHERE isEmailAlertAdminOnFrontendOrder IS NULL"  );
//		} catch (Exception e) {}
//		setPatchVersionNumber(32);
//	}
//	
//	private void upgradeTo1_8_31() {
//		ApplicationUtil.executeSql(  "UPDATE promotion SET isCodeRequired = true WHERE isCodeRequired IS NULL"  );
//		setPatchVersionNumber(31);
//	}
//	
//	private void upgradeTo1_8_30() {
//		//try {
//			
//			//turn old style (self contained) promotions into new style (constrained) promotions
//			
//			ApplicationUtil.executeSql(  "INSERT INTO promotionConstraint (id, productBrand_id, productInfo_id, productType_id) SELECT id, productBrand_id, productInfo_id, productType_id FROM promotion WHERE productBrand_id IS NOT NULL OR productInfo_id IS NOT NULL OR productType_id IS NOT NULL"  );
//			ApplicationUtil.executeSql(  "INSERT INTO promotion_promotionConstraint (promotion_id, constraints_id) SELECT id, id FROM promotion WHERE productBrand_id IS NOT NULL OR productInfo_id IS NOT NULL OR productType_id IS NOT NULL" );
//			
//		//} catch( Exception ex ) {}
//		setPatchVersionNumber(30);
//	}
//	
//	private void upgradeTo1_8_29() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE transaction SET abandonedEmailSent = 1 WHERE abandonedEmailSent IS NULL"  );
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(29);
//	}
//	
//	private void upgradeTo1_8_28() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET abandonedOrderAlertTimeout = 35 WHERE abandonedOrderAlertTimeout IS NULL"  );
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(28);
//	}
//	
//	private void upgradeTo1_8_27() {
//		//try {
//
//		ApplicationUtil.executeSql(  "UPDATE realizedProductReturn SET returnProductBeforeSerialNumberSet_id = returnProduct_id WHERE returnProductBeforeSerialNumberSet_id IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE serialnumber SET isReassigned = 0 WHERE isReassigned IS NULL"  );
//			//Long displayId = HibernateUtil.getCurrentSession().createSQLQuery("SELECT MAX(displayId) FORM serialnumber WHERE "
//					
//			//set a serial number anywhere it is missing - dont need to worry about setting customer, the next step will do this for us
//			List<RealizedProductReturn> returns = new AqlBeanDao(RealizedProductReturn.class).setWhereCriteria("bean.serialNumber = null").getAll();
//			HibernateUtil.initialiseList(returns, true);
//			for (RealizedProductReturn realizedProductReturn : returns) {
//				SerialNumber serialNumber = new SerialNumber();
//				serialNumber.setReassigned(true);
//				serialNumber.setRealizedProduct(realizedProductReturn.getReturnProductBeforeSerialNumberSet());
//				//displayId++;
//				//serialNumber.setDisplayId(displayId);
//				serialNumber.saveDetails(CommonUtil.getAdminUser(), HibernateUtil.getCurrentSession());
//				realizedProductReturn.setSerialNumber(serialNumber);
//				realizedProductReturn.saveDetails(CommonUtil.getAdminUser(), HibernateUtil.getCurrentSession());
//			}
//			HibernateUtil.getCurrentSession().flush();
//			for (int i = 0, n = returns.size(); i < n; i++ ) {
//				ApplicationUtil.executeSql(  "UPDATE serialNumber sn LEFT OUTER JOIN realizedProductReturn rpr ON sn.id = rpr.serialNumber_id SET sn.realizedProduct_id = rpr.returnProduct_id WHERE sn.id = " + returns.get( i ).getSerialNumber().getId()  );
//			}
//			
//			
//			ApplicationUtil.executeSql(  "UPDATE realizedproductreturn rpr LEFT JOIN serialnumber sn ON sn.id=rpr.serialNumber_id SET sn.currentCustomer_id=rpr.endCustomer_id WHERE rpr.endCustomer_id IS NOT NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE realizedproductreturn rpr LEFT JOIN serialnumber sn ON sn.id=rpr.serialNumber_id SET sn.currentCustomer_id=rpr.cachedOriginalCustomer_id WHERE rpr.cachedOriginalCustomer_id IS NOT NULL AND rpr.endCustomer_id IS NULL"  );
//			//wont work - need to insert a customer history and use that id in the join table and check what positionidx to give it
//			List<Object[]> joinedIds = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT sn.id, rpr.cachedOriginalCustomer_id FROM realizedproductreturn rpr LEFT JOIN serialnumber sn ON sn.id=rpr.serialNumber_id WHERE rpr.cachedOriginalCustomer_id IS NOT NULL AND rpr.endcustomer_id IS NOT NULL" ).list();
//			for (Object[] idSet : joinedIds) {
//				ApplicationUtil.executeSql(  "INSERT INTO customerhistory (active,customer_id,dateCreated) VALUES (1," + ((BigInteger)idSet[1]).longValue() + ",NOW())" );
//				ApplicationUtil.executeSql(  "INSERT INTO serialnumber_customerhistory SELECT " + ((BigInteger)idSet[0]).longValue() + ", MAX(ch.id) FROM customerhistory ch LIMIT 1"  ); 
//				ApplicationUtil.executeSql(  "DELETE FROM serialnumber_customerhistory WHERE customerHistory_id IS NULL" );
//			}
//			
//		//} catch( Exception ex ) {}
//		setPatchVersionNumber(27);
//	}
//	
//	private void upgradeTo1_8_26() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isDeliveryPostcodeRequired = 0 WHERE isDeliveryPostcodeRequired IS NULL"  );
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(26);
//	}
//
//	private void upgradeTo1_8_25() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isEmailAlertAdminOnOrder = 0 WHERE isEmailAlertAdminOnOrder IS NULL"  );
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(25);
//	}
//	
//	private void upgradeTo1_8_24() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE GeneratorMenuCmsAtom SET generatorMenuItemSortOption = 1 WHERE generatorMenuItemSortOption IS NULL"  );
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(24);
//	}
//
//	private void upgradeTo1_8_23() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE productListModule SET productListRoot_id = categoryId, productListRoot_type = 'PRODUCT_BRAND' WHERE categoryId IS NOT NULL and productCategory = 0"  );
//			ApplicationUtil.executeSql(  "UPDATE productListModule SET productListRoot_id = categoryId, productListRoot_type = 'PRODUCT_TYPE' WHERE categoryId IS NOT NULL and productCategory = 1"  );
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(23);
//	}
//
//	private void upgradeTo1_8_22() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE shippingService SET isShowingOnWebsite = true"  );
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(22);
//	}
//
//	private void upgradeTo1_8_21() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE cmsPlaceholderContent SET content = REPLACE( content, 'FavouriteProductTypesCmsAtom', 'FavouriteProductListsCmsAtom' )"  );
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(21);
//	}
//	
//	private void upgradeTo1_8_20() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommerceConfiguration SET notRequiredShippingService_id = NULL, customShippingService_id = NULL, customersShippingService_id = NULL"  );
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(20);
//	}
//	
//	private void upgradeTo1_8_19() {
//		try {
//			StringBuffer strBuf = new StringBuffer( "UPDATE shoppingcart sc " );
//			strBuf.append( "LEFT OUTER JOIN availableShippingService ass ON sc.availableShippingService_id = ass.id " );
//			strBuf.append( "SET sc.additionalShippingCost = ass.additionalShippingCost, sc.cachedCost = ass.cachedCost " );
//			strBuf.append( ",sc.cachedServiceName = ass.cachedServiceName, sc.additionalShippingOption_id = ass.additionalShippingOption_id " );
//			strBuf.append( ",sc.courierService_id = ass.courierService_id, sc.shippingService_id = ass.shippingService_id" );
//			HibernateUtil.getCurrentSession().createSQLQuery( strBuf.toString() ).list();
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(19);
//	}
//	
//	private void upgradeTo1_8_18() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE favouriteproductlistscmsatom SET cmsAtomIdStr = 'favouriteProductLists' WHERE cmsAtomIdStr = 'favouriteProductTypes'"  );
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(18);
//	}
//	
//	private void upgradeTo1_8_17() {
//		StringBuffer strBuf = new StringBuffer();
//		try {
//			strBuf.append( "INSERT INTO favouriteproductlistscmsatom" );
//			strBuf.append( "(`id`, `active`, `deletable`, `editable`, `persistentData`," );
//			strBuf.append( "`dateCreated`, `dateInactivated`, `dateLastModified`, `displayId`," );
//			strBuf.append( "`userIdCreated`, `userIdInactivated`, `userIdLastModified`, `name`," );
//			strBuf.append( "`sslProtocolEnum`, `cmsAtomIdStr`, `owner_id`, `parentWebsite_id`) " );
//			strBuf.append( "SELECT `id`, `active`, `deletable`, `editable`, `persistentData`, " );
//			strBuf.append( "`dateCreated`, `dateInactivated`, `dateLastModified`, `displayId`, " );
//			strBuf.append( "`userIdCreated`, `userIdInactivated`, `userIdLastModified`, `name`," );
//			strBuf.append( "`sslProtocolEnum`, `cmsAtomIdStr`, `owner_id`, `parentWebsite_id` FROM favouriteproducttypescmsatom" );
//			ApplicationUtil.executeSql(  strBuf.toString()  );
//		} catch( Exception ex ) {
//			strBuf = new StringBuffer();
//			strBuf.append( "INSERT INTO favouriteproductlistscmsatom" );
//			strBuf.append( "(`id`, `active`, `deletable`, `editable`, `persistentData`," );
//			strBuf.append( "`dateCreated`, `dateInactivated`, `dateLastModified`, `displayId`," );
//			strBuf.append( "`userIdCreated`, `userIdInactivated`, `userIdLastModified`, `name`," );
//			strBuf.append( "`cmsAtomIdStr`, `owner_id`, `parentWebsite_id`) " );
//			strBuf.append( "SELECT `id`, `active`, `deletable`, `editable`, `persistentData`, " );
//			strBuf.append( "`dateCreated`, `dateInactivated`, `dateLastModified`, `displayId`, " );
//			strBuf.append( "`userIdCreated`, `userIdInactivated`, `userIdLastModified`, `name`," );
//			strBuf.append( "`cmsAtomIdStr`, `owner_id`, `parentWebsite_id` FROM favouriteproducttypescmsatom" );
//			ApplicationUtil.executeSql(  strBuf.toString()  );
//		}
//
//		strBuf = new StringBuffer();
//		strBuf.append( "INSERT INTO favouriteproductlist (`id`, `active`, `deletable`, " );
//		strBuf.append( "`editable`, `persistentData`, `dateCreated`, `dateInactivated`, " );
//		strBuf.append( "`dateLastModified`, `displayId`, `userIdCreated`, `userIdInactivated`," );
//		strBuf.append( "`userIdLastModified`, `generatorMenuItem_type`, `generatorMenuItem_id`," );
//		strBuf.append( "`positionIdx`, `owner_id`) SELECT `id`, `active`, `deletable`, `editable`," );
//		strBuf.append( "`persistentData`, `dateCreated`, `dateInactivated`, `dateLastModified`," );
//		strBuf.append( "`displayId`, `userIdCreated`, `userIdInactivated`, `userIdLastModified`," );
//		strBuf.append( "'PRODUCT_TYPE', `producttype_id`, `positionIdx`, `owner_id` FROM favouriteproducttype" );
//		ApplicationUtil.executeSql(  strBuf.toString()  );
//
//		strBuf = new StringBuffer();
//		strBuf.append( "INSERT INTO favouriteproductlistscmsatom_favouriteproductlist" );
//		strBuf.append( "(`FavouriteProductListsCmsAtom_id`, `favouriteProductLists_id`) " );
//		strBuf.append( "SELECT `FavouriteProductTypesCmsAtom_id`, `favouriteProductTypes_id` FROM favouriteproducttypescmsatom_favouriteproducttype" );
//		ApplicationUtil.executeSql(  strBuf.toString()  );
//		setPatchVersionNumber(17);
//	}
//	
//	private void upgradeTo1_8_16() {
//		try { 
//		
//			//remove abstract/dead backing page classes (which have no SerialUID (and shouldnt))
//			ApplicationUtil.executeSql(  "DELETE FROM " + AplosBean.getTableName( TabClass.class ) + " WHERE backingPageClass = 'com.aplos.ecommerce.backingpage.EcommercePageRevisionListPage' OR backingPageClass LIKE 'com.aplos.ecommerce.backingpage.order.%'" );
//		
//		} catch( Exception ex ) {}
//		setPatchVersionNumber(16);
//	}
//	
//	private void upgradeTo1_8_15() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE additionalShippingOption SET postalZone_id = royalMailZone_id"  );
//		} catch (Exception ex) {}
//		setPatchVersionNumber(15);
//	}
//	
//	private void upgradeTo1_8_13() {
//		try {
//			StringBuffer strBuff = new StringBuffer();
//			strBuff.append( "INSERT INTO postalzoneinternationalshippingservice " );
//			strBuff.append( "(`extra20gCost`, `maxWeight`, `noticePercentage`, `tariff`, " );
//			strBuff.append( "`threshold`, `id`, `postalZone_id`) SELECT `extra20gCost`," );
//			strBuff.append( "`maxWeight`, 0, `tariff`, 0, `id`, `royalMailzone_id` FROM royalmailinternationalshippingservice" );
//			ApplicationUtil.executeSql(  strBuff.toString()  );
//		} catch( Exception ex ) {
//			// field name probably doesn't exist
//		}
//		setPatchVersionNumber(13);
//	}
//	
//	private void upgradeTo1_8_12() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE courierservice SET shippingServiceClass = 'com.aplos.ecommerce.beans.couriers.PostalZoneInternationalShippingService' WHERE shippingServiceClass = 'com.aplos.ecommerce.beans.couriers.RoyalMailInternationalShippingService'"  );
//		} catch (Exception ex) {}
//		setPatchVersionNumber(12);
//	}
//
//	private void upgradeTo1_8_11() {
//		//Migration from RoyalMailZone (Common) to PostageZone (Ecommerce)
//		try {
//			
//			ApplicationUtil.executeSql(  "ALTER TABLE postalzone_country ADD PRIMARY KEY (`countries_id`, `PostalZone_id`), DROP INDEX `countries_id`" );
//			ApplicationUtil.executeSql(  "UPDATE royalmailzone SET name = REPLACE(name, 'Inland', 'Mainland')"  );
//			ApplicationUtil.executeSql(  "UPDATE royalmailzone SET name = REPLACE(name, 'Northern Europe', 'Scandinavia')"  );
//			ApplicationUtil.executeSql(  "UPDATE royalmailzone SET name = REPLACE(name, 'USA', 'USA and Canada')"  ); //america - continent
//			ApplicationUtil.executeSql(  "UPDATE royalmailzone SET name = REPLACE(name, 'western europe', 'Western Europe')"  ); //america - continent
//			ApplicationUtil.executeSql(  "INSERT INTO postalzone (active,dateCreated,userIdCreated,dateLastModified,userIdLastModified,isVisibleAsACountry,name,primaryCountry_id) SELECT active,dateCreated,userIdCreated,dateLastModified,userIdLastModified,0,name,null FROM royalmailzone"  );
//			ApplicationUtil.executeSql(  "INSERT INTO postalzone_country (PostalZone_id, countries_id) SELECT zone.id, ctry.id FROM country ctry LEFT JOIN royalmailzone rmz ON ctry.royalMailZone_id=rmz.id LEFT JOIN postalzone zone ON zone.name=rmz.name WHERE ctry.royalMailZone_id IS NOT NULL"  );
//			BigInteger unitedStatesId = (BigInteger) HibernateUtil.getCurrentSession().createSQLQuery("SELECT id FROM country WHERE name = 'United States'").uniqueResult();
//			if (unitedStatesId != null) {
//				ApplicationUtil.executeSql(  "INSERT INTO postalzone_country (PostalZone_id, countries_id) SELECT id," + unitedStatesId.intValue() + " FROM postalzone WHERE name='USA and Canada'" );
//			}
//			
//		} catch (Exception ex) {
//			
//			ex.printStackTrace();
//			
//		}
//		setPatchVersionNumber(11);
//	}
//	
//	private void upgradeTo1_8_10() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommerceCmsPageRevisions SET newsCpr_id = (SELECT cpr.id FROM cmsPageRevision cpr LEFT JOIN cmspagerevision_cmsatomlist cal ON cal.CmsPageRevision_id=cpr.id WHERE cpr.active=1 AND cal.cmsAtom_type='NEWS_MODULE' LIMIT 1) WHERE newsCpr_id IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE ecommerceCmsPageRevisions SET newsletterSignUpCpr_id = (SELECT cpr.id FROM cmsPageRevision cpr LEFT JOIN cmspagerevision_cmsatomlist cal ON cal.CmsPageRevision_id=cpr.id LEFT JOIN unconfigurabledevelopercmsatom udc ON udc.id = cal.cmsAtom_id WHERE cpr.active=1 AND udc.cmsAtomIdStr='newsletterSignup' LIMIT 1) WHERE newsletterSignUpCpr_id IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE ecommerceCmsPageRevisions SET emailAFriendCpr_id = (SELECT cpr.id FROM cmsPageRevision cpr LEFT JOIN cmspagerevision_cmsatomlist cal ON cal.CmsPageRevision_id=cpr.id LEFT JOIN unconfigurabledevelopercmsatom udc ON udc.id=cal.cmsAtom_id WHERE cpr.active=1 AND udc.cmsAtomIdStr='emailAFriend' LIMIT 1) WHERE emailAFriendCpr_id IS NULL"  );
//		} catch (Exception ex) {}
//		setPatchVersionNumber(10);
//	}
//
//	private void upgradeTo1_8_9() {
//		List<CourierService> courierServiceList = new AqlBeanDao( CourierService.class ).addWhereCriteria( "bean.address IS NULL" ).getAll();
//		for( int i = 0, n = courierServiceList.size(); i < n; i++ ) {
//			Address address = new Address();
//			address.saveDetails();
//			courierServiceList.get( i ).setAddress( address );
//			courierServiceList.get( i ).saveDetails();
//		}
//		setPatchVersionNumber(9);
//	}
//
//	private void upgradeTo1_8_8() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE productListModule SET multiType = 0 WHERE multiType IS NULL"  );
//		} catch (Exception ex) {}
//		setPatchVersionNumber(8);
//	}
//
//	private void upgradeTo1_8_7() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE serviceBookingModule SET additionalBookingsIncludeDate = 0 WHERE additionalBookingsIncludeDate IS NULL"  );
//		} catch (Exception ex) {}
//		setPatchVersionNumber(7);
//	}
//
//	private void upgradeTo1_8_6() {
//		Object maxPositionIdx = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT max(positionIdx) FROM paymentmethod" ).uniqueResult();
//		if( maxPositionIdx == null ) {
//			int idx = 0;
//			List<PaymentMethod> paymentMethodList = new AqlBeanDao( PaymentMethod.class ).getAll();
//			for( PaymentMethod paymentMethod : paymentMethodList ) {
//				paymentMethod.setPositionIdx( idx++ );
//				paymentMethod.saveDetails();
//			}
//		}
//		setPatchVersionNumber(6);
//	}
//
//	private void upgradeTo1_8_5() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE serviceBookingModule SET automaticallyCreateProvider = 0 WHERE automaticallyCreateProvider IS NULL"  );
//		} catch (Exception ex) {}
//		setPatchVersionNumber(5);
//	}
//
//	private void upgradeTo1_8_4() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE availableShippingService SET additionalShippingCost = 0 WHERE additionalShippingCost IS NULL"  );
//		} catch (Exception ex) {}
//		setPatchVersionNumber(4);
//	}
//
//	private void upgradeTo1_8_3() {
//		ApplicationUtil.executeSql(  "UPDATE commonConfiguration SET defaultVatType_id = ukVatType_id WHERE defaultVatType_id IS NULL"  );
//		try {
//			ApplicationUtil.executeSql(  "UPDATE realizedproduct SET vatType_id = (SELECT defaultVatType_id FROM CommonConfiguration)"  );
//		} catch (Exception ex) {}
//		setPatchVersionNumber(3);
//	}
//
//	private void upgradeTo1_8_2() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE commonConfiguration SET isVatInclusive=(select ecommerceSettings.isVatInclusive FROM ecommerceSettings limit 1) WHERE commonConfiguration.isVatInclusive IS NULL"  );
//		} catch (Exception ex) {}
//		setPatchVersionNumber(2);
//	}
//
//	private void upgradeTo1_8_1() {
//		ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isTimeTilAvailabilityAutoDecrement=1 WHERE isTimeTilAvailabilityAutoDecrement IS NULL"  );
//		setPatchVersionNumber(1);
//	}
//
//	private void upgradeTo1_8_0() {
//		//project in maven
//		setMinorVersionNumber(8);
//		setPatchVersionNumber(0);
//	}
//
//	private void upgradeTo1_7_52() {
//		ApplicationUtil.executeSql(  "UPDATE competition SET winningEmailsSent=0 WHERE winningEmailsSent IS NULL"  );
//		ApplicationUtil.executeSql(  "UPDATE competition SET consulationEmailsSent=0 WHERE consulationEmailsSent IS NULL"  );
//		ApplicationUtil.executeSql(  "UPDATE competition SET nonClaimantEmailsSent=0 WHERE nonClaimantEmailsSent IS NULL"  );
//		setPatchVersionNumber(52);
//	}
//
//	private void upgradeTo1_7_51() {
//		//update imported addresses to correctly use the address states for canada and the us
//		AqlBeanDao areaDao = new AqlBeanDao(CountryArea.class);
//		areaDao.addWhereCriteria("bean.country.id = 124");
//		List<CountryArea> canadianAreas = areaDao.getAll();
//		AqlBeanDao addressDao = new AqlBeanDao(Address.class);
//		addressDao.addWhereCriteria("bean.country.id = 124");
//		List<Address> canadianAddresses = addressDao.getAll();
//		for (Address address : canadianAddresses) {
//			String stateReplaced = null;
//			for (CountryArea countryArea : canadianAreas) {
//				if (address.getCity() != null &&
//						(address.getCity().toUpperCase().trim().equals(countryArea.getName().toUpperCase()) ||
//						(countryArea.getAreaCode() != null &&
//						address.getCity().toUpperCase().trim().equals(countryArea.getAreaCode().toUpperCase())))) {
//					address.setCity(null);
//					stateReplaced=address.getState();
//					address.setState(countryArea.getAreaCode());
//					address.setCountryArea(countryArea);
//					address.saveDetails();
//					break;
//				} else if (address.getLine3() != null &&
//						(address.getLine3().toUpperCase().trim().equals(countryArea.getName().toUpperCase()) ||
//								(countryArea.getAreaCode() != null &&
//								address.getLine3().toUpperCase().trim().equals(countryArea.getAreaCode().toUpperCase())))) {
//					address.setLine3(null);
//					stateReplaced=address.getState();
//					address.setState(countryArea.getAreaCode());
//					address.setCountryArea(countryArea);
//					address.saveDetails();
//					break;
//				} else if (address.getState() != null &&
//						(address.getState().toUpperCase().trim().equals(countryArea.getName().toUpperCase()) ||
//						(countryArea.getAreaCode() != null &&
//						address.getState().toUpperCase().trim().equals(countryArea.getAreaCode().toUpperCase())))) {
//					stateReplaced=address.getState();
//					address.setState(countryArea.getAreaCode());
//					address.setCountryArea(countryArea);
//					address.saveDetails();
//					break;
//				}
//			}
//			if (stateReplaced == null && address.getState() != null && !address.getState().equals("")) {
//				stateReplaced=address.getState();
//				address.setState(null);
//				address.saveDetails();
//			}
//			if (address.getCountryArea() != null && (address.getState() == null || address.getState().equals(""))) {
//				address.setState(address.getCountryArea().getAreaCode());
//			}
//			if (stateReplaced != null && !stateReplaced.equals("")) {
//				//create a note on the company/customer as the interface will hide the 'state' text field
//				AqlBeanDao customerDao = new AqlBeanDao(Customer.class);
//				customerDao.addWhereCriteria("(bean.billingAddress.id = " + address.getId() + " OR bean.shippingAddress.id = " + address.getId() + " OR bean.altShippingAddress.id = " + address.getId() + ")");
//				Customer customer = customerDao.getFirstResult();
//				if (customer != null) {
//					customer.addCustomerNote("System Note: Text entry for state (before switching to a state dropdown), used to read: '" + stateReplaced + "'");
//					customer.saveDetails();
//					address.setState(null);
//					address.saveDetails();
//				} else {
//					AqlBeanDao companyDao = new AqlBeanDao(Company.class);
//					companyDao.addWhereCriteria("(bean.billingAddress.id = " + address.getId() + " OR bean.shippingAddress.id = " + address.getId() + " OR bean.altShippingAddress.id = " + address.getId() + ")");
//					Company company = companyDao.getFirstResult();
//					if (company != null) {
//						company.addNote("System Note: Text entry for state (before switching to a state dropdown), used to read: '" + stateReplaced + "'");
//						company.saveDetails();
//						address.setState(null);
//						address.saveDetails();
//					}
//				}
//			}
//		}
//
//		//united states checks
//		AqlBeanDao usAreaDao = new AqlBeanDao(CountryArea.class);
//		usAreaDao.addWhereCriteria("bean.country.id = 840");
//		List<CountryArea> usAreas = usAreaDao.getAll();
//		AqlBeanDao usAddressDao = new AqlBeanDao(Address.class);
//		usAddressDao.addWhereCriteria("bean.country.id = 840");
//		List<Address> usAddresses = usAddressDao.getAll();
//		for (Address address : usAddresses) {
//			String stateReplaced = null;
//			for (CountryArea countryArea : usAreas) {
//				if (address.getCity() != null &&
//						(address.getCity().toUpperCase().trim().equals(countryArea.getName().toUpperCase()) ||
//						(countryArea.getAreaCode() != null &&
//						address.getCity().toUpperCase().trim().equals(countryArea.getAreaCode().toUpperCase())))) {
//					address.setCity(null);
//					stateReplaced=address.getState();
//					address.setState(countryArea.getAreaCode());
//					address.setCountryArea(countryArea);
//					address.saveDetails();
//					break;
//				} else if (address.getLine3() != null &&
//						(address.getLine3().toUpperCase().trim().equals(countryArea.getName().toUpperCase()) ||
//						(countryArea.getAreaCode() != null &&
//						address.getLine3().toUpperCase().trim().equals(countryArea.getAreaCode().toUpperCase())))) {
//					address.setLine3(null);
//					stateReplaced=address.getState();
//					address.setState(countryArea.getAreaCode());
//					address.setCountryArea(countryArea);
//					address.saveDetails();
//					break;
//				} else if (address.getState() != null &&
//						(address.getState().toUpperCase().trim().equals(countryArea.getName().toUpperCase()) ||
//						(countryArea.getAreaCode() != null &&
//						address.getState().toUpperCase().trim().equals(countryArea.getAreaCode().toUpperCase())))) {
//					stateReplaced=address.getState();
//					address.setState(countryArea.getAreaCode());
//					address.setCountryArea(countryArea);
//					address.saveDetails();
//					break;
//				}
//			}
//			if (stateReplaced == null && address.getState() != null && !address.getState().equals("")) {
//				stateReplaced=address.getState();
//				address.setState(null);
//				address.saveDetails();
//			}
//			if (address.getCountryArea() != null && (address.getState() == null || address.getState().equals(""))) {
//				address.setState(address.getCountryArea().getAreaCode());
//			}
//			if (stateReplaced != null && !stateReplaced.equals("")) {
//				//create a note on the company/customer as the interface will hide the 'state' text field
//				AqlBeanDao customerDao = new AqlBeanDao(Customer.class);
//				customerDao.addWhereCriteria("(bean.billingAddress.id = " + address.getId() + " OR bean.shippingAddress.id = " + address.getId() + " OR bean.altShippingAddress.id = " + address.getId() + ")");
//				Customer customer = customerDao.getFirstResult();
//				if (customer != null) {
//					customer.addCustomerNote("System Note: Text entry for state (before switching to a state dropdown), used to read: '" + stateReplaced + "'");
//					customer.saveDetails();
//					address.setState(null);
//					address.saveDetails();
//				} else {
//					AqlBeanDao companyDao = new AqlBeanDao(Company.class);
//					companyDao.addWhereCriteria("(bean.billingAddress.id = " + address.getId() + " OR bean.shippingAddress.id = " + address.getId() + " OR bean.altShippingAddress.id = " + address.getId() + ")");
//					Company company = companyDao.getFirstResult();
//					if (company != null) {
//						company.addNote("System Note: Text entry for state (before switching to a state dropdown), used to read: '" + stateReplaced + "'");
//						company.saveDetails();
//						address.setState(null);
//						address.saveDetails();
//					}
//				}
//			}
//		}
//
//		setPatchVersionNumber(51);
//	}
//
//	private void upgradeTo1_7_50() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE promotion SET freePostage=0 WHERE freePostage IS NULL"  );
//		} catch( Exception ex ) {
//			// field name probably doesn't exist.
//		}
//		setPatchVersionNumber(50);
//	}
//
//	private void upgradeTo1_7_49() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isSendRegistrationEmails=0 WHERE isSendRegistrationEmails IS NULL"  );
//		} catch( Exception ex ) {
//			// field name probably doesn't exist.
//		}
//		setPatchVersionNumber(49);
//	}
//
//	private void upgradeTo1_7_48() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE transaction SET firstInvoicedDate=invoiceFirstPrintedDate WHERE firstInvoicedDate IS NULL"  );
//		} catch( Exception ex ) {
//			// field name probably doesn't exist.
//		}
//		setPatchVersionNumber(48);
//	}
//
//	private void upgradeTo1_7_47() {
//		ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET enticementValidForDays=15 WHERE enticementValidForDays IS NULL"  );
//		setPatchVersionNumber(47);
//	}
//
//	private void upgradeTo1_7_46() {
//		ApplicationUtil.executeSql(  "UPDATE promotion SET oneUsePerCustomer=false WHERE oneUsePerCustomer IS NULL"  );
//		setPatchVersionNumber(46);
//	}
//
//	private void upgradeTo1_7_45() {
//		ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isUsingCategoriesInProductUrls=false WHERE isUsingCategoriesInProductUrls IS NULL"  );
//		setPatchVersionNumber(45);
//	}
//
//	private void upgradeTo1_7_44() {
//		//moved to cms
//		//ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET paginationItemsPerPage=15 WHERE paginationItemsPerPage IS NULL"  );
//		setPatchVersionNumber(44);
//	}
//
//	private void upgradeTo1_7_43() {
//		ApplicationUtil.executeSql(  "UPDATE producttypemenucmsatom SET ecommercePageGenerator_id=cmsPageGenerator_id WHERE ecommercePageGenerator_id IS NULL AND cmsPageGenerator_id IS NOT NULL"  );
//		setPatchVersionNumber(43);
//	}
//
//	private void upgradeTo1_7_42() {
//		if( ((BigInteger) HibernateUtil.getCurrentSession().createSQLQuery( "SELECT COUNT(*) FROM Country WHERE id > 10000" ).uniqueResult()).intValue() == 0 ) {
//			ApplicationUtil.executeSql(  "INSERT INTO country (id, active, deletable, iso2, iso3, name) VALUES (10001, true , true, 'GB', 'GBR', 'Northern Island')"  );
//			ApplicationUtil.executeSql(  "INSERT INTO country (id, active, deletable, iso2, iso3, name) VALUES (10002, true , true, 'GB', 'GBR', 'England')"  );
//			ApplicationUtil.executeSql(  "INSERT INTO country (id, active, deletable, iso2, iso3, name) VALUES (10003, true , true, 'ES', 'ESP', 'Balearic Islands')"  );
//			ApplicationUtil.executeSql(  "INSERT INTO country (id, active, deletable, iso2, iso3, name) VALUES (10004, true , true, 'ES', 'ESP', 'Canary Islands')"  );
//			ApplicationUtil.executeSql(  "INSERT INTO country (id, active, deletable, iso2, iso3, name) VALUES (10005, true , true, 'PT', 'PTR', 'Madeira Islands')"  );
//			ApplicationUtil.executeSql(  "INSERT INTO country (id, active, deletable, iso2, iso3, name) VALUES (10006, true , true, 'GB', 'GBR', 'Scotland')"  );
//			ApplicationUtil.executeSql(  "INSERT INTO country (id, active, deletable, iso2, iso3, name) VALUES (10007, true , true, 'GB', 'GBR', 'Wales')"  );
//			ApplicationUtil.executeSql(  "INSERT INTO country (id, active, deletable, iso2, iso3, name) VALUES (10008, true , true, 'ES', 'ESP', 'Tenerife')"  );
//		}
//		setPatchVersionNumber(42);
//	}
//
//	private void upgradeTo1_7_41() {
//		ApplicationUtil.executeSql(  "UPDATE cmspage SET DTYPE='EcommercePageGenerator' WHERE DTYPE='CmsPageGenerator'"  );
//		setPatchVersionNumber(41);
//	}
//
//	private void upgradeTo1_7_40() {
//		ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isAlwaysChargedInDefaultCurrency=0 WHERE isAlwaysChargedInDefaultCurrency IS NULL"  );
//		setPatchVersionNumber(40);
//	}
//
//	private void upgradeTo1_7_39() {
//		ApplicationUtil.executeSql(  "UPDATE productInfo SET lowStockThreshold=4 WHERE lowStockThreshold IS NULL"  );
//		setPatchVersionNumber(39);
//	}
//
//	private void upgradeTo1_7_38() {
//		ApplicationUtil.executeSql(  "UPDATE friendReferral SET isReferralPayoutPercentage=0 WHERE isReferralPayoutPercentage IS NULL"  );
//		setPatchVersionNumber(38);
//	}
//
//	private void upgradeTo1_7_37() {
//		ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isProductListGroupByColour=true WHERE isProductListGroupByColour IS NULL"  );
//		setPatchVersionNumber(37);
//	}
//
//	private void upgradeTo1_7_36() {
//		ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET freeShippingNoticePercentage=75 WHERE freeShippingNoticePercentage IS NULL"  );
//		setPatchVersionNumber(36);
//	}
//
//	private void upgradeTo1_7_35() {
//		ApplicationUtil.executeSql(  "UPDATE productType SET productSizeType_id=(SELECT productSizeType_id FROM productSize WHERE id=(SELECT standardProductSize_id FROM ecommerceConfiguration LIMIT 1)) WHERE productType.id=(SELECT giftVoucherProductType_id FROM ecommerceConfiguration LIMIT 1)"  );
//		ApplicationUtil.executeSql(  "DELETE FROM productSizeType WHERE name = 'Gift Vouchers' AND editable=0"  );
//		setPatchVersionNumber(35);
//	}
//
//	private void upgradeTo1_7_34() {
//		ApplicationUtil.executeSql(  "UPDATE productsizecategory SET productsizecategory.productSizeType_id=(SELECT productSizeType_id FROM productsizetype_productsizecategory WHERE productSizeCategories_id=productsizecategory.id) WHERE productsizecategory.productSizeType_id IS NULL"  );
//		AqlBeanDao dao = new AqlBeanDao(ProductSizeCategory.class);
//		dao.setWhereCriteria("bean.positionIdx=null");
//		dao.addWhereCriteria("bean.productSizeType != null");
//		List<ProductSizeCategory> categories = dao.setIsReturningActiveBeans(true).getAll();
//		for (ProductSizeCategory cat : categories) {
//			if (cat != null) {
//				Integer maxPosition = (Integer) HibernateUtil.getCurrentSession().createSQLQuery("SELECT MAX(positionIdx) FROM productsizecategory WHERE productSizeType_id=" + cat.getProductSizeType().getId()).uniqueResult();
//				if (maxPosition == null) {
//					maxPosition = 0;
//				}
//				cat.setPositionIdx(maxPosition);
//				cat.setOldPositionIdx(cat.getPositionIdx());
//				cat.saveDetails();
//			}
//		}
//		//ApplicationUtil.executeSql(  "UPDATE productsizecategory SET positionIdx=(SELECT MAX(positionIdx) FROM productSizeCategory others WHERE others.productSizeType_id=productSizeType_id) WHERE positionIdx IS NULL"  );
//		ApplicationUtil.executeSql(  "UPDATE productsizecategory SET active=0 WHERE productsizecategory.productSizeType_id IS NULL"  );
//		setPatchVersionNumber(34);
//	}
//
//	private void upgradeTo1_7_33() {
//		ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isKitItemsFixed=true WHERE isKitItemsFixed IS NULL"  );
//		setPatchVersionNumber(33);
//	}
//
//	private void upgradeTo1_7_32() {
//		ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isUsingStoreCredit=false WHERE isUsingStoreCredit IS NULL"  );
//		setPatchVersionNumber(32);
//	}
//
//	private void upgradeTo1_7_31() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE transaction SET isUsingStoredCreditCardDetails=false WHERE isUsingStoredCreditCardDetails IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(31);
//	}
//
//	private void upgradeTo1_7_30() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isUpdatingCustomerAddressesFromTransaction=false WHERE isUpdatingCustomerAddressesFromTransaction IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(30);
//	}
//
//	private void upgradeTo1_7_29() {
//		//this update fixes a mistake from #28
//		try {
//			ApplicationUtil.executeSql(  "UPDATE productType SET mapping=LOWER(replace(mapping,'-_-','-or-'))"  );
//			ApplicationUtil.executeSql(  "UPDATE productBrand SET mapping=LOWER(replace(mapping,'-_-','-or-'))"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(29);
//	}
//
//	private void upgradeTo1_7_28() {
//		//Added automatic mapping generation back to product type and brand, i'm sure i did this previously but it was missing
//		//replaces everything that makeSafeUrlMapping does except the following because it seems reasonable they wouldnt have made it into the db
////		 .replace( "\"", "" )
////		  .replace( "'", "" )
////		  .replace( "`", "")
//		try {
//			ApplicationUtil.executeSql(  "UPDATE productType SET mapping=replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(name,'/','_'),'?',''),':',''),'#',''),'.',''),'!',''),'£',''),'$',''),'&','and'),'%26','and'),'%2b','-'),'+','-'),' ','-') WHERE mapping IS NULL OR mapping=''"  );
//			ApplicationUtil.executeSql(  "UPDATE productBrand SET mapping=replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(name,'/','_'),'?',''),':',''),'#',''),'.',''),'!',''),'£',''),'$',''),'&','and'),'%26','and'),'%2b','-'),'+','-'),' ','-') WHERE mapping IS NULL OR mapping=''"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(28);
//	}
//
//	private void upgradeTo1_7_27() {
//		try {
//			//ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isUsingShopz illa=0 WHERE isUsingShopz illa IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(27);
//	}
//
//	private void upgradeTo1_7_26() {
//		// Add isKitItem to ProductInfo
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ProductInfo SET isKitItem=0 WHERE isKitItem IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(26);
//	}
//
//	private void upgradeTo1_7_25() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET repeatCustomEnticementPromotionName='Repeat Custom Enticement' WHERE repeatCustomEnticementPromotionName IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(25);
//	}
//
//	private void upgradeTo1_7_24() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET repeatCustomEnticementValidForDays=14 WHERE repeatCustomEnticementValidForDays IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(24);
//	}
//
//
//	private void upgradeTo1_7_23() {
//		// Implementation of Customer account credit
//		try {
//			ApplicationUtil.executeSql(  "UPDATE RealizedProduct SET isDiscountAllowed=1 WHERE isDiscountAllowed IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(23);
//	}
//
//	private void upgradeTo1_7_22() {
//		// Implementation of Customer account credit
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ShoppingCart SET storeCreditUsed=0 WHERE storeCreditUsed IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(22);
//	}
//
//	private void upgradeTo1_7_21() {
//		//implementation of 'New customer count down offers' ie repeat custom enticements
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isUsingRepeatCustomEnticements=0,isUsingIndividualCustomEnticements=1,repeatCustomEnticementPercentage='10.00',isRepeatCustomEnticementForNewCustomersOnly=1 WHERE isUsingRepeatCustomEnticements IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(21);
//	}
//
//	private void upgradeTo1_7_20() {
//		// Add Gift Voucher Product Type
//		// Add Gift Voucher Issued Email
//		try {
//			ApplicationUtil.executeSql(  "INSERT INTO producttype (active,deletable,name,editable) VALUES (1,0,'Gift Vouchers',0)"  );
//			ApplicationUtil.executeSql(  "UPDATE ecommerceconfiguration SET giftVoucherProductType_id=(SELECT id FROM producttype WHERE name='Gift Vouchers' LIMIT 1) WHERE giftVoucherProductType_id IS NULL"  );
//			String emailContent="<table width=\"670\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">	<tr>	  <td>	  	<p>Hello, {RECIPIENT_NAME},</p><br />	  	<p>Exciting news - {BUYER_NAME} has sent you a gift voucher to redeem at {COMPANY_NAME}!</p>	  	<ul style=\"list-style:none\">	  		<!-- BEGIN DYNAMIC : voucherList -->	  			<li>{VOUCHER_VALUE_REPEATED}</li>	  		<!-- END DYNAMIC : voucherList -->	  	</ul>	  	<p>Congratulations and we hope you enjoy your purchase(s). To get started, redeem your voucher by visiting</p>	  	<p><a href=\"{REDEEM_URL}\">{REDEEM_URL}</a></p><br/>	  	<p>Enjoy!</p>	  	<p>- The {COMPANY_NAME} staff</p>	</td>  </tr></table>";
//			ApplicationUtil.executeSql(  "INSERT INTO emailtemplate (active,deletable,editable,DTYPE,content,name,subject) VALUES (1,0,1,'GiftVoucherIssuedEmail','" + emailContent + "','Gift Voucher Issued','{COMPANY_NAME} gift vouchers from {BUYER_NAME}')"  );
//			ApplicationUtil.executeSql(  "UPDATE ecommerceconfiguration SET giftVoucherIssuedEmailTemplate_id=(SELECT id FROM emailtemplate WHERE name='{COMPANY_NAME} gift vouchers from {BUYER_NAME}' LIMIT 1) WHERE giftVoucherIssuedEmailTemplate_id IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(20);
//	}
//
//	private void upgradeTo1_7_19() {
//		// Implementation of Promotions - Add column to cart
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET cartShowingDiscountColumn=0 WHERE cartShowingDiscountColumn IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(19);
//	}
//
//	private void upgradeTo1_7_18() {
//		// Implementation of Customer account credit
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ShoppingCart SET adminCharge=0 WHERE adminCharge IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(18);
//	}
//
//	private void upgradeTo1_7_17() {
//		// Implementation of Customer account credit
//		try {
//			ApplicationUtil.executeSql(  "UPDATE Product SET isShowingOnWebsite=true WHERE isShowingOnWebsite IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(17);
//	}
//
//	private void upgradeTo1_7_16() {
//		// Implementation of Customer account credit
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ShoppingCart SET otherStoreCreditUsed=0 WHERE otherStoreCreditUsed IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE ShoppingCart SET cachedTotalCreditUsedAmount=0 WHERE cachedTotalCreditUsedAmount IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE ShoppingCart SET cachedGiftVoucherCreditUsed=0 WHERE cachedGiftVoucherCreditUsed IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE ShoppingCart SET cachedOtherStoreCreditUsed=0 WHERE cachedOtherStoreCreditUsed IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(16);
//	}
//
//	private void upgradeTo1_7_15() {
//		// Implementation of Customer account credit
//		try {
//			ApplicationUtil.executeSql(  "UPDATE Transaction SET isGiftDeliveryAddressTheSame=false WHERE isGiftDeliveryAddressTheSame IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE Transaction SET giftsSentViaEmail=false WHERE giftsSentViaEmail IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(15);
//	}
//
//	private void upgradeTo1_7_14() {
//		// Implementation of Customer account credit
//		try {
//			ApplicationUtil.executeSql(  "UPDATE " + AplosBean.getTableName(Customer.class) + " SET storeCreditAmount=0 WHERE storeCreditAmount IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(14);
//	}
//
//	private void upgradeTo1_7_13() {
//		// Implementation of Gift Vouchers
//		try {
//			///* Removed the boolean */ ApplicationUtil.executeSql(  "UPDATE " + AplosBean.getTableName(RealizedProduct.class) + " SET isGiftVoucher=0 WHERE isGiftVoucher IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE " + AplosBean.getTableName(ShoppingCartItem.class) + " SET isGiftItem=0 WHERE isGiftItem IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE " + AplosBean.getTableName(EcommerceSettings.class) + " SET isAllowGiftItems=0 WHERE isAllowGiftItems IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE " + AplosBean.getTableName(EcommerceSettings.class) + " SET isAllowGiftVouchers=0 WHERE isAllowGiftVouchers IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(13);
//	}
//
//	private void upgradeTo1_7_12() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET cartShowingItemCodes=1,cartShowingImages=1,cartShowingSizes=1,cartShowingColours=1 WHERE cartShowingItemCodes IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(12);
//	}
//
//	private void upgradeTo1_7_11() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isVatInclusive = 1 WHERE isVatInclusive IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(11);
//	}
//
//	private void upgradeTo1_7_10() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommercesettings SET isPreOrderAllowedOnOrder = 0 WHERE isPreOrderAllowedOnOrder IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(10);
//	}
//
//	private void upgradeTo1_7_9() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE customer SET isVerified = false WHERE isVerified IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(9);
//	}
//
//	private void upgradeTo1_7_8() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommerceSettings SET isCustomerEmailConfirmationRequired = false WHERE isCustomerEmailConfirmationRequired IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE ecommerceSettings SET isOutOfStockTxnAuthRequired = false WHERE isOutOfStockTxnAuthRequired IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(8);
//	}
//
//	private void upgradeTo1_7_7() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE ecommerceconfiguration SET cartDisplayName = 'cart' WHERE cartDisplayName IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(7);
//	}
//
//	private void upgradeTo1_7_6() {
//		try {
//
//			ApplicationUtil.executeSql(  "INSERT INTO productinfo_optionalaccessorieslist (productInfo_id, optionalAccessory_type, optionalAccessory_id) SELECT productInfo_id, 'REALIZED_PRODUCT', optionalAccessoriesList_id FROM productInfo_realizedProduct WHERE optionalAccessoriesList_id IS NOT NULL"  );
//			List<Object[]> includedProducts = HibernateUtil.getCurrentSession().createSQLQuery( "SELECT productInfo_id, includedProducts_id FROM productInfo_realizedProduct WHERE includedProducts_id IS NOT NULL" ).list();
//			for( Object[] includedProductObj : includedProducts ) {
//				IncludedProduct includedProduct = new IncludedProduct();
//				includedProduct.setQuantity( 1 );
//				includedProduct.setSerialNumberRequired( true );
//				includedProduct.saveDetails();
//				ApplicationUtil.executeSql(  "UPDATE includedProduct SET realizedProductRetriever_type = 'REALIZED_PRODUCT', realizedProductRetriever_id = " + includedProductObj[ 1 ] + " WHERE id = " + includedProduct.getId()  );
//				ApplicationUtil.executeSql(  "INSERT INTO productinfo_includedProduct (productInfo_id, includedProducts_id) VALUES (" + includedProductObj[ 0 ] + "," + includedProduct.getId() + ")"  );
//			}
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(6);
//	}
//
//	private void upgradeTo1_7_5() {
////		//change optional accessories and included products to be of the RetrievableProduct interface type
////		try {
////			ApplicationUtil.executeSql(  "UPDATE productinfo_includedproducts SET includedproduct_type = 'REALIZED_PRODUCT' WHERE includedproduct_type IS NULL"  );
////			ApplicationUtil.executeSql(  "UPDATE productinfo_optionalaccessorieslist SET optionalAccessory_type = 'REALIZED_PRODUCT' WHERE optionalAccessory_type IS NULL"  );
////		} catch( Exception ex ) {
////			// do nothing
////		}
////		setPatchVersionNumber(5);
//	}
//
//	private void upgradeTo1_7_4() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE Customer SET isUsingAlternativeAddress = false WHERE isUsingAlternativeAddress IS NULL"  );
//			ApplicationUtil.executeSql(  "UPDATE Company SET isUsingAlternativeAddress = false WHERE isUsingAlternativeAddress IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(4);
//	}
//
//	private void upgradeTo1_7_3() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE EcommerceSettings SET isUsingProductColours = false, isUsingProductSizes = false WHERE isUsingProductSizes IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(3);
//	}
//
//	private void upgradeTo1_7_2() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE Customer SET isCompanyConnectionRequested = false WHERE isCompanyConnectionRequested IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(2);
//	}
//
//	private void upgradeTo1_7_1() {
//		try {
//			StringBuffer hql = new StringBuffer();
//			hql.append( "INSERT INTO `customertype` (`id`, `active`," );
//			hql.append( "`deletable`,`persistentData`,`dateCreated`,`dateInactivated`,`dateLastModified`,`displayId`,`userIdCreated`," );
//			hql.append( "`userIdInactivated`,`userIdLastModified`,`description`,`name`,`owner_id`,`mailRecipientFinder_id`)" );
//			hql.append( " SELECT `id`, `active`, `deletable`, `persistentData`," );
//			hql.append( "`dateCreated`,`dateInactivated`,`dateLastModified`,`displayId`,`userIdCreated`,`userIdInactivated`," );
//			hql.append( "`userIdLastModified`,`description`,`name`,`owner_id`,`mailRecipientFinder_id` FROM `companyType`)" );
//
//			ApplicationUtil.executeSql(  hql.toString()  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(1);
//	}
//
//	private void upgradeTo1_7_0() {
//		setMinorVersionNumber(7);
//		setPatchVersionNumber(0);
//	}
//
//	private void upgradeTo1_6_5() {
//		try {
//			AqlBeanDao companyDao = new AqlBeanDao( Company.class );
//			companyDao.setOptimisedSearch( true );
//			companyDao.setSelectCriteria( "bean.id" );
//			companyDao.setWhereCriteria( "bean.altShippingAddress IS NULL" );
//			List<Company> companyList = companyDao.getAll();
//			for( Company company : companyList ) {
//				Address address = new Address();
//				address.saveDetails();
//				ApplicationUtil.executeSql(  "UPDATE Company SET altShippingAddress_id = " + address.getId() + " WHERE id = " + company.getId()  );
//			}
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(5);
//	}
//
//	private void upgradeTo1_6_4() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE CompanyContact SET isAdminUser = true WHERE isAdminUser IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(4);
//	}
//
//	private void upgradeTo1_6_3() {
//		try {
//			AqlBeanDao customerDao = new AqlBeanDao( Customer.class );
//			customerDao.setOptimisedSearch( true );
//			customerDao.setSelectCriteria( "bean.id" );
//			customerDao.setWhereCriteria( "bean.altShippingAddress IS NULL" );
//			List<Customer> customerList = customerDao.getAll();
//			for( Customer customer : customerList ) {
//				Address address = new Address();
//				address.saveDetails();
//				ApplicationUtil.executeSql(  "UPDATE Customer SET altShippingAddress_id = " + address.getId() + " WHERE id = " + customer.getId()  );
//			}
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(3);
//	}
//
//	private void upgradeTo1_6_2() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE CompanyContact SET isUsingCompanyAddressForAltShipping = false WHERE isUsingCompanyAddressForAltShipping IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(2);
//	}
//
//	private void upgradeTo1_6_1() {
//		try {
//			ApplicationUtil.executeSql(  "UPDATE Transaction SET isDeliveryAddressTheSame = false WHERE isDeliveryAddressTheSame IS NULL"  );
//		} catch( Exception ex ) {
//			// do nothing
//		}
//		setPatchVersionNumber(1);
//	}
}
