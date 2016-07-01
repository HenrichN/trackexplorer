package org.trackexplorer.model;

import java.util.List;

/**
 * A class which represents geographical bounds.
 */
public class Bounds {
	private LatLng southWest;
	private LatLng northEast;
	
	public Bounds() {
		super();
		this.southWest = new LatLng(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		this.northEast = new LatLng(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
	}
	
	/**
	 * Creates a bound using the two given points.
	 * 
	 * It is not checked whether the point {@code southWest}
	 * is really to the south and west of {@code northEast}
	 * and vice versa.
	 */
	public Bounds(final LatLng southWest, final LatLng northEast) {
		super();
		this.southWest = southWest;
		this.northEast = northEast;
	}
		
	/**
	 * Creates a bound which is the tightest fit around
	 * all the given points.
	 */
	public Bounds(final List<LatLng> latLngs) {
		Bounds bounds = latLngs.stream()
						.collect(
								Bounds::new,
								Bounds::extendInplace,
								Bounds::combineInplace);
		
		this.southWest = bounds.getSouthWest();
		this.northEast = bounds.getNorthEast();
	}

	public LatLng getSouthWest() {
		return southWest;
	}

	public LatLng getNorthEast() {
		return northEast;
	}	
	
	/**
	 * Creates a new bound, extended by the given point.
	 */
	public Bounds extend(final LatLng latLng) {
		LatLng newSouthWest = this.extendSoutWest(latLng); 
		LatLng newNorthEast = this.extendNorthEast(latLng);
		
		return new Bounds(newSouthWest, newNorthEast);
	}
	
	/**
	 * Tests whether the given point is inside the bound.
	 */
	public boolean isInside(final LatLng latLng) {
		return isInsideSoutWest(latLng) && isInsideNorthWest(latLng);
	}
	
	private LatLng extendSoutWest(final LatLng latLng) {
		return new LatLng(
				Math.min(latLng.getLatitude(), this.southWest.getLatitude()),
				Math.min(latLng.getLongitude(), this.southWest.getLongitude()));
	}
	
	private LatLng extendNorthEast(final LatLng latLng) {
		return new LatLng(
				Math.max(latLng.getLatitude(), this.northEast.getLatitude()),
				Math.max(latLng.getLongitude(), this.northEast.getLongitude()));
	}
					
	private boolean isInsideSoutWest(final LatLng latLng) {
		return (latLng.getLatitude() > this.getSouthWest().getLatitude()) &&
				(latLng.getLongitude() > this.getSouthWest().getLongitude());
	}

	private boolean isInsideNorthWest(final LatLng latLng) {
		return (latLng.getLatitude() < this.getNorthEast().getLatitude()) &&
				(latLng.getLongitude() < this.getNorthEast().getLongitude());
	}

	private void extendInplace(final LatLng latLng) {
		if(! this.isInsideSoutWest(latLng)) {
			this.southWest = extendSoutWest(latLng);
		}
		
		if(! this.isInsideNorthWest(latLng)) {
			this.northEast = extendNorthEast(latLng);
		}		
	}

	private void combineInplace(final Bounds bounds) {
		if(! this.isInsideSoutWest(bounds.getSouthWest())) {
			this.southWest = extendSoutWest(bounds.getSouthWest());
		}
		
		if(! this.isInsideNorthWest(bounds.getNorthEast())) {
			this.northEast = extendNorthEast(bounds.getNorthEast());
		}		
	} 
	
	public Bounds copy() {
		return new Bounds(this.getSouthWest(), this.getNorthEast());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((northEast == null) ? 0 : northEast.hashCode());
		result = prime * result
				+ ((southWest == null) ? 0 : southWest.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bounds other = (Bounds) obj;
		if (northEast == null) {
			if (other.northEast != null)
				return false;
		} else if (!northEast.equals(other.northEast))
			return false;
		if (southWest == null) {
			if (other.southWest != null)
				return false;
		} else if (!southWest.equals(other.southWest))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return 	"[south west] " + this.southWest + "\n" +
				"[north east] " + this.northEast;				
	}
	
}
