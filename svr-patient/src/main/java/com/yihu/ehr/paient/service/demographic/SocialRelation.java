package com.yihu.ehr.paient.service.demographic;


import com.yihu.ehr.paient.service.card.IdCard;

import javax.smartcardio.Card;

/**
 * 社会关系实现类，如：父子
 * @author Sand
 * @version 1.0
 * @updated 25-5月-2015 19:45:07
 */
public class SocialRelation {

	/**
	 * 社交关系名称
	 */
	private String name;
	/**
	 * 社交关系成员，以身份证ID为关系
	 */
	private Card withWho;

	public SocialRelation(){
	}

	public void finalize() throws Throwable {

	}

	public SocialRelation(IdCard aPerson, IdCard withWho){
	}

	public String getName(){
		return "";
	}

	public Card withWho(){
		return null;
	}
}