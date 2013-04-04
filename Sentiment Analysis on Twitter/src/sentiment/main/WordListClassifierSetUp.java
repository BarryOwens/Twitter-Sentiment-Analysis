package sentiment.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import file.reader.ResourceReader;

import weka.classifiers.bayes.NaiveBayesMultinomial;

// Class to create a second classifier, using the list of positive and negative terms used for negation detection
public class WordListClassifierSetUp {
	
	public ClassifierCreator cl;
	
	public WordListClassifierSetUp(ResourceReader resourceReader)
	{
		
		try {				
				// List of strings containing words \t and sentiment
				ArrayList<String> wordsAndSentiment = resourceReader.getSentimentWordList();
				
				
				ArrayList<wordSentiment> PosSentiWordList = new ArrayList<wordSentiment>();
				ArrayList<wordSentiment> NegSentiWordList = new ArrayList<wordSentiment>();

				String line;
				String word;
				String sentiment;
				
				for(int i=0;i<wordsAndSentiment.size(); i++)					
				{
					line = wordsAndSentiment.get(i);
				    StringTokenizer st = new StringTokenizer(line,"\t"); 
				    word = st.nextToken();
				    sentiment = st.nextToken();
				    wordSentiment tempWord = new wordSentiment(word, sentiment);
				    if(sentiment.equals("negative"))
				    {
				    	NegSentiWordList.add(tempWord);
				    }
				    else
				    {
				    	PosSentiWordList.add(tempWord);

				    }
				}
				
				cl = new ClassifierCreator(new NaiveBayesMultinomial());
				cl.addCategory("positive");
	            cl.addCategory("negative");
	            cl.setupAfterCategorysAdded();
	            
	            
	            for(int i=0; i<PosSentiWordList.size(); i++)
	            {	            	
	            	cl.addData(PosSentiWordList.get(i).getWord(), "positive");
	            }
	            for(int j=0; j<NegSentiWordList.size(); j++)
	            {
	            	cl.addData(NegSentiWordList.get(j).getWord(), "negative");	            	            	
	            }
	            
	            // Balance the data by random subsampling the minor class
	            balanceData(cl, PosSentiWordList, NegSentiWordList);
	            
	            
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	// Function used to balance the skewed dataset by randomly sub sampling the minority class.
	public void balanceData(ClassifierCreator cl, ArrayList<wordSentiment> PosList, ArrayList<wordSentiment> NegList)
	{
		
		int majorityClass;
        int minorityClass;
        
        // Initalize the max as the positive word and minorityClass as Negative words
        majorityClass = PosList.size();
        minorityClass = NegList.size();
        boolean negIsSmaller = true;

        
        System.out.println("Size of Positive: " +PosList.size());
        System.out.println("Size of Negative: " +NegList.size());

        if(majorityClass<NegList.size())
        {
        	majorityClass = NegList.size();
        	minorityClass = PosList.size();
        	negIsSmaller = false;	           
        }
        
        int newpossize = PosList.size();
        int newnegsize = NegList.size();

        Random ran = new Random();
        int randomMax = minorityClass;
        int randomLine = ran.nextInt(randomMax) + (0);
        
        // Keep adding data to the classifier until the same amount of classes are negative and positive
        while(minorityClass<majorityClass)
        {
            
            randomLine = ran.nextInt(randomMax) + (0);

        	if(negIsSmaller==true)
        	{
        		cl.addData(NegList.get(randomLine).getWord(), "negative");
        		newnegsize++;
        	}
        	else
        	{
        		cl.addData(PosList.get(randomLine).getWord(), "positive");
        		newpossize++;
        	}
        	minorityClass++;
        }
        System.out.println("New Negative size is: " +newnegsize +"\n New Positive size is: " +newpossize);	
	}
	public ClassifierCreator getClassifier()
	{
		return cl;
	}	
}

class wordSentiment
{
	public String word;
	public String sentiment;
	
	public wordSentiment(String w, String s)
	{
		word = w;
		sentiment = s;
	}
	
	public String getSentiment()
	{
		return sentiment;
	}
	
	public String getWord()
	{
		return word;
	}
}
