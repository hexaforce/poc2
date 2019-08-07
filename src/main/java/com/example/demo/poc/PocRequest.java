package com.example.demo.poc;

import lombok.Data;

@Data
public class PocRequest {
	private String ContactId;
	private String CustomerAddress;
	private String SystemAddress;
	private String StartFragmentNumber;
	private String StartTimestamp;
	private String StreamARN;
	private String LambdaFunctionName;
}
