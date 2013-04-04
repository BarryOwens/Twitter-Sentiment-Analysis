package sentiment.main;

import java.util.ArrayList;

import sentiment.analise.NegationDetection;
import sentiment.analise.SentimentTweet;
import sentiment.analise.TermFilter;
import file.reader.ResourceReader;

public class TestSentimentLabeler {
	
	ClassifierCreator myClassifier;
	WordListClassifierSetUp secondClassifier;
	boolean twoClassifiers = false;
	
	ArrayList<SentimentTweet> tweetsToBeLabeled = new ArrayList<SentimentTweet>();
	ResourceReader resourceReader;
	
	public TestSentimentLabeler(ClassifierCreator cl, ArrayList<SentimentTweet> tweets, ResourceReader reader)
	{
		this.myClassifier = cl;
		this.tweetsToBeLabeled = tweets;
		this.resourceReader = reader;
	}
	public TestSentimentLabeler(ClassifierCreator cl, WordListClassifierSetUp class2, ArrayList<SentimentTweet> tweets, ResourceReader reader)
	{
		this.myClassifier = cl;
		this.secondClassifier = class2;
		this.tweetsToBeLabeled = tweets;
		this.resourceReader = reader;
		this.twoClassifiers = true;
	}
	
	public ArrayList<SentimentTweet> classifyTweets()
	{
		SentimentTweet tempTweet;		
		NegationDetection negationD = new NegationDetection(resourceReader);
		ArrayList<SentimentTweet> returnTweets = new ArrayList<SentimentTweet>();
		for(int tweetNum=0; tweetNum<tweetsToBeLabeled.size(); tweetNum++)
		{
		try {
				tempTweet = tweetsToBeLabeled.get(tweetNum);
				
				// Check for negation in the tweet text
				String tweetText = negationD.detection(tempTweet.getTweetContent());
		    	TermFilter tf = new TermFilter(resourceReader);
		    	tweetText = tf.removeCommonWords(tweetText);
				
				double[] result = myClassifier.classifyMessage(tweetText);
				if(twoClassifiers==true)
				{
					double[] result2 = secondClassifier.getClassifier().classifyMessage(tweetText);
					// Put lower weight on these results
					result[0] = (result[0]+(result2[0]*.65))/2;
					result[1] = (result[1]+(result2[1]*.65))/2;					
				}
				if(result[0]>result[1])
				{
					if(result[0]>.60)
					{
						tempTweet.setPredictedSentiment("positive");
						tempTweet.setPredictedValue(result[0]);
					}
					else
					{
						tempTweet.setPredictedSentiment("neutral");
						tempTweet.setPredictedValue(result[0]);
					}
				}
				else{
					
					if(result[1]>.60)
					{
						tempTweet.setPredictedSentiment("negative");
						tempTweet.setPredictedValue(result[1]);
					}
					else
					{
						tempTweet.setPredictedSentiment("neutral");	
						tempTweet.setPredictedValue(result[1]);
					}
				}
				
				returnTweets.add(tempTweet);					
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return returnTweets;
	}

}
