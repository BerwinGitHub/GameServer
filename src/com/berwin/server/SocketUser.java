package com.berwin.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.berwin.domain.User;

public class SocketUser implements Runnable {

	private User user;

	private Socket socket;

	public SocketUser(Socket s) {
		this.socket = s;
		new Thread(this).start();
	}

	public void send(byte[] data) {
		OutputStream os;
		try {
			os = socket.getOutputStream();
			os.write(data);
		} catch (IOException e) {
			this.close();
		}
	}

	public void recive(byte[] data) {
		System.out.println("收到数据:" + new String(data));
	}

	public void close() {
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			// 3、获取输入流，并读取服务器端的响应信息
			InputStream is;
			try {
				// 获取客户端发来的数据
				is = socket.getInputStream();
				int len = is.available() + 1;
				byte[] buff = new byte[len];
				int flag = is.read(buff);

				// read()返回-1，说明客户端的socket已断开
				if (flag == -1) {
					this.close();
					break;
				}
				this.recive(buff);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
