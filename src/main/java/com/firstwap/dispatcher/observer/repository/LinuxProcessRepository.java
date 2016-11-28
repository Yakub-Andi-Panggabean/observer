package com.firstwap.dispatcher.observer.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstwap.dispatcher.observer.domain.Application;
import com.firstwap.dispatcher.observer.domain.Container;
import com.firstwap.dispatcher.observer.domain.ProcessDomain;
import com.firstwap.dispatcher.observer.utility.CmdUtil;
import com.firstwap.dispatcher.observer.utility.CommonUtil;
import com.firstwap.dispatcher.observer.utility.ObserverException;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 5, 2016 5:54:43 PM
 *
 */
@Repository
public class LinuxProcessRepository {

	private static final Logger LOG = LoggerFactory
			.getLogger(LinuxProcessRepository.class);

	@Autowired
	private ObjectMapper mapper;

	/**
	 *
	 * fetch list of application process
	 *
	 * @param jsonApplication
	 *            content of json registry apps
	 * @return list of registry apps
	 * @throws Exception
	 */
	public List<ProcessDomain> fetchProcesses(String jsonApplication)
			throws Exception {

		LOG.debug("request at {}",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

		List<ProcessDomain> list = new ArrayList<>();

		File file = new File(jsonApplication);

		if (!file.exists()) {
			throw new RuntimeException("application file is not exist");
		}

		Container container = mapper.readValue(file, Container.class);

		ProcessDomain domain = null;

		for (Application application : CommonUtil
				.validateApplications(container.getApplications())) {
			try {
				domain = new ProcessDomain();

				Map<String, String> map = defineProcessStatus(application);
				Map<String, String> topData = retrieveMonitoringDataByPid(map
						.get("pid"));

				domain.setStartScript(application.getStartScript());
				domain.setName(application.getName());
				domain.setAutomaticStatus(application.isAutomatic() ? "Auto"
						: "Manual");
				domain.setAutomatic(application.isAutomatic());
				domain.setPriority(application.getPriority());
				domain.setStopScript(application.getStopScript() != null
						&& !application.getStopScript().equals("") ? application
								.getStopScript() : "-");
				domain.setStatus(map.get("status"));

				if (isProcessRunning(map.get("pid"), application.getName(),
						application.getPidFile())) {
					domain.setPid(map.get("pid"));
					domain.setMemoryUsage(topData.get("memoryUsage") != null ? topData
							.get("memoryUsage").concat(" %") : "");

					domain.setCpuUsage(topData.get("cpuUsage") != null ? topData
							.get("cpuUsage").concat(" %") : "");
					domain.setUser(topData.get("user") != null ? topData
							.get("user") : "");
					domain.setPidFile(map.get("pidFile") != null ? map
							.get("pidFile") : application.getPidFile());
				} else {
					domain.setPid("-");
					domain.setMemoryUsage("0");
					domain.setCpuUsage("0");
					domain.setUser("-");
					domain.setPidFile(application.getPidFile());
				}
			} catch (Exception exception) {
				LOG.error("cannot process application : {} because {}",
						application.toString(), exception.getMessage());
				continue;
			}
			list.add(domain);

		}

		return list;
	}

	/**
	 *
	 * read process based on pid file
	 *
	 * @param applicationName
	 *            the name of application
	 * @return
	 * @throws RuntimeException
	 */
	public Map<String, String> defineProcessStatus(Application application)
			throws RuntimeException {
		Map<String, String> condition = new HashMap<>();

		File file = new File(application.getStartScript()).getParentFile();

		File pidFile = null;

		if (file.exists() && file.isDirectory()) {

			if (!application.getPidFile().contains("/")) {
				pidFile = new File(file, application.getPidFile());
			} else {
				pidFile = new File(application.getPidFile());
			}

			LOG.debug("pid file : {}", pidFile.getPath());

			if (pidFile.exists() && pidFile.isFile()) {
				FileReader reader = null;
				BufferedReader bufferedReader = null;
				String line = "";
				try {
					reader = new FileReader(pidFile);
					bufferedReader = new BufferedReader(reader);
					if ((line = bufferedReader.readLine()) != null) {
						// if pid file exist and process running by grepping it
						if (isProcessRunning(line, application.getName(),
								application.getPidFile())) {
							// set status and grep pid from pid file and put it
							// on map memory
							condition.put("pid", line);
							condition.put("status", "running");
							condition.put("pidFile", pidFile.getPath());
						} else {
							// pid file exist but proccess not running
							// remove pid file
							pidFile.delete();
							condition.put("pid", "");
							condition.put("status", "off");
							condition.put("pidFile", "-");
						}
					}

				} catch (Exception exception) {
					throw new RuntimeException(exception.getMessage());
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException ioException) {
							LOG.debug("IOException occured with message : {}",
									ioException.getMessage());
						}
					}
				}

			} else {
				LOG.debug("pid file is not valid");
				condition.put("pid", "");
				condition.put("status", "off");
			}
		}

