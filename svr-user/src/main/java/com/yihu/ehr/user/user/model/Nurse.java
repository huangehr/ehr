package com.yihu.ehr.user.user.model;


import org.hibernate.annotations.AttributeAccessor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 护士
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:16
 */
@Entity
@Table( name = "users" )
@AttributeAccessor( "FIELD" )
public class Nurse extends AbstractMedicalUser implements XMedicalUser {

	public Nurse(){
	}
}