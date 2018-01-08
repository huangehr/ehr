package com.yihu.ehr.basic.patient.service.card;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 抽象虚拟卡
 * @author Sand
 * @version 1.0
 * @updated 11-6月-2015 9:52:03
 */
@Entity
@Table(name = "virtual_cards")
@Access(value = AccessType.PROPERTY)

public class AbstractVirtualCard extends AbstractCard {


	String id;				    // 卡ID
	String number;				// 卡号
	String ownerName;			// 持有人姓名
	String status;			// 状态 CardStatus
	String cardType;				// 类型 CardType
	String description;			// 描述
	Date createDate;				// 创建日期
	String idCardNo;          //身份证号
	String DType;
	String platform;

	public AbstractVirtualCard() {
//		id  = UUID.randomUUID().toString().replace("-","");
	}

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

	@Column(name = "number", nullable = false)
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	@Column(name = "owner_name",  nullable = true)
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	@Column(name = "card_status", nullable = true)
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}



	@Column(name = "card_type",  nullable = true)
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	@Column(name = "description",  nullable = true)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "create_date",  nullable = true)
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "id_card_no", nullable = true)
	public String getIdCardNo() {
		return idCardNo;
	}
	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	@Column(name = "DType",  nullable = true )
	public String getDType() {
		return DType;
	}
	public void setDType(String DType) {
		this.DType = DType;
	}

	@Column(name = "platform",  nullable = false )
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
}

