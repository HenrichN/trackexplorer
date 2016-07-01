package org.trackexplorer.parts.overview.permanenttracks;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

/**
 * Converts colors given in hex format into 
 * color resources managed by the operating system.
 * 
 * The converted colors are cached and 
 * freed automatically when the parent
 * {@code Composite} is disposed.
 */
public class ColorResourceManager {
	private LocalResourceManager resManager;
	private Map<String, Color> colors;
	
	public ColorResourceManager(Composite parent) {
		colors = new HashMap<>();
		this.resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
	}
	
	/**
	 * @param color Given in hex format {@code #XXXXXX}
	 * @return The color object representing the given color
	 */
	public Color getColor(String color) {
		return this.colors.computeIfAbsent(color, val -> resManager.createColor(rgbFromHex(val)));
	}
	
	private RGB rgbFromHex(String color) {
		return new RGB(Integer.parseInt(color.substring(1, 3), 16),
				Integer.parseInt(color.substring(3, 5), 16),
				Integer.parseInt(color.substring(5, 7), 16));
	}
}
