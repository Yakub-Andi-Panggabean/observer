package com.firstwap.dispatcher.observer.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstwap.dispatcher.observer.domain.Application;
import com.firstwap.dispatcher.observer.domain.Container;
import com.firstwap.dispatcher.observer.utility.CmdUtil;
import com.firstwap.dispatcher.observer.utility.OsChecker;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 15, 2016 9:26:08 AM
 *
 */
@Repository
public class ProcessRegistryRepository {

	private static final Logger LOG = LoggerFactory
			.getLogger(ProcessRegistryRepository.class.getName());

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 *
	 * write registry to json file
	 *
	 * @param file
	 *            json source file
	 * @param container
	 *            container value object
	 * @throws Exception
	 *             ObserverException
	 */
	public synchronized void writeRegistry(File file, Container container)
			throws Exception {

		if (file != null && file.exists() && file.isFile()) {
			if (container != null) {
				String jsonString = mapper.writeValueAsString(container);

				// rename old file for backup if something happen
				File dir = new File(file.getParentFile().toString());
				File newFile = null;
				String fileName = file.getAbsolutePath();
				LOG.debug("file absolute path : {}", fileName);
				LOG.debug("directory : {} ", dir.getPath());
				if (dir != null && dir.isDirectory()) {
					newFile = new File(dir, file
							.getName()
							.concat(".")
							.concat(new SimpleDateFormat("yyyyMMdd-HHmmss")
									.format(new Date())));
					file.renameTo(newFile);
				} else {
					throw new Exception(
							"cannot rename previous file because directory is not valid");
				}

				// create new file with the same name on the same directory
				File createdFile = new File(fileName);
				if (!createdFile.exists()) {
					createdFile.createNewFile();
				}

				BufferedWriter writer = new BufferedWriter(new FileWriter(
						createdFile));

				// write to new file
				writer.write(jsonString);
				writer.flush();
				if (writer != null) {
					writer.close();
				}
			} else {
				throw new Exception("applications data is not valid");
			}
		} else {
			throw new Exception("destination file is not valid");
		}

	}

	/**
	 *
	 *
	 * @param file
	 *            json source file
	 * @return value
	 * @throws Exception
	 */
	public synchronized Container readRegistry(File file) throws Exception {
		Container container = null;
		if (file != null && file.exists() && file.isFile()) {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			StringBuilder builder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			container = mapper.readValue(builder.toString(), Container.class);
			LOG.debug("read value : {}", container);
			if (reader != null) {
				reader.close();
			}
		} else {
			throw new Exception("json source file is not valid");
		}
		return container;
	}

	/**
	 *
	 *
	 * validation section
	 *
	 * @param existing
	 *            existing application list
	 * @param newApp
	 *            new app to be registered
	 * @param mode
	 *            process mode (using static constant from CmdUtil class)
	 * @throws Exception
	 */
	public void validateApps(Container existing, Application newApp, int mode)
			throws Exception {

		if (newApp.getName() == null || newApp.getName().equals("")
				|| newApp.getName().isEmpty()) {
			throw new Exception("Application name may not be empty");
		}

		if (OsChecker.isUnix()) {
			if (!newApp.getStartScript().contains(newApp.getName())) {
				throw new Exception(
						"unix system detected, application name must be contained on application start script folder or path");
			}
		}

		if (newApp.getPidFile() == null || newApp.getPidFile().equals("")
				|| newApp.getPidFile().isEmpty()) {
			throw new Exception("Pid File may not be empty");
		}

		if (newApp.getPriority() == null || newApp.getPriority() < 1) {
			throw new Exception("Priority is not valid");
		}

		// update and delete validation
		if (mode == CmdUtil.DELETE_MODE || mode == CmdUtil.UPDATE_MODE) {
			// validate same name
			int exist = 0;
			if (existing.getApplications().size() > 0) {
				for (Application app : existing.getApplications()) {
					if (app.getName().equalsIgnoreCase(newApp.getName())) {
						exist = 1;
					}
				}

				if (exist == 0) {
					throw new Exception("Application with name ".concat(
							newApp.getName()).concat(" is not exist"));
				}
			}
		}

		// include in insert validation
		if (mode == CmdUtil.INSERT_MODE) {
			// validate same name
			if (existing.getApplications().size() > 0) {
				for (Application app : existing.getApplications()) {
					// validate name
					if (app.getName().equalsIgnoreCase(newApp.getName())) {
						throw new Exception("Application with name ".concat(
								newApp.getName()).concat(" already exist"));
					}

					// validate location
					if (app.getStartScript().equalsIgnoreCase(
							newApp.getStartScript())) {
						throw new Exception("Execution file : ".concat(
								newApp.getStartScript()).concat(
										" already registered"));
					}

				}
			}
		}

		// other than delete validation
		if (mode == CmdUtil.INSERT_MODE || mode == CmdUtil.UPDATE_MODE) {
			// validate location
			if (newApp.getStartScript() == null
					|| newApp.getStartScript().equals("")) {
				throw new Exception(
						"Application executable file may not be empty");
			}

			File file = new File(newApp.getStartScript());

			// check if executable file is exist
			if (!file.exists()) {
				throw new Exception(
						"Application executable file is not exist, please choose right location");
			}

			// check if file is a file or directory
			if (file.isDirectory()) {
				throw new Exception(
						"Application executable file is a directory");
			}

			if (newApp.getStartScript() != null
					&& !newApp.getStopScript().equals("")
					&& !newApp.getStopScript().equals("-")) {
				File stopScript = new File(newApp.getStopScript());
				// check if stop script file is exist
				if (!stopScript.exists()) {
					throw new Exception(
							"Application stop script is not exist, please choose right location");
				}

				// check if stop script is a file or directory
				if (stopScript.isDirectory()) {
					throw new Exception(
							"Application stop script is a directory");
				}
			}

			if (newApp.getPidFile().contains("/")) {
				File pidFile = new File(newApp.getPidFile());

				// check if pidFile file is exist
				if (!pidFile.getParentFile().exists()) {
					throw new Exception(
							"Pid File Location is not valid, please choose right location");
				}
			}

		}

	}
}
