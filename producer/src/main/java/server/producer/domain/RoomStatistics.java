package server.producer.domain;

import server.producer.entity.Room;

import java.util.List;
import java.util.stream.Collectors;

public class RoomStatistics {
	public static String calculateMonthlyRent(List<Room> rooms) {
		int minMonthlyRent = rooms.stream()
				.mapToInt(Room::getMonthlyRent)
				.min()
				.orElse(10000000); // 기본값

		int maxMonthlyRent = rooms.stream()
				.mapToInt(Room::getMonthlyRent)
				.max()
				.orElse(0); // 기본값
		return (minMonthlyRent/10000) + "~" + (maxMonthlyRent/10000);
	}

	public static String calculateDeposit(List<Room> rooms) {
		int minDeposit = rooms.stream()
				.mapToInt(Room::getDeposit)
				.min()
				.orElse(10000000); // 기본값

		int maxDeposit = rooms.stream()
				.mapToInt(Room::getDeposit)
				.max()
				.orElse(0); // 기본값
		return (minDeposit/10000) + "~" + (maxDeposit/10000);
	}

	public static String calculateOccupancyType(List<Room> rooms) {
		List<Integer> occupancyTypes = rooms.stream()
				.map(Room::getOccupancyType)
				.distinct()
				.sorted()
				.toList();
		return occupancyTypes.stream()
				.map(String::valueOf)
				.collect(Collectors.joining(",")) + "인실";
	}
}
