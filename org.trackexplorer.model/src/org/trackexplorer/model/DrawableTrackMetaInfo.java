package org.trackexplorer.model;

import java.nio.file.Path;

/**
 * Extends the class {@link TrackMetaInfo} with an attribute 
 * to capture a color.
 * 
 * The color can be used when drawing the track.
 * The value of the color is expected to be a hex value of the form {@code #XXXXXX}.
 */
public class DrawableTrackMetaInfo extends TrackMetaInfo {

	private String color;
	
	public DrawableTrackMetaInfo(String id, String name, Path path, String color) {
		super(id, name, path);
		this.color = color;
	}
	
	public DrawableTrackMetaInfo(TrackMetaInfo trackInfo, String color) {
		this(trackInfo.getId(), trackInfo.getName(), trackInfo.getPath(), color);
	}

	public String getColor() {
		return color;
	}
}
