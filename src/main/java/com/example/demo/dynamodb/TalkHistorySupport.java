package com.example.demo.dynamodb;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class TalkHistorySupport extends BaseSupport {

	public enum Key {

		CONTACTID("ContactId"), 
		CONTACTTIME("ContactTime"), 
		VOICETEXT("VoiceText"), 
		UTTERANCE("Utterance");

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
	
	protected Map<String, AttributeValue> marshal(TalkHistory talkHistory) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put(Key.CONTACTID.field(), S(talkHistory.getContactId()));
		return item;
	}
	
	protected TalkHistory unmarshal(Map<String, AttributeValue> item) {
		return TalkHistory.builder()
				.ContactId(item.get(Key.CONTACTID.field()).getS())
				.build();
	}
	
}
