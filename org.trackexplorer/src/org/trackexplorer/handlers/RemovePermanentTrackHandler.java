package org.trackexplorer.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.trackexplorer.events.TrackExplorerEventConstants;
import org.trackexplorer.model.TrackMetaInfo;

/**
 * Sends the event {@code PERMANENT_TRACK_REMOVED} on the bus.
 */
public class RemovePermanentTrackHandler {
	@Execute
	public void execute(final @Optional @Named(IServiceConstants.ACTIVE_SELECTION) TrackMetaInfo trackInfo,
			IEventBroker broker) {
		broker.post(TrackExplorerEventConstants.PERMANENT_TRACK_REMOVED, trackInfo);
	}
}
