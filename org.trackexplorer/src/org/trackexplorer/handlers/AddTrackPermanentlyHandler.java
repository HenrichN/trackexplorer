package org.trackexplorer.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.trackexplorer.events.TrackExplorerEventConstants;
import org.trackexplorer.model.DrawableTrackMetaInfo;
import org.trackexplorer.model.IColorService;
import org.trackexplorer.model.TrackMetaInfo;

/**
 * Sets the color for the permanent track and sends the event {@code PERMANENT_TRACK_ADDED} on the bus.
 */
public class AddTrackPermanentlyHandler {	
	@Inject
	private IColorService colorService;
		
	@Execute
	public void execute(final @Optional @Named(IServiceConstants.ACTIVE_SELECTION) TrackMetaInfo trackInfo,
			IEventBroker broker) {
		// Only execute the command if data is available
		if(trackInfo != null) {
			String color = this.colorService.getDynamicColor();			
			DrawableTrackMetaInfo drawableTrackInfo = new DrawableTrackMetaInfo(trackInfo, color);
			
			broker.post(TrackExplorerEventConstants.PERMANENT_TRACK_ADDED, drawableTrackInfo);
			
			this.colorService.nextDynamicColor();
		}
	}
}
