package com.example.demo.poc;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoClientBuilder;
import com.example.demo.livemediastreams.LMSService;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@RequestMapping("/poc")
@RestController
public class AutomaticSpeechRecognitionController {

	private static volatile BlockingQueue<byte[]> sharedQueue = new LinkedBlockingQueue<byte[]>();

//    @Autowired
//    ExampleService exampleService;

	@PostMapping("/nul")
	@ResponseBody
	public NaturalLanguageUnderstandingResponse nul(@RequestBody NaturalLanguageUnderstandingRequest request) {
		try {
			
			Regions regions = Regions.AP_NORTHEAST_1;
			AWSCredentialsProvider credentials = AmazonKinesisVideoClientBuilder.standard().getCredentials();
			String streamName = request.getStreamARN().split("/")[1];
			String fragmentNumber = request.getStartFragmentNumber();

			log.info(request.toString());
			new LMSService(regions, credentials, streamName).execute(fragmentNumber, sharedQueue);
			
		} catch (IOException e) {
			
			log.error("NaturalLanguageUnderstanding", e);
			
		}
		
		return new NaturalLanguageUnderstandingResponse("Thanks For Posting!!!");
		
	}

}
