package com.recomendationapi.config.security;

import com.recomendationapi.model.User;
import lombok.ToString;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@ToString(of = {"user"})
public class UserAuthentication implements Authentication {

	private User user;

	public UserAuthentication(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getAuthorities();
	}

	@Override
	public String getCredentials() {
		return user.getPassword();
	}

	@Override
	public User getDetails() {
		return user;
	}

	@Override
	public User getPrincipal() {
		return user;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

	}

	@Override
	public String getName() {
		return user.getName();
	}
}