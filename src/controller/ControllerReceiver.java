package controller;

import database.Database;
import exception.CommunicationException;
import exception.DatabaseException;
import exception.ThresholdException;
import sendable.Sendable;
import sendable.alarm.Alarm;
import sendable.alarm.DataCause;
import sendable.alarm.PlayerCause;
import sendable.data.Acceleration;
import sendable.data.Position;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;


public class ControllerReceiver implements Runnable {
    private static final Logger LOG = Logger.getLogger("CLogger");
    private final double threshold;
    private final Socket client;

    public ControllerReceiver(double threshold, Socket client) {
        this.threshold = threshold;
        this.client = client;
    }

    @Override
    public void run() {
        Database database = new Database();
        Controller controller = new Controller(threshold);
        InputStream clientIn;

        // Setup controller and database
        try {
            database.connect();
            database.init();
            clientIn = client.getInputStream();
        } catch (DatabaseException e) {
            LOG.severe("Could not start database: " + e.getLocalizedMessage());
            return;
        } catch (IOException e) {
            LOG.severe("Could not get input stream from client: " + e.getLocalizedMessage());
            return;
        }

        // Initialize last position holder
        Position lastPosition = null;

        while (true) {
            try {
                List<Sendable> received = controller.receive(clientIn);

                for (Sendable sendable : received) {
                    try {
                        database.store(sendable);

                        if (sendable instanceof Position) {
                            if (lastPosition == null) {
                                lastPosition = (Position) sendable;
                            } else {
                                Acceleration acceleration = controller.calculate(lastPosition, (Position) sendable);
                                database.store(acceleration);
                            }

                        }

                    } catch (ThresholdException e) {
                        LOG.info("Threshold exception detected: " + e.getLocalizedMessage());

                        database.store(e.getAcceleration());
                        Alarm alarm = new Alarm(e.getAcceleration().getUID(), System.currentTimeMillis(),
                                new DataCause((int) threshold));

                        database.store(alarm);

                        // TODO add the sending to the app

                    }
                }

            } catch (CommunicationException e) {
                LOG.severe("Error receiving message from sensor: " + e.getLocalizedMessage());
                break;
            }
        }

        if (!client.isClosed()) {
            try {
                client.close();
            } catch (IOException e) {
                LOG.severe("Could not close client: " + e.getLocalizedMessage());
            }
        }
    }
}
