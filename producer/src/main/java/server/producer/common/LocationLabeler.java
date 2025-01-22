package server.producer.common;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocationLabeler {
	private static ConcurrentHashMap<String, Integer> locationLabel = new ConcurrentHashMap<>();

	public LocationLabeler() {
		locationLabel.put("서대문구", 1);
		locationLabel.put("마포구", 1);
		locationLabel.put("은평구", 1);
	}

	public int findLabelByLocation(String location) {
		int label;
		try{
			label = locationLabel.get(location);
		} catch (NullPointerException e) {
			label = 0;
		}
		return label;
	}
}
