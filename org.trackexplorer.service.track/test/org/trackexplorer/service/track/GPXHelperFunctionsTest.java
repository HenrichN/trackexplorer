package org.trackexplorer.service.track;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.alternativevision.gpx.beans.Waypoint;
import org.junit.Test;

public class GPXHelperFunctionsTest {

	@Test
	public void testDistance() {
		Waypoint p1 = new Waypoint();
		Waypoint p2 = new Waypoint();
				
		p1.setLatitude(40.0);
		p1.setLongitude(40.0);		
		p2.setLatitude(41.0);
		p2.setLongitude(41.0);		
		assertEquals(139700.0, GPXHelperFunctions.DistanceInMeter(p1, p2), 100.0);
		assertEquals(139700.0, GPXHelperFunctions.DistanceInMeter(p2, p1), 100.0);
		
		p1.setLatitude(41.0);
		p1.setLongitude(41.0);		
		p2.setLatitude(41.0);
		p2.setLongitude(41.0);
		assertEquals(0.0, GPXHelperFunctions.DistanceInMeter(p1, p2), 1.0);
	}
	
	@Test
	public void testComputeTotalDistance() {
		Waypoint wp = new Waypoint();
		wp.setLatitude(40.0);
		wp.setLongitude(40.0);		
		
		List<Waypoint> points = IntStream.range(0, 10)
				.mapToObj( v -> wp)
				.collect(Collectors.toList());
		
		assertEquals(0.0, GPXHelperFunctions.ComputeTotalDistance(points), 1.0);
		
		List<Double> lats = Arrays.asList(1.0, 1.1, 1.2);
		List<Double> longs = Arrays.asList(0.0, 0.0, 0.0);
		points = MSF.Join(lats, longs, (lat, lng) -> {
			Waypoint p = new Waypoint();
			p.setLatitude(lat);
			p.setLongitude(lng);
			return p;});
		assertEquals(11.12 * 2.0 * GPXHelperFunctions.M, GPXHelperFunctions.ComputeTotalDistance(points), 100);		
	}
	
	@Test
	public void testComputeTotalHeight() {
		List<Double> heights = Arrays.asList(0.0, 100.0, 200.0, 50.0, 100.0);
		List<Waypoint> points = heights.stream()
				.map(elevation -> {
					Waypoint p = new Waypoint();
					p.setElevation(elevation);
					return p;
				})
				.collect(Collectors.toList());
		assertEquals(250.0, GPXHelperFunctions.ComputeTotalElevation(points), 1.0);
	}
}
