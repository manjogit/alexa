package dekaskill;

import java.io.Serializable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NachInvestKol implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 650766500835782358L;
	private String title, topic, text, type, imageURL, link;
	private static String headerId = "anker5969452";
	private static String textId = "anker5969437";
	
	public NachInvestKol(String html, String type, String link){
		Document doc = Jsoup.parse(html);
		Element divHeader = doc.getElementById(headerId);
		Element divText = doc.getElementById(textId); // anker5549418
		Elements overline = divHeader.getElementsByClass("overline");
		Elements headline = divHeader.getElementsByClass("headline");
		Elements pTag = divText.getElementsByTag("p");
	
		this.title = overline.get(0).text();
		this.topic = headline.get(0).text();
		this.text = pTag.get(0).text();
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
