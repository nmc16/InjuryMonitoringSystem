package controller;


import exception.CommunicationException;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class ControllerRunner {
    private static final Logger LOG = Logger.getLogger("CLogger");
    private double threshold;
    private int hostPort;
    private InetAddress ip;

    public ControllerRunner(double threshold, int hostPort, InetAddress ip) {
        this.threshold = threshold;
        this.hostPort = hostPort;
        this.ip = ip;
    }

    public void run() {
        // Start the connection accepting thread for the sensors
        (new Thread(new Runnable() {
            @Override
            public void run() {
                // Create Controller and host on target port
                Controller controller = new Controller(threshold);

                try {
                    controller.host(hostPort, ip);
                } catch (CommunicationException e) {
                    LOG.severe("Could not bind host to port (" + hostPort +"): " + e.getLocalizedMessage());
                    System.exit(1);
                }

                // Accept connections on host socket
                while (true) {
                    try {
                        Socket client = controller.acceptClient();
                        (new Thread(new ControllerReceiver(threshold, client))).start();

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
        InetAddress ip;

        try {
            ip = InetAddress.getByAddress(args[2].getBytes());
            ControllerRunner controllerRunner = new ControllerRunner(threshold, hostPort, ip);
            controllerRunner.run();

        } catch (UnknownHostException e) {
            LOG.severe("Could not connect to host provided (" + args[2] + "): " + e.getLocalizedMessage());
            System.exit(1);
        }


    }
}
