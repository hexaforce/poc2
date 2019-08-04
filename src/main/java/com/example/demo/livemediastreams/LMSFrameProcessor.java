package com.example.demo.livemediastreams;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import com.amazonaws.kinesisvideo.parser.mkv.Frame;
import com.amazonaws.kinesisvideo.parser.mkv.FrameProcessException;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadata;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadataVisitor;
import com.amazonaws.kinesisvideo.parser.utilities.MkvTrackMetadata;
import com.example.demo.speech2text.STTService2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LMSFrameProcessor implements com.amazonaws.kinesisvideo.parser.utilities.FrameVisitor.FrameProcessor {

	protected LMSFrameProcessor() {
	}

	public static LMSFrameProcessor create() {
		return new LMSFrameProcessor();
	}

	public void process(Frame frame, 
			MkvTrackMetadata trackMetadata, 
			Optional<FragmentMetadata> fragmentMetadata) throws FrameProcessException {
		toCloudCpeech(frame);
	}

	public void process(Frame frame, 
			MkvTrackMetadata trackMetadata, 
			Optional<FragmentMetadata> fragmentMetadata, 
			Optional<FragmentMetadataVisitor.MkvTagProcessor> tagProcessor) throws FrameProcessException {
		if (tagProcessor.isPresent()) {
			toCloudCpeech(frame);
		} else {
			process(frame, trackMetadata, fragmentMetadata);
		}
	}

	private String toCloudCpeech(Frame frame) {
		try {
//			String result = new STTService(frame.getFrameData()).execute();
//			log.info(result);
//			return result;
			new STTService2().execute(frame.getFrameData());
			return "";
		} catch (FileNotFoundException e) {
			log.error("toCloudCpeech FileNotFoundException", e);
		} catch (IOException e) {
			log.error("toCloudCpeech IOException", e);
		}
		return null;
	}

}