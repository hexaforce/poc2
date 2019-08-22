package com.example.demo.livemediastreams;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import com.amazonaws.kinesisvideo.parser.mkv.Frame;
import com.amazonaws.kinesisvideo.parser.mkv.FrameProcessException;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadata;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadataVisitor;
import com.amazonaws.kinesisvideo.parser.utilities.MkvTrackMetadata;
import com.google.protobuf.ByteString;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioFrameProcessor implements com.amazonaws.kinesisvideo.parser.utilities.FrameVisitor.FrameProcessor {

	protected AudioFrameProcessor(BlockingQueue<ByteString> audioQueue) {
		this.audioQueue = audioQueue;
	}

	private final BlockingQueue<ByteString> audioQueue;

	public static AudioFrameProcessor create(BlockingQueue<ByteString> audioQueue) {
		return new AudioFrameProcessor(audioQueue);
	}

	public void process(Frame frame, MkvTrackMetadata trackMetadata, Optional<FragmentMetadata> fragmentMetadata) throws FrameProcessException {
		addQueue(frame);
	}

	public void process(Frame frame, MkvTrackMetadata trackMetadata, Optional<FragmentMetadata> fragmentMetadata, Optional<FragmentMetadataVisitor.MkvTagProcessor> tagProcessor) throws FrameProcessException {
		if (tagProcessor.isPresent()) {
			addQueue(frame);
		} else {
			process(frame, trackMetadata, fragmentMetadata);
		}
	}

	private void addQueue(Frame frame) {
		ByteBuffer byteBuffer = frame.getFrameData();
		byte[] frameBytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(frameBytes);
		try {
			audioQueue.put(ByteString.copyFrom(frameBytes));
		} catch (InterruptedException e) {
			log.error("addQueue InterruptedException ", e);
		}
	}

}