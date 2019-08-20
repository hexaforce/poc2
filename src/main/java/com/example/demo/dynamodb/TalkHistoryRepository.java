package com.example.demo.dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TalkHistoryRepository extends TalkHistorySupport {

	private final Regions regions = Regions.AP_NORTHEAST_1;
	
	
	private final String tableName;
	private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
			.withRegion(regions)
			.withCredentials(new ProfileCredentialsProvider("profile_test"))
			.build();

	public void PutItem(TalkHistory talkHistory) {
		client.putItem(tableName, marshal(talkHistory));
	}
	
}
