package sentiment.analise;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

// Class to create a list of unigrams and bigrams
public class BiGramGeneration {
	
	public BiGramGeneration(){}

	public List<String> addToList(String sentence)
	{
		List<String> words = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(sentence, " ");
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();
			words.add(token);
		}	
		return words;
	}
	
	// Method to create unigrams and bigrams from a string
	public static List<String[]> biGramCreator(String s) {
		// Split the string up
		String[] stringParts = s.split(" ");
		// List of arrays to hold arrays of both unigrams and bigrams
		List<String[]> results = new ArrayList<String[]>();
		// Len indicates what level of ngram is being generated. 0 is unigrams, 1 is bigrams
		int len=0;
		for(int j=0;j<2; j++)
		{
			// Size of array created
			int size;
			if(j==0)
			{ //unigrams
				size = stringParts.length;
			}
			else
			{//bigrams
				size = stringParts.length-1;
			}
			// Result array
			String[] result = new String[size];
			
			for(int i = 0; i < size; i++) 
			{				
			   StringBuilder sbuild = new StringBuilder();
			   for(int termNum = 0; termNum < len; termNum++) 
			   {
				   // If its not the first run though, append a space
			       if(termNum > 0) 
			       {
			    	   sbuild.append(' ');
			       }
			       sbuild.append(stringParts[i+termNum]);
			   }
			   result[i] = sbuild.toString();
			}						
			results.add(result);
			len++;
		}
		//for(int i=0; i<results.size(); i++)
		//{
			//for(int j=0; j<results.get(i).length; j++)
			//{
			//	System.out.println("Result: i="+i +" j=" +j +" " +results.get(i)[j]);
			//}
		//}
		return results;
	}
}

