package clusterautodeploy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cassandra12Ubuntu12Node extends CassandraNode {
	
	
	public Cassandra12Ubuntu12Node(String ip_address, String user, String password, String listen_address, String rpc_address) {
		// instantiating the seeds data member
		super();
		set_ip_address(ip_address);
		set_user(user);
		set_password(password);
		
		set_listen_address(listen_address);
		set_rpc_address(rpc_address);
		
		set_installation_location("/home/" + get_user_name() + "/cassandra");
	}
	
	
	// This method checks and see if the java is installed on the host or is it at least 1.7
	// https://www.digitalocean.com/community/tutorials/how-to-install-java-on-ubuntu-with-apt-get
	@Override
        public void installJavaIfRequired() {
                System.out.println("Cassandra12Ubuntu12Node::installJavaIfRequired() method has been called !");
		String result = SSH.run_info_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "which java");
                if (result.trim().equals("")) {
			// java has not been installed on the machine, So try to install it !
                        System.out.println("Cassandra12Ubuntu12Node::installJavaIfRequired():: java has not been installed !!");
			installJava();
		}
		else {
			// 1.7.0_91 ==> 7
			String commandResult = SSH.run_info_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "java -version 2>&1 | head -n 1 | awk -F '\"' '{print $2}'");
			if (commandResult != null) {
				String[] javaVersionComponents = commandResult.split(".");
				int version = Integer.parseInt(javaVersionComponents[1]);
				
				if (version < MIN_JAVA_VERSION) {
					installJava();
				}
			} else {
				installJava();
			}
		}
	}
	
	// In case the java is not installed (or it is old version), it updates the java to 1.7
	// https://www.digitalocean.com/community/tutorials/how-to-install-java-on-ubuntu-with-apt-get
	
        private void installJava() {
                System.out.println("Cassandra12Ubuntu12Node::installJava() method has been called !");
		// based on the link: http://stackoverflow.com/questions/16263556/installing-java-7-on-ubuntu
		// sudo apt-get update		
                System.out.println("Cassandra12Ubuntu12Node::installJava():: sudo apt-get update has been issued!!");
		SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo apt-get update");
		System.out.println("Cassandra12Ubuntu12Node::installJava():: sudo apt-get update has been finished!!");
		// sudo apt-get install openjdk-7-jdk
                System.out.println("Cassandra12Ubuntu12Node::installJava():: sudo apt-get install openjdk-7-jdk has been issued!!");
		SSH.run_sudo_command_interactive(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo apt-get install openjdk-7-jdk");
                System.out.println("Cassandra12Ubuntu12Node::installJava():: sudo apt-get install openjdk-7-jdk has been finished!!");
	}
	
	// This method installs cassandra on the node
	// https://www.digitalocean.com/community/tutorials/how-to-install-cassandra-and-run-a-single-node-cluster-on-a-ubuntu-vps
	// https://www.digitalocean.com/community/tutorials/how-to-configure-a-multi-node-cluster-with-cassandra-on-a-ubuntu-vps
	// http://docs.datastax.com/en/cassandra/1.2/cassandra/initialize/initializeSingleDS.html
	@Override
        public void installCassandraifRequired() {
		
                System.out.println("Cassandra12Ubuntu12Node::installCassandraifRequired() method has been called !");
		
		// TODO: to be tested !!
		// Deleting the previous installed cassandra, if it exits !
		String cassandra_directory = SSH.run_info_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "which cassandra");
		if (!cassandra_directory.trim().equals("")) { // it means it is installed 
			
			// Killing all the related processes
			String pidList = SSH.run_info_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "pidof cassandra");
			String [] pids = pidList.split(" ");
			for (String pid: pids) {
				SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo kill -9 " + pid);
			}
			
			// Deleting the installation directory
			SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo rm -rf " + cassandra_directory);
		}
		
		// installing the Cassandra version 2.1.2
		
		//wget http://www.us.apache.org/dist/cassandra/2.1.2/apache-cassandra-2.1.2-bin.tar.gz
		SSH.run_info_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "wget https://archive.apache.org/dist/cassandra/2.1.2/apache-cassandra-2.1.2-bin.tar.gz");
		
		// tar xzf apache-cassandra-2.1.2-bin.tar.gz
		SSH.run_info_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "tar xzf apache-cassandra-2.1.2-bin.tar.gz");
		
		//rm -rf cassandra           # May have been created by earlier execution - want to start afresh
		SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo rm -rf " + this.get_installation_location());
		
		// mkdir -p installation_location
		SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo mkdir -p " + this.get_installation_location());
		
		//mv apache-cassandra-2.1.2 installation_location 
		SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo mv apache-cassandra-2.1.2/* " + this.get_installation_location());
		
		// removing the un-necessary folders
		SSH.run_info_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "rm -rf apache-cassandra-2.1.2");
		SSH.run_info_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "rm -f apache-cassandra-2.1.2-bin.tar.gz");
		
		String [] folders = {"/var/log/cassandra", "/var/lib/cassandra", 
                    "/var/lib/cassandra/commitlog", "/var/lib/cassandra/data", "/var/lib/cassandra/saved_caches"};
		
		for (String folder: folders) {
			// delete if previously created !
			SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo rm -rf " + folder);
			
			// create the folder
			SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo mkdir " + folder);
		}
		
		SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo chmod -R 777 /var/lib/cassandra");
		SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo chmod -R 777 /var/log/cassandra");
		
	}
	
	
	// This method updates the cassandra.yaml configuration file
	// Changes (in the .yaml file) required are:
	//	- cluster_name:
	//	- seeds:
	//	- listen_address:
        @Override
	public void updateCassandraYamlFile(List<String> seeds, String cluster_name) {
		// First copying the cassandra.yaml file
		SSH.downloadTheFile(this.get_ip_address(), this.get_user_name(), this.get_password(), this.get_installation_location() + "/conf", "cassandra.yaml");
		
		// deleting the copied cassandra.yaml file
		SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo rm -f " + this.get_installation_location() + "/conf/cassandra.yaml");
	
		// reading the copied cassandra.yaml file and trying to make an updated version called cassandra2.yaml
		try{
                    String line, putData;
                    File file = new File("cassandra2.yaml");
                    file.createNewFile();
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.flush();

                    FileReader fr = new FileReader(new File("cassandra.yaml"));
                    BufferedReader br = new BufferedReader(fr);

                    while( (line = br.readLine()) != null )
                    { 
                        if(line != null)
                        {
                            if (line.contains("cluster_name: ")) {
                                    putData = "cluster_name: '" + cluster_name + "'\n";
                                    bw.write(putData);
                            }
                            else if (line.contains("seeds:")) {
                                    putData = line.substring(0, line.indexOf(':') + 1) + " \"";
                                    for (int i = 0; i < seeds.size() - 1; ++i) {
                                            putData += seeds.get(i) + ",";
                                    }

                                    putData += seeds.get(seeds.size() - 1) + "\"\n";
                                    bw.write(putData);
                            }
                            else if (line.startsWith("rpc_address:")) {
                                    putData = "rpc_address: " + this.get_ip_address() + "\n";
                                    bw.write(putData);
                            } 
                            else if (line.contains("listen_address:")) {
                                    putData = "listen_address: " + this.get_ip_address() + "\n";
                                    bw.write(putData);
                            } else {
                                    bw.write(line + "\n");
                            }
                        }
                    }
                    br.close();
                    bw.close();

	    } catch(IOException e){
	    	e.printStackTrace();
	    }
		
            // deleting the old version of cassandra.yaml file
            File file = null; 
            try{

            file = new File("cassandra.yaml");
            file.delete();

            } catch(Exception e){
                e.printStackTrace();
            }
        // renaming the updated version of cassandra2.yaml file to cassandra.yaml file
        File updatedFile = new File("cassandra2.yaml");
        updatedFile.renameTo(file);

        // copying back the updated version to the remote machine
        //  SSH.uploadTheFile("192.168.56.104", "paul", "paul", "cassandra.yaml", "/home/paul/cassandra/conf");
        SSH.uploadTheFile(this.get_ip_address(), this.get_user_name(), this.get_password(), "cassandra.yaml", this.get_installation_location() + "/conf");

        // deleting the local cassandra.yaml file
        File toBeDeletedFile = new File("cassandra.yaml");
        toBeDeletedFile.delete();
    }
	
	// This method starts up the cassandra on the host
	@Override
        public void startCassandra() {
		SSH.run_sudo_command(this.get_ip_address(), this.get_user_name(), this.get_password(), "sudo sh " + this.get_installation_location() + "/bin/cassandra");
	}

}
