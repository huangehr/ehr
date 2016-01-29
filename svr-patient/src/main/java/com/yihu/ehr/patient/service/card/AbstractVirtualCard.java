package com.yihu.ehr.patient.service.card;

import com.yihu.ehr.patient.service.demographic.DemographicId;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

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

	protected String id;				    // 卡ID
	protected String number;				// 卡号
	protected String ownerName;			// 持有人姓名
	protected String status;			// 状态 CardStatus
	protected String type;				// 类型 CardType
	protected String description;			// 描述
	protected Date createDate;				// 创建日期
	protected DemographicId demographicId;	// 人口学ID

	public AbstractVirtualCard() {
		id  = UUID.randomUUID().toString().replace("-","");
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


}

