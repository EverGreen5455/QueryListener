package com.netcracker.dao;

import com.netcracker.model.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository can be used to delegate CRUD operations against the data source
 *
 * @author Sekachkin Mikhail
 */

@Repository
public interface QueryDAO extends JpaRepository<Query, Long> {

    List<Query> findAllByCreatedAtAfter(LocalDateTime dateTime);
}
