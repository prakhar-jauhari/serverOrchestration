package serverOrchestration;

import java.util.HashSet;
import java.util.Set;

public class deployMyCluster {

	public static void main(String[] args) {
		
		clusterManager CM = new clusterManager();
		Set<String> aDep = new HashSet<String>();
		aDep.add("B");
		aDep.add("C");
		Set<String> bDep = new HashSet<String>();
		bDep.add("C");
		Set<String> cDep = new HashSet<String>();
		Set<String> dDep = new HashSet<String>();
		dDep.add("A");
		Set<String> eDep = new HashSet<String>();
		//eDep.add("B");
		//eDep.add("A");
		
		serverInstance A = new serverInstance("A", aDep);
		serverInstance B = new serverInstance("B", bDep);
		serverInstance C = new serverInstance("C", cDep);
		serverInstance D = new serverInstance("D", dDep);
		serverInstance E = new serverInstance("E", eDep);
		
		CM.addServer(C);
		CM.addServer(B);
		CM.addServer(A);
		CM.addServer(D);
		CM.addServer(E);
		
		CM.prepare();
		CM.spawn_cluster();
		
		CM.instance_down(B);
		//CM.instance_down(E);
		
	}

}
