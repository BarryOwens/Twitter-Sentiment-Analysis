import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;


import java.io.PrintWriter;
import java.util.StringTokenizer;


public class Datalabel {
	String file;
	int startLine=0;
	int amountLines=0;
	String lines[] = new String[100000];
	// read file
	// read line
	// label positive, negative or other
	// 
	public Datalabel(String fileName, int start)
	{
		file = fileName;
		startLine=start;
	}
	public Datalabel(String fileName)
	{
		file = fileName;
		startLine=0;
	}

	public void getTweets()
	{
		try {
		String line;
		
	    BufferedReader bufRdr  = new BufferedReader(new FileReader(file));  
		
	    System.out.println("Putting lines into array");
				while((line = bufRdr.readLine()) != null)  
				{
					lines[amountLines] = line;
					amountLines++;
				}
		
				System.out.println(lines[4]);
		
		
		//PrintWriter Pout = new PrintWriter(new BufferedWriter(new FileWriter("LabelledTweets.tsv", true)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void markTweets()
	{
		String[] options = new String[] {"Positive", "Negative", "Neutral", "Useless/Other", "Quit"};
		int choice = 0;
		StringTokenizer st;
		String line;
		int count=0;
		for(count=startLine; count<amountLines; count++)
		{
			
			st = new StringTokenizer(lines[count], "\t");
			st.nextToken();//id
			st.nextToken();//date
			st.nextToken();//username
			line = st.nextToken();
			
			// print line
			// ask user to click 3 options
			// if user enters 1. append to file X Y Z
			choice =    JOptionPane.showOptionDialog(null, line, "Message", 
			        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
			        null, options, options[0]);
			System.out.println(choice);
			
			
			if(choice==0)
			{
				// append to file as positive
				appendToFile(lines[count], "positive", "labelledTweets.tsv");
			}
			if(choice==1)
			{
				// append to file as negative
				appendToFile(lines[count], "negative", "labelledTweets.tsv");

			}
			if(choice==2)
			{
				// append to file as neutral
				appendToFile(lines[count], "neutral", "labelledTweets.tsv");

			}
			if(choice==3)
			{
				// append to other file
				appendToFile(lines[count], "other", "NotLabeledTweets.tsv");
			}
			if(choice==-1 || choice==4)
			{
				System.out.println(count);
				JOptionPane.showMessageDialog(null, "Please remember to start reading from line "+count +" next time you label this file");
				break;
			}
		}
			
	}
	
	public static void appendToFile(String line, String sentiment, String fileName)
	{	    
		try {
			PrintWriter Pout = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			// Make sure there are no tab delimeters or new line delimeteres in the tweet
			StringTokenizer st;
			String id;
			String user;
			String content;
			String created;
			System.out.println("About to append " +line +" as " +sentiment);
				st = new StringTokenizer(line, "\t");
				// print out Id, created at, user, content
				id = st.nextToken();
				created = st.nextToken();
				user= st.nextToken();
				content = st.nextToken();
				Pout.println(id+"\t"+sentiment +"\t"+content);
				Pout.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
		
}
	
	
	
		
		
		




