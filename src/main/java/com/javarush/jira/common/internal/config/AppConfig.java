package com.javarush.jira.common.internal.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.javarush.jira.common.util.JsonUtil;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.ProblemDetail;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@Configuration
@Slf4j
@EnableCaching
@RequiredArgsConstructor
@EnableScheduling
public class AppConfig {

    private final AppProperties appProperties;
    private final Environment env;

    @Bean("mailExecutor")
    Executor getAsyncExecutor() {
        return new ThreadPoolTaskExecutor() {
            {
                setCorePoolSize(appProperties.getMailSendingProps().corePoolSize);
                setMaxPoolSize(appProperties.getMailSendingProps().maxPoolSize);
                setThreadNamePrefix("mail-");
            }
        };
    }

    public boolean isProd() {
        return env.acceptsProfiles(Profiles.of("prod"));
    }

    public boolean isTest() {
        return env.acceptsProfiles(Profiles.of("test"));
    }

    @Autowired
    void configureAndStoreObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new Hibernate5JakartaModule());
        // https://stackoverflow.com/questions/7421474/548473
        objectMapper.addMixIn(ProblemDetail.class, MixIn.class);
        JsonUtil.setMapper(objectMapper);
    }

    //    https://stackoverflow.com/a/74630129/548473
    @JsonAutoDetect(fieldVisibility = NONE, getterVisibility = ANY)
    interface MixIn {
        @JsonAnyGetter
        Map<String, Object> getProperties();
    }

//    @Bean
//    @Profile("prod")
//    @ConfigurationProperties ("spring.datasource.primary")
//    public DataSource primaryDataSource(){
//        return new DataSourceProperties().initializeDataSourceBuilder().build();
//    }
//    @Bean
//    @Profile("test")
//    @ConfigurationProperties ("spring.datasource.secondary")
//    public DataSource secondaryDataSource(){
//        try{
//            var dbBuilder = new EmbeddedDatabaseBuilder();
//            return dbBuilder.setType(EmbeddedDatabaseType.H2)
//                    .addScripts("classpath:db/changelog_h2.sql", "classpath:db/data.sql")
//                    .build();
//        }
//        catch (Exception e){
//            System.out.println("EMMBEDDED DATA SOURCE!!!!");
//            return  null;
//
//        }
//    }
//    @Bean
//    public SpringLiquibase liquibase (DataSource dataSource){
//        SpringLiquibase liquibase = new SpringLiquibase();
//        liquibase.setDataSource(dataSource);
//        return liquibase;
//    }
}
