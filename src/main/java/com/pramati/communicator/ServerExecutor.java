package com.pramati.communicator;

import java.io.IOException;
import java.nio.channels.Selector;

import com.pramati.communicator.server.CommunicationServer;

public class ServerExecutor {
	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		CommunicationServer commServer = new CommunicationServer(selector);
		commServer.listen("localhost", 9090);
	}
}
