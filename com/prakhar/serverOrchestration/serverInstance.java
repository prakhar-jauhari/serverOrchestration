package com.prakhar.serverOrchestration;

import java.util.Set;

public class serverInstance implements Server
{
	
	private String serverName;
	private Set<String> serverDependencySet;

	serverInstance(String name, Set<String> serverDependency)
	{
		serverName = name;
		serverDependencySet = serverDependency;
	}
	
	@Override
	public String getName() {
		return serverName;
	}

	@Override
	public Set<String> getDependencies() {
		return serverDependencySet;
	}

}
