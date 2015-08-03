package com.aplos.ecommerce.backingpage.product.amazon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.UploadedFile;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.amazon.AmazonBrowseNode;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=AmazonBrowseNode.class)
public class AmazonBrowseNodeListPage extends ListPage {
	private static final long serialVersionUID = 265550285921444590L;
	
	private static final int NODE_ID = 0;
	private static final int NODE_PATH = 1;
	
	private UploadedFile uploadedFile;

	public void importBrowseNodes() {
		List<AmazonBrowseNode> amazonBrowseNodes = new BeanDao( AmazonBrowseNode.class ).getAll();
		Map<String,AmazonBrowseNode> amazonBrowseNodeMap = new HashMap<String,AmazonBrowseNode>();
		for( int i = 0, n = amazonBrowseNodes.size(); i < n; i++ ) {
			amazonBrowseNodeMap.put( amazonBrowseNodes.get( i ).getName(), amazonBrowseNodes.get( i ));
		}

		String tempParentNodeName;
		if( CommonUtil.isFileUploaded( getUploadedFile() ) ) {
			if( getUploadedFile().getFileName().endsWith( ".csv" ) ) {
				try { 
					InputStream inputStream = getUploadedFile().getInputstream();
					BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
					String line = reader.readLine();
					boolean nodeIdLinesStarted = false;
					
					BeanDao browseNodeDao = new BeanDao( AmazonBrowseNode.class );
					browseNodeDao.setIsReturningActiveBeans(null);
					browseNodeDao.addWhereCriteria( "bean.nodeId = :nodeId" );
					
					AmazonBrowseNode tempAmazonBrowseNode;
					String tempNodeIdStr;
					Long tempNodeId;
					while( line != null ) {
						if( nodeIdLinesStarted ) {
							String[] barcodeLineParts = new String[ 10 ];
							String[] availableParts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
							for( int i = 0, n = availableParts.length; i < n; i++ ) {
								barcodeLineParts[ i ] = FormatUtil.removeQuotesFromCsvString( availableParts[ i ] );	
							}
	
							tempNodeIdStr = barcodeLineParts[ NODE_ID ];
							if( !CommonUtil.isNullOrEmpty( tempNodeIdStr ) ) {
								browseNodeDao.setNamedParameter( "nodeId", tempNodeIdStr );
								tempAmazonBrowseNode = browseNodeDao.getFirstBeanResult();
								if( tempAmazonBrowseNode == null ) {
									try {
										tempNodeId = Long.parseLong( tempNodeIdStr );
										tempAmazonBrowseNode = new AmazonBrowseNode();
										tempAmazonBrowseNode.setNodeId( tempNodeId );
										tempAmazonBrowseNode.setName( barcodeLineParts[ NODE_PATH ] );
										if( !CommonUtil.isNullOrEmpty( barcodeLineParts[ NODE_PATH ] ) && barcodeLineParts[ NODE_PATH ].lastIndexOf( "/" ) > -1 ) {
											tempParentNodeName = barcodeLineParts[ NODE_PATH ].substring( 0, barcodeLineParts[ NODE_PATH ].lastIndexOf( "/" ) );
											if( amazonBrowseNodeMap.get( tempParentNodeName ) != null ) {
												tempAmazonBrowseNode.setParentNode( amazonBrowseNodeMap.get( tempParentNodeName ) );
												amazonBrowseNodeMap.get( tempParentNodeName ).setAParentNode( true );
												amazonBrowseNodeMap.get( tempParentNodeName ).saveDetails();
											}
										}
										tempAmazonBrowseNode.saveDetails();	
										amazonBrowseNodeMap.put( tempAmazonBrowseNode.getName(), tempAmazonBrowseNode);
									} catch( NumberFormatException nfEx ) {
										ApplicationUtil.handleError( nfEx );
										break;
									}
								}
							}
						} else {
							if( line.startsWith( "Node ID," ) ) {
								nodeIdLinesStarted = true;
							}
						}
						line = reader.readLine();
					}
					reader.close();
					inputStream.close();
	
					if( !nodeIdLinesStarted ) {
						JSFUtil.addMessageForWarning( "This does not appear to be a valid file" );
					}
				} catch( IOException ioex ) {
					ApplicationUtil.getAplosContextListener().handleError( ioex );
				}
			} else {
				JSFUtil.addMessageForWarning( "This file is not in CSV format.  If it is an Excel please open it and then go to Save As - Other formats.  A dialog will open where you should select CSV (MS-DOS) from the dropdown.  You can then click to save the file in the correct format." );
			}
		}
	} 

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}
}
