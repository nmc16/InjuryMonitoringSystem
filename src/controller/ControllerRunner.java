package controller;


import exception.CommunicationException;
import sendable.Sendable;
import sendable.data.Initialization;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

public class ControllerRunner {
    private static final Logger LOG = Logger.getLogger("CLogger");
    private double threshold;
    private Socket appSocket;
    private int hostPort, emergPort;
    private String emergIP;
    private InetAddress hostIP;

    public ControllerRunner(double threshold, int hostPort, InetAddress hostIP, String emergIP, int emergPort) {
        this.threshold = threshold;
        this.hostPort = hostPort;
        this.hostIP = hostIP;
        this.emergPort = emergPort;
        this.emergIP = emergIP;
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

                        // If the GUI has not connected yet, wait for init signal
                        if (appSocket == null) {
                            List<Sendable> received = controller.receive(client);

                            // Check that the received list has the init signal with the correct UID
                            if (received != null && received.size() >= 1 && received.get(0) instanceof Initialization) {
                                Initialization initialization = (Initialization) received.get(0);

                                // Set the app socket to the client and move on
                                if (initialization.getUID() == Controller.APP_UID) {
                                    appSocket = client;
                                    LOG.info("Connected to GUI...");
                                }
                            }
                        } else {

                            (new Thread(new ControllerReceiver(threshold, client, emergIP, emergPort,
                        		                               appSocket))).start();
                        }

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

        InetAddress ip;

        try {
            ip = InetAddress.getByName(args[2]);
            ControllerRunner controllerRunner = new ControllerRunner(threshold, hostPort, ip, args[3], emergPort);
            controllerRunner.run();

        } catch (UnknownHostException e) {
            LOG.severe("Could not connect to host provided (" + args[2] + "): " + e.getLocalizedMessage());
            System.exit(1);
        }


    }
}
