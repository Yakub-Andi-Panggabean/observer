package com.firstwap.dispatcher.observer.utility;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 14, 2016 11:00:17 AM
 *
 */
public class OsChecker {

	private static String OS = System.getProperty("os.name").toLowerCase();

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS
				.indexOf("aix") > 0);
	}

	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

}
