package com.yihu.ehr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableEurekaClient
@EnableZuulProxy
public class EHRPlatformGatewayApp  {
    @Value("${server.port}")
    int port;

    @Value("${keystore.file}")
    String keystore;

    @Value("${keystore.password}")
    String password;

	public static void main(String[] args) {
		SpringApplication.run(EHRPlatformGatewayApp.class, args);
	}

    //@Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() throws FileNotFoundException {
        final String absoluteKeystoreFile = ResourceUtils.getFile(keystore).getAbsolutePath();

        final TomcatConnectorCustomizer customizer = null;//new TomcatConnCustomizer(absoluteKeystoreFile, password, port);

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
