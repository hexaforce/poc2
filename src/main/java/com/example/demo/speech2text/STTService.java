package com.example.demo.speech2text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.springframework.util.ResourceUtils;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class STTService {

	private final String LANG_CODE = "ja-JP";
	private final int SAMPLE_RATE = 8000;// 16000;

	private final ByteBuffer buffer;
	private final SpeechSettings settings;

	public STTService(ByteBuffer buffer) throws FileNotFoundException, IOException {
		this.buffer = buffer;

		// Cloud Speech-to-Text credentials
		InputStream file = new FileInputStream(ResourceUtils.getFile("/home/fedora/poc2/src/main/resources/Credentials.json"));
		GoogleCredentials credentials = GoogleCredentials.fromStream(file).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

		this.settings = SpeechSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
	}

	public String execute() throws IOException {

		byte[] frameBytes = new byte[buffer.remaining()];
		buffer.get(frameBytes);

		log.info("frameBytes size {}", frameBytes.length);

		// Instantiates a client
		try (SpeechClient speechClient = SpeechClient.create(settings)) {

			ByteString audioBytes = ByteString.copyFrom(frameBytes);

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

		return null;

	}

}
