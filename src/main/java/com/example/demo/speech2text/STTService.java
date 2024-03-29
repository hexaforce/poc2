package com.example.demo.speech2text;

import java.io.IOException;
import java.util.List;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

public class STTService {

	private final String LANG_CODE = "ja-JP";
	private final int SAMPLE_RATE = 8000;// 16000;

	private final SpeechSettings settings;

	public STTService(SpeechSettings settings){
		this.settings = settings;
	}
	
	public String execute(ByteString audioBytes) throws IOException {
		
		// Instantiates a client
		try (SpeechClient speechClient = SpeechClient.create(settings)) {

			// Builds the sync recognize request
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16).setSampleRateHertz(SAMPLE_RATE).setLanguageCode(LANG_CODE).build();
			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

			// Performs speech recognition on the audio file
			RecognizeResponse response = speechClient.recognize(config, audio);
			List<SpeechRecognitionResult> results = response.getResultsList();

			for (SpeechRecognitionResult result : results) {
				// There can be several alternative transcripts for a given chunk of speech.
				// Just use the first (most likely) one here.
				for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
					return alternative.getTranscript();
				}
			}
		}

		return "";

	}

}
