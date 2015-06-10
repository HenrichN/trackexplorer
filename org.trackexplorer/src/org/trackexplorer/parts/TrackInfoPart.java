package org.trackexplorer.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.trackexplorer.events.TrackExplorerEventConstants;
import org.trackexplorer.model.ITrackService;
import org.trackexplorer.model.TrackMetaInfo;

/**
 * The TrackInfoPart is responsible for displaying 
 * informations about the current selected track.
 * 
 * This information contains the name, total distance
 * and total elevation.
 */
public class TrackInfoPart {
	@Inject
	private ITrackService trackService;

	private Label lblDataName;
	private Label lblDataTotalDistance;
	private Label lblDataTotalElevation;
	
	/**
	 * Create controls for displaying the name, total distance
	 * and total elevation of a track.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblName = new Label(parent, SWT.NONE);
		lblName.setText("Name:");
		
		lblDataName = new Label(parent, SWT.NONE);
		lblDataName.setLayoutData(new RowData(426, 14));
		lblDataName.setText("No track selected");
		
		Label lblTotalDistance = new Label(parent, SWT.NONE);
		lblTotalDistance.setText("Total distance:");
		
		lblDataTotalDistance = new Label(parent, SWT.NONE);
		lblDataTotalDistance.setText("-");
		lblDataTotalDistance.setLayoutData(new RowData(82, SWT.DEFAULT));
		
		Label lblTotalElevation = new Label(parent, SWT.NONE);
		lblTotalElevation.setText("Total elevation:");
		
		lblDataTotalElevation = new Label(parent, SWT.NONE);		
		lblDataTotalElevation.setText("-");
		lblDataTotalElevation.setLayoutData(new RowData(88, SWT.DEFAULT));
	}
	
	/**
	 * Invoked whenever a track shall be displayed.
	 */
	@Inject
	@Optional
	private void subscribeShowTrack(final @UIEventTopic(TrackExplorerEventConstants.SHOW_TRACK) TrackMetaInfo trackInfo) {
		if(trackInfo != null) {
			lblDataName.setText(trackInfo.getName());
			
			double totalDistance = trackService.getTotalDistanceInKilometer(trackInfo.getId());
			String distanceString = String.format("%.2fkm", totalDistance);
			lblDataTotalDistance.setText(distanceString);
			
			double totalElevation = trackService.getTotalElevation(trackInfo.getId());
			String elevationString = String.format("%.2fm", totalElevation);
			lblDataTotalElevation.setText(elevationString);
		}
	}

}
