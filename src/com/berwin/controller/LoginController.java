package com.berwin.controller;

import com.berwin.domain.User;
import com.berwin.proto.Protobufs;
import com.berwin.proto.Protobufs.Action;
import com.berwin.proto.Protobufs.Login;
import com.berwin.server.SocketUser;
import com.berwin.service.LoginService;
import com.google.protobuf.InvalidProtocolBufferException;

public class LoginController implements IController {

	private LoginService loginService = null;

	public LoginController() {
		loginService = new LoginService();
	}

	@Override
	public void dispatch(SocketUser su, int action, byte[] data) {
		try {
			Login login = Login.parseFrom(data);
			if (action == Action.ACTION_LOGIN_VALUE) {
				User u = new User();
				u.setName(login.getUser().getUserName());
				u.setPassWord(login.getUser().getPassWord());
				User u2 = loginService.login(u);
				// proto user
				Protobufs.User.Builder builder = Protobufs.User.newBuilder();
				builder.setVerify(false);
				if (u2 != null) {
					builder.setUserId(0);
					builder.setUserName(u2.getName());
					builder.setVerify(true);
					su.setUser(u2);
				}
				Protobufs.Login.Builder loginBuilder = Protobufs.Login.newBuilder();
				loginBuilder.setUser(builder.build());
				// 发送消息回去
				su.send(Action.ACTION_LOGIN_VALUE, loginBuilder.build()
						.toByteArray());
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
}
