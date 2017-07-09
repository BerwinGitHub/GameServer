package com.berwin.service.domain;

import com.berwin.domain.User;
import com.berwin.service.MatchService.ResultListener;

public class MatchUser {

	private User user;

	private ResultListener listener;

	public MatchUser(User user, ResultListener listener) {
		super();
		this.user = user;
		this.listener = listener;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ResultListener getListener() {
		return listener;
	}

	public void setListener(ResultListener listener) {
		this.listener = listener;
	}

}
