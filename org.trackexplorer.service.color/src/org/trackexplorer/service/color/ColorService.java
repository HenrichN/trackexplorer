package org.trackexplorer.service.color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;

import org.trackexplorer.model.IColorService;

/**
 * Reads color values from a file.
 *
 * The first color is treated as the static color.
 * The rest of the colors are used as dynamic colors.
 * The dynamic colors are treated as a circular buffer.
 */
public class ColorService implements IColorService {
	private String staticColor;
	private LinkedList<String> dynamicColors;
	
	public ColorService() {
		// Load colors from file
		dynamicColors = loadColors("colors.txt");
		
		// Treat the first color as the static color
		staticColor =  dynamicColors.remove();
	}
	
	private LinkedList<String> loadColors(String fileName) {
		LinkedList<String> colors;
		
		try {
			// The file is expected to contain a list of hex characters, separated
			// by a newline. These values are not checked!
			URL url = new URL("platform:/plugin/org.trackexplorer.service.color/data/" + fileName);
			InputStream inputStream = url.openConnection().getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			colors = in.lines().collect(LinkedList<String>::new,
					LinkedList<String>::add,
					LinkedList<String>::addAll);
			
			in.close();

		} catch (IOException e) {
			// Provide some default values
			colors = new LinkedList<String>(Arrays.asList("#0000FF","#00FF00", "#FF0000"));
		}	

		return colors;
	}
	
	public String getDynamicColor() {
		return dynamicColors.peek();
	}
	
	public void nextDynamicColor() {
		String color = dynamicColors.remove();
		dynamicColors.addLast(color);	
	}

	@Override
	public String getStaticColor() {		
		return staticColor;
	}	
}

