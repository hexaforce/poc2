package com.example.demo.livemediastreams;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.kinesisvideo.parser.utilities.FrameVisitor;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.model.StartSelector;
import com.amazonaws.services.kinesisvideo.model.StartSelectorType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LMSService extends LMSCommon {

	public LMSService(Regions region, AWSCredentialsProvider credentialsProvider, String streamName) throws IOException {
		super(region, credentialsProvider, streamName);
	}

	public void execute(String fragmentNumber) throws IOException {

		LMSOptions streamOps = new LMSOptions(getRegion(), getCredentialsProvider(), getStreamName());
		StartSelector startSelector = new StartSelector().withStartSelectorType(StartSelectorType.FRAGMENT_NUMBER).withAfterFragmentNumber(fragmentNumber);
		FrameVisitor frameVisitor = FrameVisitor.create(LMSFrameProcessor.create());

		LMSWorker worker = LMSWorker.create(getRegion(), getCredentialsProvider(), getStreamName(), 
				startSelector, streamOps.getAmazonKinesisVideo(), frameVisitor);

		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(worker);

		// Wait for the workers to finish.
		executorService.shutdown();
		try {
			executorService.awaitTermination(120, TimeUnit.SECONDS);
			if (!executorService.isTerminated()) {
				log.info("Shutting down executor service by force");
				executorService.shutdownNow();
			} else {
				log.info("Executor service is shutdown");
			}
		} catch (InterruptedException e) {
			log.error("Await termination error: ", e);
		}

	}

}
