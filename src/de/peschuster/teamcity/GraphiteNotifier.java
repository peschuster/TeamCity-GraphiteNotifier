package de.peschuster.teamcity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jetbrains.buildServer.notification.*;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.UserPropertyInfo;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.SVcsModification;

public class GraphiteNotifier extends NotificatorAdapter {
	
	private static final int GraphiteDefaultPort = 2003;
	
	private static final String TYPE = "GraphiteNotifier";
	
	private static final String GRAPHITE_HOST = "GRAPHITE_HOST";
	private static final String GRAPHITE_PORT = "GRAPHITE_PORT";
	private static final String GRAPHITE_KEY_PREFIX = "GRAPHITE_KEY_PREFIX";

	public GraphiteNotifier(NotificatorRegistry notificatorRegistry) {
		ArrayList<UserPropertyInfo> userProps = new ArrayList<UserPropertyInfo>();
		
        userProps.add(new UserPropertyInfo(GRAPHITE_HOST, "Host/IP"));
        userProps.add(new UserPropertyInfo(GRAPHITE_PORT, "Port"));
        userProps.add(new UserPropertyInfo(GRAPHITE_KEY_PREFIX, "Prefix key"));
        
        notificatorRegistry.register(this, userProps);
	}
	
	@Override
	public String getNotificatorType() {
		// Unique key of the notifier
		return TYPE;
	}
	
	public String getDisplayName() {
		return "Graphite Notifier";
	}
	
	public void notifyBuildSuccessful(SRunningBuild build, Set<SUser> users) {
		for (SUser user : users) {
			ReportEvent(build, user, "success");
		}
	}

	public void notifyBuildFailed(SRunningBuild build, Set<SUser> users) {
		for (SUser user : users) {
			ReportEvent(build, user, "failed");
		}
	}
	
	protected void ReportEvent(SRunningBuild build, SUser user, String buildKey) {
		String host, port, keyPrefix;
		
		host = user.getPropertyValue(new NotificatorPropertyKey(TYPE, GRAPHITE_HOST));
		port = user.getPropertyValue(new NotificatorPropertyKey(TYPE, GRAPHITE_PORT));
		keyPrefix = user.getPropertyValue(new NotificatorPropertyKey(TYPE, GRAPHITE_KEY_PREFIX));
		
		int portNumber;
		try {
			portNumber = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			portNumber = GraphiteDefaultPort;
		}
		
		String projectId = build.getProjectExternalId();
        String buildConfigId = build.getBuildType().getExternalId();
        
        String key = keyPrefix + "." + projectId + "." + buildConfigId + ".";
        
        List<SVcsModification> changes = build.getContainingChanges();
        
        try 
        {
	        // create socket.
	        GraphiteAdapter graphite = new GraphiteAdapter(host, portNumber);
	        
	        graphite.Report(key + buildKey, 1);
	                
	        if (changes != null) {
	        	graphite.Report(key + "changes", changes.size());
	        }
	        
	        // close socket.
	        graphite.Dispose();
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
}
