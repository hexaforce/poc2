package com.example.demo.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TalkHistory {
	private String ContactId;
	private long ContactTime;
	private String VoiceText;
	private String Utterance;
}


