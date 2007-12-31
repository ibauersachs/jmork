package mork.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Tries to find the first address book of the current user on the local machine
 * and suggest the absolute path to the address book mork file.
 * 
 * @author mhaller
 */
class ProfileLocator {

	public String locateFirstThunderbirdAddressbookPath() {
		// 1. Use environment variable "APPDATA"/Thunderbird/
		String appData = System.getenv().get("APPDATA");
		File appDataFolder = new File(appData);
		if (!appDataFolder.exists()) {
			return null;
		}
		if (!appDataFolder.isDirectory()) {
			return null;
		}
		File thunderbird = new File(appDataFolder, "Thunderbird");
		if (!thunderbird.exists()) {
			return null;
		}
		if (!thunderbird.isDirectory()) {
			return null;
		}
		File profiles = new File(thunderbird, "profiles.ini");
		if (!profiles.exists()) {
			return null;
		}
		if (!profiles.isFile()) {
			return null;
		}
		if (!profiles.canRead()) {
			return null;
		}
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(profiles));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!props.containsKey("Path")) {
			return null;
		}
		String path = (String) props.getProperty("Path");
		File firstProfileFolder = new File(thunderbird, path);
		if (!firstProfileFolder.exists()) {
			return null;
		}
		if (!firstProfileFolder.isDirectory()) {
			return null;
		}
		return firstProfileFolder.getAbsolutePath();
	}

}
