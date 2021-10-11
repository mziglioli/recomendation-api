package com.recomendationapi.config.security;

import com.recomendationapi.model.User;
import lombok.ToString;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@ToString(of = {"user", "auths"})
public class UserAuthentication implements Authentication, UserDetails {

	private User user;
	private Collection<GrantedAuthority> auths;

	public UserAuthentication(User user) {
		this.user = user;
		this.auths = Collections.singletonList(new SimpleGrantedAuthority("USER"));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return auths;
	}

	@Override
	public String getPassword() {
		return "no_password";
	}

	@Override
	public String getUsername() {
		return "user";
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getCredentials() {
		return "no_password";
	}

	@Override
	public Object getDetails() {
		return "user";
	}

	@Override
	public Object getPrincipal() {
		return "user";
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
		return "user";
	}
}