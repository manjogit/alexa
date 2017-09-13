package dekaskill;


import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class VeranstaltungIntents {
    private AmazonDynamoDB dbClient;
    private static final String INTENT_BEGRIFF_SLOT = "begriff";
	
	
	public VeranstaltungIntents(final AmazonDynamoDB dbClient){
		this.dbClient=dbClient;
	}
	
	
    public SpeechletResponse handleVeranstaltung(Intent intent, Session session){
    	SsmlOutputSpeech speech = new SsmlOutputSpeech();
    	DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
    	
    	if(intent.getSlot(INTENT_BEGRIFF_SLOT).getValue()==null){
    		speech.setSsml("<speak>Sie haben nach keinem Begriff gefragt. Bitte wiederholen Sie die Frage.</speak>");
    		
    	}else{
    		String intentSlot = intent.getSlot(INTENT_BEGRIFF_SLOT).getValue().toLowerCase();
    		intentSlot = (intentSlot.contains("stocker")) ? "stocker" : intentSlot;
    		Begriff begriff = mapper.load(Begriff.class, intentSlot);
    			if (begriff != null){
          			speech.setSsml("<speak>"+begriff.getText()+"</speak>");
          		}else{	
          			speech.setSsml("<speak>Zu "+intentSlot+" habe ich leider keine Informationen.</speak>");
          		}
    		}
      		
      		return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
      	}
    
    public SpeechletResponse handleVeranstaltungsFrage(){
    	SsmlOutputSpeech speech = new SsmlOutputSpeech();
    	DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
    	Begriff begriff = mapper.load(Begriff.class, "was");
    	
    	speech.setSsml("<speak>"+begriff.getText()+"</speak>");
    	return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
    }
    
    public SpeechletResponse handleWitz(){
    	SsmlOutputSpeech speech = new SsmlOutputSpeech();
    	DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
    	Begriff begriff = mapper.load(Begriff.class, "witz");
    	
    	speech.setSsml("<speak>"+begriff.getText()+"</speak>");
    	return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
    }
    
    
    private Reprompt createRepromptSpeech() {
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Frag Alexa um Hilfe, falls du nicht weiter weisst.");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);
        return reprompt;
                  }
	
}
