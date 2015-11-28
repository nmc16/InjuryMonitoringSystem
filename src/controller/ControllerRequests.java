package controller;


import java.util.logging.Logger;

public class ControllerRequests implements Runnable {
    private static final Logger LOG = Logger.getLogger("CLogger");
    private final double threshold;

    public ControllerRequests(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void run() {
        Controller controller = new Controller(threshold);

    }
}
