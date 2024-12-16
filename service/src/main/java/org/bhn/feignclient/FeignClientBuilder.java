package org.bhn.feignclient;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class FeignClientBuilder {

    private static final String USER_MANAGEMENT_URL_ENV = "USER_MANAGEMENT_URL";
    private static final String FEIGN_LOG_LEVEL_ENV = "FEIGN_LOG_LEVEL";
    private static final Logger.Level DEFAULT_LOG_LEVEL = Logger.Level.FULL;


    public static PersonManagementClient getPersonManagementClient() {
        String userManagementUrl = Optional.ofNullable(System.getenv(USER_MANAGEMENT_URL_ENV))
                .orElseThrow(() -> new RuntimeException("Environment variable USER_MANAGEMENT_URL not found"));

        String feignClientLogLevel = String.valueOf(Optional.ofNullable(System.getenv(FEIGN_LOG_LEVEL_ENV))
                .map(String::toUpperCase)
                .map(Logger.Level::valueOf)
                .orElseGet(() -> {
                    log.warn("Feign Client log level not defined; using default level: {}", DEFAULT_LOG_LEVEL);
                    return DEFAULT_LOG_LEVEL;
                }));


        log.info("USER_MANAGEMENT_URL: {}", userManagementUrl);
        log.info("FEIGN_LOG_LEVEL: {}", feignClientLogLevel);

        // LOG is the Lombok SLF4j object
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Logger() {
                    @Override
                    protected void log(String configKey, String format, Object... args) {
                        log.info("{} {}", methodTag(configKey), args);
                    }
                })
                .logLevel(Logger.Level.valueOf(feignClientLogLevel))
                .target(PersonManagementClient.class, userManagementUrl);

    }
}