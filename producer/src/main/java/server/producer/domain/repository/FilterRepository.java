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

		Join<House, Room> room = house.join("rooms", JoinType.LEFT); // House와 Room 관계 조인

		List<Predicate> predicates = new ArrayList<>();
		List<Integer> occupancyTypes = filter.occupancyTypes()
				.stream()
				.map(li -> Integer.parseInt(li.replaceAll("[^0-9]", "")))
				.toList();

		// 적합한 구 그룹 찾기
		int label = locationLabeler.findLabelByLocation(filter.location().split(" ")[0]);
		predicates.add(cb.equal(house.get("label"), label));

		// 필수 조건
		predicates.add(cb.between(room.get("deposit"), filter.depositRange().min()*TENTHOUSAND, filter.depositRange().max()*TENTHOUSAND));
		predicates.add(cb.between(room.get("monthlyRent"), filter.monthlyRentRange().min()*TENTHOUSAND, filter.monthlyRentRange().max()*TENTHOUSAND));

		predicates.add(cb.notEqual(room.get("status"), room.get("occupancyType")));
		// Optional 조건: moodTag
		if (filter.moodTag() != null) {
			predicates.add(cb.equal(house.get("moodTag"), filter.moodTag()));
		}

		// Optional 조건: moodTags (리스트, OR 조건)
		if (filter.getMoodTags() != null && !filter.getMoodTags().isEmpty()) {
			List<Predicate> moodTagPredicates = new ArrayList<>();
			for (String moodTag : filter.getMoodTags()) {
				moodTagPredicates.add(cb.equal(house.get("moodTag"), moodTag));
			}
			predicates.add(cb.or(moodTagPredicates.toArray(new Predicate[0])));
		}

		// Optional 조건: roomCapacities (정확히 일치)
		if (filter.getRoomCapacities() != null && !filter.getRoomCapacities().isEmpty()) {
			List<Integer> exactCapacities = filter.getRoomCapacities().stream()
					.map(capacity -> Integer.parseInt(capacity.replace("인실", "")))
					.toList();
			predicates.add(room.get("occupancyType").in(exactCapacities));
		}

		// Optional 조건: minRoomCapacity (4인실 이상)
		if (filter.getMinRoomCapacity() != null) {
			predicates.add(cb.greaterThanOrEqualTo(room.get("occupancyType"), filter.getMinRoomCapacity()));
		}

		// Optional 조건: genderPolicy
		if (filter.genderPolicy() != null && !filter.genderPolicy().isEmpty()) {
			predicates.add(house.get("genderPolicy").in(filter.genderPolicy()));
		}

		// Optional 조건: contractPeriod
		if (filter.contractPeriod() != null && !filter.contractPeriod().isEmpty()) {
			predicates.add(house.get("contractTerm").in(filter.contractPeriod()));
		}

		if (!occupancyTypes.isEmpty()) {
			predicates.add(room.get("occupancyType").in(occupancyTypes));
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
		System.out.println(typedQuery.unwrap(org.hibernate.query.Query.class).getQueryString());
		return typedQuery.getResultList();
	}
}
