package com.yihu.ehr.api.model;

import com.yihu.ehr.model.app.MApp;

import java.util.Date;
import java.util.Set;

/**
 * Created by Sand Wen on 2016.3.8.
 */
public class MToken {
    long id;
    String url;

    public void setId(long id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setToken_last_eight(String token_last_eight) {
        this.token_last_eight = token_last_eight;
    }

    public void setHashed_token(String hashed_token) {
        this.hashed_token = hashed_token;
    }

    public void setApp(MApp app) {
        this.app = app;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setNote_url(String note_url) {
        this.note_url = note_url;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public String getToken() {
        return token;
    }

    public String getToken_last_eight() {
        return token_last_eight;
    }

    public String getHashed_token() {
        return hashed_token;
    }

    public MApp getApp() {
        return app;
    }

    public String getNote() {
        return note;
    }

    public String getNote_url() {
        return note_url;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    Set<String> scopes;
    String token;
    String token_last_eight;
    String hashed_token;
    MApp app;
    String note;
    String note_url;
    Date updated_at;
    Date created_at;
    String fingerprint;
}
