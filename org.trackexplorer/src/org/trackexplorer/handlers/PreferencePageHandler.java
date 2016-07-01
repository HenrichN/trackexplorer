package org.trackexplorer.handlers;

import javax.inject.Named;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.trackexplorer.preferences.PreferenceConstants;
import org.trackexplorer.preferences.PreferencePagePathSelection;

/**
 * Sets up the preference store and connects it to the preference dialog
 * to be displayed.
 */
public class PreferencePageHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		// Create the preference manager
	    PreferenceManager mgr = new PreferenceManager();
	 
	    // Create the nodes
	    PreferenceNode paths = new PreferenceNode("paths", new PreferencePagePathSelection());
	 
	    // Add the nodes
	    mgr.addToRoot(paths);
	 	    
	    // Set the preference store
	    IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, PreferenceConstants.ROOT_NODE_LOCATIONS);	    
	    
	    // Create the preferences dialog
	    PreferenceDialog dlg = new PreferenceDialog(shell, mgr);

	    dlg.setPreferenceStore(store);
	    dlg.open();
	}
}
