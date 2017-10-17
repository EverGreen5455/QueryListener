package com.netcracker;

import com.netcracker.controller.QueryController;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Class that represents main Spring Boot application class. It configures Spring Boot, JPA
 *
 * @author Sekachkin Mikhail
 */

@SpringBootApplication
@EnableScheduling
@CommonsLog
public class QueryListenerStartUpPoint {

    private static ConfigurableApplicationContext applicationContext;
    private static ChatMonitor chatMonitor = new ChatMonitor();
    private QueryController queryController;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(QueryListenerStartUpPoint.class, args);
        chatMonitor.onMessage(applicationContext);
    }

    @Autowired
    public void setQueryController(QueryController queryController) {
        this.queryController = queryController;
        chatMonitor.setQueryController(queryController);
    }

    @Scheduled(fixedRate = 30000)
    public void startListener() {
        this.queryController.startListening();
    }
}

