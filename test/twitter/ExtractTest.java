package twitter;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.*;
import org.junit.Test;

public class ExtractTest {

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet t1 = new Tweet(1, "alice", "hello @Bob", d1);
    private static final Tweet t2 = new Tweet(2, "bob", "no mentions here", d2);
    private static final Tweet t3 = new Tweet(3, "charlie", "email test bob@mit.edu", d3);
    private static final Tweet t4 = new Tweet(4, "dave", "@Eve and @eve again!", d2);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // --- getTimespan ---
    @Test
    public void testTimespanSingleTweet() {
        Timespan ts = Extract.getTimespan(Arrays.asList(t1));
        assertEquals(d1, ts.getStart());
        assertEquals(d1, ts.getEnd());
    }

    @Test
    public void testTimespanMultipleTweets() {
        Timespan ts = Extract.getTimespan(Arrays.asList(t2, t1, t3));
        assertEquals(d1, ts.getStart());
        assertEquals(d3, ts.getEnd());
    }

    // --- getMentionedUsers ---
    @Test
    public void testMentionedUsersNone() {
        Set<String> m = Extract.getMentionedUsers(Arrays.asList(t2));
        assertTrue(m.isEmpty());
    }

    @Test
    public void testMentionedUsersSimple() {
        Set<String> m = Extract.getMentionedUsers(Arrays.asList(t1));
        assertTrue(m.contains("bob"));
        assertEquals(1, m.size());
    }

    @Test
    public void testMentionedUsersIgnoresEmail() {
        Set<String> m = Extract.getMentionedUsers(Arrays.asList(t3));
        assertTrue(m.isEmpty());
    }

    @Test
    public void testMentionedUsersCaseInsensitiveAndUnique() {
        Set<String> m = Extract.getMentionedUsers(Arrays.asList(t4));
        assertEquals(1, m.size());
        assertTrue(m.contains("eve"));
    }
}
