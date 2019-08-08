package com.example.demo.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ContactHistoryStore {
	private String ContactId;
	private String CustomerAddress;
	private String SystemAddress;
	private String StartFragmentNumber;
	private long StartTimestamp;
	private String StreamARN;
	private String LambdaFunctionName;
}
