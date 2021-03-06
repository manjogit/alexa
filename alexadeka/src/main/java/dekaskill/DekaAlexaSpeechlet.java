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
            private static final String SESSION_RUBRIK = "rubrik";
            private static final String INTENT_SLOT = "rubrik";
            private static final String INTENT_RUBRIK = "chooseRubrik";
            private static final String INTENT_SHOW = "showRubrik";
            private static final String INTENT_REPEAT = "repeatRubrik";
            private static final String INTENT_POST_FACE = "postFacebook";
            private static final String INTENT_PAUSE = "handlePause";
            private static final String INTENT_VERANST = "handleVeranstaltung";
            private static final String INTENT_VERANST_FRAGE = "handleVeranstaltungsFrage";
            private static final String INTENT_VERANST_WITZ = "handleWitz";
            private static final Logger log = Logger.getLogger(DekaAlexaSpeechlet.class);
            private static final String[] WELLCOMES = {
            											"Willkommen bei der Deka Anleger Welt."
            											,"Deka Anleger Welt."
            											,"Willkommen."
            											,"Willkommen, Sie k�nnen gerne fragen welche Rubriken es gibt."
            										  };
            
            private static final String CHECK_USER = "checkUser"; 
            private static final String IMAGE_SRC = "https://s3-eu-west-1.amazonaws.com/dekabucket/";

            //moeglicherweit simpledateformat auslagern, da diese klasse am vortag erstellt wurde und intent aufruf am naechsten tag
            private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            private HashMap<String, Rubrik> hMap = new HashMap<String,Rubrik>();
            private String themen;
            private static AmazonDynamoDB dbClient;
            private VeranstaltungIntents veranstaltung;
             
 
            public SpeechletResponse onIntent(IntentRequest arg0, Session arg1) throws SpeechletException {
                        // TODO Auto-generated method stub
                Intent intent = arg0.getIntent();
                String intentName = (intent != null) ? arg0.getIntent().getName() : null;
               
                log.info("INTENT: "+ intentName +" onIntent requestId="+arg0.getRequestId()+", sessionId="+arg1.getSessionId());
                
                if(INTENT_RUBRIK.equals(intentName)) {
                        return handleChooseRubrik(intent,arg1);
                } else if(INTENT_SHOW.equals(intentName)) {
                        return handleShowRubrik();
                } else if(INTENT_VERANST_FRAGE.equals(intentName)){
                		return veranstaltung.handleVeranstaltungsFrage();
                } else if(INTENT_VERANST_WITZ.equals(intentName)){
                		return veranstaltung.handleWitz();
                } else if (INTENT_VERANST.equals(intentName)){
                		return veranstaltung.handleVeranstaltung(intent,arg1);
                } else if (INTENT_REPEAT.equals(intentName)) {
                        return handleRepeatRubrik(arg1);
                } else if (INTENT_POST_FACE.equals(intentName)) {
                        return handleFacebookPost(arg1);
                } else if ("AMAZON.HelpIntent".equals(intentName)) {
                        return handleHelpIntent();
                } else if ("AMAZON.StopIntent".equals(intentName)) {
                        return handleStopIntent();
                } else if (INTENT_PAUSE.equals(intentName)){ 
                		return handleStopIntent();
                } else if ("AMAZON.CancelIntent".equals(intentName)){
                		return handleStopIntent();
                } else {
                        throw new SpeechletException("Invalid Intent");
                }
            }
 
            public SpeechletResponse onLaunch(LaunchRequest arg0, Session arg1) throws SpeechletException {
                        // TODO Auto-generated method stub
                log.info("onLaunch requestId="+arg0.getRequestId()+", sessionId="+arg1.getSessionId());
                int random = (int) (Math.random()*4);
                SsmlOutputSpeech speech = new SsmlOutputSpeech();
                //pruefen ob neuer user
                if(arg1.getAttribute(CHECK_USER).equals("0")){
                
                	speech.setSsml("<speak><audio src=\"https://s3-eu-west-1.amazonaws.com/dekabucket/deka_alexa.mp3\" />"
                					+ "Willkommen bei der Deka Anleger Welt. Sie k�nnen aus einen der folgenden Rubriken w�hlen: "+themen+" Falls Sie den Artikel ueber Facebook "
                					+ "teilen m�chten, sagen Sie einfach: poste den Artikel.</speak>");
                	
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
                repromptSpeech.setText("Frag Alexa um Hilfe, falls du nicht weiter weisst.");
                Reprompt reprompt = new Reprompt();
                reprompt.setOutputSpeech(repromptSpeech);
                return reprompt;
                          }
             
           
            private SpeechletResponse handleChooseRubrik(Intent intent, Session session){
                        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                        StandardCard card = new StandardCard();
                        Image image = new Image();
                       
                        if(intent.getSlot(INTENT_SLOT).getValue() == null){
                               speech.setText("ich habe nicht verstanden welche rubrik sie gewaehlt haben");
                        }else{
                               String rubrik = intent.getSlot(INTENT_SLOT).getValue().toString().toLowerCase();
                               if(rubrik.equals("zertifikate kolumne")){
                                   Rubrik zKol = hMap.get("zertifikate-kolumne");
                                   speech.setText(zKol.getTitle()+". "+zKol.getText());
                                   image.setSmallImageUrl(IMAGE_SRC+"Masri_720x480.jpg");
                                   image.setLargeImageUrl(IMAGE_SRC+"Masri_1200x800.jpg");
                                   zKol.setImageURL(IMAGE_SRC+"Masri_720x480.jpg");
                                   card.setTitle(zKol.getTitle());
                                   card.setText(zKol.getTopic());
                                   card.setImage(image);
                                   session.setAttribute(SESSION_RUBRIK, zKol.getType());
                                              
                               }else if(rubrik.equals("katers welt")){
                                   Rubrik kWelt = hMap.get("katers welt");
                                   speech.setText(kWelt.getTitle()+". "+kWelt.getText());
                                   image.setSmallImageUrl(IMAGE_SRC+"Kater_720x480.jpg");
                                   image.setLargeImageUrl(IMAGE_SRC+"Kater_1200x800.jpg");
                                   kWelt.setImageURL(IMAGE_SRC+"Kater_720x480.jpg");
                                   card.setTitle(kWelt.getTitle());
                                   card.setText(kWelt.getTopic());
                                   card.setImage(image);
                                   session.setAttribute(SESSION_RUBRIK, kWelt.getType());
                                              
                               }else if(rubrik.equals("nachhaltige investments")){
                                   Rubrik nKol = hMap.get("nachhaltige investments");
                                   speech.setText(nKol.getTitle()+". "+nKol.getText());
                                   image.setSmallImageUrl(IMAGE_SRC+"NachhaltigeInvestments_720x480.jpg");
                                   image.setLargeImageUrl(IMAGE_SRC+"NachhaltigeInvestments_1200x800.jpg");
                                   nKol.setImageURL(IMAGE_SRC+"NachhaltigeInvestments_720x480.jpg");
                                   card.setTitle(nKol.getTitle());
                                   card.setText(nKol.getTopic());
                                   card.setImage(image);
                                   session.setAttribute(SESSION_RUBRIK, nKol.getType());
                                             
                               }else if(!hMap.containsKey(rubrik)){
                                   speech.setText("Die Rubrik "+rubrik+" existiert nicht.");
                               }
                        }
                       return SpeechletResponse.newAskResponse(speech, createRepromptSpeech(), card);
            }
           
            private SpeechletResponse handleShowRubrik(){
                        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                        speech.setText("Folgende Rubriken sind verf�gbar: "+themen);
                        return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
                       
            }
           
            private SpeechletResponse handleRepeatRubrik(Session session){
                        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                        Rubrik rubrik = (Rubrik) hMap.get(session.getAttribute(SESSION_RUBRIK));
                        speech.setText(rubrik.getTitle()+". "+rubrik.getText());
                       
                        return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
            }
           
            private SpeechletResponse handleStopIntent() {
                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText("auf wiedersehen.");
                return SpeechletResponse.newTellResponse(speech);
                          }
                       
                          
            private SpeechletResponse handleHelpIntent() {
                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText("Fragen Sie Alexa welche Rubriken es gibt.");
                return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
                          }
           
            private SpeechletResponse handleFacebookPost(Session session){
                        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                        String accessToken = session.getUser().getAccessToken();
                        String message;
                        
                        if(session.getAttribute(SESSION_RUBRIK) == null){
                        	speech.setText("Sie haben noch kein Thema gewaehlt. Bitte waehlen Sie eins.");
                        	
                        	return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
                        }
                       
                        if(accessToken == null){
                            log.info("AccessToken noch nicht vergeben.");
                            message = "Bitte oeffnen Sie die Alexa App, um ihren Facebook Account zu verlinken.";
                            LinkAccountCard card = new LinkAccountCard();
                            speech.setText(message);
                                  
                            return SpeechletResponse.newTellResponse(speech, card);
                        }else{
                            Rubrik rubrik = (Rubrik) hMap.get(session.getAttribute(SESSION_RUBRIK));
                            FacebookPost facebookPost = new FacebookPost(accessToken,rubrik);
                            message = facebookPost.postBeitrag();
                            speech.setText(message);
                                                        
                            return SpeechletResponse.newTellResponse(speech);
                        }
            }
            
            
            private void initHashMap(){
            	log.info("initHashMap Funktion aufgerufen!");
                if(hMap.isEmpty()){
	                log.info("HashMap ist leer");
	            	HttpContent http = new HttpContent();
	                String html = http.getContent("https://deka.de/privatkunden/im-fokus");
	               
	                Document doc = Jsoup.parse(html);
	                Element divTopics1 = doc.getElementById("anker6357911");
	                Element divTopics2 = doc.getElementById("anker6357912");
	               
	               
	                Elements links1 = divTopics1.getElementsByTag("a");
	                Elements links2 = divTopics2.getElementsByTag("a");
	               
	               

	                for(int i=0; i<links1.size(); i++){
	                
	                	String link = "https://deka.de"+links1.get(i).attr("href");
	                    String html1 = http.getContent(link);
	                    String type = links1.get(i).attr("title").toLowerCase();
	                    
	                    if(type.matches("^nach.*|^zert.*|^kate.*")){
	                    	hMap.put(type, new Rubrik(html1,type,link));
	                    }
	                }
	               
	               
	                for(int i=0; i<links2.size(); i++){
	                    String link = "https://deka.de"+links2.get(i).attr("href");
	                    String html1 = http.getContent(link);
	                    String type = links2.get(i).attr("title").toLowerCase();
	                    
	                    if(type.matches("^nach.*|^zert.*|^kate.*")){
	                    	hMap.put(type, new Rubrik(html1,type,link));
	                    }
	                }
	               
	                    StringBuilder sb = new StringBuilder();
	                    Set<String> set = hMap.keySet();
	                    Iterator<String> i = set.iterator();
	                    
	                    while(i.hasNext()){
	                    sb.append(i.next() + ", ");
	                    }
	                    
	                    sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",")+1, ".");
	                    
	                    //liste der gefundenen rubriken auf
	                    themen=sb.toString();
	            }else{
                    log.info("HashMap bereits befuellt");
                }

            }
            
            private void initialize(){
            	initHashMap();
            	if(dbClient==null){
            		log.info("dynamodb client wird erstellt");
                    dbClient = AmazonDynamoDBClientBuilder.defaultClient();
            	}
            	if(veranstaltung==null){
            		log.info("veranstaltungintents objekt wird erstellt");
            		veranstaltung = new VeranstaltungIntents(dbClient);
            	}
            }
}
