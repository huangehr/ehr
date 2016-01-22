package com.yihu.ehr.paient.service.family;


import com.yihu.ehr.paient.service.card.IdCard;
import com.yihu.ehr.paient.service.demographic.SocialRelation;

import java.awt.*;

/**
 * @author Witness
 * @version 1.0
 * @updated 25-5月-2015 20:38:39
 */
public class Member {

	private long familyId;
	private IdCard idCard;
	private SocialRelation relationship;
	public SocialRelation m_XSocialRelation;

	public Member(){

	}

	public void finalize() throws Throwable {

	}

	public Member(IdCard idcard){

	}

	public SocialRelation getRelationShip(Member member){
		return null;
	}

	public void setReleationShip(Member member, SocialRelation relation){

	}

	public void subscribe(Member member, Event event){

	}

	public void acquire(){

	}

	public <T> T queryInterface(Class<T> type){
		return null;
	}

	public void release(){

	}

}
