package file.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import sentiment.analise.SentimentTweet;
import sentiment.main.ClassifierSetup;

// Class to read in all files necessary for application
public class ResourceReader {
	
	// Servlet context to find file paths
	public ServletContext servletC;
	
	// Logger for log4j
	Logger log;
	// Positive and negative tweets
	ArrayList<SentimentTweet> posTweets = new ArrayList<SentimentTweet>();
	ArrayList<SentimentTweet> negTweets = new ArrayList<SentimentTweet>();
	
	// Test Data
	ArrayList<SentimentTweet> testTweets;

	
	// Terms related to the profile, used to filter out
	ArrayList<String> profileTerms;
	ArrayList<String> stopWords;
	
	// Terms related to negation detection
	ArrayList<String> negationWords;
	ArrayList<String> positiveWords;
	ArrayList<String> negativeWords;
	
	// Word list for the second classifier
	ArrayList<String> wordSentimentList;

	public ResourceReader(ServletContext serv, String profile)
	{

    	log = Logger.getLogger(ClassifierSetup.class);
    	log.debug("Starting to Read in files");
    	
		servletC = serv;
		negationWords = getWordList("wordsOfNegation.txt");
		positiveWords = getWordList("positiveWords.txt");
		negativeWords = getWordList("negativeWords.txt");
		stopWords = getWordList("StopList.txt");		
		wordSentimentList = getWordList("wordSentimentList.txt");
		profileTerms = getProfileTerms(profile);
		getTweetList(profile);
	}
	
	public ArrayList<String> getWordList(String fileName)
	{
		ArrayList<String> listCreated = new ArrayList<String>();
		String line = "";
		log.debug("Getting word list for "+fileName);
		InputStream pdInputStream = servletC.getResourceAsStream("/WEB-INF/Resources/"+fileName);  
		// Pass the reference to the inputstream reader for further processing  
		Reader reader = new InputStreamReader(pdInputStream);		
		BufferedReader bufRdr  = new BufferedReader(reader);
		try {
			while((line = bufRdr.readLine()) != null)  
			{
				listCreated.add(line.toLowerCase());		
			}
		
		bufRdr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listCreated;
	}
	
	public ArrayList<String> getProfileTerms(String folder)
	{
		ArrayList<String> listCreated = new ArrayList<String>();
		String line = "";
		log.debug("looking for terms in /WEB-INF/Resources/"+folder+"/Terms.txt");
		InputStream pdInputStream = servletC.getResourceAsStream("/WEB-INF/Resources/"+folder+"/Terms.txt");  
		// Pass the reference to the inputstream reader for further processing  
		Reader reader = new InputStreamReader(pdInputStream);		
		BufferedReader bufRdr  = new BufferedReader(reader);
		try {
			while((line = bufRdr.readLine()) != null)  
			{
				listCreated.add(line.toLowerCase());		
			}
		
		bufRdr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		log.debug("Got terms");
		return listCreated;		
		
	}
	
	public void getTweetList(String folder)
	{
		String line;
      	long tid;
		String user, senti, tcont;
		
		try{
			log.debug("Looking for labelled tweets in /WEB-INF/Resources/"+folder+"/LabelledTweets.tsv");
			InputStream pdInputStream = servletC.getResourceAsStream("/WEB-INF/Resources/"+folder+"/LabelledTweets.tsv");  
			// Pass the reference to the inputstream reader for further processing  
			Reader reader = new InputStreamReader(pdInputStream);		
			BufferedReader bufRdr  = new BufferedReader(reader);
			while((line = bufRdr.readLine()) != null)  
				{
				    StringTokenizer st = new StringTokenizer(line,"\t"); 
				    tid = Long.parseLong(st.nextToken());	
				    user ="";
				    senti = st.nextToken();			    
				    tcont = st.nextToken();
				    SentimentTweet tempTweet = new SentimentTweet(tid,user,tcont);
				    tempTweet.setSentiment(senti);
				    if(senti.equals("positive"))
				    {
				    	posTweets.add(tempTweet);
				    }
				    else
				    {
				    	negTweets.add(tempTweet);
				    }
				}
				bufRdr.close();
          } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		           
	}
	
	// This function reads in the Test Tweets, it must be called in order to read the test data. This is because this file may not always need to be read
	public ArrayList<SentimentTweet> readTestTweets(String folder)
	{
		testTweets =  new ArrayList<SentimentTweet>();
		String line;
      	long tid;
		String user, senti, tcont;
		
		try{
			InputStream pdInputStream = servletC.getResourceAsStream("/WEB-INF/Resources/"+folder+"/TestData.tsv");  
			// Pass the reference to the inputstream reader for further processing  
			Reader reader = new InputStreamReader(pdInputStream);		
			BufferedReader bufRdr  = new BufferedReader(reader);
			while((line = bufRdr.readLine()) != null)  
				{
				    StringTokenizer st = new StringTokenizer(line,"\t"); 
				    tid = Long.parseLong(st.nextToken());	
				    // User not used for test data
				    user = "nulluser";
				    senti = st.nextToken();			    
				    tcont = st.nextToken();
				    SentimentTweet tempTweet = new SentimentTweet(tid,user,tcont);	
				    tempTweet.setSentiment(senti);
				    testTweets.add(tempTweet);
				}
				bufRdr.close();
          } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return testTweets;
		
	}
	
	public ArrayList<String> readQueryTerms(String folder)
	{
		String line;
      	ArrayList<String> terms = new ArrayList<String>();
		try{
			InputStream pdInputStream = servletC.getResourceAsStream("/WEB-INF/Resources/"+folder+"/TwitterQueryTerms.txt");  
			// Pass the reference to the inputstream reader for further processing  
			Reader reader = new InputStreamReader(pdInputStream);		
			BufferedReader bufRdr  = new BufferedReader(reader);
			while((line = bufRdr.readLine()) != null)  
				{
				terms.add(line);
				}
				bufRdr.close();
           	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return terms;		
	}
	public ArrayList<SentimentTweet> getPositiveTweets()
	{
		return posTweets;
	}
	public ArrayList<SentimentTweet> getNegativeTweets()
	{
		return negTweets;
	}	
	public ArrayList<String> getNegationWords()
	{
		return negationWords;
	}	
	public ArrayList<String> getPositiveWords()
	{
		return positiveWords;
	}
	public ArrayList<String> getNegativeWords()
	{
		return negativeWords;
	}
	public ArrayList<String> getStopWords()
	{
		return stopWords;
	}
	public ArrayList<String> getProfileTerms()
	{
		return profileTerms;
	}
	public ArrayList<String> getSentimentWordList()
	{
		return wordSentimentList;
	}
	
	
}
