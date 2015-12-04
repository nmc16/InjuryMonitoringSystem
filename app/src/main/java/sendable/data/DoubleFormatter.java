package sendable.data;

import java.text.DecimalFormat;

/**
 * Static helper class for formatting the doubles to strings.
 *
 * @version 1
 */
public class DoubleFormatter {
    private static final DecimalFormat df = new DecimalFormat("#.#");

    // Hide public constructor
    private DoubleFormatter() {}

    public static String format(double d) {
        return df.format(d);
    }
}
