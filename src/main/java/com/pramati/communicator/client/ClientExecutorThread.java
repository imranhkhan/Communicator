package com.pramati.communicator.client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ClientExecutorThread implements Runnable {

	private final CountDownLatch countDownLatch;
	private static final int PORT = 9090;
	private String message;

	public ClientExecutorThread(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void run() {
		try {
			CommunicationClient commClient = new CommunicationClient();
			countDownLatch.countDown();
			countDownLatch.await();
			if (commClient.connect("localhost", PORT)) {
				commClient.write(message);
				System.out.println(commClient.read());
				commClient.close();
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}
