package org.trackexplorer.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Tests for the {@link Bounds} class.
 *
 */
public class TestBounds {

	@Test
	public void testExtend() {
		Bounds b = new Bounds(new LatLng(-5.0, -5.0),
				new LatLng(5.0, 5.0));
		
		// North
		assertEquals(
				new Bounds(new LatLng(-5.0, -5.0), new LatLng(5.0, 6.0)),
				b.extend(new LatLng(2.0, 6.0)));
		
		// North east
		assertEquals(
				new Bounds(new LatLng(-5.0, -5.0), new LatLng(6.0, 6.0)),
				b.extend(new LatLng(6.0, 6.0)));
		
		// East
		assertEquals(
				new Bounds(new LatLng(-5.0, -5.0), new LatLng(6.0, 5.0)),
				b.extend(new LatLng(6.0, -3.0)));
		
		// South east
		assertEquals(
				new Bounds(new LatLng(-5.0, -5.0), new LatLng(6.0, 6.0)),
				b.extend(new LatLng(6.0, 6.0)));
		
		// South
		assertEquals(
				new Bounds(new LatLng(-15.0, -5.0), new LatLng(5.0, 5.0)),
				b.extend(new LatLng(-15.0, 2.0)));

		// South west
		assertEquals(
				new Bounds(new LatLng(-20.0, -22.0), new LatLng(5.0, 5.0)),
				b.extend(new LatLng(-20.0, -22.0)));

		// West
		assertEquals(
				new Bounds(new LatLng(-20.0, -5.0), new LatLng(5.0, 5.0)),
				b.extend(new LatLng(-20.0, 0.0)));

		// North west
		assertEquals(
				new Bounds(new LatLng(-20.0, -5.0), new LatLng(5.0, 10.0)),
				b.extend(new LatLng(-20.0, 10.0)));
	}

	@Test
	public void testIsInside() {
		Bounds b = new Bounds(new LatLng(-1.0,-1.0), new LatLng(1.0, 1.0));
		assertTrue(b.isInside(new LatLng(0,0)));
		assertTrue(b.isInside(new LatLng(-0.5,0.9)));
		
		assertFalse(b.isInside(new LatLng(-1.5,0.9)));
		assertFalse(b.isInside(new LatLng(1.5,0.9)));
		assertFalse(b.isInside(new LatLng(0.9,-1.5)));
		assertFalse(b.isInside(new LatLng(0.9,1.5)));
		
		assertFalse(b.isInside(new LatLng(-1.5,1.5)));
	}
	
	@Test
	public void testBoundForWayPoints() {
		List<LatLng> wayPoints = Arrays.asList(
				new LatLng(0, 0),
				new LatLng(-1, 0),
				new LatLng(-0.5, 0),
				new LatLng(-0.2, 1),
				new LatLng(1.0, 0.5),
				new LatLng(0.1, 0.1),
				new LatLng(0.1, -1.0));
		 
		assertEquals(
				new Bounds(new LatLng(-1.0, -1.0),
						new LatLng(1.0, 1.0)),
				new Bounds(wayPoints));
		
		wayPoints = Arrays.asList(
				new LatLng(0, 0),
				new LatLng(-1, 0),
				new LatLng(-0.5, 0),
				new LatLng(-0.2, 0),
				new LatLng(1.0, 0.5),
				new LatLng(0.1, 0.1),
				new LatLng(0.1, -1.0));
		
		assertEquals(
				new Bounds(new LatLng(-1.0, -1.0),
						new LatLng(1.0, 0.5)),
				new Bounds(wayPoints));
		
		wayPoints = Arrays.asList(
				new LatLng(0, 0),
				new LatLng(-1, 0),
				new LatLng(-10.0, 10.0),
				new LatLng(-0.2, 0),
				new LatLng(10.0, -10.0),
				new LatLng(0.1, 0.1),
				new LatLng(0.1, -1.0));
		
		assertEquals(
				new Bounds(new LatLng(-10.0, -10.0),
						new LatLng(10.0, 10.0)),
				new Bounds(wayPoints));

	}
}
