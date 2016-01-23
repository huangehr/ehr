package com.yihu.ehr.paient.service.card;


import com.yihu.ehr.paient.service.demographic.DemographicId;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 卡抽象类，仅作为继承时的数据成员使用
 * @author Witness
 * @version 1.0
 * @created 25-5月-2015 17:47:13
 */
@Entity
@Table(name = "physical_cards")
@Access(value = AccessType.PROPERTY)
public  class AbstractPhysicalCard extends AbstractCard {

	protected String id;				    // 卡ID
	protected String number;				// 卡号
	protected String ownerName;			// 持有人姓名
	protected String status;			// 状态 CardStatus
	protected String type;				// 类型 CardType
	protected String description;			// 描述
	protected Date createDate;				// 创建日期
	protected DemographicId demographicId;	// 人口学ID

	//特殊字段
	protected String local;				// 发行地/归属地
	protected String releaseOrg;				// 发行机构
	protected Date releaseDate;			// 发行时间
	protected Date validityDateBegin;		// 有效期起始时间
	protected Date validityDateEnd;		// 有效期结束时间


	@Id
    @GeneratedValue(generator = "id")
    @GenericGenerator(name = "id", strategy = "id")
    @Column(name = "id", unique = true, nullable = false)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "number", unique = true, nullable = false ,insertable = false, updatable = false)
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	@Column(name = "owner_name", unique = true, nullable = false ,insertable = false, updatable = false)
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	@Column(name = "card_status", unique = true, nullable = false ,insertable = false, updatable = false)
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "card_type", unique = true, nullable = false ,insertable = false, updatable = false)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "description", unique = true, nullable = false ,insertable = false, updatable = false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "create_date", unique = true, nullable = false ,insertable = false, updatable = false)
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "id_card_no", unique = true, nullable = false ,insertable = false, updatable = false)
	public DemographicId getDemographicId() {
		return demographicId;
	}
	public void setDemographicId(DemographicId demographicId) {
		this.demographicId = demographicId;
	}

	@Column(name = "local", unique = true, nullable = false ,insertable = false, updatable = false)
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}

	@Column(name = "release_org", unique = true, nullable = false ,insertable = false, updatable = false)
	public String getReleaseOrg() {
		return releaseOrg;
	}
	public void setReleaseOrg(String releaseOrg) {
		this.releaseOrg = releaseOrg;
	}

	@Column(name = "release_date", unique = true, nullable = false ,insertable = false, updatable = false)
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	@Column(name = "validity_date_begin", unique = true, nullable = false ,insertable = false, updatable = false)
	public Date getValidityDateBegin() {
		return validityDateBegin;
	}
	public void setValidityDateBegin(Date validityDateBegin) {
		this.validityDateBegin = validityDateBegin;
	}

	@Column(name = "validity_date_end", unique = true, nullable = false ,insertable = false, updatable = false)
	public Date getValidityDateEnd() {
		return validityDateEnd;
	}
	public void setValidityDateEnd(Date validityDateEnd) {
		this.validityDateEnd = validityDateEnd;
	}
}