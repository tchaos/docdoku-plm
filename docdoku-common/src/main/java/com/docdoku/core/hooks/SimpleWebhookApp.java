package com.docdoku.core.hooks;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Table(name = "SIMPLEWEBHOOKAPP")
@Entity
public class SimpleWebhookApp extends WebhookApp {

    public static final String APP_NAME = "SIMPLEWEBHOOK";

    private String method;

    private String uri;

    private String authorization;

    public SimpleWebhookApp(String method, String authorization, String uri) {
        this.method = method;
        this.authorization = authorization;
        this.uri = uri;
    }

    public SimpleWebhookApp() {
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    @Override
    public String getAppName() {
        return APP_NAME;
    }
}