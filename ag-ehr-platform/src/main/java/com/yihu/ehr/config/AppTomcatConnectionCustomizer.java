package com.yihu.ehr.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 18:36
 */
public class AppTomcatConnectionCustomizer implements TomcatConnectorCustomizer {

    int port;
    private String absoluteKeystoreFile;
    private String keystorePassword;

    public AppTomcatConnectionCustomizer(
            String absoluteKeystoreFile,
            String keystorePassword,
            int port) {
        this.absoluteKeystoreFile = absoluteKeystoreFile;
        this.keystorePassword = keystorePassword;
        this.port = port;
    }

    @Override
    public void customize(Connector connector) {
        connector.setPort(port);
        connector.setSecure(true);
        connector.setScheme("https");

        connector.setAttribute("SSLEnabled", true);
        connector.setAttribute("sslProtocol", "TLS");
        connector.setAttribute("protocol", "org.apache.coyote.http11.Http11Protocol");
        connector.setAttribute("clientAuth", false);
        connector.setAttribute("keystoreFile", absoluteKeystoreFile);
        //connector.setAttribute("keystoreType", keystoreType);
        connector.setAttribute("keystorePass", keystorePassword);
        //connector.setAttribute("keystoreAlias", keystoreAlias);
        connector.setAttribute("keyPass", keystorePassword);
    }
}