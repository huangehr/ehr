package com.yihu.ehr.patient.service.family;
import com.yihu.ehr.model.address.MAddress;

import java.util.Date;
import java.util.List;

/**
 * 家庭接口实现类.
 * @author Witness
 * @version 1.0
 * @created 25-5月-2015 20:26:29
 */
public class Family  {

	/**
	 * 家庭管理员
	 */
	private Member admin;
	/**
	 * 小区/社区名称
	 */
	private String cominuty;
	/**
	 * 创建日期.
	 */
	private Date createDate;
	/**
	 * 创建人
	 */
	private Member creator;
	/**
	 * 家庭ID. 自动生成, 并且只读
	 */
	private long id;
	/**
	 * 家庭所在地，可以与小区关联起来
	 */
	private MAddress location;
	/**
	 * 成员列表.
	 */
	private List<Member> members;
	/**
	 * 家庭名称.
	 */
	private String name;
	public Member m_XMember;

	public Family(){

	}

	public void finalize() throws Throwable {

	}
	/**
	 * 接受请求.
	 */
	public boolean acceptJoinRequest(){
		return false;
	}

	/**
	 * 添加成员.
	 * 
	 * @param member
	 */
	public void addMember(Member member){

	}

	/**
	 * 获取管理员.
	 */
	public Member getAdmin(){
		return null;
	}

	/**
	 * 获取创建日期.
	 */
	public Date getCreateDate(){
		return null;
	}

	/**
	 * 获取创建人.
	 */
	public Member getCreator(){
		return null;
	}

	public long getId(){
		return 0;
	}

	public MAddress getLocation(){
		return null;
	}

	/**
	 * 获取成员列表.
	 */
	public List<Member> getMembers(){
		return null;
	}

	/**
	 * 处理加入请求.
	 * 
	 * @param request
	 */
	public boolean handleRequest(FamilyJoinRequest request){
		return false;
	}

	/**
	 * 请求加入.
	 * 
	 * @param member
	 */
	public boolean joinRequest(Member member){
		return false;
	}

	/**
	 * 删除成员.
	 * 
	 * @param member
	 */
	public void removeMember(Member member){

	}

	/**
	 * 设置家庭管理员
	 * 
	 * @param member
	 */
	public void setAdmin(Member member){

	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name){

	}

	public void setLocation(MAddress location) {

	}
}