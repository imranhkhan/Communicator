package com.pramati.communicator.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReaderThread implements Runnable {

	private final BlockingQueue<SocketChannel> socketQueue = new LinkedBlockingQueue<SocketChannel>();
	private EchoThread echoThread;

	public void setEchoThread(EchoThread echoThread) {
		this.echoThread = echoThread;
	}

	public BlockingQueue<SocketChannel> getSocketQueue() {
		return socketQueue;
	}

	public void run() {
		while (true) {
			if (socketQueue.isEmpty()) {
				continue;
			}
			read(socketQueue.poll());
		}
	}

	private void read(SocketChannel socketChannel) {
		int count = 0;
		String message = "";
		ByteBuffer byteBuffer = ByteBuffer.allocate(100);
		try {
			if ((count = socketChannel.read(byteBuffer)) > 0) {
				byteBuffer.flip();
				message += Charset.defaultCharset().decode(byteBuffer)
						.toString();
			}
			echoThread.getSocketmap().put(socketChannel, message);
			closeAtEOF(socketChannel, count);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byteBuffer.clear();
	}

	private void closeAtEOF(SocketChannel socketChannel, int count)
			throws IOException {
		if (count < 0) {
			socketChannel.close();
		}
	}

}
