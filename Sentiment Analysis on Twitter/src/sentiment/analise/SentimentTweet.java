package sentiment.analise;

import twitter4j.Tweet;

// Class to create tweet objects that also have a predicted and sometimes an actual sentiment
public class SentimentTweet {
	
	public long id;
	public String userName;
	public String tweetContent;
	public String sentiment;
	public String predictedSentiment;
	public double predictedValue;
	private Tweet TwitterTweet;
	
	public SentimentTweet(long i, String u, String tw)
	{
		id = i;
		userName = u;
		tweetContent = tw;		
	}
	
	// Getters and setters for actual sentiment, used for training and test data
	public String getSentiment()
	{
		return sentiment;
	}
	
	public void setSentiment(String s)
	{
		sentiment = s;
	}
	
	// Get tweet string and ID
	public String getTweetContent()
	{
		return tweetContent;	
	}	
	public long getID()
	{
		return id;
	}
	
	// Getters and setters for predicted sentiment and predicted value
	public void setPredictedSentiment(String psentiment)
	{
		predictedSentiment = psentiment;
	}
	public String getPredictedSentiment()
	{
		return predictedSentiment;
	}
	public void setPredictedValue(double pValue)
	{
		predictedValue = pValue;
	}
	public double getPredictedValue()
	{
		return predictedValue;
	}
	
	// Getters and setters for Twttier4j tweet objects
	public void setTwitterTweet(Tweet t)
	{
		TwitterTweet = t;
	}
	public Tweet getTwitterTweet()
	{
		return TwitterTweet;
	}
}
