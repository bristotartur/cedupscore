package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.Event;
import com.bristotartur.cedupscore_api.enums.EventType;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class EventSpecifications {
    
    private EventSpecifications() {
    }

    public static Specification<Event> hasType(EventType type) {

        return (root, query, criteria) -> (type != null)
                ? criteria.equal(root.get("type"), type)
                : null;
    }

    public static Specification<Event> hasParticipant(Long participantId) {

        return (root, query, criteria) -> {
            if (participantId == null) return criteria.conjunction();

            var registrationJoin = root.join("registrations", JoinType.LEFT);
            var participantJoin = registrationJoin.join("participant", JoinType.LEFT);

            return criteria.equal(participantJoin.get("id"), participantId);
        };
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