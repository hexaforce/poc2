package com.example.demo.dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.Getter;
public class ContactHistoryStoreSupport {
	
	public enum TableKey {
		
		CONTACTID("ContactId"), 
		CUSTOMERADDRESS("CustomerAddress"), 
		SYSTEMADDRESS("SystemAddress"), 
		STARTFRAGMENTNUMBER("StartFragmentNumber"), 
		STARTTIMESTAMP("StartTimestamp"), 
		STREAMARN("StreamARN"), 
		LAMBDAFUNCTIONNAME("LambdaFunctionName");
		
		@Getter
		String field;

		TableKey(String field) {
			this.field = field;
		}
		String alias() {
			return "#" + field;
		}
		String value() {
			return ":" + field;
		}

	}

	protected HashMap<String, AttributeValue> marshal(ContactHistoryStore contactHistoryStore) {

		return null;
	}

	protected ContactHistoryStore unmarshal(HashMap<String, AttributeValue> itme) {
		return null;
	}

	protected List<HashMap<String, AttributeValue>> marshals(List<ContactHistoryStore> contactHistoryStores) {
		return contactHistoryStores.stream().map(x -> marshal(x)).collect(Collectors.toList());
	}

	protected List<ContactHistoryStore> unmarshals(List<HashMap<String, AttributeValue>> itmes) {
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


}
