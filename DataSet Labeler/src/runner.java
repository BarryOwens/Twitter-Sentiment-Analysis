import javax.swing.JOptionPane;


public class runner {
	
	public static void main(String args[])
	{
		System.out.println("Starting application");
		
		JOptionPane.showMessageDialog ( 
				   null, "You will be shown a tweet and given options on what to label that tweet as: Positive, negative neutral or other/useless. \nIf the tweet does not relate to the company choose useless/other. E.g 'I love apple tarts' -> Does not relate to the company Apple \nIf you are unsure or do not know which choice to select choose Useless/Other \n\nYou can quit and pick up at anytime, just make sure you remember the line number given to you at the end" ); 
		String amount=   
			JOptionPane.showInputDialog ( "Please enter what line you would like to start reading from, if this is the first time to start labeling enter 0" ); 
		int lineStart = Integer.parseInt(amount);
		String fileName = "tweets.tsv";
	
		Datalabel tw;
		if(lineStart==0)
		{
			tw = new Datalabel(fileName);
		}
		else
		{
			tw = new Datalabel(fileName, lineStart);
		}
		// Get the tweets
		tw.getTweets();
		// Start the user marking tweets
		tw.markTweets();
	}

}
