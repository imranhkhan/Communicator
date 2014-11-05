package com.pramati.communicator;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pramati.communicator.client.ClientExecutorThread;

public class ClientExecutor {

	private static final String PREFIX = "Welcome connection";

	public static void main(String[] args) throws IOException {
		int counter = 1;
		if (args.length > 0) {
			int nConnection = Integer.valueOf(args[0]);
			ExecutorService executor = Executors
					.newFixedThreadPool(nConnection);
			final CountDownLatch startLatch = new CountDownLatch(nConnection);
			while (nConnection != 0) {
				String message = PREFIX + counter;
				ClientExecutorThread clientExecutorThread = new ClientExecutorThread(
						startLatch);
				clientExecutorThread.setMessage(message);
				executor.execute(clientExecutorThread);
				nConnection--;
				counter++;
			}
			executor.shutdown();
		}
	}
}
