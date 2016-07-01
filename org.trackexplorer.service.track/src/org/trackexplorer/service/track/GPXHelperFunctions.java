package org.trackexplorer.service.track;

import java.util.List;
import java.util.stream.Collectors;

import org.alternativevision.gpx.beans.Waypoint;

/**
 * Helper functions for computing various properties (e.g. total distance) of a track.
 * 
 */
public class GPXHelperFunctions {
	public static double M = 1000.0;
	public static double Km = 1.0 / 1000.0;
	
	public static double DistanceInMeter(final Waypoint p1, final Waypoint p2) {
		final double R = 6371000;
		double phi1 = Math.toRadians(p1.getLatitude());
		double phi2 = Math.toRadians(p2.getLatitude());
		double delta_phi = Math.toRadians(p2.getLatitude()-p1.getLatitude());
		double delta_lambda = Math.toRadians(p2.getLongitude()-p1.getLongitude());

		double a = Math.sin(delta_phi/2) * Math.sin(delta_phi/2) +
		        Math.cos(phi1) * Math.cos(phi2) *
		        Math.sin(delta_lambda/2) * Math.sin(delta_lambda/2);
		double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0-a));
		
		return (R * c);		
	}
	
	public static double Elevation(final Waypoint p1, final Waypoint p2) {
		assert((p1.getElevation() != null) && (p2.getElevation() != null));
		double difference = p1.getElevation() - p2.getElevation();
		return (difference > 0 ? difference : 0.0);
	}	
	
	public static double ComputeTotalDistance(final List<Waypoint> waypoints) {
		if(waypoints.size() > 1) {
			return MSF.Join(waypoints, waypoints.subList(1, waypoints.size()), GPXHelperFunctions::DistanceInMeter).stream()
					.reduce(0.0,Double::sum);
		}
		else {
			return 0.0;
		}
	}

	public static double ComputeTotalElevation(final List<Waypoint> waypoints) {
		if(waypoints.size() > 1) {
			// Remove all waypoints for which no elevation is given
			List<Waypoint> points = waypoints.stream()
					.filter(p -> (p.getElevation() != null))
					.collect(Collectors.toList());

			// If there are no points, return immediately
			if(points.size() == 0) {
				return 0.0;
			}

			// Compute average elevation threshold
			double averageElevationThreshold = points.stream()
					.mapToDouble(Waypoint::getElevation)
					.average().orElse(0.0) * 0.25;

			// Filter elevation deltas and compute total
			return MSF.Join(points.subList(1, points.size()), points, GPXHelperFunctions::Elevation).stream()
					.filter(delta -> delta < averageElevationThreshold)
					.reduce(0.0,Double::sum);
		}
		else {
			return 0.0;
		}
	}
}
