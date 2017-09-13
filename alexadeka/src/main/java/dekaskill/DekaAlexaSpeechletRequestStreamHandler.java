package dekaskill;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;


public class DekaAlexaSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler{
		
    private static final Set<String> supportedApplicationIds;
		
    static {
	    	 
        supportedApplicationIds = new HashSet<String>();
        supportedApplicationIds.add("amzn1.ask.skill.46550740-88e8-4ebf-a8c4-7b047094587d");
	        
    	}


    public DekaAlexaSpeechletRequestStreamHandler(){
        super(new DekaAlexaSpeechlet(), supportedApplicationIds);
    }

    public DekaAlexaSpeechletRequestStreamHandler(Speechlet speechlet, Set<String> supportedApplicationIds) {
		super(speechlet, supportedApplicationIds);
	}

}
