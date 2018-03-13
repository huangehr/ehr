package com.yihu.ehr.entity.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.id.ObjectId;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 公-私钥。
 *
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Entity
@Table(name = "user_security")
@Access(value = AccessType.PROPERTY)
public class UserSecurity implements Serializable {
	@Value("${admin-region}")
	short adminRegion;

	public UserSecurity() {
		id = new ObjectId(adminRegion, BizObject.User).toString();
	}

	private String id;
	private String privateKey;
	private String publicKey;
	private Date fromDate;
	private Date expiryDate;
	private Integer valid;

	@Id
	@GeneratedValue(generator = "Generator")
	@GenericGenerator(name = "Generator", strategy = "assigned")
	@Column(name = "id", unique = true, nullable = false)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "private_key",  nullable = false)
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	@Column(name = "public_key",  nullable = false)
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Column(name = "from_date",  nullable = true)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	@Column(name = "expiry_date",  nullable = true)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Column(name = "valid",  nullable = false)
	public Integer getValid() {
		return valid;
	}
	public void setValid(Integer valid) {
		this.valid = valid;
	}
}