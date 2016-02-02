package com.yihu.ehr.patient.service.family;

import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.patient.service.card.IdCard;

import java.util.Date;

/**
 * 家庭加入请求.
 * @author Witness
 * @version 1.0
 * @created 25-5月-2015 20:26:29
 */
public class FamilyJoinRequest {

	/**
	 * 家庭id
	 */
	public long familyId;
	/**
	 * 请求的统一ID。
	 */
	public long id;
	/**
	 * 请求创建日期.
	 */
	public Date requestDate;
	/**
	 * 请求人.
	 */
	public IdCard requestor;
	/**
	 * 请求状态.
	 */
	public MConventionalDict state;

	public FamilyJoinRequest(){

	}

	public void finalize() throws Throwable {

	}
	/**
	 * 
	 * @param requestor
	 */
	public FamilyJoinRequest(IdCard requestor){

	}
}