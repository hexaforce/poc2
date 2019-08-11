package com.example.demo.dynamodb;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;

public class BaseSupport {

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
