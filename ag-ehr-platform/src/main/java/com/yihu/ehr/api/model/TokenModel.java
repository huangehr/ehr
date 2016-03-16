package com.yihu.ehr.api.model;

import com.yihu.ehr.model.app.MApp;

import java.util.Date;
import java.util.Set;

/**
 * Created by Sand Wen on 2016.3.8.
 */
public class TokenModel {
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

    public void setTokenLastEight(String token_last_eight) {
        this.tokenLastEight = token_last_eight;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public void setApp(MApp app) {
        this.app = app;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setNoteUrl(String noteUrl) {
        this.noteUrl = noteUrl;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    public String getTokenLastEight() {
        return tokenLastEight;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public MApp getApp() {
        return app;
    }

    public String getNote() {
        return note;
    }

    public String getNoteUrl() {
        return noteUrl;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    Set<String> scopes;
    String token;
    String tokenLastEight;
    String tokenHash;
    String note;
    String noteUrl;
    Date updatedAt;
    Date createdAt;
    String fingerprint;

    MApp app;
}
