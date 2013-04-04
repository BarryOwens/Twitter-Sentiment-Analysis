package sentiment.analise;

import org.apache.log4j.Logger;

// Class to search for emoticons or smilies in the string
public class EmoticonDetection {
	
	Logger log;

	public EmoticonDetection()
	{
		log = Logger.getLogger(EmoticonDetection.class);
	}
	
	public int checkForEmoticons(String sentence)
	{
		sentence = sentence.toUpperCase();
		int returnValue = 0;
		boolean NegFound = false;
		// Known smilies taken from http://www.see-search.com
		String[] knownPosEmoticons = {":)",":-)",";-)","(:","(-:",":-D",":-)",":-P",":D","XD"};
		String[] knownNegEmoticons = {":(",":-(",":-@",":-O",":-/",">:(",">:-(","/-:"};

		for(int i=0; i<knownNegEmoticons.length; i++)
		{
			if(sentence.contains(knownNegEmoticons[i]))
			{
				log.debug("Found Neg Emoticon: " +knownNegEmoticons[i] +" in '" +sentence +"'");
				returnValue = -1;
				NegFound=true;
				break;
			}
			else
			{
				returnValue = 0;
			}
		}
		// Check if a negative smiley has been found, so not to write over the return value
		if(NegFound==false)
		{
			for(int i=0; i<knownPosEmoticons.length; i++)
			{
				if(sentence.contains(knownPosEmoticons[i]))
				{
					log.debug("Found Pos Emoticon: " +knownPosEmoticons[i] +" in '" +sentence +"'");
					returnValue = 1;
					break;
				}
				else
				{
					returnValue = 0;
				}
			}
		}			
		// Return the value
		return returnValue;
		
	}

}
