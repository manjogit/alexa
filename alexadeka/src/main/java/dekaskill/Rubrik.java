package dekaskill;

import java.io.Serializable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Rubrik implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7583860656188883440L;
	private String title, topic, text, type, imageURL, link;
	
	public Rubrik(String html, String type, String link){
		String headerId="";
		String textId="";
		Document doc = Jsoup.parse(html);
		
		// je rubrik unterschiedliche anker-punkte
		if(type.matches("^kate.*")){
			headerId = "anker5645602";
			textId= "anker5549418";
		}else if(type.matches("^zert.*")){
			headerId = "anker5134422";
			textId= "anker6152781";
		}else if(type.matches("^nach.*")){
			headerId = "anker5969452";
			textId= "anker5969447";
		}
		
		Element divHeader = doc.getElementById(headerId);
		Element divText = doc.getElementById(textId);
		Elements overline = divHeader.getElementsByClass("overline");
		Elements headline = divHeader.getElementsByClass("headline");
		Elements pTag = divText.getElementsByTag("p");
	
		this.title = overline.get(0).text();
		this.topic = headline.get(0).text();
		
		//der Text soll für die Tests nicht zu lang sein
		String[] texts = pTag.get(0).text().split("\\.");
		this.text = (texts.length > 3) ? texts[0]+"."+texts[1]+"."+texts[2]+"." : texts[0]+"."+texts[1]+"."; //pTag.get(0).text();
		this.type = type;
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
