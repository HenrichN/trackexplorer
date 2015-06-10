package org.trackexplorer.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

/**
 * The about dialog presents the user with the software version
 * and the licenses for third party libraries.
 * 
 * Receives its information from the {@link AboutHandler}.
 */
public class AboutDialog extends Dialog {
	private final String infoText;
	private final String licenseText;
	private Text text;

	/**
	 * Create the AboutDialog.
	 * 
	 * @param parentShell Reference to the parent shell
	 * @param infoText The info text (e.g. program name, version information, ...)
	 * @param licenseText The license information to be displayed
	 */
	public AboutDialog(Shell parentShell,
			final String infoText,
			final String licenseText) {
		super(parentShell);
		
		this.infoText = infoText;
		this.licenseText = licenseText;
	}

	/**
	 * Create controls consisting of a label and a text area for displaying the license information.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		Label lblLabel = new Label(container, SWT.NONE);
		lblLabel.setText(this.infoText);
		
		Composite composite = new Composite(container, SWT.NONE);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite.heightHint = 8;
		composite.setLayoutData(gd_composite);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("Track Explorer was build using the following awesome software:");
		
		text = new Text(container, SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.heightHint = 280;
		text.setLayoutData(gd_text);
		text.setText(this.licenseText);
				
		return container;
	}
	
	/**
	 * See {@link org.eclipse.jface.window.Window}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("About Track Explorer");				
	}
	
	/**
	 * Prevents creating an 'OK' and 'Cancel' button 
	 */
	@Override
	protected Button createButton(Composite arg0, int arg1, String arg2, boolean arg3) 
	{
		//Return null such that no default buttons like 'OK' and 'Cancel' will be created
		return null;
	}
	
	/**
	 * Set initial size of dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(650, 450);
	}
}
