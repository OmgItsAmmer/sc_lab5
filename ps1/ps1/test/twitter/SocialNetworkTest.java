/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // removed incorrect duplicate test that expected AssertionError
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    // 2. Tweets Without Mentions: tweets with no mentions do not add entries
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Instant t = Instant.parse("2020-01-01T00:00:00Z");
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "Ammer", "hello world", t));
        tweets.add(new Tweet(2, "Muhid", "another tweet", t));
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected empty graph when no mentions", followsGraph.isEmpty());
    }

    // 3. Single Mention: author added and follows mentioned user
    @Test
    public void testGuessFollowsGraphSingleMention() {
        Instant t = Instant.parse("2020-01-01T00:00:00Z");
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "Ammer", "hi @muhid", t));
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("ammer should be a key", followsGraph.containsKey("ammer"));
        assertTrue("ammer should follow muhid", followsGraph.get("ammer").contains("muhid"));
    }

    // 4. Multiple Mentions: multiple mentioned users are linked to the author
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Instant t = Instant.parse("2020-01-01T00:00:00Z");
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "Ammer", "hi @muhid @carol @dave", t));
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue(followsGraph.containsKey("ammer"));
        assertTrue(followsGraph.get("ammer").contains("muhid"));
        assertTrue(followsGraph.get("ammer").contains("carol"));
        assertTrue(followsGraph.get("ammer").contains("dave"));
    }

    // 5. Multiple Tweets from One User: repeated mentions are captured
    @Test
    public void testGuessFollowsGraphRepeatedMentionsAcrossTweets() {
        Instant t = Instant.parse("2020-01-01T00:00:00Z");
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "Ammer", "hi @muhid", t));
        tweets.add(new Tweet(2, "Ammer", "hello again @carol", t));
        tweets.add(new Tweet(3, "Ammer", "and @muhid once more", t));
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue(followsGraph.containsKey("ammer"));
        assertTrue(followsGraph.get("ammer").contains("muhid"));
        assertTrue(followsGraph.get("ammer").contains("carol"));
    }
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    // 7. Single User Without Followers: no influencers
    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        // Ammer follows nobody
        // graph can either omit "Ammer" or include empty set; we choose include
        followsGraph.put("Ammer", java.util.Collections.emptySet());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("expected no influencers when nobody is followed", influencers.isEmpty());
    }

    // 8. Single Influencer: only one user has followers
    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("Ammer", new java.util.HashSet<>(java.util.Arrays.asList("muhid")));
        followsGraph.put("carol", new java.util.HashSet<>(java.util.Arrays.asList("muhid")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertFalse(influencers.isEmpty());
        assertEquals("muhid", influencers.get(0));
    }

    // 9. Multiple Influencers: correct ordering by follower count
    @Test
    public void testInfluencersOrdering() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("Ammer", new java.util.HashSet<>(java.util.Arrays.asList("muhid", "carol")));
        followsGraph.put("dave", new java.util.HashSet<>(java.util.Arrays.asList("muhid")));
        followsGraph.put("erin", new java.util.HashSet<>(java.util.Arrays.asList("carol")));
        // muhid has 2 followers (ammer, dave); carol has 2 (ammer, erin)
        // tie handled in next test; add another follower to break tie
        followsGraph.put("frank", new java.util.HashSet<>(java.util.Arrays.asList("muhid")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertFalse(influencers.isEmpty());
        assertEquals("muhid", influencers.get(0));
        assertTrue(influencers.indexOf("carol") > 0);
    }

    // 10. Tied Influence: equal influencers handled (order between ties unspecified but both first two)
    @Test
    public void testInfluencersTies() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("Ammer", new java.util.HashSet<>(java.util.Arrays.asList("muhid", "carol")));
        followsGraph.put("dave", new java.util.HashSet<>(java.util.Arrays.asList("muhid")));
        followsGraph.put("erin", new java.util.HashSet<>(java.util.Arrays.asList("carol")));
        // now muhid and carol both have 2 followers
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue(influencers.size() >= 2);
        int iBob = influencers.indexOf("muhid");
        int iCarol = influencers.indexOf("carol");
        assertTrue("both muhid and carol should be at the top two positions",
            (iBob == 0 && iCarol == 1) || (iBob == 1 && iCarol == 0));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
