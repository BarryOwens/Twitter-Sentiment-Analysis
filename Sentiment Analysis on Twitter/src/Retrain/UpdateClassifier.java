package Retrain;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import sentiment.analise.SentimentTweet;
import sentiment.main.ClassifierSetup;

public class UpdateClassifier {
	
	Logger log;
	private ServletContext servletC;
	private String folder;
	public UpdateClassifier(String profile, ServletContext sc)
	{
		log = Logger.getLogger(ClassifierSetup.class);
    	log.debug("Starting update classifier");
		folder = profile;
		servletC = sc;
	}
	
	public void addToTrainingSet(SentimentTweet tweet) 
	{
		PrintWriter Pout;
		log.debug("Started addTo Training set method");
		try {
				String filename = servletC.getRealPath("/WEB-INF/Resources/"+folder+"/LabelledTweets.tsv");
				log.debug("File name is: " +filename);
				Pout = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
				Pout.println(tweet.getID()+"\t"+ tweet.getSentiment()+"\t"+tweet.getTweetContent());
				Pout.close();
			} catch (IOException e) {
			log.error("Error adding tweet to file");
			e.printStackTrace();
			}
		
	}

}
