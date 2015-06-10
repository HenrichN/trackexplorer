package org.trackexplorer.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.trackexplorer.dialogs.AboutDialog;

/**
 * Gathers the version and third party information to be
 * displayed in the {@link AboutDialog}.
 * 
 * License information is read from a separate file.
 */
public class AboutHandler {
	private final String INFO_MESSAGE = "Track Explorer ";
	private final String COPYRIGHT = "Niklas Henrich (www.github.com/henrichn)";
	private final String LICENSE = "Licensed under the MIT license";
	
	private String licenseInfo;
	
	/**
	 * Creates the AboutHandler and reads the licence information from
	 * a local file.
	 */
	public AboutHandler() {
		
		// Try to load the licenses from a file
		try {
			URL url = new URL("platform:/plugin/org.trackexplorer/data/licenses.txt");
			InputStream inputStream = url.openConnection().getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			
			this.licenseInfo = in.lines().collect(Collectors.joining("\n"));

			in.close();

		} catch (IOException e) {
			this.licenseInfo = "";
		}			
	}
	
	/**
	 * Show the dialog
	 */
	@Execute
	public void execute(Shell shell) {
		AboutDialog aboutDialog = new AboutDialog(shell, buildInfoString(), licenseInfo);
		aboutDialog.open();
	}
	
	/**
	 * Concat info message, version information, copyright and license information.
	 */
	private String buildInfoString() {
		return String.join("\n",
				Arrays.asList(INFO_MESSAGE + getVersion(), COPYRIGHT, LICENSE));
		 
	}
	
	/**
	 * Returns the version of the org.trackexplorer bundle.
	 */
	private String getVersion() {
		// We are using the version of the org.trackexplorer bundle to identify
		// the whole program
		return Platform.getBundle("org.trackexplorer").getHeaders().get("Bundle-version");
	}
	
}
