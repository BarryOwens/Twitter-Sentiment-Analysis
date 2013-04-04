import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TweetDownloader extends Thread{
	
	public static ArrayList tweetID = new ArrayList();
	public static String queryName = "";
	public static void main(String args[])
	{
	try {
		int i = 0;
		//Infinate loop
		while(i==i)
		{
			queryName = args[0];
			searchForTweets(args[0]);
			// Wait time is 15 mins
			long time = Long.parseLong(args[1]);
			sleep(time);
			
		}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void appendToFile(Tweet tweet)
	{	    
		try {
			String content = new String(tweet.getText());
			PrintWriter Pout = new PrintWriter(new BufferedWriter(new FileWriter("outputTweets"+queryName +".tsv", true)));
			// Make sure there are no tab delimeters or new line delimeteres in the tweet
			if(tweet.getText().indexOf("	")<0 && tweet.getText().indexOf("\n")<0)
			{
				Pout.println(tweet.getId()+"\t"+tweet.getCreatedAt().toString()+"\t"+tweet.getFromUser().toString()+"\t"+tweet.getText().toString());
				System.out.println(tweet.getId()+"\t"+tweet.getCreatedAt().toString()+"\t"+tweet.getFromUser().toString()+"\t"+content);
				Pout.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void searchForTweets(String args)
	{
		
				
				long maxID = 0;
				try {
			    	ConfigurationBuilder cb = new ConfigurationBuilder();
			    	cb.setDebugEnabled(true)
			    	  .setOAuthConsumerKey("SGRw3YiRfdqAlY3kJu2Nw")
			    	  .setOAuthConsumerSecret("R5tdM1ZVX6PPZVQXuxET5y8HnJt9E6EcC8pnWCJUZQ")
			    	  .setOAuthAccessToken("185466859-YI958f7VSkzm32cHXbhPfjF8IBmRwLukUUXhNSTD")
			    	  .setOAuthAccessTokenSecret("s5cgcCIalKGcX1N60I6bs58yk9MaLozQfTgSzmXac");
					Twitter twitter = new TwitterFactory(cb.build()).getInstance();
					Query query = new Query(args);
					// Must be in English
					query.lang("en");
					// Collect 50 Tweets for each call
					query.setRpp(50);

				QueryResult result;	
				result = twitter.search(query);					
				for (Tweet tweet : result.getTweets()) {
					// If the tweet has not yet been appended
					if(tweetID.contains(tweet.getId()))
					{
					System.out.println("Two items match " +tweet.getId());
					}
					else
					{
						tweetID.add(tweet.getId());
						appendToFile(tweet);
					}
				}
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}

