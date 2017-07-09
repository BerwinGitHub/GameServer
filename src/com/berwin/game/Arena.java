package com.berwin.game;

import com.berwin.proto.Protobufs.Action;
import com.berwin.proto.Protobufs.Match;
import com.berwin.proto.Protobufs.User;
import com.berwin.server.SocketUser;
import com.berwin.server.SocketUser.ActionProxy;

public class Arena implements ActionProxy {

	private SocketUser homeUser;

	private SocketUser awayUser;

	public Arena(SocketUser homeUser, SocketUser awayUser) {
		super();
		this.homeUser = homeUser;
		this.awayUser = awayUser;
		// 把事件放到这来做
		this.homeUser.addActionProxy(Action.ACTION_GAMING_VALUE, this);
		this.awayUser.addActionProxy(Action.ACTION_GAMING_VALUE, this);
		this.homeUser.addActionProxy(Action.ACTION_GAMEND_VALUE, this);
		this.awayUser.addActionProxy(Action.ACTION_GAMEND_VALUE, this);
		// 通知两个用户匹配成功
		User.Builder pHomeUserBuilder = User.newBuilder();
		pHomeUserBuilder.setUserName(this.homeUser.getUser().getName());

		User.Builder pAwayUserBuilder = User.newBuilder();
		pAwayUserBuilder.setUserName(this.awayUser.getUser().getName());
		// 通知主场用户
		Match.Builder homeBuilder = Match.newBuilder();
		homeBuilder.setEnemy(pAwayUserBuilder.build());
		this.homeUser.send(Action.ACTION_MATCHED_VALUE, homeBuilder.build()
				.toByteArray());
		// 通知客场用户
		Match.Builder awayBuilder = Match.newBuilder();
		awayBuilder.setEnemy(pHomeUserBuilder.build());
		this.awayUser.send(Action.ACTION_MATCHED_VALUE, awayBuilder.build()
				.toByteArray());
	}

	public SocketUser getHomeUser() {
		return homeUser;
	}

	public void setHomeUser(SocketUser homeUser) {
		this.homeUser = homeUser;
	}

	public SocketUser getAwayUser() {
		return awayUser;
	}

	public void setAwayUser(SocketUser awayUser) {
		this.awayUser = awayUser;
	}

	@Override
	public boolean actionProxy(SocketUser su, int action, byte[] data) {
		this.forwarding(
				su.getUser().getName()
						.equals(this.homeUser.getUser().getName()) ? this.awayUser
						: this.homeUser, data);
		return false;// 这个事件不向下分发
	}

	private void forwarding(SocketUser target, byte[] data) {
		target.send(Action.ACTION_GAMING_VALUE, data);
	}
}
