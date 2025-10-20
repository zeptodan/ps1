package twitter;

import java.util.*;
import java.util.regex.*;

public class SocialNetwork {

    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)");

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            Matcher matcher = mentionPattern.matcher(tweet.getText().toLowerCase());
            Set<String> mentionedUsers = new HashSet<>();

            while (matcher.find()) {
                String mentioned = matcher.group(1);
                if (!mentioned.equals(author)) { // user can't follow themselves
                    mentionedUsers.add(mentioned);
                }
            }

            if (!mentionedUsers.isEmpty()) {
                followsGraph.putIfAbsent(author, new HashSet<>());
                followsGraph.get(author).addAll(mentionedUsers);
            }
        }

        return followsGraph;
    }

    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCount = new HashMap<>();

        // Count followers for each followed user
        for (String user : followsGraph.keySet()) {
            for (String followed : followsGraph.get(user)) {
                followerCount.put(followed, followerCount.getOrDefault(followed, 0) + 1);
            }
        }

        // Sort only users who have at least one follower
        List<String> influencers = new ArrayList<>(followerCount.keySet());
        influencers.sort((a, b) -> followerCount.get(b) - followerCount.get(a));

        return influencers;
    }

}
