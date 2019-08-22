package com.example.demo.livemediastreams;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoPutMediaClientBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class KinesisCommon {
	
	private final Regions region;
	private final AWSCredentialsProvider credentialsProvider;
	protected final String streamName;

	protected void configureClient(AwsClientBuilder<?, ?> clientBuilder) {
		clientBuilder.withCredentials(credentialsProvider).withRegion(region);
	}

	protected void conifgurePutMediaClient(AmazonKinesisVideoPutMediaClientBuilder builder) {
		builder.withCredentials(credentialsProvider).withRegion(region);
	}

}
