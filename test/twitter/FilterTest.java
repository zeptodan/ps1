package twitter;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.*;
import org.junit.Test;

public class FilterTest {

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet t1 = new Tweet(1, "Alice", "talk about Rivest", d1);
    private static final Tweet t2 = new Tweet(2, "bob", "no keywords", d2);
    private static final Tweet t3 = new Tweet(3, "alice", "Talk again", d3);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // --- writtenBy ---
    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> list = Filter.writtenBy(Arrays.asList(t1, t2, t3), "alice");
        assertEquals(2, list.size());
        assertTrue(list.contains(t1));
        assertTrue(list.contains(t3));
    }

    @Test
    public void testWrittenByNone() {
        List<Tweet> list = Filter.writtenBy(Arrays.asList(t1, t2), "charlie");
        assertTrue(list.isEmpty());
    }

    // --- inTimespan ---
    @Test
    public void testInTimespanIncludesBoundaries() {
        Timespan span = new Timespan(d1, d2);
        List<Tweet> list = Filter.inTimespan(Arrays.asList(t1, t2, t3), span);
        assertEquals(2, list.size());
        assertTrue(list.contains(t1));
        assertTrue(list.contains(t2));
    }

    @Test
    public void testInTimespanEmpty() {
        Timespan span = new Timespan(Instant.parse("2015-01-01T00:00:00Z"), Instant.parse("2015-01-02T00:00:00Z"));
        List<Tweet> list = Filter.inTimespan(Arrays.asList(t1, t2), span);
        assertTrue(list.isEmpty());
    }

    // --- containing ---
    @Test
    public void testContainingSingleWordCaseInsensitive() {
        List<Tweet> list = Filter.containing(Arrays.asList(t1, t2, t3), Arrays.asList("talk"));
        assertEquals(2, list.size());
        assertTrue(list.contains(t1));
        assertTrue(list.contains(t3));
    }

    @Test
    public void testContainingMultipleWords() {
        List<Tweet> list = Filter.containing(Arrays.asList(t1, t2), Arrays.asList("rivest", "nope"));
        assertEquals(1, list.size());
        assertTrue(list.contains(t1));
    }

    @Test
    public void testContainingNoMatches() {
        List<Tweet> list = Filter.containing(Arrays.asList(t2), Arrays.asList("missing"));
        assertTrue(list.isEmpty());
    }
}
