package serverOrchestration;

import java.util.Set;

public interface Server 
{
		public String getName();
		public Set<String> getDependencies();
}
