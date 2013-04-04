package twitter.download;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import twitter4j.*;


public class Downloader{
	
	// tweetID is used to check for duplicates
	ArrayList<Long> tweetID = new ArrayList<Long>();
	int tweetNumber = 0;
	ArrayList<Tweet> tweetsCollected = new ArrayList<Tweet>();
		
	Logger log; 
	
	// Constructors allowing Tweets to be searched for one, two and three queries
	public Downloader(ArrayList<String> terms)
	{
		log = Logger.getLogger(Downloader.class);
		tweetsCollected = new ArrayList<Tweet>();
		// for each of the Twitter Query terms passed, search twitter
		for(int i=0; i<terms.size(); i++)
		{
			searchForTweets(terms.get(i));
			log.debug("Amount of tweets collected" +tweetNumber);
		}

	}
	
	public void addToArray(Tweet tweet)
	{	    		
		// Make sure there are no tab delimiters or new line delimiters in the Tweet
		if(tweet.getText().indexOf("	")<0 && tweet.getText().indexOf("\n")<0)
		{
			tweetsCollected.add(tweet);
			tweetNumber++;
			//System.out.println(tweet.getId()+"\t"+tweet.getCreatedAt().toString()+"\t"+tweet.getFromUser().toString()+"\t"+content);
		}
	}
	
	public void searchForTweets(String args)
	{
			log.debug("Searching for tweets for " +args);
			long maxID = 0;
		try {
				Twitter twitter = new TwitterFactory().getInstance();
				QueryResult result;	
				Query query = new Query(args+" +exclude:retweets");
				// Must be in English
				query.lang("en");
				query.sinceId(maxID);					
				query.setRpp(100);
				result = twitter.search(query);					
				for (Tweet tweet : result.getTweets()) {
					// If the tweet has not yet been appended
					if(maxID<tweet.getId())
					{
						maxID = tweet.getId();
					}						
					if(tweetID.contains(tweet.getId()))
					{
						// Two tweets found in same search, do not add both
						//System.out.println("Two items match " +tweet.getId());
					}
					else
					{
						tweetID.add(tweet.getId());
						addToArray(tweet);
					}
				}
			} catch (TwitterException e) {
				log.error("Error downloading tweets");
				e.printStackTrace();
			}
		
	}
	
	public ArrayList<Tweet> getTweets()
	{
		return tweetsCollected;
	}
}
