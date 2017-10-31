package com.netcracker.util;

import com.netcracker.controller.QueryController;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.ConfigurableApplicationContext;

@CommonsLog
public class QueryThread extends Thread {

    @Setter
    private static QueryController queryController;

    @Setter
    private static long periodBetweenThreads;

    @Setter
    private static ConfigurableApplicationContext applicationContext;

    @Override
    public void run() {
        while (applicationContext.isActive()) {
            queryController.startListening(applicationContext);
            try {
                Thread.sleep(periodBetweenThreads);
            } catch (InterruptedException e) {
                log.error("An error has occurred. Error details: ", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}