package com.netcracker;

import com.netcracker.controller.QueryController;
import com.netcracker.util.ConstantsProvider;
import com.netcracker.util.QueryThread;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class that represents main Spring Boot application class. It configures Spring Boot, JPA
 *
 * @author Sekachkin Mikhail
 */

@SpringBootApplication
@EnableScheduling
@CommonsLog
public class QueryListenerStartUpPoint {

    private static ChatMonitor chatMonitor = new ChatMonitor();
    private static List<QueryThread> threadList = new ArrayList<>();

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(QueryListenerStartUpPoint.class, args);
        QueryThread.setApplicationContext(applicationContext);
        startListener();
        chatMonitor.onMessage(applicationContext);
    }

    private static void startListener() {
        for (QueryThread t : threadList) {
            t.start();
        }
    }

    @Autowired
    public void setQueryController(QueryController queryController) {
        chatMonitor.setQueryController(queryController);
        QueryThread.setQueryController(queryController);
    }

    @PostConstruct
    private void initList() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(ConstantsProvider.PROPERTIES_FILE_PATH_NAME)));
        QueryThread.setPeriodBetweenThreads(Long.parseLong(properties.getProperty("PERIOD_BETWEEN_THREADS")));
        for (int i = 0; i < Long.parseLong(properties.getProperty("NUMBER_OF_THREADS")); i++) {
            threadList.add(new QueryThread());
        }
    }
}

