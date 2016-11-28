package com.firstwap.dispatcher.observer.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 14, 2016 4:40:30 PM
 *
 */
public class CmdUtil {

	private static final Logger LOG = LoggerFactory.getLogger(CmdUtil.class);

	public static final int INSERT_MODE = 1;
	public static final int UPDATE_MODE = 2;
	public static final int DELETE_MODE = 0;
	public static final String CMD_SHELL = "cmd.exe";
	public static final String POWER_SHELL = "powershell.exe";

	/**
	 *
	 * executing bash shell for *nix operating system
	 *
	 * @param workingDirectory
	 *            : process working directory
	 * @param bashCommands
	 *            : vargs command of list which will be executed
	 * @return : instance of process class
	 * @throws Exception
	 */
	public static Process BashProcess(String workingDirectory,
			String... bashCommands) throws Exception {
		List<String> command = new ArrayList<>();
		command.add("bash");
		command.add("-c");
		for (int i = 0; i < bashCommands.length; i++) {
			command.add(bashCommands[i]);
		}

		return executeProcess(workingDirectory, command);

	}

	/**
	 *
	 * executing batch script for windows operating system
	 *
	 * @param windowsShellOption
	 *            : using CMD.exe or powershell.exe
	 * @param dir
	 *            : process working directory
	 * @param batchCommand
	 *            : batch command line which will be executed
	 * @return : instance of process
	 * @throws Exception
	 */
	public static Process batchProcess(String windowsShellOption, String dir,
			String... batchCommand) throws Exception {
		List<String> command = new ArrayList<>();
		command.add(windowsShellOption);
		command.add("/c");
		for (int i = 0; i < batchCommand.length; i++) {
			command.add(batchCommand[i]);
		}

		return executeProcess(dir, command);
	}

	/**
	 *
	 * @param workingDirectory
	 *            : process working directory
	 * @param commands
	 *            : command line which will be passed depends on operating
	 *            system used
	 * @return : instance of process
	 * @throws IOException
	 */
	private static Process executeProcess(String workingDirectory,
			List<String> commands) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		LOG.debug("executing command : {} ", processBuilder.command());

		if (workingDirectory != null && !workingDirectory.equals("")) {
			File workingDir = new File(workingDirectory);

			if (workingDir.exists() && workingDir.isDirectory()) {
				processBuilder.directory(workingDir);

				LOG.debug("process directory : {}", processBuilder.directory()
						.getPath());
			}

		}

		Process process = processBuilder.start();
		return process;
	}

}
