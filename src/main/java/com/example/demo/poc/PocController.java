package com.example.demo.poc;

import java.io.IOException;

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
@RestController
@RequestMapping("/poc")
public class PocController {

//    @Autowired
//    ExampleService exampleService;

	@ResponseBody
	@PostMapping("/nul")
	public PocResponse nul(@RequestBody PocRequest request) {
		try {
			request.getContactId();
			Regions regions = Regions.AP_NORTHEAST_1;
			AWSCredentialsProvider credentials = AmazonKinesisVideoClientBuilder.standard().getCredentials();
			String streamName = request.getStreamARN().split("/")[1];
			String fragmentNumber = request.getStartFragmentNumber();
			log.info(request.toString());
			new LMSService(regions, credentials, streamName).execute(fragmentNumber);
		} catch (IOException e) {
			log.error("NaturalLanguageUnderstanding", e);
			return new PocResponse(ResponseStatus.Error.name());
		}
		return new PocResponse(ResponseStatus.Accept.name());
	}
	
	@ResponseBody
	@PostMapping("/nul2")
	public PocResponse nul2(@RequestBody PocRequest request) {
		try {
			request.getContactId();
			Regions regions = Regions.AP_NORTHEAST_1;
			AWSCredentialsProvider credentials = AmazonKinesisVideoClientBuilder.standard().getCredentials();
			String streamName = request.getStreamARN().split("/")[1];
			String fragmentNumber = request.getStartFragmentNumber();
			log.info(request.toString());
			new LMSService(regions, credentials, streamName).execute(fragmentNumber);
		} catch (IOException e) {
			log.error("NaturalLanguageUnderstanding", e);
			return new PocResponse(ResponseStatus.Error.name());
		}
		return new PocResponse(ResponseStatus.Accept.name());
	}
	
}
