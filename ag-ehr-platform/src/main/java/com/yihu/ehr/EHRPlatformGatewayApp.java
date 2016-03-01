package com.yihu.ehr;

import com.yihu.ehr.config.AppTomcatConnectionCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

@SpringBootApplication
public class EHRPlatformGatewayApp {
    @Value("${server.port}")
    int port;

	public static void main(String[] args) {
		SpringApplication.run(EHRPlatformGatewayApp.class, args);
	}

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() throws FileNotFoundException {
        final String absoluteKeystoreFile = ResourceUtils.getFile("C:/Windows/tomcat.keystore").getAbsolutePath();

        final TomcatConnectorCustomizer customizer = new AppTomcatConnectionCustomizer(absoluteKeystoreFile, "123456", port);

        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                if(container instanceof TomcatEmbeddedServletContainerFactory) {
                    TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) container;
                    containerFactory.addConnectorCustomizers(customizer);
                }
            };
        };
    }
}
