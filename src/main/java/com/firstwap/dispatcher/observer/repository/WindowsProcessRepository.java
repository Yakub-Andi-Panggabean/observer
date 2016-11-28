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

/**
 *
 * @author yakub
 *
 * @createdOn Sep 14, 2016 11:12:50 AM
 *
 */
@Repository
public class WindowsProcessRepository {

	private static final Logger LOG = LoggerFactory
			.getLogger(WindowsProcessRepository.class);

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

		for (Application application : container.getApplications()) {
			domain = new ProcessDomain();

			Map<String, String> map = defineProcessStatus(application);
			Map<String, String> systemData = retrieveMonitoringDataByPid(map
					.get("pid"));

			domain.setStartScript(application.getStartScript().replaceAll(
					"\\\\", "\\\\\\\\"));
			domain.setName(application.getName());
			domain.setAutomaticStatus(application.isAutomatic() ? "Auto"
					: "Manual");
			domain.setAutomatic(application.isAutomatic());
			domain.setPriority(application.getPriority());
			domain.setStopScript(application.getStopScript() != null
					&& !application.getStopScript().equals("") ? application
					.getStopScript().replaceAll("\\\\", "\\\\\\\\") : "-");
			domain.setStatus(map.get("status"));
			if (isProcessRunning(map.get("pid"))) {
				domain.setPid(map.get("pid"));
				domain.setMemoryUsage(systemData.get("memoryUsage") != null ? systemData
						.get("memoryUsage").concat(" k") : "");

				domain.setCpuUsage(systemData.get("cpuUsage") != null ? systemData
						.get("cpuUsage").concat(" (s)") : "");
				domain.setUser(systemData.get("user") != null ? systemData
						.get("user") : "");
				domain.setPidFile(map.get("pidFile") != null ? map.get(
						"pidFile").replaceAll("\\\\", "\\\\\\\\") : application
						.getPidFile().replaceAll("\\\\", "\\\\\\\\"));
			} else {
				domain.setPid("-");
				domain.setMemoryUsage("0");
				domain.setCpuUsage("0");
				domain.setUser("-");
				domain.setPidFile(application.getPidFile());
			}
			list.add(domain);

		}

		LOG.debug("contents : {}", list);

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

			if (!application.getPidFile().contains("\\")) {
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
						if (isProcessRunning(line)) {
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
					exception.printStackTrace();
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
	 * retrieving cpu usage by executing command get-process -id
	 *
	 * @param pid
	 *            process pid
	 * @return top data information
	 * @throws Exception
	 */
	public Map<String, String> retrieveCpuUsageDataByPid(String pid)
			throws Exception {
		LOG.debug("display cpu info with pid {}", pid);
		Map<String, String> results = new HashMap<>();
		InputStream stream = null;
		BufferedReader bufferedReader = null;
		try {
			if (pid != null && !pid.equals("")) {
				String batchCommand = "Get-Process -Id ".concat(pid);
				Process process = CmdUtil.batchProcess(CmdUtil.POWER_SHELL,
						null, batchCommand);
				stream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(
						stream));
				String line = "";
				StringBuilder builder = new StringBuilder();
				StringBuilder columns = new StringBuilder();
				while ((line = bufferedReader.readLine()) != null) {
					builder.append(line);
					if (line.contains("CPU(s)")) {
						columns = new StringBuilder(line.trim().replace("  ",
								" "));
					}
					if (line.contains(pid)) {
						builder = new StringBuilder(line.trim().replace("  ",
								" "));
						break;
					}
				}
				LOG.debug("header ouput : {}", columns);
				LOG.debug("row ouput : {}", builder);

				String[] array = builder.toString().split("\\s+");
				if (array.length > 5) {
					String[] arrayColumns = columns.toString().split("\\s+");
					for (int i = 0; i < arrayColumns.length; i++) {
						if (arrayColumns[i].equalsIgnoreCase("CPU(s)")) {
							results.put("cpuUsage", array[i]);
						}
					}
				}
			} else {
				results.put("cpuUsage", "-");
			}
		} catch (Exception ex) {
			LOG.debug("exception :{}", ex.getMessage());
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (stream != null) {
				stream.close();
			}
		}
		return results;
	}

	/**
	 *
	 * retrieving cpu usage by executing command get-process -id
	 *
	 * @param pid
	 *            process pid
	 * @return top data information
	 * @throws Exception
	 */
	public Map<String, String> retrieveMonitoringDataByPid(String pid)
			throws Exception {
		LOG.debug("display tasklist info with pid {}", pid);
		Map<String, String> results = new HashMap<>();
		InputStream stream = null;
		BufferedReader bufferedReader = null;
		try {
			results.putAll(retrieveCpuUsageDataByPid(pid));

			String batchCommand = "tasklist.exe /v";
			Process process = CmdUtil.batchProcess(CmdUtil.CMD_SHELL, null,
					batchCommand);
			stream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(stream));
			String line = "";
			StringBuilder builder = new StringBuilder();
			StringBuilder columns = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				// LOG.debug("stream : {}", bufferedReader.readLine());
				builder.append(line);
				if (line.contains("User") || line.contains("Image")) {
					columns = new StringBuilder(line.trim().replace("  ", " "));
				}
				if (line.contains(pid)) {
					builder = new StringBuilder(line.trim().replace("  ", " "));
					break;
				}
			}
			LOG.debug("main header ouput : {}", columns);
			LOG.debug("main row ouput : {}", builder);

			String[] array = builder.toString().split("\\s+");
			if (array.length > 6) {
				results.put("pid", array[1]);// pid
				results.put("user", array[7]);// user
				results.put("memoryUsage", array[4]);// memory usage
			}

		} catch (Exception ex) {
			LOG.debug("exception :{}", ex.getMessage());
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
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
	public void killProcess(String pid) throws Exception {
		LOG.debug("please wait ...");
		Process process = CmdUtil.batchProcess(CmdUtil.CMD_SHELL, null,
				"taskkill /pid ".concat(pid));
		process.waitFor();
		LOG.debug("killing process with pid {} done", pid);
	}

	public void forceKillProcess(String pid) throws Exception {
		LOG.debug("please wait ...");
		Process process = CmdUtil.batchProcess(CmdUtil.CMD_SHELL, null,
				"taskkill /pid ".concat(pid).concat(" /T /F"));
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
		LOG.debug("please wait ...");
		Process process = CmdUtil.batchProcess(CmdUtil.CMD_SHELL,
				file.getPath(),
				"START /B ".concat(fileLocation).concat(" > NUL"));
		process.waitFor();
		LOG.debug("done executing ".concat(fileLocation));
	}

	/**
	 *
	 * check is process running by grepping into its pid
	 *
	 * @param pid
	 * @param appName
	 * @return
	 * @throws Exception
	 */
	public boolean isProcessRunning(String pid) throws Exception {
		String appName = "java";
		if (pid != null && !pid.equals("")) {
			Process process = CmdUtil.batchProcess(CmdUtil.CMD_SHELL, null,
					"tasklist | findstr ".concat(pid));

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains(appName)) {// if contains java.exe
					LOG.debug("process already running with pid {} ... ", pid);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *
	 * find process by pid
	 *
	 * @param pid
	 *            process id
	 * @param processes
	 *            list of registered process
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
