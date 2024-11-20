package com.bristotartur.cedupscore_api.repositories;

import com.bristotartur.cedupscore_api.domain.EditionRegistration;
import com.bristotartur.cedupscore_api.domain.EventRegistration;
import com.bristotartur.cedupscore_api.domain.Participant;
import com.bristotartur.cedupscore_api.enums.Gender;
import com.bristotartur.cedupscore_api.enums.ParticipantType;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public final class ParticipantSpecifications {

    private ParticipantSpecifications() {
    }

    public static Specification<Participant> hasName(String name) {

        return (root, query, criteria) -> (name != null && !name.isEmpty())
                ? criteria.like(criteria.upper(root.get("name")), "%" + name.toUpperCase() + "%")
                : null;
    }

    public static Specification<Participant> fromEdition(Long editionId) {

        return (root, query, criteria) -> {
            if (editionId == null) return null;

            var registrationJoin = root.join("editionRegistrations", JoinType.LEFT);
            var editionJoin = registrationJoin.join("edition", JoinType.LEFT);

            return criteria.equal(editionJoin.get("id"), editionId);
        };
    }

    public static Specification<Participant> fromEvent(Long eventId, Long editionId) {

        return (root, query, criteria) -> {
            if (eventId == null) return null;

            var registrationJoin = root.join("eventRegistrations", JoinType.LEFT);
            var eventJoin = registrationJoin.join("event", JoinType.LEFT);

            if (editionId != null) {
                var editionJoin = eventJoin.join("edition", JoinType.LEFT);

                return criteria.and(
                        criteria.equal(eventJoin.get("id"), eventId),
                        criteria.equal(editionJoin.get("id"), editionId)
                );
            }
            return criteria.equal(eventJoin.get("id"), eventId);
        };
    }

    public static Specification<Participant> notFromEvent(Long eventId, Long editionId) {
        return (root, query, criteria) -> {
            if (eventId == null) return null;

            query.distinct(true);

            var subquery = query.subquery(Participant.class);
            var subRoot = subquery.from(EventRegistration.class);
            var subEvent = subRoot.join("event", JoinType.LEFT);

            if (editionId != null) {
                var subEdition = subEvent.join("edition", JoinType.LEFT);

                subquery.select(subRoot.get("participant"))
                        .where(criteria.and(
                                criteria.equal(subEdition.get("id"), editionId),
                                criteria.equal(subEvent.get("id"), eventId)
                        ));
            } else {
                subquery.select(subRoot.get("participant"))
                        .where(criteria.equal(subEvent.get("id"), eventId));
            }
            return criteria.not(criteria.in(root).value(subquery));
        };
    }

    public static Specification<Participant> fromTeam(Long teamId, Long editionId) {

        return (root, query, criteria) -> {
            if (teamId == null) return null;

            var registrationJoin = root.join("editionRegistrations", JoinType.LEFT);
            var teamJoin = registrationJoin.join("team", JoinType.LEFT);

            if (editionId == null) {
                var subquery = query.subquery(Long.class);
                var subRoot = subquery.from(EditionRegistration.class);

                subquery.select(criteria.max(subRoot.get("id")));
                subquery.where(criteria.equal(subRoot.get("participant"), root));

                return criteria.and(
                        criteria.equal(teamJoin.get("id"), teamId),
                        criteria.equal(registrationJoin.get("id"), subquery)
                );
            }
            return criteria.and(
                    criteria.equal(registrationJoin.get("edition").get("id"), editionId),
                    criteria.equal(teamJoin.get("id"), teamId)
            );
        };
    }

    public static Specification<Participant> hasGender(Gender gender) {

        return (root, query, criteria) -> (gender != null)
                ? criteria.like(criteria.upper(root.get("gender")), gender.name())
                : null;
    }

    public static Specification<Participant> hasType(ParticipantType type) {

        return (root, query, criteria) -> (type != null)
                ? criteria.like(criteria.upper(root.get("type")), type.name())
                : null;
    }

    public static Specification<Participant> hasStatus(String status) {

        return (root, query, criteria) -> {
            if (status == null) return null;

            var isActive = status.equals("active");
            return criteria.equal(root.get("isActive"), isActive);
        };
    }

    public static Specification<Participant> hasEditionRegistrationCount(int count) {

        return (root, query, criteria) -> {
            if (count < 0) return null;
        
            var subquery = query.subquery(Long.class);
            var subRoot = subquery.from(EditionRegistration.class);
        
            subquery.select(criteria.count(subRoot.get("id")));
            subquery.where(criteria.equal(subRoot.get("participant"), root));
        
            return criteria.equal(subquery, (long) count);
        };
    }

    public static Specification<Participant> withoutIds(List<Long> excludeIds) {

        return ((root, query, criteria) -> {
           if (excludeIds == null || excludeIds.isEmpty()) {
               return criteria.conjunction();
           }
           return criteria.not(root.get("id").in(excludeIds));
        });
    }

}
