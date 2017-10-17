package com.netcracker.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Class that represents a simple query entity
 *
 * @author Sekachkin Mikhail
 */

@Entity
@Data
@Table(name = "queries")
public class Query {

    @Id
    @Column(name = "id", nullable = false)
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(name = "type_query", nullable = false)
    private String typeQuery;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "query", nullable = false)
    @Type(type = "text")
    private String requestText;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;
}
