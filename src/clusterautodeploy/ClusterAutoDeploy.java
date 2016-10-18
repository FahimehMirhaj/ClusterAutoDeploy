/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clusterautodeploy;

/**
 *
 * @author fahimeh
 */
public class ClusterAutoDeploy {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CassandraSimulator simulator = new CassandraSimulator();
        simulator.setVisible(true);
    }
    
}
