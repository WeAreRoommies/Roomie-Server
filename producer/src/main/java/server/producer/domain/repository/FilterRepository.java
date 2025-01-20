package server.producer.domain.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import server.producer.domain.dto.request.FilterRequestDto;
import entity.House;
import entity.Room;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilterRepository {
	private final EntityManager entityManager;

	public List<House> findFilteredHouses(FilterRequestDto filter) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<House> query = cb.createQuery(House.class);
		Root<House> house = query.from(House.class);
		Join<House, Room> room = house.join("rooms", JoinType.LEFT); // House와 Room 관계 조인

		List<Predicate> predicates = new ArrayList<>();

		// 필수 조건
		predicates.add(cb.equal(house.get("location"), filter.location()));
		predicates.add(cb.between(room.get("deposit"), filter.depositRange().min(), filter.depositRange().max()));
		predicates.add(cb.between(room.get("monthlyRent"), filter.monthlyRentRange().min(), filter.monthlyRentRange().max()));

		// Optional 조건: moodTag
		if (filter.moodTag() != null) {
			predicates.add(cb.equal(house.get("moodTag"), filter.moodTag()));
		}

		// Optional 조건: genderPolicy
		if (filter.genderPolicy() != null && !filter.genderPolicy().isEmpty()) {
			predicates.add(house.get("genderPolicy").in(filter.genderPolicy()));
		}

		// Optional 조건: contractPeriod
		if (filter.contractPeriod() != null && !filter.contractPeriod().isEmpty()) {
			predicates.add(house.get("contractTerm").in(filter.contractPeriod()));
		}

		// Optional 조건: room 조건
		if (filter.preferredDate() != null) {
			predicates.add(cb.or(
					room.get("contractPeriod").isNull(), // 계약 기간이 null인 경우 조건 만족
					cb.lessThanOrEqualTo(room.get("contractPeriod"), filter.preferredDate()) // 계약기간이 preferredDate 이전이나 같을 경우 조건 만족
			));
		}

		query.select(house).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<House> typedQuery = entityManager.createQuery(query);
		return typedQuery.getResultList();
	}
}
