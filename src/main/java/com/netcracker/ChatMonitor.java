package com.netcracker;

import com.netcracker.controller.QueryController;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class that represents monitor commands in the console
 *
 * @author Sekachkin Mikhail
 */

@Component
@CommonsLog
public class ChatMonitor {

    private QueryController queryController;

    public void setQueryController(QueryController queryController) {
        this.queryController = queryController;
    }

    public void onMessage(ConfigurableApplicationContext applicationContext) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (applicationContext.isActive()) {
                switch (bufferedReader.readLine().trim()) {
                    case "/stop":
                        applicationContext.close();
                        break;
                    case "/show":
                        System.out.print("Enter the number of minutes  = ");
                        String period = bufferedReader.readLine();
                        if (checkString(period)) {
                            this.queryController.printQueryForThePeriod(Integer.parseInt(period));
                        } else {
                            log.error("Input Error!");
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            log.error("An error has occurred. Error details: ", e);
        }
    }

    private boolean checkString(String string) {
        return string != null && string.matches("^-?\\d+$");
    }
}
