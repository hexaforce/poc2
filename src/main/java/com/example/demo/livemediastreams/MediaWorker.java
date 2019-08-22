package com.example.demo.livemediastreams;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.kinesisvideo.parser.ebml.InputStreamParserByteSource;
import com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitException;
import com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitor;
import com.amazonaws.kinesisvideo.parser.mkv.StreamingMkvReader;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideo;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoMedia;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoMediaClientBuilder;
import com.amazonaws.services.kinesisvideo.model.APIName;
import com.amazonaws.services.kinesisvideo.model.GetDataEndpointRequest;
import com.amazonaws.services.kinesisvideo.model.GetMediaRequest;
import com.amazonaws.services.kinesisvideo.model.GetMediaResult;
import com.amazonaws.services.kinesisvideo.model.StartSelector;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MediaWorker extends KinesisCommon implements Runnable {

	private final AmazonKinesisVideoMedia videoMedia;
	private final MkvElementVisitor elementVisitor;
	private final StartSelector startSelector;

	private MediaWorker(Regions region, AWSCredentialsProvider credentialsProvider, String streamName, StartSelector startSelector, String endPoint, MkvElementVisitor elementVisitor) {
		super(region, credentialsProvider, streamName);
		EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endPoint, region.getName());
		AmazonKinesisVideoMediaClientBuilder builder = AmazonKinesisVideoMediaClientBuilder.standard().withEndpointConfiguration(config).withCredentials(getCredentialsProvider());
		this.videoMedia = builder.build();
		this.elementVisitor = elementVisitor;
		this.startSelector = startSelector;
	}

	public static MediaWorker create(Regions region, AWSCredentialsProvider credentialsProvider, String streamName, StartSelector startSelector, AmazonKinesisVideo amazonKinesisVideo, MkvElementVisitor visitor) {
		GetDataEndpointRequest request = new GetDataEndpointRequest().withAPIName(APIName.GET_MEDIA).withStreamName(streamName);
		String endPoint = amazonKinesisVideo.getDataEndpoint(request).getDataEndpoint();
		return new MediaWorker(region, credentialsProvider, streamName, startSelector, endPoint, visitor);
	}

	@Override
	public void run() {
		try {
			GetMediaRequest request = new GetMediaRequest().withStreamName(getStreamName()).withStartSelector(startSelector);
			GetMediaResult result = videoMedia.getMedia(request);
			InputStreamParserByteSource source = new InputStreamParserByteSource(result.getPayload());
			StreamingMkvReader mkvStreamReader = StreamingMkvReader.createDefault(source);
			try {
				mkvStreamReader.apply(this.elementVisitor);
			} catch (MkvElementVisitException e) {
				log.error("Exception while accepting visitor {}", e);
			}
		} catch (Throwable t) {
			log.error("Failure in GetMediaWorker for streamName {} {}", streamName, t);
		} finally {
			log.info("Exiting GetMediaWorker for stream {}", streamName);
		}
	}

}
