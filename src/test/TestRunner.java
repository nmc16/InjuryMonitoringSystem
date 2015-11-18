package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String args[]) {
        Result result = JUnitCore.runClasses(ControllerTest.class, DatabaseTest.class);
        System.out.println("Tests Passed: " + (result.getRunCount() - result.getFailureCount()));
        System.out.println("Tests Failed: " + result.getFailureCount());
        for (Failure failure: result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println("Test Result: " + (result.wasSuccessful() ? "PASSED" : "FAILED"));
    }
}
