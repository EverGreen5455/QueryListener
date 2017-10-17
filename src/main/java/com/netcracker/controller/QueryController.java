package com.netcracker.controller;

import com.google.common.base.Splitter;
import com.netcracker.model.Query;
import com.netcracker.service.QueryService;
import com.netcracker.util.ConstantsProvider;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Class implements {@link Controller @Controller} layer for entity Query
 * contains methods performing processing tasks
 *
 * @author Sekachkin Mikhail
 */

@Controller
@CommonsLog
public class QueryController {

    private final QueryService queryService;

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(ConstantsProvider.DATE_TIME_FORMAT_WITH_HIGH_PRECISION);
    private Properties properties = new Properties();
    private List<String> queriesStatement;
    private List<String> blackList;
    private long fileSize = 0;
    private String schema = "";

    @Autowired
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostConstruct
    private void initList() throws IOException {
        blackList = new ArrayList<>();
        queriesStatement = new ArrayList<>();
        properties.load(new FileInputStream(new File(ConstantsProvider.PROPERTIES_FILE_PATH_NAME)));
        fileSize = Long.parseLong(properties.getProperty("FILE_SIZE", "0"));
        schema = properties.getProperty("LAST_SCHEMA");
        Collections.addAll(blackList, properties.getProperty("BLACK_LIST").split(";"));
        queriesStatement.add("insert");
        queriesStatement.add("update");
        queriesStatement.add("delete");
        queriesStatement.add("select");
    }

    public void startListening() {
        File file = new File(ConstantsProvider.FILE_PATH_NAME);
        if (fileSize != file.length()) {
            if (fileSize > file.length()) {
                fileSize = 0;
            }
            try {
                logParse();
            } catch (IOException e) {
                log.error("An error has occurred. Error details: ", e);
            }

        }
    }

    private void logParse() throws IOException {
        List<String> lines = new ArrayList<>();
        Optional.ofNullable(readFileData()).ifPresent(lines::addAll);
        Iterator iterator = lines.iterator();
        LocalDateTime time = null;
        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            StringTokenizer stringTokenizer = new StringTokenizer(s, "\t");
            if (stringTokenizer.countTokens() != 3) {
                continue;
            }
            time = LocalDateTime.parse(stringTokenizer.nextToken().trim(), dateFormat).plusHours(3);
            String typeQuery = stringTokenizer.nextToken().trim();
            String query = stringTokenizer.nextToken().trim();
            if (typeQuery.contains("Connect")) {
                String newScheme = query.substring(query.indexOf(" on ") + 4, query.indexOf(" using ")).trim();
                if (!newScheme.equals(schema) && !newScheme.isEmpty()) {
                    schema = newScheme;
                }
            } else if (typeQuery.contains("Query") &&
                    !schema.isEmpty()) {
                String queryStatement = query.substring(0, 6).toLowerCase();
                if (queriesStatement.contains(queryStatement) &&
                        !query.contains("@@")) {
                    String tableName = parseTableName(queryStatement, query);
                    if (blackList.contains(tableName)) {
                        continue;
                    } else if (tableName.contains(".")) {
                        List<String> helper = Splitter.on(".").splitToList(tableName);
                        schema = helper.get(0);
                        tableName = helper.get(1);
                    }
                    this.queryService.addQuery(buildQuery(schema, time, queryStatement, tableName, query));
                    properties.setProperty("LAST_SCHEMA", schema);
                    properties.store(new FileOutputStream(ConstantsProvider.PROPERTIES_FILE_PATH_NAME), null);
                }
            }
        }
        iterator = null;
    }

    private Query buildQuery(String schema, LocalDateTime time, String queryStatement, String tableName, String query) {
        Query queryForInsert = new Query();
        queryForInsert.setSchemeName(schema);
        queryForInsert.setCreatedAt(time);
        queryForInsert.setTypeQuery(queryStatement);
        queryForInsert.setTableName(tableName);
        queryForInsert.setRequestText(query);
        return queryForInsert;
    }

    private String parseTableName(String queryStatement, String query) {
        List<String> helper = Splitter.on(" ").splitToList(query.toLowerCase());
        switch (queryStatement) {
            case ConstantsProvider.INSERT:
                return helper.get(2);
            case ConstantsProvider.UPDATE:
                return helper.get(1);
            default:
                return helper.get(helper.indexOf("from") + 1);
        }
    }

    private List<String> readFileData() {
        List<String> list = new ArrayList<>();
        try (RandomAccessFile file = new RandomAccessFile(ConstantsProvider.FILE_PATH_NAME, "r")) {
            file.seek(fileSize);
            String s = null;
            while ((s = file.readLine()) != null) {
                list.add(s);
            }
            fileSize = file.getFilePointer();
            properties.setProperty("FILE_SIZE", String.valueOf(fileSize));
            properties.store(new FileOutputStream(ConstantsProvider.PROPERTIES_FILE_PATH_NAME), null);
        } catch (Exception e) {
            log.error("An error has occurred. Error details: ", e);
        }
        return list;
    }

    public void getQueryForThePeriod(long period) {
        List<Query> queryList = this.queryService.getQueryForThePeriod(LocalDateTime.now().minusMinutes(period));
        if (!queryList.isEmpty()) {
            for (Query q : queryList) {
                System.out.println(q);
            }
        } else {
            System.out.println("No requests for this period were found!");
        }
    }
}
