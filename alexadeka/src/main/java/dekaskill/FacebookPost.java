package dekaskill;

import java.net.MalformedURLException;
import java.net.URL;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.PostUpdate;
import facebook4j.auth.AccessToken;

public class FacebookPost {
    //private String accessToken;
    private String link;
    private String picture;
    private String name;
    private String caption;
    private String description;
    private Facebook facebook;
    
    
    public FacebookPost(String accessToken, Rubrik rubrik){
    	facebook = new FacebookFactory().getInstance();
        facebook.setOAuthAppId("", "");
        facebook.setOAuthAccessToken(new AccessToken(accessToken));
    	//this.accessToken = accessToken;
    	this.link = rubrik.getLink();
    	this.picture = rubrik.getImageURL();
        this.name = rubrik.getTitle();
        this.description = rubrik.getTopic();
        this.caption = rubrik.getType();    	
    }
    
    public String postBeitrag(){
        PostUpdate post;
        try {
              post = new PostUpdate(new URL(link))
            		  				.picture(new URL(picture))
                                    .name(name)
                                    .caption(caption)
                                    .description(description);
              
                    facebook.postFeed(post);
        } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    return "Foto URL konnte nicht geladen werden.";
        } catch (FacebookException e) {
                    // TODO Auto-generated catch block
                   return "Facebook API hat nicht funktionert.";
        }
    	
    	return "Der Artikel wurde soeben auf ihrer Facebook Seite veroeffentlicht.";
    }
}
