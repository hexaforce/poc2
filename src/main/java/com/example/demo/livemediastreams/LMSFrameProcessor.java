package com.example.demo.livemediastreams;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import com.amazonaws.kinesisvideo.parser.mkv.Frame;
import com.amazonaws.kinesisvideo.parser.mkv.FrameProcessException;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadata;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadataVisitor;
import com.amazonaws.kinesisvideo.parser.utilities.MkvTrackMetadata;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LMSFrameProcessor implements com.amazonaws.kinesisvideo.parser.utilities.FrameVisitor.FrameProcessor {

	private volatile BlockingQueue<byte[]> sharedQueue;

	protected LMSFrameProcessor(BlockingQueue<byte[]> sharedQueue) {
		this.sharedQueue = sharedQueue;
	}

	public static LMSFrameProcessor create(BlockingQueue<byte[]> sharedQueue) {
		return new LMSFrameProcessor(sharedQueue);
	}

	public void process(Frame frame, MkvTrackMetadata trackMetadata, Optional<FragmentMetadata> fragmentMetadata) throws FrameProcessException {
		saveSharedQueue(frame);
	}

	public void process(Frame frame, MkvTrackMetadata trackMetadata, Optional<FragmentMetadata> fragmentMetadata, Optional<FragmentMetadataVisitor.MkvTagProcessor> tagProcessor) throws FrameProcessException {
		if (tagProcessor.isPresent()) {
			saveSharedQueue(frame);
		} else {
			process(frame, trackMetadata, fragmentMetadata);
		}
	}

	private void saveSharedQueue(Frame frame) {
		try {
			ByteBuffer frameBuffer = frame.getFrameData();
			byte[] frameBytes = new byte[frameBuffer.remaining()];
			frameBuffer.get(frameBytes);
			sharedQueue.put(frameBytes);
		} catch (InterruptedException e) {
			log.error("saveSharedQueue", e);
		}

	}

}