/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
//new test cases

@Test
public void testGetTimespanSameTimestamp() {
    Instant sameTime = Instant.parse("2020-01-01T12:00:00Z");
    Tweet t1 = new Tweet(1, "user1", "tweet one", sameTime);
    Tweet t2 = new Tweet(2, "user2", "tweet two", sameTime);
    Tweet t3 = new Tweet(3, "user3", "tweet three", sameTime);

    Timespan timespan = Extract.getTimespan(Arrays.asList(t1, t2, t3));
    
    assertEquals("expected start == end with same timestamps", sameTime, timespan.getStart());
    assertEquals("expected start == end with same timestamps", sameTime, timespan.getEnd());
}


@Test
public void testGetTimespanLargeInterval() {
    Instant early = Instant.parse("2022-01-01T00:00:00Z");
    Instant late = Instant.parse("2022-01-10T23:59:59Z");

    Tweet earlyTweet = new Tweet(1, "earlyUser", "happy new year", early);
    Tweet lateTweet = new Tweet(2, "lateUser", "end of year", late);

    Timespan timespan = Extract.getTimespan(Arrays.asList(earlyTweet, lateTweet));
    
    assertEquals("expected start is earliest instant", early, timespan.getStart());
    assertEquals("expected end is latest instant", late, timespan.getEnd());
}



@Test
public void testGetTimespanUnorderedTweets() {
    Timespan timespan = Extract.getTimespan(Arrays.asList(tweet2, tweet1)); 
    
    assertEquals("expected start", d1, timespan.getStart());
    assertEquals("expected end", d2, timespan.getEnd());
}

@Test
public void testGetMentionedUsersMultipleMentions() {
    Tweet tweetA = new Tweet(3, "userA", "Hello @Ammer and @muhid! @AMMER how are you?", d1);
    Set<String> mentions = Extract.getMentionedUsers(Arrays.asList(tweetA));
    
    Set<String> expected = Set.of("ammer", "muhid");
    
    assertEquals("expected mentions ignoring case and duplicates", expected, mentions);
}


@Test
public void testGetMentionedUsersIgnoresEmailsAndInvalid() {
    Tweet tweetB = new Tweet(4, "userB", "Contact me at ammer.muhid@example.com or @ammer!", d2);
    Tweet tweetC = new Tweet(5, "userC", "Check out @invalid-user and @muhid", d2);
    
    Set<String> mentions = Extract.getMentionedUsers(Arrays.asList(tweetB, tweetC));
    
    Set<String> expected = Set.of("ammer", "muhid");
    
    assertEquals("expected valid mentions only, ignoring emails and invalid mentions", expected, mentions);
}






    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
