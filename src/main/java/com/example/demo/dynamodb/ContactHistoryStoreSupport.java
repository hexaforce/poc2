package com.example.demo.dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;


public class ContactHistoryStoreSupport extends BaseSupport {

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
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put(Key.CONTACTID.field(), S(contactHistoryStore.getContactId()));
		item.put(Key.CUSTOMERADDRESS.field(), S(contactHistoryStore.getCustomerAddress()));
		item.put(Key.SYSTEMADDRESS.field(), S(contactHistoryStore.getSystemAddress()));
		item.put(Key.STARTFRAGMENTNUMBER.field(), S(contactHistoryStore.getStartFragmentNumber()));
		item.put(Key.STARTTIMESTAMP.field(), N(contactHistoryStore.getStartTimestamp()));
		item.put(Key.STREAMARN.field(), S(contactHistoryStore.getStreamARN()));
		item.put(Key.LAMBDAFUNCTIONNAME.field(), S(contactHistoryStore.getLambdaFunctionName()));
		return item;
	}
	
	protected ContactHistoryStore unmarshal(Map<String, AttributeValue> item) {
		return ContactHistoryStore.builder()
				.ContactId(item.get(Key.CONTACTID.field()).getS())
				.CustomerAddress(item.get(Key.CUSTOMERADDRESS.field()).getS())
				.SystemAddress(item.get(Key.SYSTEMADDRESS.field()).getS())
				.StartTimestamp(Long.parseLong(item.get(Key.STARTTIMESTAMP.field()).getN()))
				.StreamARN(item.get(Key.STREAMARN.field()).getS())
				.LambdaFunctionName(item.get(Key.LAMBDAFUNCTIONNAME.field()).getS())
				.build();
	}

	protected List<Map<String, AttributeValue>> marshals(List<ContactHistoryStore> contactHistoryStores) {
		return contactHistoryStores.stream().map(x -> marshal(x)).collect(Collectors.toList());
	}

	protected List<ContactHistoryStore> unmarshals(List<Map<String, AttributeValue>> items) {
		return items.stream().map(x -> unmarshal(x)).collect(Collectors.toList());
	}

}
