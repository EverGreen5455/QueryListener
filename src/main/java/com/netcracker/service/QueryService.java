package com.netcracker.service;

import com.netcracker.model.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface describes {@link Service @Service} layer for entity Query
 * contains CRUD methods
 *
 * @author Sekachkin Mikhail
 */

public interface QueryService {

    void addQuery(Query query);

    void removeQuery(Long id);

    List<Query> listQueries();

    List<Query> getQueryForThePeriod(LocalDateTime dateTime);
}
