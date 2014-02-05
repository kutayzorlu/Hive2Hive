package org.hive2hive.core.test.process;

import java.io.File;
import java.security.KeyPair;

import org.hive2hive.core.H2HConstants;
import org.hive2hive.core.H2HSession;
import org.hive2hive.core.IFileConfiguration;
import org.hive2hive.core.exceptions.GetFailedException;
import org.hive2hive.core.exceptions.IllegalFileLocation;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.file.FileManager;
import org.hive2hive.core.model.FileTreeNode;
import org.hive2hive.core.model.Locations;
import org.hive2hive.core.model.MetaDocument;
import org.hive2hive.core.model.UserProfile;
import org.hive2hive.core.network.NetworkManager;
import org.hive2hive.core.network.data.UserProfileManager;
import org.hive2hive.core.process.Process;
import org.hive2hive.core.process.ProcessStep;
import org.hive2hive.core.security.EncryptionUtil;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.core.test.H2HWaiter;
import org.hive2hive.core.test.processes.util.UseCaseTestUtil;
import org.junit.Assert;

/**
 * Helper class for JUnit tests to get some documents from the DHT.
 * All methods are blocking until the result is here.
 * 
 * @author Nico, Seppi
 * 
 */
@Deprecated
public class ProcessTestUtil {

	private ProcessTestUtil() {
		// only static methods
	}

	public static void waitTillSucceded(TestProcessListener listener, int maxSeconds) {
		H2HWaiter waiter = new H2HWaiter(maxSeconds);
		do {
			if (listener.hasFailed())
				Assert.fail();
			waiter.tickASecond();
		} while (!listener.hasSucceeded());
	}

	public static void waitTillFailed(TestProcessListener listener, int maxSeconds) {
		H2HWaiter waiter = new H2HWaiter(maxSeconds);
		do {
			if (listener.hasSucceeded())
				Assert.fail();
			waiter.tickASecond();
		} while (!listener.hasFailed());
	}

	/**
	 * Executes a process step and waits until it's done. This is a simple helper method to reduce code
	 * clones.
	 * 
	 * @param networkManager
	 *            a network manager
	 * @param toExecute
	 *            the process step to execute
	 */
	public static void executeStep(NetworkManager networkManager, ProcessStep toExecute) {
		Process process = new Process(networkManager) {
		};
		process.setNextStep(toExecute);
		TestProcessListener listener = new TestProcessListener();
		process.addListener(listener);
		process.start();

		waitTillSucceded(listener, 30);
	}

	/**
	 * Executes a process and waits until it's done. This is a simple helper method to reduce code
	 * clones.
	 */
	private static void executeProcess(Process process) {
		TestProcessListener listener = new TestProcessListener();
		process.addListener(listener);
		process.start();

		waitTillSucceded(listener, 60);
	}

	@Deprecated
	public static UserProfile register(UserCredentials credentials, NetworkManager networkManager) {
		try {
			UseCaseTestUtil.register(credentials, networkManager);
		} catch (NoPeerConnectionException e) {
			Assert.fail(e.getMessage());
		}

		return getUserProfile(networkManager, credentials);
	}

	@Deprecated
	public static UserProfile login(UserCredentials credentials, NetworkManager networkManager, File root) {
		try {
			UseCaseTestUtil.login(credentials, networkManager, root);
		} catch (NoPeerConnectionException e) {
			Assert.fail(e.getMessage());
			return null;
		}

		return getUserProfile(networkManager, credentials);
	}

	@Deprecated
	public static UserProfile getUserProfile(NetworkManager networkManager, UserCredentials credentials) {
		try {
			return UseCaseTestUtil.getUserProfile(networkManager, credentials);
		} catch (GetFailedException e) {
			Assert.fail(e.getMessage());
			return null;
		}
	}

	@Deprecated
	public static MetaDocument getMetaDocument(NetworkManager networkManager, KeyPair keys) {
		try {
			return UseCaseTestUtil.getMetaDocument(networkManager, keys);
		} catch (NoPeerConnectionException e) {
			return null;
		}
	}

	@Deprecated
	public static Locations getLocations(NetworkManager networkManager, String userId) {
		try {
			return UseCaseTestUtil.getLocations(networkManager, userId);
		} catch (NoPeerConnectionException e) {
			return null;
		}
	}

	@Deprecated
	public static File downloadFile(NetworkManager networkManager, FileTreeNode file,
			UserProfileManager profileManager, FileManager fileManager, IFileConfiguration config) {
		networkManager.setSession(new H2HSession(EncryptionUtil
				.generateRSAKeyPair(H2HConstants.KEYLENGTH_USER_KEYS), profileManager, config, fileManager));
		login(profileManager.getUserCredentials(), networkManager, fileManager.getRoot().toFile());
		try {
			return UseCaseTestUtil.downloadFile(networkManager, file.getFileKey());
		} catch (NoSessionException | GetFailedException e) {
			return null;
		}
	}

	@Deprecated
	public static void uploadNewFile(NetworkManager networkManager, File file,
			UserProfileManager profileManager, FileManager fileManager, IFileConfiguration config)
			throws IllegalFileLocation {
		login(profileManager.getUserCredentials(), networkManager, fileManager.getRoot().toFile());
		uploadNewFile(file, networkManager);
	}

	@Deprecated
	public static void uploadNewFile(File file, NetworkManager networkManager) throws IllegalFileLocation {
		try {
			UseCaseTestUtil.uploadNewFile(networkManager, file);
		} catch (NoSessionException | NoPeerConnectionException e) {
			Assert.fail();
		}
	}

	@Deprecated
	public static void uploadNewFileVersion(NetworkManager networkManager, File file,
			UserProfileManager profileManager, FileManager fileManager, IFileConfiguration config)
			throws IllegalArgumentException {
		login(profileManager.getUserCredentials(), networkManager, fileManager.getRoot().toFile());
		try {
			UseCaseTestUtil.uploadNewVersion(networkManager, file);
		} catch (NoSessionException | NoPeerConnectionException e) {
			Assert.fail();
		}
	}

	@Deprecated
	public static void deleteFile(NetworkManager networkManager, File file,
			UserProfileManager profileManager, FileManager fileManager, IFileConfiguration config) {
		login(profileManager.getUserCredentials(), networkManager, fileManager.getRoot().toFile());
		try {
			UseCaseTestUtil.deleteFile(networkManager, file);
		} catch (NoSessionException | NoPeerConnectionException e) {
			Assert.fail();
		}
	}

	@Deprecated
	public static void moveFile(NetworkManager networkManager, File source, File destination,
			UserProfileManager profileManager, FileManager fileManager, IFileConfiguration config) {
		login(profileManager.getUserCredentials(), networkManager, fileManager.getRoot().toFile());
		try {
			UseCaseTestUtil.moveFile(networkManager, source, destination);
		} catch (NoSessionException | NoPeerConnectionException e) {
			Assert.fail();
		}
	}
}
