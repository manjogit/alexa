package dekaskill;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Begriffe")
public class Begriff {
	private String begriff,text,topic,title,imageURL,imageURLSmall,url;
	
	public Begriff(){}
	
	public Begriff(String begriff, String text, String title, String topic, String imageURL, String imageURLSmall, String url){
		this.begriff=begriff;
		this.text=text;
		this.title=title;
		this.topic=topic;
		this.imageURL=imageURL;
		this.imageURLSmall=imageURLSmall;
		this.imageURL=url;
	}
	
    @DynamoDBHashKey(attributeName="begriff")
	public String getBegriff() {return begriff;}
	public void setBegriff(String begriff) {this.begriff = begriff;}

	@DynamoDBAttribute(attributeName="text")  
	public String getText() {return text;}
	public void setText(String text) {this.text = text;}
	
	@DynamoDBAttribute(attributeName="topic")  
	public String getTopic() {return topic;}
	public void setTopic(String topic) {this.topic = topic;}
	
	@DynamoDBAttribute(attributeName="title")  
	public String getTitle() {return title;}
	public void setTitle(String title) {this.title = title;}
	
	@DynamoDBAttribute(attributeName="imageURL")  
	public String getImageURL() {return imageURL;}
	public void setImageURL(String imageURL) {this.imageURL = imageURL;}
	
	@DynamoDBAttribute(attributeName="imageURLSmall")  
	public String getImageURLSmall() {return imageURLSmall;}
	public void setImageURLSmall(String imageURLSmall) {this.imageURLSmall = imageURLSmall;}
	
	@DynamoDBAttribute(attributeName="url")  
	public String getUrl() {return url;}
	public void setUrl(String url) {this.url = url;}
	
}
