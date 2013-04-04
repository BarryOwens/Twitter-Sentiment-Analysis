package sentiment.analise;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import sentiment.main.ClassifierSetup;

import file.reader.ResourceReader;

public class TermFilter {
	
	public String sentenceToCheck;
	public ArrayList<String> stopWords;
	public ArrayList<String> profileTerms;
	
	// Log4j log
	Logger log;
	
	public TermFilter(ResourceReader resourceReader)
	{
    	log = Logger.getLogger(TermFilter.class);
    	//log.debug("Starting Term Filter");
		stopWords = resourceReader.getStopWords();
		profileTerms = resourceReader.getProfileTerms();
		
	}
	// This method removes any words in the stop list and company terms. It also removes links and commas and full stops.
	public String removeCommonWords(String sentenceToCheck)
	{
		String updatedSentence = "";
		boolean removed = false;
		sentenceToCheck = sentenceToCheck.toLowerCase();
	    StringTokenizer st = new StringTokenizer(sentenceToCheck," ");

	    // Examine each term
	    while(st.hasMoreTokens())
	    {
	    	String word = st.nextToken();
	    	word = word.toLowerCase();
	    	// If the word is a stop word
	    	if(stopWords.contains(word))	    	
	    	{
	    		removed = true;
	    	}
	    	else if(word.startsWith("http://"))
	    	{
	    		removed = true;
	    	}
	    	else if(profileTerms.contains(word))
	    	{
	    		removed = true;
	    	}
	    	else
	    	{
	    		word = tidyUp(word);
	    		if(word.equals(""))
	    		{
	    			// Do Nothing
	    		}
	    		else
	    		{
	    		updatedSentence = updatedSentence +word +" ";
	    		}
	    	}

	    }
	    if (removed==true)
	    {
	    	// One or more terms have been removed
	    	// log.debug("Sentence before is:" +sentenceToCheck);
	    	// log.debug("Sentence after is:" +updatedSentence);
	    }

	    updatedSentence= updatedSentence.substring(0, updatedSentence.length());
	    return updatedSentence;

	}
	//Tidy up each word,
	public String tidyUp(String w)
	{
		// Replace the symbols below to so that words are not unique from words without them. E.g "cool!" =/= "cool"
		w = w.replace(",", "");
		w = w.replace(".", "");
		w = w.replace(":", "");
		w = w.replace(";", "");
		w = w.replace("-", " ");			
		w = w.replace("!", "");
		w = w.replace(")", "");
		w = w.replace("(", "");
		w = w.replace("?", "");	
		w = w.replace("\"", "");
		w = w.replace("#", "");	
		w = w.replace(" ", "");	
		
		return w;
	}

	
}
