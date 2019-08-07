package com.example.demo.dynamodb;

import lombok.Data;

@Data
public class ContactHistoryStore {
	private String ContactId;
	private String CustomerAddress;
	private String SystemAddress;
	private String StartFragmentNumber;
	private String StartTimestamp;
	private String StreamARN;
	private String LambdaFunctionName;
}
