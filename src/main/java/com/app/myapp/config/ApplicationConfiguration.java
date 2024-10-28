package com.app.myapp.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger("ApplicationLogger");
    }

    @Bean
    public ModelMapper modelMapper(Logger logger) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        modelMapper.addConverter(ctx -> ctx.getSource() == null ? "" : ctx.getSource().toString(), String.class,
                String.class);
        logger.debug("ModelMapper configured with strict matching and skip null enabled.");

        return modelMapper;
    }

    @Bean
    // @LoadBalanced
    public WebClient.Builder webClientBuilder(Logger logger) {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
                .maxConnections(50)
                .maxIdleTime(Duration.ofSeconds(30))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofSeconds(10));

        logger.debug("WebClient builder configured with connection pooling and response timeout.");

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest(logger))
                .filter(handleErrors(logger));
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

    private ExchangeFilterFunction logRequest(Logger logger) {
        return (clientRequest, next) -> {
            logger.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
            return next.exchange(clientRequest);
        };
    }

    private ExchangeFilterFunction handleErrors(Logger logger) {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                logger.error("Error Response: {}", clientResponse.statusCode());
            }
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> {
                        logger.debug("Response body: {}", body);
                        return clientResponse.createException().flatMap(Mono::error);
                    });
        });
    }
}
