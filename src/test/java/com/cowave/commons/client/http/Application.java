package com.cowave.commons.client.http;

import lombok.RequiredArgsConstructor;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author shanhuiming
 *
 */
@SpringBootApplication
@RequiredArgsConstructor
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            connector.setScheme("http");
            connector.setPort(8080);
            connector.setAllowTrace(true);
            factory.addAdditionalTomcatConnectors(connector);
        };
    }

    @Bean
    public HttpServiceChooser httpServiceChooser() {
        return name -> {
            if ("xxxService".equals(name)) {
                return "http://127.0.0.1:8080";
            } else {
                return "";
            }
        };
    }

    @Bean
    public HttpClientInterceptor httpClientInterceptor() {
        return httpRequest -> httpRequest.header("X-id", "qwert");

    }
}
