package sentiment.main;

import java.util.ArrayList;
import java.util.Arrays;

import file.reader.ResourceReader;

import sentiment.analise.EmoticonDetection;
import sentiment.analise.NegationDetection;
import sentiment.analise.SentimentTweet;
import sentiment.analise.TermFilter;
import twitter4j.Tweet;
// Class to label tweets after they have been downloaded and the classifier has been set up
public class TwitterSentimentLabeler {
	
	public ClassifierCreator myClassifier;
	public ArrayList<Tweet> tweetsToBeLabeled = new ArrayList<Tweet>();
	public ResourceReader resourceReader;
	public TwitterSentimentLabeler(ClassifierCreator cl, ArrayList<Tweet> tweets, ResourceReader reader)
	{
		this.myClassifier = cl;
		this.tweetsToBeLabeled = tweets;
		this.resourceReader = reader;
	}
	
	public ArrayList<SentimentTweet> classifyTweets()
	{
		
		Tweet tempTweet;		
		NegationDetection negationD = new NegationDetection(resourceReader);
		EmoticonDetection emotionD = new EmoticonDetection();
    	TermFilter tf = new TermFilter(resourceReader );
    	// List of tweets of type sentiment tweet
		ArrayList<SentimentTweet> returnTweets = new ArrayList<SentimentTweet>();
		int typeEmoticons = 0;
		for(int tweetNum=0; tweetNum<tweetsToBeLabeled.size(); tweetNum++)
		{
		try {
				tempTweet = tweetsToBeLabeled.get(tweetNum);	
				SentimentTweet tempSentiTweet = new SentimentTweet(tempTweet.getId(), tempTweet.getFromUserName(), tempTweet.getText());
				// Set the Twitter4j Tweet object within the sentimentTweet object
				tempSentiTweet.setTwitterTweet(tempTweet);
				// Check for negation in the Tweet text
				String tweetText = negationD.detection(tempTweet.getText());
				// Check for emoticons in each tweet
				typeEmoticons = emotionD.checkForEmoticons(tweetText);
				// Remove common terms as was done with training data
				tweetText = tf.removeCommonWords(tweetText);
				
				// Double to hold value to increase the result if a an emoticon is in the tweet
				double emoticonAddition = 0;
				if(typeEmoticons==1)
				{
					emoticonAddition = .35;
				}//Negative emoticon found
				if(typeEmoticons==-1)
				{
					emoticonAddition = -.35;
				}
				else
				{// No emoticon found so no addition
					emoticonAddition = 0;
				}
				
				double[] result = myClassifier.classifyMessage(tweetText);	
				// do not let the probability go above 1, if +35 added causes it to go over 1, then it will already be correctly classified
				if(result[0]+emoticonAddition<1)
				{
					result[0] = result[0]+emoticonAddition;
				}
				
				if((result[1]-emoticonAddition<1))
				{
					// -1*-35 if negative
					result[1] = result[1]-emoticonAddition;
				}

				// Threshold level for neutral is 60%
				if(result[0]>result[1])
				{
					if(result[0]>.60)
					{
						tempSentiTweet.setPredictedSentiment("positive");
						tempSentiTweet.setPredictedValue(result[0]);
					}
					else
					{
						tempSentiTweet.setPredictedSentiment("neutral");
						tempSentiTweet.setPredictedValue(result[0]);
					}
				}
				else{
					
					if(result[1]>.60)
					{
						tempSentiTweet.setPredictedSentiment("negative");
						tempSentiTweet.setPredictedValue(result[1]);
					}
					else
					{
						tempSentiTweet.setPredictedSentiment("neutral");	
						tempSentiTweet.setPredictedValue(result[1]);
					}
				}
				
				returnTweets.add(tempSentiTweet);	
				
				
			} catch (Exception e) {				
				e.printStackTrace();
			}			
		}		
		return returnTweets;
	}

}
