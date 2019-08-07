package com.example.demo.speech2text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.springframework.util.ResourceUtils;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class STTSettings {

	//private final String GCP_CREDENTIALS = "/root/.gcp/hexaforce-867578ab2dff.json";
	private final String GCP_CREDENTIALS = "/Users/relics9/.gcp/hexaforce-867578ab2dff.json";
	private final ArrayList<String> SCOPED = Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform");
	
	public SpeechSettings getSpeechSettings() {
		try {
			// Cloud Speech-to-Text credentials
			InputStream file = new FileInputStream(ResourceUtils.getFile(GCP_CREDENTIALS));
			GoogleCredentials credentials = GoogleCredentials.fromStream(file).createScoped(SCOPED);
			FixedCredentialsProvider provider = FixedCredentialsProvider.create(credentials);
			return SpeechSettings.newBuilder().setCredentialsProvider(provider).build();
		} catch (FileNotFoundException e) {
			log.error("toCloudCpeech FileNotFoundException", e);
		} catch (IOException e) {
			log.error("toCloudCpeech IOException", e);
		}
		return null;
	}
}
