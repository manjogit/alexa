package dekaskill;
 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Image;
import com.amazon.speech.ui.LinkAccountCard;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazon.speech.ui.StandardCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
 
public class DekaAlexaSpeechlet implements Speechlet{
			private static final String IMAGE_SRC = "https://s3-eu-west-1.amazonaws.com/dekabucket/";
            private static final String INTENT_RUBRIK = "chooseRubrik";
            private static final String INTENT_SHOW = "showRubrik";
            private static final String UNHANDLED = "unhandledIntent";
            private static final Logger log = Logger.getLogger(DekaAlexaSpeechlet.class);
            private static final String[] WELLCOMES = {
            											"Willkommen bei der Deka Anleger Welt."
            											,"Willkommen. Welche Rubrik möchten Sie hören?"
            										  };
            
            private static final String CHECK_USER = "checkUser"; 

            //moeglicherweit simpledateformat auslagern, da diese klasse am vortag erstellt wurde und intent aufruf am naechsten tag
            private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            private String themen = "Katers Welt, nachhaltige Investments und Zertifikate-Kolumne";
            private static AmazonDynamoDB dbClient;
            private BegriffIntents begriffIntents;
             
 
            public SpeechletResponse onIntent(IntentRequest arg0, Session arg1) throws SpeechletException {
                        // TODO Auto-generated method stub
                Intent intent = arg0.getIntent();
                String intentName = (intent != null) ? arg0.getIntent().getName() : null;
               
                log.info("INTENT: "+ intentName +" onIntent requestId="+arg0.getRequestId()+", sessionId="+arg1.getSessionId());
                
                if(INTENT_RUBRIK.equals(intentName)) {
                        return begriffIntents.handleBegriffIntent(intent,arg1);
                } else if(INTENT_SHOW.equals(intentName)) {
                        return handleShowRubrik();
                } else if ("AMAZON.HelpIntent".equals(intentName)) {
                        return handleHelpIntent();
                } else if ("AMAZON.StopIntent".equals(intentName)) {
                        return handleStopIntent();
                } else if ("AMAZON.CancelIntent".equals(intentName)){
                		return handleStopIntent();
                } else if ("AMAZON.NoIntent".equals(intentName)){
                		return handleStopIntent();
                } else if (UNHANDLED.equals(intentName)){
            			return unhandledIntent();
                } else {
                        return unhandledIntent();
                }
            }
 
            public SpeechletResponse onLaunch(LaunchRequest arg0, Session arg1) throws SpeechletException {
                        // TODO Auto-generated method stub
                log.info("onLaunch requestId="+arg0.getRequestId()+", sessionId="+arg1.getSessionId());
                int random = (int) (Math.random()*(WELLCOMES.length-1));
                SsmlOutputSpeech speech = new SsmlOutputSpeech();
                //pruefen ob neuer user
                if(arg1.getAttribute(CHECK_USER).equals("0")){
                
                	speech.setSsml("<speak><audio src=\"https://s3-eu-west-1.amazonaws.com/dekabucket/deka_alexa.mp3\" />"
                					+ "Willkommen bei der Deka Anleger Welt. Folgende Rubriken sind verfügbar: "+themen +" Falls Sie den Artikel über Facebook "
                					+ "teilen möchten, sagen Sie einfach: poste den Artikel. Welche Rubrik möchten Sie hören?</speak>");
                	
                    StandardCard card = new StandardCard();
                    Image image = new Image();
                    card.setText("Willkommen!");
                    card.setTitle("Deka");
                    image.setSmallImageUrl(IMAGE_SRC+"deka_720x480.png");
                    image.setLargeImageUrl(IMAGE_SRC+"deka_1200x800.png");
                    card.setImage(image);
                    return SpeechletResponse.newAskResponse(speech, createRepromptSpeech(),card);
                    
                }else{
                
                	speech.setSsml("<speak><audio src=\"https://s3-eu-west-1.amazonaws.com/dekabucket/deka_alexa.mp3\" />"+WELLCOMES[random]+"</speak>");
                    return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
                }
            }
 
            public void onSessionEnded(SessionEndedRequest arg0, Session arg1) throws SpeechletException {
                        // TODO Auto-generated method stub
                        log.info("onSessionEnded requestId="+arg0.getRequestId()+", sessionId="+arg1.getSessionId());
                        //
            }
 
            public void onSessionStarted(SessionStartedRequest arg0, Session arg1) throws SpeechletException {
                        // TODO Auto-generated method stub
                log.info("onSessionStarted requestId="+arg0.getRequestId()+", sessionId="+arg1.getSessionId());
                String userId = arg1.getUser().getUserId();
                String date = format.format(new Date());
                User user = new User();
                user.setUserId(userId);
                user.setDate(date);
                
                //clients anlegen, hashmpap befuellung usw.
                initialize();
                  
                
                //reicht eine mapper instanz?
                DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
                User userGet = mapper.load(user);
                 
                //pruefung ob user vorhanden
                if(userGet == null){
	                arg1.setAttribute(CHECK_USER, "0");
	                mapper.save(user);
	                log.info("User: "+user.getUserId()+" nicht vorhanden!");
                }else{
	                arg1.setAttribute(CHECK_USER, "1");
	                mapper.save(user);
	                log.info("User: "+user.getUserId()+" schon vorhanden!");
                }
            }
           
           
            private Reprompt createRepromptSpeech() {
                PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
                repromptSpeech.setText("Welche Rubrik möchten Sie wählen? oder Fragen Sie Alexa um Hilfe.");
                Reprompt reprompt = new Reprompt();
                reprompt.setOutputSpeech(repromptSpeech);
                return reprompt;
                          }
             
           
           
            private SpeechletResponse handleShowRubrik(){
                        SsmlOutputSpeech speech = new SsmlOutputSpeech();
                        speech.setSsml("<speak>Folgende Rubriken sind verfügbar: Katers Welt, nachhaltige Investments und Zertifikate-Kolumne. Bitte wählen Sie eine Rubrik aus.</speak>");
                        return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
                       
            }
           
            
            
            private SpeechletResponse unhandledIntent(){
            	PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            	speech.setText("Frag Alexa um Hilfe.");
            	return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
            }
           
            private SpeechletResponse handleStopIntent() {
                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText("Auf wiedersehen");
                return SpeechletResponse.newTellResponse(speech);
                          }
                       
                          
            private SpeechletResponse handleHelpIntent() {
                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText("Fragen Sie Alexa welche Rubriken es gibt, um demnach ein auszuwählen.");
                return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
                          }
           
            

            
            private void initialize(){
            	if(dbClient==null){
            		log.info("dynamodb client wird erstellt");
                    dbClient = AmazonDynamoDBClientBuilder.defaultClient();
            	}
            	
            	if(begriffIntents==null){
            		log.info("BegriffIntents Objekt wird angelegt!");
            		begriffIntents = new BegriffIntents(dbClient);
            	}
            }
}
