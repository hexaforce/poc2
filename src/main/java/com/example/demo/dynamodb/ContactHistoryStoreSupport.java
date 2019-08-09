package com.example.demo.dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;


public class ContactHistoryStoreSupport {

	public enum Key {

		CONTACTID("ContactId"), 
		CUSTOMERADDRESS("CustomerAddress"), 
		SYSTEMADDRESS("SystemAddress"), 
		STARTFRAGMENTNUMBER("StartFragmentNumber"), 
		STARTTIMESTAMP("StartTimestamp"), 
		STREAMARN("StreamARN"), 
		LAMBDAFUNCTIONNAME("LambdaFunctionName");
		
		private String field;

		Key(String field) {
			this.field = field;
		}

		String field() {
			return field;
		}

		String alias() {
			return "#" + field;
		}

		String value() {
			return ":" + field;
		}

	}

	protected Map<String, AttributeValue> primaryKey(String systemAddress, String contactId) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put(Key.SYSTEMADDRESS.field(), S(systemAddress));
		item.put(Key.CONTACTID.field(), S(contactId));
		return item;
	}

	protected Map<String, String> aliasMap() {
		Map<String, String> item = new HashMap<String, String>();
		item.put(Key.SYSTEMADDRESS.alias(), Key.SYSTEMADDRESS.field());
		return item;
	}

	protected Map<String, AttributeValue> valueMap(String systemAddress) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put(Key.SYSTEMADDRESS.value(), S(systemAddress));
		return item;
	}

	protected String condition() {
		return String.format("%s = %s", Key.SYSTEMADDRESS.alias(), Key.SYSTEMADDRESS.value());
	}

	protected Map<String, AttributeValue> marshal(ContactHistoryStore contactHistoryStore) {

		return null;
	}

	protected ContactHistoryStore unmarshal(Map<String, AttributeValue> itme) {
		return null;
	}

	protected List<Map<String, AttributeValue>> marshals(List<ContactHistoryStore> contactHistoryStores) {
		return contactHistoryStores.stream().map(x -> marshal(x)).collect(Collectors.toList());
	}

	protected List<ContactHistoryStore> unmarshals(List<Map<String, AttributeValue>> itmes) {
		return itmes.stream().map(x -> unmarshal(x)).collect(Collectors.toList());
	}

	protected AttributeValue S(String s) {
		if (StringUtils.isEmpty(s))
			return new AttributeValue().withS(s);
		return new AttributeValue().withS("");
	}

	protected AttributeValue N(int n) {
		return new AttributeValue().withN(String.valueOf(n));
	}

	protected AttributeValue N(long n) {
		return new AttributeValue().withN(String.valueOf(n));
	}

	protected AttributeValue BOOL(boolean bool) {
		return new AttributeValue().withBOOL(bool);
	}

	protected AttributeValueUpdate UpS(String s, AttributeAction action) {
		return new AttributeValueUpdate(S(s), action);
	}

	protected AttributeValueUpdate UpN(int n, AttributeAction action) {
		return new AttributeValueUpdate(N(n), action);
	}

	protected AttributeValueUpdate UpN(long n, AttributeAction action) {
		return new AttributeValueUpdate(N(n), action);
	}

	protected AttributeValueUpdate UpBOOL(boolean bool, AttributeAction action) {
		return new AttributeValueUpdate(BOOL(bool), action);
	}

}
