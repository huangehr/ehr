package com.yihu.ehr.patient.service.card;

/**
 * 护照接口实现类.
 * @author Sand
 * @version 1.0
 * @created 10-6月-2015 16:28:59
 */
public class Passport extends AbstractPhysicalCard {

	public Passport(){
		super();
		this.type = "Passport";
	}

	/**
	 * 因私普通护照号码格式有:14/15+7位数,G+8位数；因公普通的是:P.+7位数；
	 * 公务的是：S.+7位数 或者 S+8位数,以D开头的是外交护照.D=diplomatic
	 * @param number
	 * @return
	 */
	protected boolean validateCardNumber(String number){
		return true;
	}
}