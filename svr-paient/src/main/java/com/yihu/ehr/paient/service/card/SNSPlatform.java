package com.yihu.ehr.paient.service.card;

import java.net.URL;
import java.util.Date;

/**
 * 社交平台接口实现类.
 * @author Witness
 * @version 1.0
 * @created 05-6月-2015 11:09:46
 */
public class SNSPlatform {

	/**
	 * 平台URL.
	 */
	private String homePage;
	/**
	 * 平台名称.
	 */
	private String name;
	/**
	 * 添加时间
	 */
	private Date createDate;
	/**
	 * 备注
	 */
	private String notes;

	public SNSPlatform(){
		this.createDate = new Date();
	}

	public String getName(){
		return name;
	}

	public URL getHomePage(){
		//// TODO: 2016/1/21 调用外部服务接口
//		try {
//			return new URL(homePage);
//		} catch (MalformedURLException e) {
//            LogService.getLogger(SNSPlatform.class).error(e.getMessage());
//		}

		return null;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setHomePage(URL url){
		this.homePage = url.toString();
	}

	public Date getCreateDate(){
		return createDate;
	}

	public String getNotes(){
		return notes;
	}

	public void setNotes(String notes){
		this.notes = notes;
	}
}