Ipackage clusterautodeploy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CassandraCluster {
	private List<CassandraNode> seedNodes;
	private List<CassandraNode> normalNodes;
	private List<String> seed_nodes_ip;
	
	private String cluster_name;
	
        public List<CassandraNode> get_seed_nodes() {
            return this.seedNodes;
        }
        
        public List<CassandraNode> get_normal_nodes() {
            return this.normalNodes;
        }
        
        public boolean isThereAnySeedNode() {
            return (seedNodes.size() != 0);
        }
	
	public void set_cluster_name(String cluster_name) {
		this.cluster_name = cluster_name;
	}
	
	public String get_cluster_name() {
		return this.cluster_name;
	}
	
	public CassandraCluster() {
		seedNodes = new ArrayList<CassandraNode>();
		normalNodes = new ArrayList<CassandraNode>();
		seed_nodes_ip = new ArrayList<String>();
	}
	
	public void add_normal_node(CassandraNode node) {
		normalNodes.add(node);
	}
        
        public void remove_normal_node(CassandraNode node) {
            for (int i = 0; i < normalNodes.size(); ++i) {
                CassandraNode theNode = normalNodes.get(i);
                if (theNode.get_ip_address().equals(node.get_ip_address())) {
                    normalNodes.remove(i);
                    break;
                }
            }
        }
	
	public void add_seed_node(CassandraNode seed_node) {
		seedNodes.add(seed_node);
		seed_nodes_ip.add(seed_node.get_ip_address());
	}

        public void remove_seed_node(CassandraNode seed_node) {
            for (int i = 0; i < seedNodes.size(); ++i) {
                CassandraNode theNode = seedNodes.get(i);
                if (theNode.get_ip_address().equals(seed_node.get_ip_address())) {
                    seedNodes.remove(i);
                    break;
                }
            }
            
            for (int i = 0; i < seed_nodes_ip.size(); ++i) {
                String theIp = seed_nodes_ip.get(i);
                if (theIp.equals(seed_node.get_ip_address())) {
                    normalNodes.remove(i);
                    break;
                }
            }
        }
        
	public void setUp() {
            System.out.println("CassandraCluster::setUp() method has been invoked");
            for (CassandraNode cass_node: seedNodes) {
                    cass_node.installJavaIfRequired();
                    cass_node.installCassandraifRequired();
                    cass_node.updateCassandraYamlFile(seed_nodes_ip, get_cluster_name());
                    cass_node.startCassandra();
            }

            for (CassandraNode normal_cass_node: normalNodes) {
                    normal_cass_node.installJavaIfRequired();
                    normal_cass_node.installCassandraifRequired();
                    normal_cass_node.updateCassandraYamlFile(seed_nodes_ip, get_cluster_name());
                    normal_cass_node.startCassandra();
            }
	}
	
	
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("Welcome to cassandra-auto-deployment-system");
		System.out.print("What is the name of the cassandra cluster you have? ");
		String clusterName = input.nextLine();
		
		System.out.print("How many nodes do you have in your cluster? ");
		
		int numberOfNodesInCluster = Integer.parseInt(input.nextLine());
		CassandraCluster cluster = new CassandraCluster();
		cluster.set_cluster_name(clusterName);
		
		for (int i = 1; i <= numberOfNodesInCluster; ++i) {
			System.out.println("Customizing the " + i + "th node in the cluster");
			String choice;
			do {
				System.out.print("\tIs the " + i + "th node, seed node? (y/n)");
				choice = input.nextLine();
			} while (!choice.equals("Y") && !choice.equals("y") && !choice.equals("n") && !choice.equals("N"));
			
			
			boolean isSeedNode = (choice.equals("y") || choice.equals("y"));
			
			System.out.print("\tPlease enter its ip_address: ");
			String ip_address = input.nextLine();
			
			System.out.print("\tPlease enter the user name of the machine: ");
			String user = input.nextLine();
			
			System.out.print("\tPlease enter the password of the machine: ");
			String password = input.nextLine();
			
			// "0.0.0.0" is indeed the rpc_address
			CassandraNode node = new Cassandra12Ubuntu12Node(ip_address, user, password, ip_address, "0.0.0.0");
			
			if (isSeedNode) {
				cluster.add_seed_node(node);
			} else {
				cluster.add_normal_node(node);
			}
		}
		
		cluster.setUp();
	}
}
