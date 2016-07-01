package org.trackexplorer.model;

import java.nio.file.Path;

/**
 * Bundles some information about a track.
 *
 * The field {@code id} shell be used to uniquely identify the track.
 */
public class TrackMetaInfo {
	private final String id;
	private final String name;
	private final Path path;
	
	public TrackMetaInfo(String id, String name, Path path) {
		super();
		this.id = id;
		this.name = name;
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public Path getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "TrackInfo [id=" + id + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		TrackMetaInfo other = (TrackMetaInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public TrackMetaInfo copy() {
		return new TrackMetaInfo(this.id, this.name, this.path);
	}
}
