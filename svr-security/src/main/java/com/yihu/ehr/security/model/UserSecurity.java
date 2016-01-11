package com.yihu.ehr.security.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * 医生
 *
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Entity
@Table(name = "user-security")
@Access(value = AccessType.PROPERTY)
public class UserSecurity {

	public UserSecurity() {
		id  = UUID.randomUUID().toString();
	}

	@Id
	@GeneratedValue(generator = "Generator")
	@GenericGenerator(name = "Generator", strategy = "assigned")
	@Column(name = "id", unique = true, nullable = false)
	private String id;

	@Column(name = "private-key",  nullable = true)
	private String privateKey;

	@Column(name = "public-key",  nullable = true)
	private String publicKey;

	@Column(name = "from-date",  nullable = true)
	private Date fromDate;

	@Column(name = "expiry-date",  nullable = true)
	private Date expiryDate;

	@Column(name = "valid",  nullable = true)
	private Integer valid;



	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
}