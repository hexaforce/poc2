package com.example.demo.dynamodb;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.ReturnValuesOnConditionCheckFailure;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItem;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest;
import com.amazonaws.services.dynamodbv2.model.TransactionCanceledException;
import com.amazonaws.services.dynamodbv2.model.Update;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ContactHistoryStoreRepository extends ContactHistoryStoreSupport {

	private final String tableName;
	private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_1).withCredentials(new ProfileCredentialsProvider("profile_test")).build();

	public void PutItem(ContactHistoryStore contactHistoryStore) {
		client.putItem(tableName, marshal(contactHistoryStore));
	}

	public void UpdateItem(String systemAddress, String contactId, String startFragmentNumber) {
		Map<String, AttributeValue> key = primaryKey(systemAddress, contactId);
		Map<String, AttributeValueUpdate> updatedValues = new HashMap<String, AttributeValueUpdate>();
		updatedValues.put(Key.STARTFRAGMENTNUMBER.field(), UpS(startFragmentNumber, AttributeAction.PUT));
		client.updateItem(tableName, key, updatedValues);
	}

	public List<ContactHistoryStore> Query(String systemAddress) {
		Map<String, String> aliasMap = aliasMap();
		Map<String, AttributeValue> valueMap = valueMap(systemAddress);
		QueryRequest queryRequest = new QueryRequest()
				.withTableName(tableName)
				.withKeyConditionExpression(condition())
				.withExpressionAttributeNames(aliasMap)
				.withExpressionAttributeValues(valueMap);
		return unmarshals(client.query(queryRequest).getItems());
	}

	public ContactHistoryStore GetItem(String systemAddress, String contactId) {
		Map<String, AttributeValue> key = primaryKey(systemAddress, contactId);
		GetItemRequest request = new GetItemRequest()
				.withTableName(tableName)
				.withKey(key);
		return unmarshal(client.getItem(request).getItem());
	}

	public void transactWriteItems(String systemAddress, String contactId) {
		Map<String, AttributeValue> key = primaryKey(systemAddress, contactId);
		Map<String, AttributeValue> expression = new HashMap<String, AttributeValue>();
		expression.put(":new_status", new AttributeValue("SOLD"));
		expression.put(":expected_status", new AttributeValue("IN_STOCK"));
		Update update = new Update().withTableName(tableName)
				.withKey(key)
				.withConditionExpression("ProductStatus = :expected_status")
				.withUpdateExpression("SET ProductStatus = :new_status")
				.withExpressionAttributeValues(expression)
				.withReturnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD);
		Collection<TransactWriteItem> actions = Arrays.asList(
				new TransactWriteItem().withUpdate(update));
		TransactWriteItemsRequest placeOrderTransaction = new TransactWriteItemsRequest()
				.withTransactItems(actions)
				.withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
		try {
			client.transactWriteItems(placeOrderTransaction);
			System.out.println("Transaction Successful");
		} catch (ResourceNotFoundException rnf) {
			System.err.println("One of the table involved in the transaction is not found" + rnf.getMessage());
		} catch (InternalServerErrorException ise) {
			System.err.println("Internal Server Error" + ise.getMessage());
		} catch (TransactionCanceledException tce) {
			System.out.println("Transaction Canceled " + tce.getMessage());
		}
	}
}
