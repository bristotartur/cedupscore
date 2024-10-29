package com.bristotartur.cedupscore_api.repositories;

import org.springframework.data.jpa.domain.Specification;

import com.bristotartur.cedupscore_api.domain.Event;
import com.bristotartur.cedupscore_api.enums.EventType;

import jakarta.persistence.criteria.JoinType;

public final class EventSpecifications {
    
    private EventSpecifications() {
    }

    public static Specification<Event> hasType(EventType type) {

        return (root, query, criteria) -> (type != null)
                ? criteria.equal(root.get("type"), type)
                : null;
    }

    public static Specification<Event> fromEdition(Long editionId) {

        return (root, query, criteria) -> {
            if (editionId == null) return null;

            var editionJoin = root.join("edition", JoinType.LEFT);
            return criteria.equal(editionJoin.get("id"), editionId);
        };
    }

    public static Specification<Event> fromUser(Long userId) {

        return (root, query, criteria) -> {
            if (userId == null) return null;

            var userJoin = root.join("responsibleUser", JoinType.LEFT);
            return criteria.equal(userJoin.get("id"), userId);
        };
    }

}