package org.trackexplorer.model;

/**
 * The IColorService provides colors for track.
 * 
 * All colors are stored as hex values, in the format {@code #XXXXXX}.
 * 
 * The static color shall not change.
 * 
 * Dynamic colors shall consist of a list of colors of unspecified length.
 */
public interface IColorService {
	/**
	 * Returns the static color.
	 * The static color shall remain the same
	 * for each invocation of this function.
	 * 
	 * @return A color in hex format {@code #XXXXXX}. 
	 */
	public String getStaticColor();
	
	
	/**
	 * Returns the current dynamic color.
	 * 
	 * The next dynamic color can be selected with {@link nextDynamicColor}.
	 * 
	 * @return A color in hex format {@code #XXXXXX}.
	 */
	public String getDynamicColor();
	
	/**
	 * Advances to the next dynamic color. 
	 * 
	 * Use {@link getDynamicColor} to retrieve it.
	 */
	public void nextDynamicColor();
}
