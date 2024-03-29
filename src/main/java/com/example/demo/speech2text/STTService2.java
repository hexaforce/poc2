package com.example.demo.speech2text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class STTService2 {

	private final String LANG_CODE = "ja-JP";
	private final int SAMPLE_RATE = 8000;// 16000;
	private final RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder().setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).setLanguageCode(LANG_CODE).setSampleRateHertz(SAMPLE_RATE).build();

	private static final int STREAMING_LIMIT = 290000; // ~5 minutes

	private final SpeechSettings settings;

	@Setter
	private boolean STOP = false;
	private volatile BlockingQueue<ByteString> speechQueue;
	public STTService2(SpeechSettings settings) {
		this.settings = settings;
		this.speechQueue = new LinkedBlockingQueue<ByteString>();
	}

	private StreamController referenceToStreamController;
	private ClientStream<StreamingRecognizeRequest> clientStream;
	private ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

	private ResponseObserver<StreamingRecognizeResponse> responseObserver = new ResponseObserver<StreamingRecognizeResponse>() {

		public void onStart(StreamController controller) {
			referenceToStreamController = controller;
		}

		public void onResponse(StreamingRecognizeResponse response) {
			
			responses.add(response);
			StreamingRecognitionResult streamingRecognitionResult = response.getResultsList().get(0);
			SpeechRecognitionAlternative speechRecognitionAlternative = streamingRecognitionResult.getAlternativesList().get(0);
			log.info("return from SpeachText!! Transcript: {} Confidence: {}", speechRecognitionAlternative.getTranscript(), speechRecognitionAlternative.getConfidence());

//			PocResponse y = new Okhttp3<PocResponse>("http://aaaa").post("", PocResponse.class);
//			PocResponse z = new Okhttp3<PocResponse>("http://aaaa").post(response, PocResponse.class);
			
		}

		public void onComplete() {
			log.info("onComplete");
		}

		public void onError(Throwable t) {
			log.error("onError", t);
			clientStream.closeSend();
			referenceToStreamController.cancel();
		}

	};
	
	
	public void execute(){

		log.info("STTService2 execute.");

		try (SpeechClient speechClient = SpeechClient.create(settings)) {
			clientStream = speechClient.streamingRecognizeCallable().splitCall(responseObserver);
			StreamingRecognitionConfig streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).setInterimResults(true).build();
			StreamingRecognizeRequest streamingRecognizeRequest = StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamingRecognitionConfig).build();
			clientStream.send(streamingRecognizeRequest);

			log.info("STTService2 clientStream.send");

			long startTime = System.currentTimeMillis();
			
			while (true) {
				
				long estimatedTime = System.currentTimeMillis() - startTime;
				
				if (STOP || estimatedTime >= STREAMING_LIMIT) {
					clientStream.closeSend();
					referenceToStreamController.cancel();
					break;
				} else {
					log.info("wating....");
					streamingRecognizeRequest = StreamingRecognizeRequest.newBuilder().setAudioContent(speechQueue.take()).build();
					log.info("send to SpeachText!!");
				}
				
				clientStream.send(streamingRecognizeRequest);
				
			}
			
		} catch (IOException | InterruptedException e) {
			log.error("Exception caught: ", e);
		}

	}

	public void send(ByteString speech) throws InterruptedException {
		speechQueue.add(speech);
	}
	
}
