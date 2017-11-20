package dekaskill;


import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Image;
import com.amazon.speech.ui.LinkAccountCard;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazon.speech.ui.StandardCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.gson.Gson;

public class BegriffIntents {
    private AmazonDynamoDB dbClient;
    private static final String INTENT_BEGRIFF_SLOT = "rubrik";
    private static final String IMAGE_SRC = "https://s3-eu-west-1.amazonaws.com/dekabucket/";
	
	
	public BegriffIntents(final AmazonDynamoDB dbClient){
		this.dbClient=dbClient;
	}
	
	
    public SpeechletResponse handleBegriffIntent(Intent intent, Session session){
    	SsmlOutputSpeech speech = new SsmlOutputSpeech();
    	DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
        StandardCard card = new StandardCard();
        Image image = new Image();
    	
    	if(intent.getSlot(INTENT_BEGRIFF_SLOT).getValue()==null){
    		speech.setSsml("<speak>Sie haben nach keinem Begriff gefragt. Bitte wiederholen Sie die Frage.</speak>");
    		
    	}else{
    		String intentSlot = intent.getSlot(INTENT_BEGRIFF_SLOT).getValue().toLowerCase();
    		intentSlot = (intentSlot.contains("stocker")) ? "stocker" : intentSlot;
    		Begriff begriff = mapper.load(Begriff.class, intentSlot);
    			if (begriff != null){
                    image.setSmallImageUrl(IMAGE_SRC+begriff.getImageURLSmall());
                    image.setLargeImageUrl(IMAGE_SRC+begriff.getImageURL());
                    card.setTitle(begriff.getTitle());
                    card.setText(begriff.getTopic());
                    card.setImage(image);
          			speech.setSsml("<speak>"+begriff.getText()+"</speak>");
          		}else{	
          			speech.setSsml("<speak>Zu "+intentSlot+" habe ich leider keine Informationen.</speak>");
          		}
    		}
      		
      		return SpeechletResponse.newTellResponse(speech,card);
      	}
}
