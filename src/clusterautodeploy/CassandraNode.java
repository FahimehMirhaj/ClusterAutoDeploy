package clusterautodeploy;

import java.awt.Point;
import java.util.List;

public abstract class CassandraNode {
	
	public static final int MIN_JAVA_VERSION = 7;
	
	
        private Point point;
        
	// ssh properties
	private String ip_address;
	private String user;
	private String password;
	
	// The IP address or hostname that other Cassandra nodes use to connect to this node.
	// Setter, Getter provided
	private String listen_address;
	
	/*
	 * The listen address for client connections (Thrift remote procedure calls). Valid values are:
			- 0.0.0.0 : Listens on all configured interfaces.
			- IP address
	 * */ 
	// Setter, Getter provided
	private String rpc_address;
	
	// This is the path where the cassandra is going to be installed
	// NOTE: for java installation (if needed), the default path is going to be used!
	// Setter, Getter provided
	private String installation_location;// = "~/cassandra" which is indeed "/home/Fahimeh/cassandra";
	
	
	// constructor
	public CassandraNode() {
		// default place where cassandra is going to be installed
	}
	
	
	// This method checks and see if the java is installed on the host or is it at least 1.7 and if required, it installs it !!
	public abstract void installJavaIfRequired();
	
	// This method installs cassandra on the node
	public abstract void installCassandraifRequired();
	
	// This method updates the cassandra.yaml configuration file
	public abstract void updateCassandraYamlFile(List<String> seeds, String cluster_name);
	
	// This method starts up the cassandra on the host
	public abstract void startCassandra();
	
	// Setter, Getter for the data members	
	
	public String get_installation_location() {
		return this.installation_location;
	}
	
	public void set_installation_location (String installation_location) {
		this.installation_location = installation_location;
	}
	
        public void set_point(Point point) {
            this.point = point;
        }
        
        public Point get_point() {
            return this.point;
        }
        
	public void set_ip_address(String ip_address) {
		this.ip_address = ip_address;
	}
	
	public String get_ip_address() {
		return this.ip_address;
	}
	
	public void set_user(String user) {
		this.user = user;
	}
	
	public String get_user_name() {
		return this.user;
	}
	
	public void set_password(String password) {
		this.password = password;
	}
	
	public String get_password() {
		return this.password;
	}
	
	public void set_listen_address(String listen_address) {
		this.listen_address = listen_address;
	}
	
	public String get_listen_address() {
		return this.listen_address;
	}
	
	public void set_rpc_address(String rpc_address) {
		this.rpc_address = rpc_address;
	}
	
	public String get_rpc_address() {
		return this.rpc_address;
	}
}
