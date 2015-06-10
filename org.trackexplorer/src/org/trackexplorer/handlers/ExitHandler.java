package org.trackexplorer.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;

/**
 * Closes the application.
 */
public class ExitHandler {
	@Execute
	public void execute(IWorkbench workbench) {
		workbench.close();
	}
}
