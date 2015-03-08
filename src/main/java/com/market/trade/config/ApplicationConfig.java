package com.market.trade.config;

import com.market.trade.processor.handlers.MessagesHandler;
import com.market.trade.processor.handlers.StatisticsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Log4jConfigurer;

@Configuration
@PropertySource("classpath:consumer.properties")
@ComponentScan(value = {"com.market.trade"}, excludeFilters = @ComponentScan.Filter(value = {Configuration.class}))
@EnableAspectJAutoProxy
public class ApplicationConfig {

    @Autowired
    Environment environment;

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(Log4jConfigurer.class);
        methodInvokingFactoryBean.setTargetMethod("initLogging");
        methodInvokingFactoryBean.setArguments(new Object[]{"resources/log4j.xml"});
        return methodInvokingFactoryBean;
    }

    @Bean
    public MessagesHandler messagesHandler(Environment environment, StatisticsHandler statisticsHandler, JdbcTemplate jdbcTemplate) {
        return new MessagesHandler(Integer.valueOf(environment.getProperty("consumer.messages.limit")), statisticsHandler, jdbcTemplate);
    }

}
