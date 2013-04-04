package sentiment.main;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import sentiment.analise.BiGramGeneration;
import sentiment.analise.NegationDetection;
import sentiment.analise.SentimentTweet;
import sentiment.analise.TermFilter;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.*;
import file.reader.ResourceReader;
import frontend.MainPage;

public class ClassifierSetup {
	
	public SentimentTweet[] theTweets;
	public int amountTweets;
	
	// Log for logging details
	Logger log;


	public NegationDetection negationD;
	public BiGramGeneration bigramGenerator = new BiGramGeneration();
	public ClassifierCreator cl;
	
	// Lists to contain Positive and Negative Tweets
	public ArrayList<SentimentTweet> NegList = new ArrayList<SentimentTweet>();
	public ArrayList<SentimentTweet> PosList = new ArrayList<SentimentTweet>();
	
	// Lists to contain the bigrams and unigrams created
	public ArrayList<String> PositiveTweetsInUnigrams = new ArrayList<String>();
	public ArrayList<String> NegativeTweetsInUnigrams = new ArrayList<String>();	
	public ArrayList<String> NegativeTweetsInBigrams = new ArrayList<String>();
	public ArrayList<String> PositiveTweetsInBigrams = new ArrayList<String>();
	
	ResourceReader resourceR;
	// WorldName refers to what world is being analised, it could be a specific company or a tv show in which a profile has been made
	public ClassifierSetup(String worldName, ResourceReader resourceReader)
	{
    	log = Logger.getLogger(ClassifierSetup.class);
    	log.debug("Starting to set up classifier");
    	
		try {			
			// Create objects of the tools to be used, pass the resource reader for file access
			resourceR = resourceReader;
        	TermFilter tf = new TermFilter(resourceReader );
        	negationD = new NegationDetection(resourceReader);
        	
        	PosList = resourceReader.getPositiveTweets();
        	NegList = resourceReader.getNegativeTweets();
        	// Create a new Naive Bayes classifier
			cl = new ClassifierCreator(new NaiveBayesMultinomial());
			cl.addCategory("positive");
            cl.addCategory("negative");
            cl.setupAfterCategorysAdded();
            
			for(int counter=0; counter<PosList.size(); counter++)
        	{				
	        		String Poscontent = PosList.get(counter).getTweetContent();  
	        		// Detect Negation in Sentences
	        		String sentenceSearched = negationD.detection(Poscontent);
	        		// Filter out common words and company terms etc
	        		String filteredPosContent = tf.removeCommonWords(sentenceSearched);
	        		        		
	        		// Create BiGrams from sentence
	        		List<String[]> generatedPositiveWords = bigramGenerator.biGramCreator(filteredPosContent);
	        		// Check if only unigrams can be made
	        		int bigramlevel = 2;
	        		if(generatedPositiveWords.size()<2)
	        		{
	        			bigramlevel=generatedPositiveWords.size();
	        		}
	        		if(bigramlevel>0)
        			{
		        		String[] PositiveUnigrams = generatedPositiveWords.get(0);
		        		for(int i=0; i<PositiveUnigrams.length; i++ )
	        			{
		        			PositiveTweetsInUnigrams.add(PositiveUnigrams[i]);
	        			}
        			}
	        		if(bigramlevel>1)
        			{
	        			String[] PositiveBigrams = generatedPositiveWords.get(1);
	        			for(int i=0; i<PositiveBigrams.length; i++ )
	        			{
	        				PositiveTweetsInBigrams.add(PositiveBigrams[i]);
	        			}
        			}
	        		
	        		for(int i=0; i<bigramlevel; i++)
	        		{
	        			for(int j=0; j<generatedPositiveWords.get(i).length; j++)
	        			{
				        	cl.addData(generatedPositiveWords.get(i)[j], "positive");	
	        			} // End for (words)
	        		} // End for  (BigramLevel)
        	}// End for amount positive tweets
        			
			for(int counter=0; counter<NegList.size(); counter++)
        	{		
        	        String Negcontent = NegList.get(counter).getTweetContent();
        	        Negcontent = negationD.detection(Negcontent);
        			String filteredNegContent = tf.removeCommonWords(Negcontent);
        			List<String[]> generatedNegativeWords = bigramGenerator.biGramCreator(filteredNegContent);
        				
        			int bigramlevel = 2;
        			// Get Min ngram of 2 or else if size of generated words is smaller than 2, use that unigrams only
        			if(generatedNegativeWords.size()<2)
        			{
        				bigramlevel=generatedNegativeWords.size();
        			}
        	
        			// if there is only one word then an idex error will arise
        			if(bigramlevel>0)
        			{
	        			String[] NegativeUnigrams = generatedNegativeWords.get(0);
	        			for(int i=0; i<NegativeUnigrams.length; i++ )
	        			{
	        				NegativeTweetsInUnigrams.add(NegativeUnigrams[i]);
	        			}
        			}
        			if(bigramlevel>1)
        			{
	        			String[] NegativeBigrams = generatedNegativeWords.get(1);
	        			for(int i=0; i<NegativeBigrams.length; i++ )
	        			{
	        				NegativeTweetsInBigrams.add(NegativeBigrams[i]);
	        			}
        			}        			
        			
        			for(int i=0; i<bigramlevel; i++)
        			{   	
        				for(int j=0; j<generatedNegativeWords.get(i).length; j++)
        				{        										
        					cl.addData(generatedNegativeWords.get(i)[j], "negative");        						
        				}
        			}        	        					
        	}// End for (amount tweets)
			
	        balanceData(cl);
		
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}	
	
	// Method to balance the skewed dataset
	public void balanceData(ClassifierCreator clas)
	{		
		log.debug("Balancing skewed data");
		int maxUnigrams = PositiveTweetsInUnigrams.size();
		int smallerUnigrams = NegativeTweetsInUnigrams.size();
		boolean negativeUniSmaller = true;
		
		if(maxUnigrams<NegativeTweetsInUnigrams.size())
		{
			maxUnigrams = NegativeTweetsInUnigrams.size();			
			smallerUnigrams = PositiveTweetsInUnigrams.size();
			negativeUniSmaller = false;			
		}
		
		int newpossize = PositiveTweetsInUnigrams.size();
	    int newnegsize = NegativeTweetsInUnigrams.size();
	    
	    Random ran = new Random();
        int randomMax = smallerUnigrams;
        int randomLine = ran.nextInt(randomMax) + (0);
        
        while(smallerUnigrams<maxUnigrams)
        {
            
            randomLine = ran.nextInt(randomMax) + (0);

        	if(negativeUniSmaller==true)
        	{
        		cl.addData(NegativeTweetsInUnigrams.get(randomLine), "negative");
        		newnegsize++;
        	}
        	else
        	{
        		cl.addData(PositiveTweetsInUnigrams.get(randomLine), "positive");
        		newpossize++;
        	}
        	smallerUnigrams++;
        }
        log.debug("New unigram Negative size is: " +newnegsize +" New unigram Positive size is: " +newpossize);
        
        
		int maxBigrams = PositiveTweetsInBigrams.size();
		int smallerBigrams = NegativeTweetsInBigrams.size();
		boolean negativeBiSmaller = true;
		
		if(maxBigrams<NegativeTweetsInBigrams.size())
		{
			maxBigrams = NegativeTweetsInBigrams.size();			
			smallerBigrams = PositiveTweetsInBigrams.size();
			negativeBiSmaller = false;			
		}
		
		newpossize = PositiveTweetsInBigrams.size();
	    newnegsize = NegativeTweetsInBigrams.size();
	    
	    
        randomMax = smallerBigrams;
        randomLine = ran.nextInt(randomMax) + (0);
        
        while(smallerBigrams<maxBigrams)
        {
            
            randomLine = ran.nextInt(randomMax) + (0);

        	if(negativeBiSmaller==true)
        	{
        		cl.addData(NegativeTweetsInBigrams.get(randomLine), "negative");
        		newnegsize++;
        	}
        	else
        	{
        		cl.addData(PositiveTweetsInBigrams.get(randomLine), "positive");
        		newpossize++;
        	}
        	smallerBigrams++;
        }
        log.debug("New bigram Negative size is: " +newnegsize +" New bigram Positive size is: " +newpossize);
      
		
	}
	
	public ClassifierCreator getClassifier()
	{
		return cl;
	}	
	public ResourceReader getResourceReader()
	{
		return resourceR;
	}
}
