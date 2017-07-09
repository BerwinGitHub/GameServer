package proj.helper;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

public class Entry {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Configuration config = new Configuration();
		config.setHostname("127.0.0.1");
		config.setPort(8867);

		final SocketIOServer server = new SocketIOServer(config);
		server.addConnectListener(new ConnectListener() {

			public void onConnect(SocketIOClient arg0) {
				System.out.println("Connect:" + arg0.getRemoteAddress());
			}
		});
		server.addDisconnectListener(new DisconnectListener() {

			public void onDisconnect(SocketIOClient arg0) {
				System.out.println("Disconnect:" + arg0.getRemoteAddress());

			}
		});
		server.addEventListener("message", byte[].class, new DataListener() {

			public void onData(SocketIOClient client, Object data,
					AckRequest ackSender) throws Exception {
				System.out.println("message:\r\n" + data);
			}

		});
		server.addEventListener("Login", String.class, new DataListener() {

			public void onData(SocketIOClient client, Object data,
					AckRequest ackSender) throws Exception {
				System.out.println("Login:\r\n" + data.toString());
				// byte bdata[] = new byte[((ByteBuf) data).readableBytes()];
				// ((ByteBuf) data).readBytes(bdata);
				// System.out.println("Login:");
				// for (int i = 0; i < bdata.length; i++) {
				// System.out.println(bdata[i] + " ");
				// }
			}

		});
		server.start();
		System.out.println("Server start.");
	}

}
