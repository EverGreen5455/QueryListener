package com.netcracker.service;

import com.netcracker.dao.QueryDAO;
import com.netcracker.model.Query;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class implements DAO layer for entity Query
 * contains CRUD methods
 *
 * @author Sekachkin Mikhail
 */

@Service
@CommonsLog
public class QueryServiceImpl implements QueryService {

    private final QueryDAO queryDAO;

    @Autowired
    public QueryServiceImpl(QueryDAO queryDAO) {
        this.queryDAO = queryDAO;
    }

    @Override
    @Transactional
    public void addQuery(Query query) {
        this.queryDAO.save(query);
    }

    @Override
    @Transactional
    public void removeQuery(Long id) {
        this.queryDAO.delete(id);
    }

    @Override
    @Transactional
    public List<Query> listQueries() {
        return this.queryDAO.findAll();
    }

    @Override
    @Transactional
    public List<Query> getQueryForThePeriod(LocalDateTime dateTime) {
        return this.queryDAO.findAllByCreatedAtAfter(dateTime);
    }
}
