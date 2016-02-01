package com.yihu.ehr.paient.service.demographic;

import java.io.Serializable;
import java.util.Objects;

/**
 * 人口学ID. 作为人口学标识, 若此人有户口, 则此号码为身份证号码, 若是新生儿或没有户口的情况, 此号码为系统生成的一个标记.
 * 这时候这些病人的
 *
 * @author Sand
 * @version 1.0
 * @created 2015.06.10 17:51
 */
public class DemographicId implements Serializable {

	public String idCardNo;	// 身份证号码

	public String getIdCardNo() {
		return idCardNo;
	}
	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public DemographicId(){}

	public DemographicId(String idCardNo){
		this.idCardNo = idCardNo == null ? "" : idCardNo;
	}

	public boolean idAvailable(){
		return idCardNo != null && idCardNo.length() > 0;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DemographicId){
			DemographicId pk = (DemographicId)obj;
			if(this.idCardNo==pk.getIdCardNo()){
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idCardNo);
	}

//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//
//		DemographicId demId = (DemographicId) o;
//		return Objects.equals(idCardNo, demId.idCardNo);
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(idCardNo);
//	}
//	public String toString(){
//		return idCardNo;
//	}
}
