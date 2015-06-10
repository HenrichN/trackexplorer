package org.trackexplorer.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PathEditor;

/**
 * Shows a dialog which lets the user add and remove file locations
 */
public class PreferencePagePathSelection extends FieldEditorPreferencePage{

	public PreferencePagePathSelection() {
		// Use the "flat" layout
		super(FLAT);
		this.setTitle("Locations");
	}
	  
	@Override
	protected void createFieldEditors() {	    
	    PathEditor pathFe = new PathEditor(PreferenceConstants.NODE_LOCATIONS,
	    		"GPX locations",
	    		"Choose path",
	    		getFieldEditorParent());
	    addField(pathFe);
	}

}
