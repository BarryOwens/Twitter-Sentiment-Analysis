package sentiment.analise;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import file.reader.ResourceReader;

// Class to find negation in the string
public class NegationDetection {

	Logger log;	
	public List<String> negationWords;
	public List<String> positiveWords;
	public List<String> negativeWords;

	public File dir = new File("Resources");
	
	
	public NegationDetection(ResourceReader reader)
	{
    	log = Logger.getLogger(NegationDetection.class);

		// Read in each of the words into the lists
		negationWords = reader.getNegationWords();
		negativeWords = reader.getNegativeWords();
		positiveWords = reader.getPositiveWords();				
	}
	// Use this if you want detection to be left out, just rename the real detection method to something else
	//public String detection(String sentence)
	//{
	//	return sentence;
	///}
	
	public String detection(String sentence)
	{
		boolean negationFound = false;
		// Sentence to return
		String updatedSentence = "";
		// Add all the words in the sentence to a list
		List<String> wordList = getWordListFromSentence(sentence);           
		
		
		// List to hold the positions of each word of negation
		List<Integer> negWordPos = findNegationPositions(wordList);
		
		// If a term related to negation has been found in the sentence, continue. Otherwise do not.
		if (negWordPos.size()>0)
		{
			List<Integer> WordsPositions = findTermPositions(wordList);	
			// For each negation word do the following:
			for(int negWord=0; negWord< negWordPos.size(); negWord++)
			{
				int negtionWordPosition = negWordPos.get(negWord);				
				for(int term=0; term< WordsPositions.size(); term++)				
				{
					int termPosition = WordsPositions.get(term);
					
					// If the term is 1 in front of the negation word. Example: 'Not good'
					if(termPosition==(negtionWordPosition+1))
					{
						log.debug("Negation found in " +sentence);
						for(int wordNum=0;wordNum<wordList.size(); wordNum++)
						{
							// If we are not in the position of the word of negation
							if(wordNum==negtionWordPosition)
							{
								// Create a new word using it and the term next to it. 
								String newWordCreated = wordList.get(negtionWordPosition)+wordList.get(negtionWordPosition+1);
								//String newWordCreated = "NOT_"+wordList.get(negtionWordPosition+1);

								if(wordNum==0)
									{	
										updatedSentence="";
										//First pass, so leave out the starting space
										updatedSentence = updatedSentence+newWordCreated;
										negationFound = true;
									}
									else
									{
										updatedSentence = updatedSentence +" "+newWordCreated;
										negationFound = true;
									}
									wordNum++;									
							}							
							else
							{							
								if(wordNum==0)
								{	//First pass, so leave out the starting space
									updatedSentence="";
									updatedSentence = updatedSentence +wordList.get(wordNum);									
								}
								else
								{
									updatedSentence = updatedSentence +" " +wordList.get(wordNum);
								}
							}							
						}
						// After going through the loop of word in the word list, update the positions of negative words as a negative word may have been replaced
						wordList = getWordListFromSentence(updatedSentence);           
						negWordPos = findNegationPositions(wordList);
						WordsPositions = findTermPositions(wordList);
						negationFound = true;
					}
					else if(termPosition==(negtionWordPosition+2))// Example: Not Very Good
					{
						log.debug("Negation found in " +sentence);
						
						// Loop through the list of word to find the positive and negative words
						for(int wordNum=0;wordNum<wordList.size(); wordNum++)
						{
							// Do not add the negation word or the positive/negative word
							if(wordNum==termPosition || wordNum==negtionWordPosition)
							{
								
								String newWordCreated = wordList.get(negtionWordPosition)+wordList.get(termPosition);
							//	String newWordCreated ="NOT_"+wordList.get(termPosition);

								if(wordNum==negtionWordPosition)
								{
									if(wordNum==0)
									{	
										updatedSentence="";
										//First pass, so leave out the starting space
										updatedSentence = updatedSentence+newWordCreated;
										negationFound = true;
									}
									else
									{
										updatedSentence = updatedSentence +" "+newWordCreated;
										negationFound = true;
									}
									wordNum++;
									wordNum++;								
								}
								
							}
							else{							
								if(wordNum==0)
								{	//First pass, so leave out the starting space
									updatedSentence = updatedSentence +wordList.get(wordNum);
								}
								else
								{
									updatedSentence = updatedSentence +" " +wordList.get(wordNum);
								}
							}
						}						
					}
											
				}		
			}
		}
		if(negationFound==false)
		{
			updatedSentence= sentence;

		}
		else
		{
			log.debug("Sentence has been updated to: "+updatedSentence);
		}
		
		return updatedSentence;		
	}
	
	public ArrayList<String> getWordListFromSentence(String sentence)
	{
		StringTokenizer st = new StringTokenizer(sentence, " ");
		ArrayList<String> wordList = new ArrayList<String>();           
		while(st.hasMoreTokens())
		{//Create a list of all unigrams in the sentence
			String word = st.nextToken().toLowerCase();
			wordList.add(word);
		}
		return wordList;
	}
	       
	public List<Integer> findNegationPositions(List<String> wordList)
	{
		List<Integer> negWordPos = new ArrayList<Integer>();
		// Loop through each word to find if there is any negation words
		for(int wordNum=0; wordNum<wordList.size(); wordNum++)
		{
			String wordInQuestion =  wordList.get(wordNum);
			wordInQuestion = wordInQuestion.toLowerCase();
			if(wordInQuestion.endsWith("n't"))
			{
				negWordPos.add(wordNum);
			}
			else
			{
				for(int lineNum = 0; lineNum< negationWords.size(); lineNum++)
				{
					if(wordInQuestion.equals(negationWords.get(lineNum)))
					{
						negWordPos.add(wordNum);
					}
				}				
			}	
		}
		return negWordPos;
	}
	
	public List<Integer> findTermPositions(List<String> wordList)
	{
		List<Integer> wordPositions = new ArrayList<Integer>();
	
		
		// Loop through each word to find if there is any negation words
		for(int wordNum=0; wordNum<wordList.size(); wordNum++)
		{
			String wordInQuestion =  wordList.get(wordNum).toLowerCase();
			
			for(int lineNum = 0; lineNum< positiveWords.size(); lineNum++)
			{
				if(wordInQuestion.equals(positiveWords.get(lineNum)))
				{
					wordPositions.add(wordNum);	
					break;
				}
			}
			for(int lineNum = 0; lineNum< negativeWords.size(); lineNum++)
			{
				if(wordInQuestion.equals(negativeWords.get(lineNum)))
				{
					wordPositions.add(wordNum);	
					break;
				}
			}
		}	
	
		return wordPositions;
	}
	
	
}

