package com.service.order.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Fix Bug: Could not find class [org.springframework.boot.autoconfigure.condition.OnBeanCondition] when upgrade to Java 11
     * @param properties Load balances client properties
     * @return Application context
     */
    @Bean
    @ConditionalOnMissingBean
    public LoadBalancerClientFactory loadBalancerClientFactory(LoadBalancerClientsProperties properties) {
        return new LoadBalancerClientFactory(properties) {
            @Override
            protected AnnotationConfigApplicationContext createContext(String name) {
                // FIXME: temporary switch classloader to use the correct one when creating the context
                ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                AnnotationConfigApplicationContext context = super.createContext(name);
                Thread.currentThread().setContextClassLoader(originalClassLoader);
                return context;
            }
        };
    }
}
