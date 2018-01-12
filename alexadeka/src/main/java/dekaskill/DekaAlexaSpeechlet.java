package dekaskill;
 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
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
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazon.speech.ui.StandardCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

 
public class DekaAlexaSpeechlet implements Speechlet{
			private static final String IMAGE_SRC = "https://s3-eu-west-1.amazonaws.com/dekabucket/";
            private static final String INTENT_RUBRIK = "chooseRubrik";
            private static final String INTENT_SHOW = "showRubrik";
            private static final String UNHANDLED = "unhandledIntent";
            private static final Logger log = Logger.getLogger(DekaAlexaSpeechlet.class);
            private static final String CHECK_USER = "checkUser"; 
            //moeglicherweit simpledateformat auslagern, da diese klasse am vortag erstellt wurde und intent aufruf am naechsten tag
            private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            private static AmazonDynamoDB dbClient;
    		private static AmazonS3 s3Client;
            private BegriffIntents begriffIntents;
    		private Properties props;
    		
            
          
 
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
                String[] wellcomes = props.get("begruessung").toString().split(";");
                
                int random = (int) (Math.random()*(wellcomes.length-1));
                SsmlOutputSpeech speech = new SsmlOutputSpeech();
                //pruefen ob neuer user
                if(arg1.getAttribute(CHECK_USER).equals("0")){
                
                	speech.setSsml(props.getProperty("firstwellcome"));
                	
                    StandardCard card = new StandardCard();
                    Image image = new Image();
                    card.setText("Willkommen!");
                    card.setTitle("Deka");
                    image.setSmallImageUrl(IMAGE_SRC+props.getProperty("dekaimageklein"));
                    image.setLargeImageUrl(IMAGE_SRC+props.getProperty("dekaimagegross"));
                    card.setImage(image);
                    return SpeechletResponse.newAskResponse(speech, createRepromptSpeech(),card);
                    
                }else{
                
                	speech.setSsml("<speak><audio src=\"https://s3-eu-west-1.amazonaws.com/dekabucket/deka_alexa.mp3\" />"+wellcomes[random]+"</speak>");
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
                  
                //properties laden
                try {
					props = getPropertiesFromS3("dekabucket", "alexa.properties");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.info("Properties konnten nicht geladen werden.");
				}
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
                repromptSpeech.setText(props.getProperty("reprompt"));
                Reprompt reprompt = new Reprompt();
                reprompt.setOutputSpeech(repromptSpeech);
                return reprompt;
                          }
             
           
           
            private SpeechletResponse handleShowRubrik(){
            	PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText(props.getProperty("verfuegbar"));
                return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
                       
            }
           
            
            
            private SpeechletResponse unhandledIntent(){
            	PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            	speech.setText(props.getProperty("hilfe"));
            	return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
            }
           
            private SpeechletResponse handleStopIntent() {
                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText(props.getProperty("abschied"));
                return SpeechletResponse.newTellResponse(speech);
                          }
                       
                          
            private SpeechletResponse handleHelpIntent() {
                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText(props.getProperty("hilfe"));
                return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
                          }
           
            

            
            private void initialize(){
            	if(dbClient==null){
            		log.info("dynamodb client wird erstellt");
                    dbClient = AmazonDynamoDBClientBuilder.defaultClient();
            	}
            	if(s3Client==null){
            		log.info("s3 Client wird erstellt");
            		s3Client =  AmazonS3ClientBuilder.defaultClient();
            	}
            	if(begriffIntents==null){
            		log.info("BegriffIntents Objekt wird angelegt!");
            		begriffIntents = new BegriffIntents(dbClient);
            	}
            }
            
            
            private Properties getPropertiesFromS3(String bucket, String file) throws IOException{
            	Properties properties = new Properties();
            	S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucket, file));
            	InputStream iStream = s3Object.getObjectContent();
            	properties.load(new InputStreamReader(iStream,StandardCharsets.UTF_8));
            	return properties;
            }
}
