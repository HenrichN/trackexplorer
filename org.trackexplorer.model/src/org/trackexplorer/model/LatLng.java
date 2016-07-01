package org.trackexplorer.model;

/**
 * A class for representing a pair of latitude / longitude values.
 */
public class LatLng {
	private final double lat;
	private final double lng;
	
	public LatLng(double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}

	public double getLatitude() {
		return this.lat;
	}
	
	public double getLongitude() {
		return this.lng;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lng);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		LatLng other = (LatLng) obj;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "[" + lat + "," + lng + "]";
	}	
}
