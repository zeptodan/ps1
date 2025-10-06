package twitter;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extract {

    public static Timespan getTimespan(List<Tweet> tweets) {
        if (tweets.isEmpty()) {
            Instant now = Instant.now();
            return new Timespan(now, now);
        }

        Instant min = tweets.get(0).getTimestamp();
        Instant max = min;

        for (Tweet t : tweets) {
            Instant time = t.getTimestamp();
            if (time.isBefore(min)) min = time;
            if (time.isAfter(max)) max = time;
        }
        return new Timespan(min, max);
    }

    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> users = new HashSet<>();
        Pattern pattern = Pattern.compile("(?<![A-Za-z0-9_-])@([A-Za-z0-9_-]+)(?![A-Za-z0-9_-])");

        for (Tweet t : tweets) {
            Matcher m = pattern.matcher(t.getText());
            while (m.find()) {
                users.add(m.group(1).toLowerCase());
            }
        }
        return users;
    }
}
