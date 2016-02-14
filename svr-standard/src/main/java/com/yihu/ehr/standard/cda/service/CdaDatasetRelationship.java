package com.yihu.ehr.standard.cda.service;


import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.util.ObjectId;
import com.yihu.ehr.util.ObjectVersion;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 17:08:41
 */

public class CdaDatasetRelationship {
	@Value("${admin-region}")
    short adminRegion;

	private String cdaId;
	private String id;
	private String setId;
	private String versionCode;

	private String dataSetCode;
	private String dataSetName;
	private String summary;

	public String getDataSetCode() {
		return dataSetCode;
	}

	public void setDataSetCode(String dataSetCode) {
		this.dataSetCode = dataSetCode;
	}

	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}


	public CdaDatasetRelationship(){
//		Object objectID = new ObjectId(adminRegion, BizObject.StdArchive);
//        this.id = objectID.toString();
		id  = new ObjectVersion().toString();
	}

	public void finalize() throws Throwable {

	}
	public CDADocument getCda(){

		CDADocumentManager cdaManager = new CDADocumentManager();
		List<String> listIds = Arrays.asList( this.cdaId.split(","));
		CDADocument[] xcdaDocuments =cdaManager.getDocumentList(this.versionCode, listIds);
		if(xcdaDocuments !=null && xcdaDocuments.length>0)
			return xcdaDocuments[0];
		return null;
	}

	public String getCdaId(){
		return this.cdaId;
	}

	public DataSet getDataset(){

		DataSetManager dataSetManager = new DataSetManager();
		CDAVersionManager cdaVersionManager = new CDAVersionManager();
		CDAVersion xcdaVersion=cdaVersionManager.getVersion(this.versionCode);

		return dataSetManager.getDataSet(Long.parseLong(this.setId),xcdaVersion);
	}

	public String getDataSetId(){
		return this.setId;
	}

	public String getId(){
		return this.id;
	}

	/**
	 *
	 * @param cdaId
	 */
	public void setCdaId(String cdaId){
		this.cdaId=cdaId;
	}

	/**
	 *
	 * @param dataSetId
	 */
	public void setDatasetId(String dataSetId){
		this.setId=dataSetId;
	}

	/**
	 *
	 * @param id
	 */
	public void setId(String id){
		this.id=id;
	}

	public void setVersionCode(String versionCode){
		this.versionCode=versionCode;
	}

	public String getVersionCode(){
		return this.versionCode;
	}

	public String getOperationType() {
		return OperationType;
	}

	public void setOperationType(String operationType) {
		OperationType = operationType;
	}

	String OperationType;
}