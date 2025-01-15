package server.producer.domain;

import server.producer.entity.House;
import server.producer.entity.Room;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public static String calculateOccupancyStatus(List<Room> rooms) {
		int max = 0;
		int current = 0;
		for (Room room : rooms) {
			current += room.getStatus();
			max += room.getOccupancyType();
		}
		return current + "/" + max;
	}

	public static List<String> mergeTags(House house) {
		String moodTag = house.getMoodTag();
		String subTag = house.getSubMoodTag();
		List<String> tags = Arrays.stream((moodTag+" "+ subTag).split(" ")).toList();
		return tags;
	}
}
