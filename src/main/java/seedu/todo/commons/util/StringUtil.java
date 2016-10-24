package seedu.todo.commons.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {
    public static boolean containsIgnoreCase(String source, String query) {
        String[] split = source.toLowerCase().split("\\s+");
        List<String> strings = Arrays.asList(split);
        return strings.stream().filter(s -> s.equals(query.toLowerCase())).count() > 0;
    }

    /**
     * Returns a detailed message of the t, including the stack trace.
     */
    public static String getDetails(Throwable t){
        assert t != null;
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if s represents an unsigned integer e.g. 1, 2, 3, ... <br>
     *   Will return false for null, empty string, "-1", "0", "+1", and " 2 " (untrimmed) "3 0" (contains whitespace).
     * @param s Should be trimmed.
     */
    public static boolean isUnsignedInteger(String s){
        return s != null && s.matches("^0*[1-9]\\d*$");
    }
    
    public static String pluralizer(int num, String singular, String plural) {
    	return num == 1 ? singular : plural;
    }
    
    /**
     * Returns <code>string</code> if not null, <code>replaceString</code> otherwise.
     * @param string String to check
     * @param replaceString String to return if <code>string</code> is null
     */
    public static String replaceNull(String string, String replaceString) {
        return (string == null) ? replaceString : string;
    }
    
    /*
     * Format the display message depending on the number of tasks and events 
     * 
     * @param numTasks
     *          the number of tasks found 
     * @param numEvents
     *          the number of events found    
     *        
     * @return the display message for console message output           
     */
    public static String formatNumberOfTaskAndEventWithPuralizer (int numTasks, int numEvents) {
        if (numTasks != 0 && numEvents != 0) {
            return String.format("%s and %s.", formatNumberOfTaskWithPuralizer(numTasks), formatNumberOfEventWithPuralizer(numEvents));
        } else if (numTasks != 0) {
            return formatNumberOfTaskWithPuralizer(numTasks);
        } else {
            return formatNumberOfEventWithPuralizer(numEvents);
        }
    }
    
    /*
     * Format the number of events found based on the events found
     * 
     *  @param numEvents 
     *          the number of events found
     */
    public static String formatNumberOfEventWithPuralizer (int numEvents) {
        return String.format("%d %s", numEvents, pluralizer(numEvents, "event", "events"));
    }
    
    /*
     * Format the number of tasks found based on the tasks found
     * 
     *  @param numTasks 
     *          the number of tasks found
     */
    public static String formatNumberOfTaskWithPuralizer (int numTasks) {
        return String.format("%d %s", numTasks, pluralizer(numTasks, "task", "tasks"));
    }
    
}
