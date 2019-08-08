package com.example.demo.dynamodb;

import java.util.HashMap;
import java.util.List;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

public class ContactHistoryStoreRepository {

	public void aaa(String table_name, String name, List<String[]> extra_fields) {

		HashMap<String, AttributeValue> values = new HashMap<String, AttributeValue>();

		values.put("Name", AttributeValue.builder().s(name).build());

		for (String[] field : extra_fields) {
			values.put(field[0], AttributeValue.builder().s(field[1]).build());
		}

		DynamoDbClient ddb = DynamoDbClient.create();
		PutItemRequest request = PutItemRequest.builder().tableName(table_name).item(values).build();

		try {
			ddb.putItem(request);
		} catch (ResourceNotFoundException e) {
			System.err.format("Error: The table \"%s\" can't be found.\n", table_name);
			System.err.println("Be sure that it exists and that you've typed its name correctly!");
			System.exit(1);
		} catch (DynamoDbException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

	}

}
