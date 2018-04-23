package com.yihu.ehr.basic.getui.model;



import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author litaohong on 2018/4/20
 * @project ehr
 * 个推--用户id与客户端clientId的映射关系
 */
@Entity
@Table(name = "getui_user_client", schema = "", catalog = "healtharchive")
public class GeTuiUserClient {
    private Long id;
    private String userId;
    private String clientId;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "identity")
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column(name = "user_id", nullable = false, insertable = true, updatable = true)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    @Column(name = "client_id", nullable = false, insertable = true, updatable = true)
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
