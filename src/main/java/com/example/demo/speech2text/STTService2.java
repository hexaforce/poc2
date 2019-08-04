package com.example.demo.speech2text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.springframework.util.ResourceUtils;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class STTService2 {

	private final String GCP_CREDENTIALS = "/root/.gcp/hexaforce-867578ab2dff.json";
	private final ArrayList<String> SCOPED = Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform");

	private final String LANG_CODE = "ja-JP";
	private final int SAMPLE_RATE = 8000;// 16000;

	private final SpeechSettings settings;

	public STTService2() throws FileNotFoundException, IOException {

		// Cloud Speech-to-Text credentials
		InputStream file = new FileInputStream(ResourceUtils.getFile(GCP_CREDENTIALS));
		GoogleCredentials credentials = GoogleCredentials.fromStream(file).createScoped(SCOPED);

		FixedCredentialsProvider provider = FixedCredentialsProvider.create(credentials);
		this.settings = SpeechSettings.newBuilder().setCredentialsProvider(provider).build();

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
			log.info("Transcript: {} Confidence: {}",speechRecognitionAlternative.getTranscript(), speechRecognitionAlternative.getConfidence());
			
		}

		public void onComplete() {
		}

		public void onError(Throwable t) {
			log.error("onError", t);
			clientStream.closeSend();
			referenceToStreamController.cancel();
		}

	};

	public void execute(ByteBuffer buffer) throws IOException {

		byte[] frameBytes = new byte[buffer.remaining()];
		buffer.get(frameBytes);
		ByteString byteString = ByteString.copyFrom(frameBytes);
		
		try (SpeechClient speechClient = SpeechClient.create(settings)) {
			clientStream = speechClient.streamingRecognizeCallable().splitCall(responseObserver);
			RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
					.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
					.setLanguageCode(LANG_CODE)
					.setSampleRateHertz(SAMPLE_RATE).build();
			StreamingRecognitionConfig streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder()
					.setConfig(recognitionConfig)
					.setInterimResults(true).build();
			StreamingRecognizeRequest streamingRecognizeRequest = StreamingRecognizeRequest.newBuilder()
					.setStreamingConfig(streamingRecognitionConfig).setAudioContent(byteString).build();
			clientStream.send(streamingRecognizeRequest);
		}

	}

}
