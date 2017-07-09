package com.berwin.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.berwin.controller.MessageDispatcher;
import com.berwin.domain.User;
import com.berwin.proto.Protobufs.Action;
import com.berwin.proto.Protobufs.TransferData;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class SocketUser implements Runnable {

	private User user;

	private Socket socket;

	public interface ActionProxy {
		public boolean actionProxy(SocketUser su, int action, byte[] data);
	}

	/**
	 * 优先分发到代理上
	 */
	private Map<Integer, ActionProxy> actionProxy;

	public SocketUser(Socket s) {
		this.socket = s;
		this.actionProxy = new HashMap<Integer, SocketUser.ActionProxy>();
		new Thread(this).start();
	}

	/**
	 * 发送事件
	 * 
	 * @param action
	 * @param data
	 */
	public void send(int action, byte[] data) {
		TransferData.Builder builder = TransferData.newBuilder();
		builder.setActionValue(action);
		builder.setData(ByteString.copyFrom(data));
		// byte转成字符串
		String byteStr = "[";
		byte[] tdBytes = builder.build().toByteArray();
		for (int i = 0; i < tdBytes.length; i++) {
			byteStr += tdBytes[i] + (i == tdBytes.length - 1 ? "]" : ",");
		}
		OutputStream out;
		try {
			out = socket.getOutputStream();

			ByteBuffer bb = ByteBuffer.wrap(byteStr.getBytes());
			int head = 0x00;
			// 是否是输出最后的WebSocket响应片段
			head = head + 0x80;
			head = head + 0x1;

			out.write(head);
			if (bb.limit() < 126) {
				out.write(bb.limit());
			} else if (bb.limit() < 65536) {
				out.write(126);
				out.write(bb.limit() >>> 8);
				out.write(bb.limit() & 0xFF);
			} else {
				// Will never be more than 2^31-1
				out.write(127);
				out.write(0);
				out.write(0);
				out.write(0);
				out.write(0);
				out.write(bb.limit() >>> 24);
				out.write(bb.limit() >>> 16);
				out.write(bb.limit() >>> 8);
				out.write(bb.limit() & 0xFF);
			}
			// Write the content
			out.write(bb.array(), 0, bb.limit());
			out.flush();
		} catch (IOException e) {
			this.close();
		}
	}

	/**
	 * 接收消息
	 * 
	 * @param data
	 */
	public void recive(byte[] data) {
		try {
			TransferData transferData = TransferData.parseFrom(data);
			int action = transferData.getActionValue();
			this.dispatchData(action, transferData.getData().toByteArray());
		} catch (InvalidProtocolBufferException e) {
			System.out.println(new String(data));
		}
	}

	/**
	 * 分发事件，如果有代理事件，优先分发代理事件
	 * 
	 * @param data
	 */
	private void dispatchData(int action, byte[] data) {
		MessageDispatcher dispatcher = new MessageDispatcher();
		if (this.actionProxy.containsKey(action)) { // 如果有代理这个事件，那么优先走代理
			ActionProxy proxy = this.actionProxy.get(action);
			if (proxy.actionProxy(this, action, data)) {// 代理同意向下继续分发事件
				dispatcher.dispatch(this, action, data);
			}
		} else { // 这个事件暂时没有代理，直接给消息分发
			dispatcher.dispatch(this, action, data);
		}
	}

	/**
	 * 关闭socket
	 */
	public void close() {
		try {
			this.dispatchData(Action.ACTION_CLOSED_VALUE, null);
			this.socket.close();
			System.out.println("用户断开链接");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addActionProxy(int action, ActionProxy proxy) {
		this.actionProxy.put(action, proxy);
	}

	public void removeActionProxy(int action) {
		if (this.actionProxy.containsKey(action)) {
			this.actionProxy.remove(action);
		}
	}

	@Override
	public void run() {
		// 第一次进入先握手
		this.handShake();
		try {
			this.reciveMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// while (true) {
		// // 3、获取输入流，并读取服务器端的响应信息
		// InputStream is;
		// try {
		// // 获取客户端发来的数据
		// is = socket.getInputStream();
		//
		// int len = is.available() + 1;
		// byte[] buff = new byte[len];
		// int flag = is.read(buff);
		//
		// // read()返回-1，说明客户端的socket已断开
		// if (flag == -1) {
		// System.out.println("准备关闭");
		// this.close();
		// break;
		// }
		// System.out.println(new String(buff));
		// this.recive(buff);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }

	}

	/**
	 * 第一次握手
	 */
	private void handShake() {
		try {
			InputStream is = this.socket.getInputStream();
			PrintWriter writer = new PrintWriter(this.socket.getOutputStream(),
					true);
			// 读入缓存(定义一个1M的缓存区)
			byte[] buf = new byte[1024];
			// 读到字节（读取输入流数据到缓存）
			int len = is.read(buf, 0, 1024);
			// 读到字节数组（定义一个容纳数据大小合适缓存区）
			byte[] res = new byte[len];
			// 将buf内中数据拷贝到res中
			System.arraycopy(buf, 0, res, 0, len);
			// 打印res缓存内容
			String key = new String(res);
			if (key.indexOf("Key") > 0) {
				// 握手
				// 通过字符串截取获取key值
				key = key.substring(0, key.indexOf("==") + 2);
				key = key.substring(key.indexOf("Key") + 4, key.length())
						.trim();
				// 拼接WEBSOCKET传输协议的安全校验字符串
				key += "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
				// 通过SHA-1算法进行更新
				MessageDigest md = MessageDigest.getInstance("SHA-1");
				md.update(key.getBytes("utf-8"), 0, key.length());
				byte[] sha1Hash = md.digest();
				// 进行Base64加密
				sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
				key = encoder.encode(sha1Hash);
				// 服务器端返回输出内容
				writer.println("HTTP/1.1 101 Switching Protocols");
				writer.println("Upgrade: websocket");
				writer.println("Connection: Upgrade");
				writer.println("Sec-WebSocket-Accept: " + key);
				writer.println();
				writer.flush();
				// 将握手标志更新，只握一次
			}
			// 分发连接事件
			this.dispatchData(Action.ACTION_CONNECT_VALUE, null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private void reciveMessage() throws IOException {
		// ----------------------以下是获取到浏览器发送的消息--------
		// 获取socket输入流信息
		InputStream in = socket.getInputStream();
		// 接收数据
		byte[] head = new byte[1];
		// 这里会阻塞
		int read = in.read(head, 0, 1);
		// 读取第一个字节是否有值,开始接收数据
		while (read > 0) {
			// 让byte和十六进制做与运算（二进制也就是11111111）
			// 获取到第一个字节的数值
			int b = head[0] & 0xFF;
			// 1为字符数据，8为关闭socket（只要低四位的值判断）
			byte opCode = (byte) (b & 0x0F);
			if (opCode == 8) {
				break;
			}
			b = in.read();
			// 只能描述127
			int len = b & 0x7F; // payloadLength
			if (len == 126) {
				byte[] extended = new byte[2];
				in.read(extended, 0, 2);
				int shift = 0;
				len = 0;
				for (int i = extended.length - 1; i >= 0; i--) {
					len = len + ((extended[i] & 0xFF) << shift);
					shift += 2;
				}
			} else if (len == 127) {
				byte[] extended = new byte[8];
				in.read(extended, 0, 8);
				int shift = 0;
				len = 0;
				for (int i = extended.length - 1; i >= 0; i--) {
					len = len + ((extended[i] & 0xFF) << shift);
					shift += 8;
				}
			}
			// 掩码
			byte[] mask = new byte[4];
			in.read(mask, 0, 4);
			int fragment = 1;
			ByteBuffer byteBuf = ByteBuffer.allocate(len);
			// ByteBuffer byteBuf = ByteBuffer.allocate(len + 30);
			while (len > 0) {
				int masked = in.read();
				masked = masked ^ (mask[(int) ((fragment - 1) % 4)] & 0xFF);
				byteBuf.put((byte) masked);
				len--;
				fragment++;
			}
			byteBuf.flip();
			// 将内容返回给客户端
			this.recive(byteBuf.array());
			// System.out.println(new String(byteBuf.array()));
			in.read(head, 0, 1);// 再执行一次这个代码，不然后面接收到的数据是乱码
		}
		this.close();
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
