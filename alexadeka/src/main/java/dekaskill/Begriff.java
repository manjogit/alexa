package dekaskill;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Begriffe")
public class Begriff {
	private String begriff,text;
	
	public Begriff(){}
	
	public Begriff(String begriff, String text){
		this.begriff=begriff;
		this.text=text;
	}
	
    @DynamoDBHashKey(attributeName="begriff")
	public String getBegriff() {return begriff;}
	public void setBegriff(String begriff) {this.begriff = begriff;}

	@DynamoDBAttribute(attributeName="text")  
	public String getText() {return text;}
	public void setText(String text) {this.text = text;}
	
}
