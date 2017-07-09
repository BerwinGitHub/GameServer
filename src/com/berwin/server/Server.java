package com.berwin.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private int port;
	private ServerSocket serverSocket;

	public Server(int port) {
		this.port = port;
	}

	public void startServer() {
		try {
			serverSocket = new ServerSocket(this.port);
			System.out.println("Server start at " + this.port);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("A user connect:" + socket.getInetAddress());
				new SocketUser(socket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
