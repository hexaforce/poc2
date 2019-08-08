package com.example.demo.dynamodb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ConditionCheck;
import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.Put;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.ReturnValuesOnConditionCheckFailure;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItem;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest;
import com.amazonaws.services.dynamodbv2.model.TransactionCanceledException;
import com.amazonaws.services.dynamodbv2.model.Update;
import com.google.gson.Gson;

public class ContactHistoryStoreRepository extends ContactHistoryStoreSupport {

	public void PutItem(String[] args) {

		String table_name = args[0];
		String name = args[1];
		ArrayList<String[]> extra_fields = new ArrayList<String[]>();

		// any additional args (fields to add to database)?
		for (int x = 2; x < args.length; x++) {
			String[] fields = args[x].split("=", 2);
			if (fields.length == 2) {
				extra_fields.add(fields);
			} else {
				System.out.format("Invalid argument: %s\n", args[x]);
				System.exit(1);
			}
		}

		System.out.format("Adding \"%s\" to \"%s\"", name, table_name);
		if (extra_fields.size() > 0) {
			System.out.println("Additional fields:");
			for (String[] field : extra_fields) {
				System.out.format("  %s: %s\n", field[0], field[1]);
			}
		}

		HashMap<String, AttributeValue> item_values = new HashMap<String, AttributeValue>();
		item_values.put(TableKey.CONTACTID.alias(), S(name));

		for (String[] field : extra_fields) {
			item_values.put(field[0], new AttributeValue(field[1]));
		}

		final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

		try {
			ddb.putItem(table_name, item_values);
		} catch (ResourceNotFoundException e) {
			System.err.format("Error: The table \"%s\" can't be found.\n", table_name);
			System.err.println("Be sure that it exists and that you've typed its name correctly!");
			System.exit(1);
		} catch (AmazonServiceException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("Done!");
	}

	public void UpdateItem(String[] args) {

		String table_name = args[0];
		String name = args[1];
		ArrayList<String[]> extra_fields = new ArrayList<String[]>();

		// any additional args (fields to add or update)?
		for (int x = 2; x < args.length; x++) {
			String[] fields = args[x].split("=", 2);
			if (fields.length == 2) {
				extra_fields.add(fields);
			}
		}

		System.out.format("Updating \"%s\" in %s\n", name, table_name);
		if (extra_fields.size() > 0) {
			System.out.println("Additional fields:");
			for (String[] field : extra_fields) {
				System.out.format("  %s: %s\n", field[0], field[1]);
			}
		}

		HashMap<String, AttributeValue> item_key = new HashMap<String, AttributeValue>();
		item_key.put("Name", new AttributeValue(name));

		HashMap<String, AttributeValueUpdate> updated_values = new HashMap<String, AttributeValueUpdate>();

		for (String[] field : extra_fields) {
			updated_values.put(field[0], new AttributeValueUpdate(new AttributeValue(field[1]), AttributeAction.PUT));
		}

		final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

		try {
			ddb.updateItem(table_name, item_key, updated_values);
		} catch (ResourceNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (AmazonServiceException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("Done!");
	}

	public void Query(String[] args) {

		String table_name = args[0];
		String partition_key_val = args[2];

		final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

		// set up an alias for the partition key name in case it's a reserved word
		HashMap<String, String> a = new HashMap<String, String>();
		a.put(TableKey.CONTACTID.alias(), TableKey.CONTACTID.getField());

		// set up mapping of the partition name with the value
		HashMap<String, AttributeValue> v = new HashMap<String, AttributeValue>();
		v.put(TableKey.CONTACTID.value(), S(partition_key_val));
		
		String condition = String.format("%s = %s",TableKey.CONTACTID.alias(),TableKey.CONTACTID.value());
		
		QueryRequest queryRequest = new QueryRequest()
				.withTableName(table_name)
				.withKeyConditionExpression(condition)
				.withExpressionAttributeNames(a)
				.withExpressionAttributeValues(v);
		try {
			QueryResult response = ddb.query(queryRequest);
			System.out.println(response.getCount());
		} catch (AmazonDynamoDBException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
		System.out.println("Done!");
	}

	public void main(String[] args) {
		Gson gson = new Gson();
		System.out.println(gson.toJson("Amazon DynamoDB Transaction Sample!"));

		// Using local profile(default)
		// AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();

		// Using custom profile
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				.withRegion(Regions.US_EAST_1)
				.withCredentials(new ProfileCredentialsProvider("profile_test")).build();
		DynamoDB dynamoDB = new DynamoDB(client);

		// Check your tables in DDB
		TableCollection<ListTablesResult> tables = dynamoDB.listTables();
		Iterator<Table> iterator = tables.iterator();

		while (iterator.hasNext()) {
			Table table = iterator.next();
			System.out.println(table.getTableName());
		}

		// Create condition for "Customers" table
		final String CUSTOMER_TABLE_NAME = "Customers";
		final String CUSTOMER_PARTITION_KEY = "CustomerId";
		final String customerId = "09e8e9c8-ec48"; // Sample ID that is must created before running
		final HashMap<String, AttributeValue> customerItemKey = new HashMap<String, AttributeValue>();
		customerItemKey.put(CUSTOMER_PARTITION_KEY, new AttributeValue(customerId));

		ConditionCheck checkItem = new ConditionCheck()
				.withTableName(CUSTOMER_TABLE_NAME)
				.withKey(customerItemKey)
				.withConditionExpression("attribute_exists(" + CUSTOMER_PARTITION_KEY + ")");

		System.out.println(gson.toJson(checkItem));

		// Create condition for "ProductCatalog" table
		final String PRODUCT_TABLE_NAME = "ProductCatalog";
		final String PRODUCT_PARTITION_KEY = "ProductId";
		final String productKey = "aaa-001"; // Product ID that is must inserted in the table before running
		HashMap<String, AttributeValue> productItemKey = new HashMap<String, AttributeValue>();
		productItemKey.put(PRODUCT_PARTITION_KEY, new AttributeValue(productKey));

		Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
		expressionAttributeValues.put(":new_status", new AttributeValue("SOLD"));
		expressionAttributeValues.put(":expected_status", new AttributeValue("IN_STOCK"));

		Update markItemSold = new Update()
				.withTableName(PRODUCT_TABLE_NAME)
				.withKey(productItemKey)
				.withUpdateExpression("SET ProductStatus = :new_status") // Status ID that is must inserted in the table before running, and have to set "IN_STOCK"
				.withExpressionAttributeValues(expressionAttributeValues)
				.withConditionExpression("ProductStatus = :expected_status")
				.withReturnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD);

		System.out.println(gson.toJson(markItemSold));

		// Create condition for "Orders" table
		final String ORDER_PARTITION_KEY = "OrderId";
		final String ORDER_TABLE_NAME = "Orders";
		final String orderId = "ord-001";
		HashMap<String, AttributeValue> orderItem = new HashMap<String, AttributeValue>();
		orderItem.put(ORDER_PARTITION_KEY, new AttributeValue(orderId));
		orderItem.put(PRODUCT_PARTITION_KEY, new AttributeValue(productKey));
		orderItem.put(CUSTOMER_PARTITION_KEY, new AttributeValue(customerId));
		orderItem.put("OrderStatus", new AttributeValue("CONFIRMED"));
		orderItem.put("OrderTotal", new AttributeValue("100"));

		Put createOrder = new Put()
				.withTableName(ORDER_TABLE_NAME)
				.withItem(orderItem)
				.withReturnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD)
				.withConditionExpression("attribute_not_exists(" + ORDER_PARTITION_KEY + ")");

		System.out.println(gson.toJson(createOrder));

		// Create a transaction with conditions
		Collection<TransactWriteItem> actions = Arrays.asList(
				new TransactWriteItem().withConditionCheck(checkItem), 
				new TransactWriteItem().withPut(createOrder), 
				new TransactWriteItem().withUpdate(markItemSold));

		TransactWriteItemsRequest placeOrderTransaction = new TransactWriteItemsRequest()
				.withTransactItems(actions)
				.withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

		// Execute the transaction and process the result.
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