		return condition;
	}

	/**
	 *
	 * retrieving top data based on pid
	 *
	 * @param pid
	 *            process pid
	 * @return top data information
	 * @throws Exception
	 */
	public Map<String, String> retrieveMonitoringDataByPid(String pid)
			throws Exception {
		LOG.debug("top with pid {}", pid);
		if (pid == null) {
			throw new ObserverException(
					"invalid pid file : pid file is empty or expired");
		}
		Map<String, String> results = new HashMap<>();
		InputStream stream = null;
		BufferedReader bufferedReader = null;
		try {
			// do not use BashUtil class because of top command
			ProcessBuilder processBuilder = new ProcessBuilder("top", "-b",
					"-n", "1");
			Process process = processBuilder.start();
			stream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(stream));
			String line = "";
			StringBuilder builder = new StringBuilder();
			StringBuilder columns = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line);
				if (line.contains("PID") || line.contains("%CPU")
						|| line.contains("%MEM")) {
					columns = new StringBuilder(line.trim().replace("  ", " "));
				}
				if (line.contains(pid)) {
					builder = new StringBuilder(line.trim().replace("  ", " "));
					break;
				}
			}
			LOG.debug("header ouput : {}", columns);
			LOG.debug("row ouput : {}", builder);
			String[] array = builder.toString().split("\\s+");
			if (array.length > 9) {
				String[] arrayColumns = columns.toString().split("\\s+");
				for (int i = 0; i < arrayColumns.length; i++) {
					if (arrayColumns[i].equalsIgnoreCase("user")) {
						results.put("user", array[i]);
					} else if (arrayColumns[i].equalsIgnoreCase("%CPU")) {
						results.put("cpuUsage", array[i]);
					} else if (arrayColumns[i].equalsIgnoreCase("%MEM")) {
						results.put("memoryUsage", array[i]);
					}
				}
			}

		} finally {
			bufferedReader.close();
			stream.close();
		}
		return results;
	}

	/**
	 *
	 * kill process based on pid
	 *
	 * @param pid
	 * @throws Exception
	 */
	public void killProcess(String pid, String stopScriptLocation)
			throws Exception {
		LOG.debug("please wait ...");
		Process process = null;
		if (stopScriptLocation != null
				&& (!stopScriptLocation.equals("") || !stopScriptLocation
						.equals("-"))) {
			process = CmdUtil.BashProcess(
					null,
					"nohup ".concat(stopScriptLocation).concat(
							" &> /dev/null &"));
			process.waitFor();
			LOG.debug("killing process by executing {} done",
					stopScriptLocation);
		} else {
			process = CmdUtil.BashProcess(null, "bash", "-c",
					"kill ".concat(pid));
			process.waitFor();
			LOG.debug("killing process with pid {} done", pid);
		}
	}

	public void forceKillProcess(String pid) throws Exception {
		Process process = CmdUtil.BashProcess(null, "kill -9 ".concat(pid));
		process.waitFor();
		LOG.debug("force killing process with pid {} done", pid);
	}

	/**
	 *
	 * executing startup file
	 *
	 * @param fileLocation
	 * @throws Exception
	 */
	public void startProcess(String fileLocation) throws Exception {
		File file = new File(fileLocation).getParentFile();
		Process process = CmdUtil.BashProcess(file.getPath(),
				"nohup ".concat(fileLocation).concat(" &> /dev/null &"));
		process.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		while (reader.readLine() != null) {
			LOG.warn(reader.readLine());
		}
		LOG.debug("done executing ".concat(fileLocation));
	}

	/**
	 *
	 * check is process running by grepping into its pid and application name
	 *
	 * @param pid
	 * @param appName
	 * @return
	 * @throws Exception
	 */
	public boolean isProcessRunning(String pid, String appName,
			String applicationPidFile) throws Exception {

		// check if application.pid is exist
		File file = new File(applicationPidFile);
		// grep using pid
		Process process = CmdUtil.BashProcess(null,
				"ps -ef | grep ".concat(pid));
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			if (file.exists() && line.contains(appName)) {
				LOG.debug("{} process already running with pid {}... ",
						appName, pid);
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * find process by pid
	 *
	 * @param pid
	 * @param processes
	 * @return
	 */
	public ProcessDomain findProcessByPid(String pid,
			List<ProcessDomain> processes) {
		for (ProcessDomain process : processes) {
			if (process.getPid().equals(pid)) {
				return process;
			}
		}
		return null;
	}

}
