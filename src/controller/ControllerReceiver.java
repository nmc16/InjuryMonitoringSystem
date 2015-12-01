package controller;

import database.Database;
import exception.CommunicationException;
import exception.DatabaseException;
import exception.ThresholdException;
import sendable.DataType;
import sendable.Sendable;
import sendable.alarm.Alarm;
import sendable.alarm.DataCause;
import sendable.data.Acceleration;
import sendable.data.Position;
import sendable.data.Request;
import sendable.data.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;


public class ControllerReceiver implements Runnable {
    private static final Logger LOG = Logger.getLogger("CLogger");
    private final double threshold;
    private final Socket client;
    private Database database;
    private Controller controller;
    private InputStream clientIn;
    private Position lastPosition;

    public ControllerReceiver(double threshold, Socket client) {
        this.threshold = threshold;
        this.client = client;
    }

    private <T extends Sendable> Service createService(Class<T> clazz, Request request, int dataType) {
        List<T> data;
        if (request.getStartTime() == -1 && request.getEndTime() == -1) {
            data = database.retrieve(clazz, request.getUID());
        } else if (request.getEndTime() == -1) {
            data = database.retrieve(clazz, request.getUID(), request.getStartTime());
        } else {
            data = database.retrieve(clazz, request.getUID(), request.getStartTime(), request.getEndTime());
        }

        return new Service<T>(request.getUID(), System.currentTimeMillis(), data, dataType);
    }

    private void decode(Sendable sendable) throws CommunicationException {
        try {
            // Check if it is a request and service it
            if (sendable instanceof Request) {
                Request request = (Request) sendable;
                Service service;

                // Check the data type requested and service the request
                if (request.getRequestType() == DataType.ACCEL) {
                    service = createService(Acceleration.class, request, DataType.ACCEL);

                } else if (request.getRequestType() == DataType.POS) {
                    service = createService(Position.class, request, DataType.POS);

                } else if (request.getRequestType() == DataType.ALARM) {
                    service = createService(Alarm.class, request, DataType.ALARM);

                } else {
                    // If we cannot find a data type match then do not service the request
                    return;
                }

                controller.send(service, client);
                return;
            }

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

            controller.send(alarm, client);
        }
    }

    @Override
    public void run() {
        database = new Database();
        controller = new Controller(threshold);

        // Setup controller and database
        try {
            database.connect();
            database.init();
        } catch (DatabaseException e) {
            LOG.severe("Could not start database: " + e.getLocalizedMessage());
            return;
        }

        // Initialize last position holder
        lastPosition = null;

        while (true) {
            try {
                List<Sendable> received = controller.receive(client);

                for (Sendable sendable : received) {
                    decode(sendable);
                }

            } catch (CommunicationException e) {
                LOG.severe("Error communicating with device: " + e.getLocalizedMessage());
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
