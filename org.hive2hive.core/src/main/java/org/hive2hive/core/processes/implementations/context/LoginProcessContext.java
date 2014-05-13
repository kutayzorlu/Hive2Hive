package org.hive2hive.core.processes.implementations.context;

import java.security.KeyPair;

import org.hive2hive.core.model.Locations;
import org.hive2hive.core.model.UserProfile;
import org.hive2hive.core.processes.implementations.context.interfaces.common.IGetUserLocationsContext;
import org.hive2hive.core.processes.implementations.context.interfaces.common.IGetUserProfileContext;
import org.hive2hive.core.processes.implementations.context.interfaces.common.IPutUserLocationsContext;
import org.hive2hive.core.security.UserCredentials;

public class LoginProcessContext implements IGetUserProfileContext, IGetUserLocationsContext, IPutUserLocationsContext {

	private final UserCredentials credentials;

	private UserProfile profile;
	private Locations locations;
	private boolean isInitial;

	public LoginProcessContext(UserCredentials credentials) {
		this.credentials = credentials;
	}

	@Override
	public UserCredentials consumeUserCredentials() {
		return credentials;
	}

	@Override
	public String consumeUserId() {
		return credentials.getUserId();
	}

	@Override
	public void provideUserProfile(UserProfile profile) {
		this.profile = profile;
	}

	public UserProfile consumeUserProfile() {
		return profile;
	}

	@Override
	public KeyPair consumeUserLocationsProtectionKeys() {
		return profile.getProtectionKeys();
	}

	@Override
	public void provideUserLocations(Locations locations) {
		this.locations = locations;
	}

	@Override
	public Locations consumeUserLocations() {
		return locations;
	}

	public void setIsInitial(boolean isInitial) {
		this.isInitial = isInitial;
	}

	public boolean getIsInitial() {
		return isInitial;
	}

}
