package com.berwin.service;

import com.berwin.domain.User;

public class LoginService {

	public LoginService() {
	}

	public User login(User u) {
		if ((u.getName().equals("Berwin") && u.getPassWord().equals("123456"))
				|| (u.getName().equals("Sammi") && u.getPassWord().equals(
						"123456"))) {
			u.setName(u.getName() + "_suc");
			return u;
		}
		return null;
	}
}
