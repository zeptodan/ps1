package twitter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Filter {

    public static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        List<Tweet> result = new ArrayList<>();
        for (Tweet t : tweets) {
            if (t.getAuthor().equalsIgnoreCase(username)) {
                result.add(t);
            }
        }
        return result;
    }

    public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        List<Tweet> result = new ArrayList<>();
        Instant start = timespan.getStart();
        Instant end = timespan.getEnd();
        for (Tweet t : tweets) {
            Instant time = t.getTimestamp();
            if (!time.isBefore(start) && !time.isAfter(end)) {
                result.add(t);
            }
        }
        return result;
    }

    public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        List<Tweet> result = new ArrayList<>();
        if (words.isEmpty()) return result;

        for (Tweet t : tweets) {
            String text = t.getText().toLowerCase();
            for (String w : words) {
                if (text.contains(w.toLowerCase())) {
                    result.add(t);
                    break;
                }
            }
        }
        return result;
    }
}
