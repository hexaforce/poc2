package com.example.demo.livemediastreams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import com.amazonaws.kinesisvideo.parser.mkv.Frame;
import com.amazonaws.kinesisvideo.parser.mkv.FrameProcessException;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadata;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadataVisitor;
import com.amazonaws.kinesisvideo.parser.utilities.MkvTrackMetadata;
import com.example.demo.speech2text.STTService2;
import com.example.demo.speech2text.STTSettings;
import com.google.protobuf.ByteString;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LMSFrameProcessor implements com.amazonaws.kinesisvideo.parser.utilities.FrameVisitor.FrameProcessor {

	private final STTService2 speechtotext;

	protected LMSFrameProcessor() {
		this.speechtotext = new STTService2(new STTSettings().getSpeechSettings());
		try {
			this.speechtotext.execute();
		} catch (IOException e) {
			log.error("toCloudCpeech IOException", e);
		}
	}

	public static LMSFrameProcessor create() {
		return new LMSFrameProcessor();
	}

	public void process(Frame frame, MkvTrackMetadata trackMetadata, Optional<FragmentMetadata> fragmentMetadata) throws FrameProcessException {
		toCloudCpeech(frame);
	}

	public void process(Frame frame, MkvTrackMetadata trackMetadata, Optional<FragmentMetadata> fragmentMetadata, Optional<FragmentMetadataVisitor.MkvTagProcessor> tagProcessor) throws FrameProcessException {
		if (tagProcessor.isPresent()) {
			toCloudCpeech(frame);
		} else {
			process(frame, trackMetadata, fragmentMetadata);
		}
	}

	private void toCloudCpeech(Frame frame) {
		ByteBuffer byteBuffer = frame.getFrameData();
		byte[] frameBytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(frameBytes);
		ByteString byteString = ByteString.copyFrom(frameBytes);
		speechtotext.send(byteString);

	}

}