package frontend;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import Retrain.UpdateClassifier;

import twitter.download.Downloader;
import twitter4j.Tweet;
import sentiment.main.*;
import sentiment.analise.*;
import file.reader.*;


/**
 * Servlet implementation class TestServlet
 */
public class MainPage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ArrayList<SentimentTweet> analisedTweets;
	public UpdateClassifier retrainer;
	Logger log;
    /**
     * Default constructor. 
     */
    public MainPage() {
    }

    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Set up log file
    	ServletContext context = this.getServletContext();
    	System.setProperty("appRootPath", context.getRealPath("/"));
    	System.out.println("Additional logs can be found at " +System.getProperty("appRootPath")+"WEB-INF"+File.separator+"logs");
    	log = Logger.getLogger(MainPage.class);    	
    	
		log.debug("In doGet, printing off start page");
		PrintWriter out = response.getWriter();
	    out.println(getHTMLHead());
	    out.println("<body>");
	    out.println(getBodyContent(request));	    
	    out.println("</body>");
	    if(request.getParameter("UpdateFigures") !=null)
     	{
	    	log.debug("Updating corrected tweets");
     		if(analisedTweets !=null )
     		{
		     	for(int i=0;i<analisedTweets.size(); i++)
		     	{
		     		// if the sentiment has been corrected for this line
		     		if(request.getParameter("sentimentCorrection"+i) != null)
		     		{
		     			String sentimentChange = request.getParameter("sentimentCorrection"+i);
		     			// Set the sentiment to the new sentiment
		     			analisedTweets.get(i).setSentiment(sentimentChange);
		     			
		     			retrainer.addToTrainingSet(analisedTweets.get(i));
		     			// Log that the file has been added to be training data
		    	    	log.debug("Adding new line to labelled tweets:"+ analisedTweets.get(i).getTweetContent() +" with sentiment: "+sentimentChange);
		     			//System.out.println("Adding new line:"+ analisedTweets.get(i).getTweetContent() +" with sentiment: "+sentimentChange);
		     		}
		     	}
     		}// Analised Tweets null check
     	}
	    out.println("</html>");
	    out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Find what profile was chosen
		// Print out Start page for that profile
		PrintWriter out = response.getWriter();
					
			String profile = request.getParameter("profile");	
			String data = request.getParameter("dataToUse");	

			// Set the re-tainer to be the new profile being analised
			retrainer = new UpdateClassifier(profile, this.getServletContext());
		
			// Print out the HTML Head
			out.println(getHTMLHead());
		    out.println("</head>");
		    out.println("<body>");
		    // Output goes here
			if(data.equals("TestData"))
			{
				printTestDataResults(profile, response);
			}
			else if(data.equals("TwitterData"))
			{
				// Do nothing here
			}
			else
			{
				log.error("Error with radio buttons");
			}
			//out.println("<button name='Analise' value=''Analise'");
			out.println("<div id='Information'> \n Welcome, now processing </div>");
			out.println("<table border='0'>");
			out.println("<tr><td><div id='infoUpdate'>  </div></td>");
			out.println("<td><div id='chart_div'>  </div></td></tr>");
			out.println("</table>");
			out.println("</body>");
			out.println("</html>");
			out.flush();		
			
			if(data.equals("TwitterData"))
			{
				printTwitterResults(profile, response);
			}

	
	}
	
	public void printTwitterResults(String profile, HttpServletResponse response) throws IOException
	{
		int amountPos=0;
		int amountNeg=0;
		int amountNeu=0;
		// Print one thing, flush, print another
		ResourceReader resourceR = new ResourceReader(this.getServletContext(), profile);
		PrintWriter out = response.getWriter();	
		
		// Setting up classifier
		out.println("<script> updateInformation(0); </script> ");		
		out.flush();		
		ClassifierSetup clSet = new ClassifierSetup(profile, resourceR);
		ClassifierCreator myClassifier = clSet.getClassifier();
		
		// Completed setting up classifier, downloading tweets
		out.println("<script> updateInformation(1); </script> ");
		out.flush();		
		// Download and get the tweets
		Downloader dl = new Downloader(resourceR.readQueryTerms(profile));
		ArrayList<Tweet> tweetsToAnalise = dl.getTweets();

		// Completed downloading Tweets, analyzing Tweets
		out.println("<script> updateInformation(2); </script> ");
		out.flush();
		// Label each of the downloaded tweets
		TwitterSentimentLabeler sl = new TwitterSentimentLabeler(myClassifier, tweetsToAnalise, resourceR);
		analisedTweets = sl.classifyTweets();
		
		// Completed analyzing Tweets
		out.println("<script> updateInformation(3); </script> ");
		out.flush();
		
		// Print out Results
		out.println("<div id='results' style=\"font-family: 'Pontano Sans', sans-serif;\">");
	    out.println("<form method='GET' Action='"+this.getServletContext().getContextPath() +"/Analysis'>");
		out.println("<table width='1450' class='gridtable'>");
		out.println("<tr><td> Tweet </td> <td> Predicted Sentiment </td> <td style='width: 230px'> Correct Sentiment </td> </tr>");
		// For each of the analised tweets, print out the predicted sentiment, keep a record of how much of eac arise to create pie chart
		for(int i=0; i<analisedTweets.size(); i++)
		{		
			SentimentTweet tempTweet = analisedTweets.get(i);
			if(tempTweet.getPredictedSentiment().equals(("negative")))
			{
				amountNeg++;
			}
			else if(tempTweet.getPredictedSentiment().equals(("neutral")))
			{
				amountNeu++;
			}
			else if(tempTweet.getPredictedSentiment().equals(("positive")))
			{
				amountPos++;
			}
			// Create a hotink to original tweet
			Tweet temp = tempTweet.getTwitterTweet();
			String url = "http://www.twitter.com/"+temp.getFromUser()+"/status/"+temp.getId();
			out.println(" <tr> <td> <a href='"+url +"' style='text-decoration: none'> " +tempTweet.getTweetContent() +"</a></td> <td>" +htmlFontAdder(tempTweet.getPredictedSentiment())+tempTweet.getPredictedValue()+"</td><td>\n<label> \n<div class='buttonPos'>\n<input type='radio' name='sentimentCorrection"+i+"' value='positive'>Pos\n</div>\n</label>\n<label>\n<div class='buttonNeu'><input type='radio' name='sentimentCorrection"+i+"' value='neutral'>Neu\n</div>\n</label>\n<label>\n<div class='buttonNeg'><input type='radio' name='sentimentCorrection"+i+"' value='negative'>Neg\n</div>\n</label></tr>\n");	
		}
		out.println( "</table>");
		out.println("<input type='submit' name='UpdateFigures' value='Update Figures' >");
	  	out.println("</form>");
	  	out.println("</div>");
	  	out.println("<script> drawChart("+amountPos+","+amountNeu+","+amountNeg+"); </script>");
	}
	
	// Print the Results from the Test data
	public void printTestDataResults(String profile, HttpServletResponse response) throws IOException
	{
		// Get files and setup classifier
		ResourceReader resourceR = new ResourceReader(this.getServletContext(), profile);
		ClassifierSetup clSet = new ClassifierSetup(profile, resourceR);
		ClassifierCreator myClassifier = clSet.getClassifier();

		// Get the tweets to analise and classify them
		ArrayList<SentimentTweet> tweetsToAnalise = resourceR.readTestTweets(profile);
		TestSentimentLabeler sl = new TestSentimentLabeler(myClassifier, tweetsToAnalise, resourceR );
		ArrayList<SentimentTweet> analisedTweets = sl.classifyTweets();
		
		
		PrintWriter out = response.getWriter();	
		double amountCorrect =0;
		double amountAnalised = 0;
		out.println("<div id='results' style=\"font-family: 'Pontano Sans', sans-serif;\">");
		out.println("<table border='1'>");
		for(int i=0; i<analisedTweets.size(); i++)
		{		
			SentimentTweet tempTweet = analisedTweets.get(i);		
			if(tempTweet.getSentiment().equals("neutral"))
			{
				// Don't try to Detect Neutral for now
			}
			else
			{
				amountAnalised++;
				out.println(" <tr> <td> " +tempTweet.getTweetContent() +"</td> <td>" +htmlFontAdder(tempTweet.getPredictedSentiment())+tempTweet.getPredictedValue()+"</td><td>"+htmlFontAdder(tempTweet.getSentiment())+"</td></tr>");
				if(tempTweet.getPredictedSentiment().equals(tempTweet.getSentiment()))
				{
					amountCorrect++;
				}
			}	
			
		}
		out.println( "</table>");
		out.println("</div>");
		//log.debug("Amount correct is:" +amountCorrect);
		//log.debug("Amount analised is:" +amountAnalised);
	//	System.out.println("Amount correct is:" +amountCorrect);
	//	System.out.println("Amount analised is:" +amountAnalised);

		double accuracy = amountCorrect/amountAnalised;
		out.println( "The classifier is "+accuracy*100 +"% accurate");

	}
	
	public String htmlFontAdder(String sentiment)
	{
		String htmlSentiment ="";
		
		if(sentiment.equals("positive"))
		{
			htmlSentiment = "<font size='3' color='green'>positive </font>";
		}
		else if(sentiment.equals("negative"))
		{
			htmlSentiment = "<font size='3' color='red'>negative </font>";
		}
		else if(sentiment.equals("neutral"))
		{
			htmlSentiment = "<font size='3' color='blue'>neutral </font>";
		}
		else
		{
			htmlSentiment = "<font size='3' color='YELLOW'>NOT FOUND </font>";			
		}
		return htmlSentiment;
	}
	
	public String getHTMLHead()
	{
		String returnString = "<head>" 
				 +" \n <title>" 
				 +" \n Twitter Sentiment Analysis tool" 
				 +" \n </title>" 
				 +" \n <link rel='icon' href='"+this.getServletContext().getContextPath()+"/design/favicon.ico' />" 
				 +" \n <link rel='stylesheet' href='"+this.getServletContext().getContextPath()+"/design/css/style.css' media='screen' type='text/css'/>" 
				 +" \n <link href='http://fonts.googleapis.com/css?family=Pontano+Sans' rel='stylesheet' type='text/css'>" 
				 +" \n <link href='http://fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>" 
				 +" \n <link href='http://fonts.googleapis.com/css?family=Open+Sans:600' rel='stylesheet' type='text/css'>" 
				 +" \n " 
				 +" \n <script src='http://code.jquery.com/jquery-latest.min.js'></script>" 
				 +" \n <script type='text/javascript' src='https://www.google.com/jsapi'></script>" 
				 +" \n <script type='text/javascript'>" 
				 +" \n 	google.load('visualization', '1.0', {'packages':['corechart']});" 
				 +" \n 	function drawChart(pos, neu, neg) { " 
				 +" \n 		$(document).ready(function(){       " 
				 +" \n 			//Create the data table.       " 
				 +" \n 			 var data = new google.visualization.DataTable();       " 
				 +" \n 			 data.addColumn('string', 'Sentiment');       " 
				 +" \n 			 data.addColumn('number', 'Amount Found');       " 
				 +" \n 			 data.addRows([       " 
				 +" \n 				   ['Positive', pos],       " 
				 +" \n 				   ['Negative', neg]," 
				 +" \n 				   ['Neutral', neu],       " 
				 +" \n 				   ]);		" 
				 +" \n 			var options = {'title':'Sentiment Analysis Results',colors:['004F00','8F0004','0E1C4D'],   	" 
				 +" \n 			'width':400,    	" 
				 +" \n 			'height':300};       " 
				 +" \n 			//Instantiate and draw our chart, passing in some options.       " 
				 +" \n 			var chart = new google.visualization.PieChart(document.getElementById('chart_div'));       " 
				 +" \n 			chart.draw(data, options);" 
				 +" \n 		})" 
				 +" \n 	}   " 
				 +" \n </script>" 	+
				getReplaceFunction()+
				 " \n </head>" ;
		return returnString;
	}
	
	public String getReplaceFunction()
	{
		String returnString = "";
		returnString = returnString+"<script>\n" +
				"function updateInformation(stage)\n" +
				"{	\n" +				
				//	"$(document).ready(function(){" +
						"if(stage=='0')\n" +
						"{\n" +
							"$('#infoUpdate').empty();\n"+				
							"$('#infoUpdate').append('<br>Setting up Classifier <img src=\""+this.getServletContext().getContextPath()+"/design/loading.gif\" alt=\"Loading\">');\n"+
						"}\n" + // End if
						"else if(stage=='1')\n" +
						"{\n" +								
							"$('#infoUpdate').empty();\n"+				
							"$('#infoUpdate').append(' Setting up Classifier <img src=\""+this.getServletContext().getContextPath()+"/design/tick.png\" alt=\"done\"> <br> Downloading Tweets <img src=\""+this.getServletContext().getContextPath()+"/design/loading.gif\" alt=\"Loading\">');\n"+
						"}\n" + // End if
						"else if(stage=='2')\n" +
						"{\n" +		
							"$('#infoUpdate').empty();\n"+	
							"$('#infoUpdate').append(' Setting up Classifier <img src=\""+this.getServletContext().getContextPath()+"/design/tick.png\" alt=\"done\"> <br> Downloading Tweets <img src=\""+this.getServletContext().getContextPath()+"/design/tick.png\" alt=\"done\"> <br> Analising Tweets <img src=\""+this.getServletContext().getContextPath()+"/design/loading.gif\" alt=\"Loading\">');\n"+
						"}\n" + // End if
						"else if(stage=='3')\n" +
						"{\n" +		
							"$('#infoUpdate').empty();\n"+	
							"$('#infoUpdate').append(' Setting up Classifier <img src=\""+this.getServletContext().getContextPath()+"/design/tick.png\" alt=\"done\"> <br> Downloading Tweets <img src=\""+this.getServletContext().getContextPath()+"/design/tick.png\" alt=\"done\">  <br> Analising Tweets <img src=\""+this.getServletContext().getContextPath()+"/design/tick.png\" alt=\"done\">');\n"+
						"}\n" + // End if
				//	"}" +
				//	");" +
				"}\n"+
						  "</script>\n";
				
		return returnString;
	}
	
	public String getBodyContent(HttpServletRequest request)
	{
		String htmlString=
				" \n 	<div id='container' align='center' style='overflow:hidden;'>" 
				 +" \n 		<div id='title' style='min-width:500px; overflow:hidden'>" 
				 +" \n 		Twitter Sentiment Analysis" 
				 +" \n 		</div>" 				
				 +" \n 		<div id='information' align='left' style='max-width:600px; position:relative; display:block; font-family: Open Sans, sans-serif;'>" 
				 +" \n 		" 
				 +" \n 		<br align='center'><div align='center' style='color:0E1C4C'> What is Sentiment Analysis?	</div>	" 
				 +" \n 		Sentiment analysis is the process of using natural language processing and data mining techniques to characterise the sentiment of a given piece of text. When this technique is applied to Twitter where many people express their opinions it can have various uses in many situations. Examples of these include predicting the winner of an event such as a presidential election, determining how well a film will do in the box office, finding out how the public reacts to a specific product or service and many more.  " 
				 +" \n 		<br>" 
				 +" \n 		<br>" 
				 +" \n 		I have chosen to test my sentiment analysis application for various areas. These include finding the sentiment towards specific companies, brands and products and analising how people react after an award winner is announced for a major event. Below you can choose from different profiles which I have chosen to train my application with. The test results for these profiles can be chosen to be shown. The latest twitter data can also be analised for most cases. For profiles such as the Grammy awards, most likely no useful data will currently exist." 
				 +" \n " 
				 +" \n 		" 
				 +" \n 		</div>" 
				 +" \n 		<div id='selectProfile'>" 
				 +" \n 		<form method='POST' Action='"+this.getServletContext().getContextPath() +"/Analysis'>" 
				 +" \n 			<select name='profile' 	style='background-color:222222;color:FFFFFF;font-size:16px;font-family:Open Sans, sans-serif;border:0;border-radius:0;-webkit-appearence:none'>" 
				 +" \n 				<option value='Windows8'> Windows 8 </option>" 
				 +" \n 				<option value='Vodafone'> Vodaphone </option>" 
				 +" \n 				<option value='Playstation4'> Playstation 4 </option>" 
				 +" \n 				<option value='Microsoft'> Microsoft </option>"
				 +" \n 				<option value='JorgeBergoglio'> New Pope </option>"
				 +" \n 				<option value='NegationTest'> Negation Test </option>" 
				 +" \n 				<option value='GrammysBestRapAlbum'> Grammys - Best Rap Album </option>" 
				 +" \n 				<option value='GrammysBestNewArtist'> Grammys - Best New Artist </option>" 
				 +" \n 			</select>" 
				 +" \n 			<br>" 
				 +" \n 			<label>" 
				 +" \n 				<div id='testData' class='dataButton' style='width:350px'>			" 
				 +" \n 					<input type='radio' name='dataToUse' checked='checked' value='TwitterData'>Use Twitter Data<br>" 
				 +" \n 				</div>" 
				 +" \n 			</label>" 
				 +" \n 			<label>" 
				 +" \n 				<div id='twitterdata' class='dataButton' style='width:350px'>" 
				 +" \n 					<input type='radio' name='dataToUse' value='TestData'>Show Test Data<br>			" 
				 +" \n 				</div>" 
				 +" \n 			<label>" 
				 +" \n 			<div>" 
				 +" \n 			Note: Google Chrome must be used for correct visual results" 
				 +" \n 			</div>" 

				 +" \n 			<input type='submit' value='Submit' >" 
				 +" \n 		</form>" 
				 +" \n 		</div>" 
				 +" \n 	</div>";
		
		return htmlString;
		
	}

}
