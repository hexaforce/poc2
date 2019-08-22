package com.example.demo.livemediastreams;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideo;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoClientBuilder;
import com.amazonaws.services.kinesisvideo.model.CreateStreamRequest;
import com.amazonaws.services.kinesisvideo.model.DeleteStreamRequest;
import com.amazonaws.services.kinesisvideo.model.DescribeStreamRequest;
import com.amazonaws.services.kinesisvideo.model.ResourceNotFoundException;
import com.amazonaws.services.kinesisvideo.model.StreamInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.Validate;

@Slf4j
@Getter
public class KinesisVideoOptions extends KinesisCommon {

	private static final long SLEEP_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(3);
	private static final int DATA_RETENTION_IN_HOURS = 48;
	
	final AmazonKinesisVideo amazonKinesisVideo;

	public KinesisVideoOptions(Regions region, AWSCredentialsProvider credentialsProvider, String streamName) {
		super(region, credentialsProvider, streamName);
		final AmazonKinesisVideoClientBuilder builder = AmazonKinesisVideoClientBuilder.standard();
		configureClient(builder);
		this.amazonKinesisVideo = builder.build();
	}
	
	public void createStream() throws InterruptedException {
		// create the stream.
		CreateStreamRequest request = new CreateStreamRequest().withStreamName(streamName).withDataRetentionInHours(DATA_RETENTION_IN_HOURS).withMediaType("video/h264");
		amazonKinesisVideo.createStream(request);
		log.info("CreateStream called for stream {}", streamName);
		// wait for stream to become active.
		final Optional<StreamInfo> createdStreamInfo = waitForStateToMatch(s -> s.isPresent() && "ACTIVE".equals(s.get().getStatus()));
		// some basic validations on the response of the create stream
		Validate.isTrue(createdStreamInfo.isPresent());
		Validate.isTrue(createdStreamInfo.get().getDataRetentionInHours() == DATA_RETENTION_IN_HOURS);
		log.info("Stream {} created ARN {}", streamName, createdStreamInfo.get().getStreamARN());
	}
	
	/**
	 * If the stream exists delete it and then recreate it. Otherwise just create
	 * the stream.
	 */
	public void recreateStreamIfNecessary() throws InterruptedException {
		deleteStreamIfPresent();
		createStream();
	}

	public void createStreamIfNotExist() throws InterruptedException {
		final Optional<StreamInfo> streamInfo = getStreamInfo();
		log.info("Stream {} exists {}", streamName, streamInfo.isPresent());
		if (!streamInfo.isPresent()) {
			createStream();
		}
	}

	private void deleteStreamIfPresent() throws InterruptedException {
		final Optional<StreamInfo> streamInfo = getStreamInfo();
		log.info("Stream {} exists {}", streamName, streamInfo.isPresent());
		if (streamInfo.isPresent()) {
			// Delete the stream
			String streamARN = streamInfo.get().getStreamARN();
			DeleteStreamRequest request = new DeleteStreamRequest().withStreamARN(streamARN);
			amazonKinesisVideo.deleteStream(request);
			log.info("DeleteStream called for stream {} ARN {} ", streamName, streamARN);
			// Wait for stream to be deleted
			waitForStateToMatch(s -> !s.isPresent());
			log.info("Stream {} deleted", streamName);
		}
	}

	private Optional<StreamInfo> waitForStateToMatch(Predicate<Optional<StreamInfo>> statePredicate) throws InterruptedException {
		Optional<StreamInfo> streamInfo;
		do {
			streamInfo = getStreamInfo();
			if (!statePredicate.test(streamInfo)) {
				Thread.sleep(SLEEP_PERIOD_MILLIS);
			}
		} while (!statePredicate.test(streamInfo));
		return streamInfo;
	}

	private Optional<StreamInfo> getStreamInfo() {
		try {
			DescribeStreamRequest request = new DescribeStreamRequest().withStreamName(streamName);
			return Optional.ofNullable(amazonKinesisVideo.describeStream(request).getStreamInfo());
		} catch (ResourceNotFoundException e) {
			return Optional.empty();
		}
	}

	@Builder
	@ToString
	public static class LMSOptionsBuilder {
		private Regions region;
		private String streamName;
		private AWSCredentialsProvider credentialsProvider;
	}

}
