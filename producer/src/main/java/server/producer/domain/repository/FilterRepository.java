package server.producer.domain.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import server.producer.common.LocationLabeler;
import server.producer.domain.dto.request.FilterRequestDto;
import entity.House;
import entity.Room;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilterRepository {
    private final EntityManager entityManager;
    public final LocationLabeler locationLabeler;
    private final int TENTHOUSAND = 10000;

    public List<House> findFilteredHouses(FilterRequestDto filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<House> query = cb.createQuery(House.class);
        Root<House> house = query.from(House.class);

        Join<House, Room> room = house.join("rooms", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        List<Integer> occupancyTypes = filter.getOccupancyTypes()
                .stream()
                .map(li -> Integer.parseInt(li.replaceAll("[^0-9]", "")))
                .toList();

        // 필수 조건
        predicates.add(cb.between(room.get("deposit"), 
            filter.getDepositRange().getMin() * TENTHOUSAND, 
            filter.getDepositRange().getMax() * TENTHOUSAND));
        predicates.add(cb.between(room.get("monthlyRent"), 
            filter.getMonthlyRentRange().getMin() * TENTHOUSAND, 
            filter.getMonthlyRentRange().getMax() * TENTHOUSAND));

        // predicates.add(cb.notEqual(room.get("status"), room.get("occupancyType")));

        // Optional 조건: moodTags (리스트, OR 조건)
        if (filter.getMoodTags() != null && !filter.getMoodTags().isEmpty()) {
            List<Predicate> moodTagPredicates = new ArrayList<>();
            for (String moodTag : filter.getMoodTags()) {
                moodTagPredicates.add(cb.equal(house.get("moodTag"), moodTag));
            }
            predicates.add(cb.or(moodTagPredicates.toArray(new Predicate[0])));
        }

        // Optional 조건: occupancyTypes
        if (!occupancyTypes.isEmpty()) {
            predicates.add(room.get("occupancyType").in(occupancyTypes));
        }

        // Optional 조건: genderPolicy
        if (filter.getGenderPolicy() != null && !filter.getGenderPolicy().isEmpty()) {
            predicates.add(house.get("genderPolicy").in(filter.getGenderPolicy()));
        }

        // Optional 조건: contractPeriod
        if (filter.getContractPeriod() != null && !filter.getContractPeriod().isEmpty()) {
            predicates.add(house.get("contractTerm").in(filter.getContractPeriod()));
        }

        // Optional 조건: preferredDate
        if (filter.getPreferredDate() != null) {
            predicates.add(cb.or(
                room.get("contractPeriod").isNull(),
                cb.lessThanOrEqualTo(room.get("contractPeriod"), filter.getPreferredDate())
            ));
        }

        query.select(house).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));
        TypedQuery<House> typedQuery = entityManager.createQuery(query);
        System.out.println(typedQuery.unwrap(org.hibernate.query.Query.class).getQueryString());
        return typedQuery.getResultList();
    }
}
