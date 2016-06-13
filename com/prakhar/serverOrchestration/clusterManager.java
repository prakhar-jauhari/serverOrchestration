package com.prakhar.serverOrchestration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class clusterManager implements Orchestrator{

	private static final Logger LOG = Logger.getLogger(clusterManager.class.getName());
	private Map<String, serverInstance> servers = new HashMap<String, serverInstance>();
	private DirectedGraph<serverInstance,serverInstance> serverDAG = null;
 
	@Override
	public boolean addServer(serverInstance server) 
	{
		LOG.info("Adding server: " + server.getName());
		
		if (servers.put(server.getName(), server) != null) 
		{
			throw new RuntimeException("Server with name: " + server.getName() + " already exists \n");
		}
		return buildGraph();
	}

	@Override
	public boolean prepare() {
		LOG.info("Creating a DAG to plot server dependencies");
		buildGraph();
		//System.out.println(serverDAG.toString());
		return true;
	}
	
	@Override
	public boolean spawn_cluster() 
	{
		boolean res = false;
		if (serverDAG.vertexSet().size() == 0) 
		{
			LOG.info("Cluster empty, nothing to spawn\n");
		}
		synchronized (serverDAG ) 
		{
    		Iterator iter = new TopologicalOrderIterator(serverDAG);
 
    		while(iter.hasNext()) 
    		{
    			serverInstance server = (serverInstance) iter.next();
    			LOG.info("Spawning   :  " + server.getName());
        		//Code to spawn the server in cluster
    		}
    		res = true;
		}
		return res;
	}

	@Override
	public boolean instance_down(serverInstance server)
	{
		boolean res = false;
		if (serverDAG.vertexSet().size() == 0) 
		{
			LOG.info("No cluster config present,ignoring server : " + server.getName() +" instance dowm notice\n");
			res = true;
		}
		synchronized (serverDAG ) 
		{
			Set<String> connectedNodes = new HashSet<String>(); 
			Stack<serverInstance> relaunchOrder = new Stack<serverInstance>();
			Stack<serverInstance> stopOrder = new Stack<serverInstance>();
			Iterator bfi = new BreadthFirstIterator(serverDAG, server); 
			while (bfi.hasNext())
			{
				serverInstance ser = (serverInstance) bfi.next();
				connectedNodes.add(ser.getName());
			}
			//System.out.println(connectedNodes.toString());
			if (connectedNodes.size() == 1)
			{
				//the server is a disconnected vertex on graph, safe to just re-launch it.
				LOG.info("Relaunching instamce of  :  " + server.getName());
				res = true;
			}
			else
			{
				// Stop nodes dependent on server in reverse topological order and re-launch in topological order. 
				Iterator iter = new TopologicalOrderIterator(serverDAG);
	    		while (iter.hasNext())
	    		{
	    			serverInstance ser = (serverInstance) iter.next();
	    			//System.out.println("server  :  " + ser.getName());
	    			if (connectedNodes.contains(ser.getName()))
	    			{
	    				stopOrder.push(ser);
	    			}
	    		}
				
	    		while(!stopOrder.empty())
	    		{
	    			serverInstance ser = stopOrder.pop();
	    			relaunchOrder.push(ser);
	    			if (ser != server)
	    				// Code to stop instance of your server
	    				LOG.info("Stoping server  :  " + ser.getName());
	    		}
	    		while(!relaunchOrder.empty())
	    		{
	    			// Code to launch instance of your server
	    			LOG.info("launching new instance of :  " + relaunchOrder.pop().getName());
	    		}
	    		res = true;
			}
		}
		return res;
	}
	
	private boolean buildGraph() 
	{
		LOG.info("Building a DAG to plot server dependencies");
		DirectedGraph tempDAG = new SimpleDirectedGraph(DefaultEdge.class);
		
		LOG.info("Adding server as nodes\n");
		for (serverInstance server: servers.values()) 
		{
			tempDAG.addVertex(server);
		}
 
		LOG.info("Adding Relationships of server\n");
		
		for (serverInstance server: servers.values()) 
		{ 
			if (server.getDependencies() != null) 
			{
    			for (String depend: server.getDependencies()) 
    			{
    				serverInstance dependOnServer = servers.get(depend);
 
    				LOG.info("Adding relationship between " + server.getName() + " and " + dependOnServer.getName());
    				tempDAG.addEdge(dependOnServer, server);
    			}
			}
		}
		CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(tempDAG);
        if (cycleDetector.detectCycles()) 
        {
        	LOG.info("Cycle, detected on adding server to DAG, cannot add\n");
        	return false;
        }
        else
        {
        	serverDAG = tempDAG;
        	return true;
        }
	}
}

