package com.app.myapp.util;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgregationPipeline {

    /**
     * Reusable method to build an aggregation pipeline based on search, sort,
     * pagination, and projection.
     *
     * @param criteria       Filtering criteria (e.g., for search).
     * @param sortOrder      Sort order, either ascending or descending.
     * @param page           Page number for pagination.
     * @param size           Page size for pagination.
     * @param fieldsToReturn Fields to project (optional).
     * @return Aggregation pipeline for querying.
     */
    public static Aggregation buildAggregationPipeline(Criteria criteria, Sort sortOrder, int page, int size,
            String... fieldsToReturn) {
        MatchOperation matchOperation = Aggregation.match(criteria);
        SortOperation sortOperation = Aggregation.sort(sortOrder);
        SkipOperation skipOperation = Aggregation.skip((long) page * size);
        LimitOperation limitOperation = Aggregation.limit(size);

        // If specific fields are provided, apply projection; otherwise, include all
        // fields
        Aggregation aggregation;
        if (fieldsToReturn.length > 0) {
            ProjectionOperation projectOperation = Aggregation.project(fieldsToReturn);
            aggregation = Aggregation.newAggregation(matchOperation, sortOperation, skipOperation, limitOperation,
                    projectOperation);
        } else {
            aggregation = Aggregation.newAggregation(matchOperation, sortOperation, skipOperation, limitOperation);
        }

        return aggregation;
    }

    public static Criteria buildCriteria(String key, String searchTerm) {
        if (key == null || searchTerm == null || searchTerm.isEmpty()) {
            return new Criteria(); // matches all
        }

        switch (key.toLowerCase()) {
            case "username":
                return Criteria.where("username").regex(searchTerm, "i");
            case "email":
                return Criteria.where("email").regex(searchTerm, "i");
            case "roles":
                // Assumes roles is an array of objects with a 'name' field
                return Criteria.where("roles.name").is(searchTerm);
            default:
                // If the key is not recognized, return an empty criteria (matches nothing)
                return Criteria.where("_id").is(null);
        }
    }
}
