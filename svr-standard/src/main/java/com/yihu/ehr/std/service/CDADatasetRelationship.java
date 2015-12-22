package com.yihu.ehr.std.service;


import com.yihu.ehr.constrant.BizObject;

import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.lang.ServiceFactory;
import com.yihu.ehr.std.model.*;
import com.yihu.ehr.util.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 17:08:41
 */
public class CDADatasetRelationship {
    @Value("${admin-region}")
    short adminRegion;

	@Autowired
	private CDADocumentManager cdaManager;

	@Autowired
	private CDAVersionManager cdaVersionManager;

	@Autowired
	private DataSetManager dataSetManager;

	private String cda_id;
	private String id;
	private String set_id;
	private String version_code;

	private String dataset_code;
	private String dataset_name;
	private String summary;

	public String getDataset_code() {
		return dataset_code;
	}

	public void setDataset_code(String dataset_code) {
		this.dataset_code = dataset_code;
	}

	public String getDataset_name() {
		return dataset_name;
	}

	public void setDataset_name(String dataset_name) {
		this.dataset_name = dataset_name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public CDADatasetRelationship(){
		this.OperationType="";
		Object objectID = new ObjectId(adminRegion, BizObject.StdArchive);

		id = objectID.toString();
	}

	public void finalize() throws Throwable {

	}
	public CDADocument getCDA(){
		List<String> listIds = Arrays.asList( this.cda_id.split(","));
		CDADocument[] xcdaDocuments =cdaManager.getCDAInfoByVersionAndId(this.version_code,listIds);
		if(xcdaDocuments !=null && xcdaDocuments.length>0)
			return xcdaDocuments[0];
		return null;
	}

	public String getCDAId(){
		return this.cda_id;
	}

	public DataSet getDataset(){

		if(cdaVersionManager==null)
			cdaVersionManager =  ServiceFactory.getService(Services.CDAVersionManager);
		CDAVersion xcdaVersion=cdaVersionManager.getVersionById(this.version_code);

		if(dataSetManager==null)
			dataSetManager = ServiceFactory.getService(Services.DataSetManager);

		return dataSetManager.getDataSet(Long.parseLong(this.set_id),xcdaVersion);
	}

	public String getDataset_id(){
		return this.set_id;
	}

	public String getId(){
		return this.id;
	}

	/**
	 * 
	 * @param cda_id
	 */
	public void setCDAId(String cda_id){
		this.cda_id=cda_id;
	}

	/**
	 * 
	 * @param dataset_id
	 */
	public void setDatasetId(String dataset_id){
		this.set_id=dataset_id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id){
		this.id=id;
	}

	public void setVersion_code(String version_code){
		this.version_code=version_code;
	}

	public String getVersion_code(){
		return this.version_code;
	}

	public String getOperationType() {
		return OperationType;
	}

	public void setOperationType(String operationType) {
		OperationType = operationType;
	}

	String OperationType;
}//end CDADatsetRelationship