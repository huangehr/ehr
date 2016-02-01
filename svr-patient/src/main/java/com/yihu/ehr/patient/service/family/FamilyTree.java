package com.yihu.ehr.patient.service.family;


/**
 * 族谱。以用户为中心，生成一个家庭的当前族谱。
 * @author Witness
 * @version 1.0
 * @updated 25-5月-2015 20:34:17
 */
public class FamilyTree {

	public FamilyTree(){

	}

	public void finalize() throws Throwable {

	}
	/**
	 * 
	 * @param mem
	 */
	public FamilyTree(Member mem){

	}

	/**
	 * 目前来讲，release/acquire两个接口只是为了提供给跨语言调用的，因此对于JAVA内部来讲，不需要使用这个接口来实现引用计数。
	 */
	public void acquire(){

	}

	/**
	 * <b>请求接口。</b>
	 * 
	 * @param type
	 */
	public <T> T queryInterface(Class<T> type){
		return null;
	}

	/**
	 * 目前来讲，release/acquire两个接口只是为了提供给跨语言调用的，因此对于JAVA内部来讲，不需要使用这个接口来实现引用计数。
	 */
	public void release(){

	}


}//end FamilyTree