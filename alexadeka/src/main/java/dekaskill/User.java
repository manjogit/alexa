package dekaskill;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Users")
public class User {
	
	private String userId;
	private String date;
	
    @DynamoDBHashKey(attributeName="UserId")  
    public String getUserId() { return userId;}
    public void setUserId(String userId) {this.userId = userId;}
    
    @DynamoDBAttribute(attributeName="date")  
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
