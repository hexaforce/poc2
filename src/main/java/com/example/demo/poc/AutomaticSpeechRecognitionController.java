package com.example.demo.poc;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoClientBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@RequestMapping("/po")
@RestController
public class AutomaticSpeechRecognitionController {

	// private static volatile BlockingQueue<byte[]> sharedQueue = new LinkedBlockingQueue<byte[]>();

//    @Autowired
//    ExampleService exampleService;
    
	@PostMapping("/nul")
	@ResponseBody
	public NaturalLanguageUnderstandingResponse nul(@RequestBody NaturalLanguageUnderstandingRequest request) {
//		try {
//			new LMSExample(Regions.AP_NORTHEAST_1, AmazonKinesisVideoClientBuilder.standard().getCredentials(), request.getStreamARN()).execute(request.getStartFragmentNumber(), sharedQueue);
//		} catch (IOException e) {
//			log.error("NaturalLanguageUnderstanding", e);
//		}
		log.info(request.toString());
		return new NaturalLanguageUnderstandingResponse("Thanks For Posting!!!");
	}

}
