package com.prakhar.serverOrchestration;

public interface Orchestrator 
{
	
	public boolean addServer(serverInstance server);
	 
	public boolean prepare();
 
	public boolean spawn_cluster();
 
	public boolean instance_down(serverInstance server);

}
