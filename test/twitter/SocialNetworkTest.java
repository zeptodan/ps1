package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;
import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing strategy:
     * 
     * Partitions for guessFollowsGraph():
     *  - tweets = empty list -> expect empty graph
     *  - tweets contain no mentions -> expect no follows
     *  - single mention -> one author follows one mentioned user
     *  - multiple mentions in one tweet -> all mentioned users followed
     *  - multiple tweets by same user -> combined mentions accumulated
     *
     * Partitions for influencers():
     *  - empty followsGraph -> empty influencer list
     *  - single user with no followers -> empty influencer list
     *  - one user with followers -> that user appears first
     *  - multiple users -> sorted by descending follower count
     *  - tie in follower counts -> any correct order of tied users accepted
     */

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // ensure -ea is used when running tests
    }

    // 1. Empty List of Tweets
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    // 2. Tweets Without Mentions
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Tweet t1 = new Tweet(1L, "alice", "Just another tweet", Instant.now());
        List<Tweet> tweets = Arrays.asList(t1);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected no follows", graph.isEmpty());
    }

    // 3. Single Mention
    @Test
    public void testGuessFollowsGraphSingleMention() {
        Tweet t1 = new Tweet(1L, "alice", "Hello @bob", Instant.now());
        List<Tweet> tweets = Arrays.asList(t1);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("should contain alice as key", graph.containsKey("alice"));
        assertTrue("alice should follow bob", graph.get("alice").contains("bob"));
    }

    // 4. Multiple Mentions
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Tweet t1 = new Tweet(1L, "alice", "Hey @bob and @charlie, check this out!", Instant.now());
        List<Tweet> tweets = Arrays.asList(t1);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        Set<String> expected = new HashSet<>(Arrays.asList("bob", "charlie"));
        assertEquals(expected, graph.get("alice"));
    }

    // 5. Multiple Tweets from One User
    @Test
    public void testGuessFollowsGraphMultipleTweetsSameUser() {
        Tweet t1 = new Tweet(1L, "alice", "Hi @bob", Instant.now());
        Tweet t2 = new Tweet(2L, "alice", "Hi again @charlie", Instant.now());
        List<Tweet> tweets = Arrays.asList(t1, t2);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        Set<String> expected = new HashSet<>(Arrays.asList("bob", "charlie"));
        assertEquals(expected, graph.get("alice"));
    }

    // 6. Empty Graph for influencers()
    @Test
    public void testInfluencersEmptyGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("expected empty list", influencers.isEmpty());
    }

    // 7. Single User Without Followers
    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", Collections.emptySet());
        List<String> influencers = SocialNetwork.influencers(graph);
        // If there are no followers for anyone, influencers should be empty
        assertTrue("expected empty influencers list", influencers.isEmpty());
    }

    // 8. Single Influencer
    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> graph = new HashMap<>();
        // alice follows bob -> bob has 1 follower
        graph.put("alice", new HashSet<>(Arrays.asList("bob")));
        List<String> influencers = SocialNetwork.influencers(graph);
        // Most implementations should include bob first; remaining users with 0 can follow
        assertFalse("influencers should not be empty", influencers.isEmpty());
        assertEquals("bob should be top influencer", "bob", influencers.get(0));
    }

    // 9. Multiple Influencers
    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", new HashSet<>(Arrays.asList("bob", "charlie"))); // bob:+1, charlie:+1
        graph.put("dave", new HashSet<>(Arrays.asList("bob")));             // bob:+1 -> bob total 2
        // bob:2 followers, charlie:1 follower
        List<String> influencers = SocialNetwork.influencers(graph);
        assertEquals("bob should be most influential", "bob", influencers.get(0));
        assertTrue("charlie should appear after bob", influencers.indexOf("charlie") > influencers.indexOf("bob"));
    }

    // 10. Tied Influence
    @Test
    public void testInfluencersTiedInfluence() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", new HashSet<>(Arrays.asList("bob")));    // bob:1
        graph.put("charlie", new HashSet<>(Arrays.asList("dave"))); // dave:1
        List<String> influencers = SocialNetwork.influencers(graph);
        // Both bob and dave have 1 follower. The order between them can be arbitrary,
        // but both must appear among top influencers.
        assertTrue("expected bob and dave to be present", influencers.containsAll(Arrays.asList("bob", "dave")));
    }
}
