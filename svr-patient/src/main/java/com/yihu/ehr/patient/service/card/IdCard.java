package com.yihu.ehr.patient.service.card;

/**
 * 身份证
 * @author Sand
 * @version 1.0
 * @created 25-5月-2015 17:47:14
 */
public class IdCard extends AbstractPhysicalCard {


	public IdCard(){
		super();

		this.type = "IdCard";
	}

	public void setNumber(String number){
		if(!validateCardNumber(number)){
			throw new RuntimeException("身份证号格式错误.");
		}
		this.number = number;
	}

	protected boolean validateCardNumber(String number) {
		if(number == null || (number.length() != 15 && number.length() != 18)) return false;
		return true;
	}
}