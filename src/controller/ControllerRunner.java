package controller;


import exception.CommunicationException;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class ControllerRunner {
    private static final Logger LOG = Logger.getLogger("CLogger");
    private double threshold;
    private int hostPort, emergPort, appPort;
    private String emergIP, appIP;
    private InetAddress hostIP;

    public ControllerRunner(double threshold, int hostPort, InetAddress hostIP, String emergIP, 
                            int emergPort, String appIP, int appPort) {
        this.threshold = threshold;
        this.hostPort = hostPort;
        this.hostIP = hostIP;
        this.emergPort = emergPort;
        this.appPort = appPort;
        this.emergIP = emergIP;
        this.appIP = appIP;
    }

    public void run() {
        // Start the connection accepting thread for the sensors
        (new Thread(new Runnable() {
            @Override
            public void run() {
                // Create Controller and host on target port
                Controller controller = new Controller(threshold);

                try {
                    controller.host(hostPort, hostIP);
                } catch (CommunicationException e) {
                    LOG.severe("Could not bind host to port (" + hostPort +"): " + e.getLocalizedMessage());
                    System.exit(1);
                }

                // Accept connections on host socket
                while (true) {
                    try {
                        Socket client = controller.acceptClient();
                        (new Thread(new ControllerReceiver(threshold, client, emergIP, emergPort, 
                        		                           appIP, appPort))).start();

                    } catch (CommunicationException e) {
                        LOG.severe("Could not accept new client: " + e.getLocalizedMessage());
                        break;
                    }
                }

                try {
                    controller.disconnectHost();
                } catch (CommunicationException e) {
                    LOG.severe("Could not disconnect the host: " + e.getLocalizedMessage());
                }
            }
        })).start();

    }

    public static void main(String args[]) {
        double threshold = Double.valueOf(args[0]);
        int hostPort = Integer.valueOf(args[1]);
        int emergPort = Integer.valueOf(args[4]);
        int appPort = Integer.valueOf(args[6]);
        
        InetAddress ip;

        try {
            ip = InetAddress.getByName(args[2]);
            ControllerRunner controllerRunner = new ControllerRunner(threshold, hostPort, ip, args[3],
            		                                                 emergPort, args[5], appPort);
            controllerRunner.run();

        } catch (UnknownHostException e) {
            LOG.severe("Could not connect to host provided (" + args[2] + "): " + e.getLocalizedMessage());
            System.exit(1);
        }


    }
}
