package com.firstwap.dispatcher.observer.service.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.firstwap.dispatcher.observer.domain.ProcessDomain;
import com.firstwap.dispatcher.observer.repository.LinuxProcessRepository;
import com.firstwap.dispatcher.observer.repository.WindowsProcessRepository;
import com.firstwap.dispatcher.observer.service.ProcessService;
import com.firstwap.dispatcher.observer.utility.ObserverException;
import com.firstwap.dispatcher.observer.utility.OsChecker;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 6, 2016 9:55:30 AM
 *
 */
@Service
public class ProcessServiceBean implements ProcessService {

	private static final Logger LOG = LoggerFactory
			.getLogger(ProcessServiceBean.class);

	@Autowired
	private LinuxProcessRepository linuxRepository;

	@Autowired
	private WindowsProcessRepository windowsRepository;

	@Value("${applications_list}")
	private String jsonSourceLocation;

	@Override
	public List<ProcessDomain> fetchListProcess() throws Exception {

		List<ProcessDomain> list = new ArrayList<>();

		if (OsChecker.isUnix()) {
			list.addAll(linuxRepository.fetchProcesses(jsonSourceLocation));
		} else {
			list.addAll(windowsRepository.fetchProcesses(jsonSourceLocation));
		}

		if (list.size() > 0) {

			Collections.sort(list, new Comparator<ProcessDomain>() {

				@Override
				public int compare(ProcessDomain o1, ProcessDomain o2) {
					// TODO Auto-generated method stub
					return o1.getPriority() == o2.getPriority() ? 0 : o1
							.getPriority() > o2.getPriority() ? -1 : 1;
				}
			});

		}

		return list;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.firstwap.dispatcher.observer.service.ProcessService#startProcess(
	 * java.lang.String)
	 */
	@Override
	public void startProcess(String location) throws ObserverException {
		// TODO Auto-generated method stub
		LOG.info("start process with location {} requested on  {}", location,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		try {
			for (ProcessDomain domain : fetchListProcess()) {
				if (domain.getStartScript().equals(location)) {
					if (OsChecker.isUnix()) {
						if (linuxRepository.isProcessRunning(domain.getPid(),
								domain.getName(), domain.getPidFile())) {
							throw new ObserverException(
									"the same instance of process is already running");
						} else {
							linuxRepository.startProcess(domain
									.getStartScript());
						}
					} else {
						// function for windows will be here
						if (windowsRepository.isProcessRunning(domain.getPid())) {
							throw new ObserverException(
									"the same instance of process is already running");
						} else {
							windowsRepository.startProcess(domain
									.getStartScript());
						}

					}

				}
			}
		} catch (Exception exception) {
			throw new ObserverException(exception.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.firstwap.dispatcher.observer.service.ProcessService#startProcess(
	 * java.lang.String)
	 */
	@Override
	public void killProcess(String pid, String stopScript)
			throws ObserverException {
		LOG.info("kill process with pid {} requested on  {}", pid,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		try {
			if (OsChecker.isUnix()) {
				LOG.debug("kill unix process with pid {}", pid);
				linuxRepository.killProcess(pid, stopScript);
				// sleep for 2 second to make sure the killing process done
				TimeUnit.SECONDS.sleep(2);
				ProcessDomain process = linuxRepository.findProcessByPid(pid,
						fetchListProcess());
				if (process != null
						&& linuxRepository.isProcessRunning(process.getPid(),
								process.getName(), process.getPidFile())) {
					linuxRepository.forceKillProcess(pid);
				}
			} else {
				// function for windows will be here
				LOG.debug("kill windows process with pid {}", pid);
				windowsRepository.killProcess(pid);
				ProcessDomain process = windowsRepository.findProcessByPid(pid,
						fetchListProcess());
				if (process != null
						&& windowsRepository.isProcessRunning(process.getPid())) {
					windowsRepository.forceKillProcess(pid);
				}
			}
			// LOG.info("wait for 10 second ... ");
			// TimeUnit.SECONDS.sleep(10);
		} catch (Exception ex) {
			throw new ObserverException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.firstwap.dispatcher.observer.service.ProcessService#killAllProcess
	 * (java.util.List)
	 */
	@Override
	public synchronized void killAllProcess(List<ProcessDomain> processes)
			throws ObserverException {
		// TODO Auto-generated method stub
		LOG.info("kill all process requested on  {}", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date()));
		try {
			if (processes.size() > 0) {

				for (ProcessDomain process : processes) {

					if (process.getPid() != null
							&& !process.getPid().equals("")
							&& !process.getPid().equals("-")) {
						if (OsChecker.isUnix()) {
							LOG.debug("killing linux process with pid : {}",
									process.getPid());
							linuxRepository.killProcess(process.getPid(),
									process.getStopScript());
							TimeUnit.SECONDS.sleep(2);
							// if process still running withing 2 second after
							// kill process then
							if (linuxRepository.isProcessRunning(
									process.getPid(), process.getName(),
									process.getPidFile())) {
								// force kill
								linuxRepository.forceKillProcess(process
										.getPid());
							}
						} else {
							// function for windows will be here
							LOG.debug("killing windows process with pid : {}",
									process.getPid());
							windowsRepository.killProcess(process.getPid());
							TimeUnit.SECONDS.sleep(2);
							// if process still running withing 2 second after
							// kill process then
							if (windowsRepository.isProcessRunning(process
									.getPid())) {
								// force kill
								windowsRepository.forceKillProcess(process
										.getPid());
							}
						}
					}
				}

				// LOG.info("wait for 10 second ... ");
				// TimeUnit.SECONDS.sleep(10);
			} else {
				throw new ObserverException("no application registered ... ");
			}
		} catch (Exception ex) {
			throw new ObserverException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.firstwap.dispatcher.observer.service.ProcessService#startAllProcess
	 * (java.util.List)
	 */
	@Override
	public synchronized void startAllProcess(List<ProcessDomain> processes)
			throws ObserverException {
		// TODO Auto-generated method stub
		LOG.info("start all process  requested on  {}", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(new Date()));
		try {
			if (processes.size() > 0) {
				int i = 0;

				if (!isInactiveProcessExist(processes)) {
					// no inactive process found
					throw new ObserverException("no inactive process found");
				} else {
					while (i < processes.size()) {
						if (OsChecker.isUnix()) {
							// reload process
							processes = fetchListProcess();
							// check process
							if (linuxRepository.isProcessRunning(
									processes.get(i).getPid(), processes.get(i)
											.getName(), processes.get(i)
											.getPidFile())) {
								// current apps already running move to the next
								// index
								i++;
							} else {
								LOG.info("trying to run {}", processes.get(i)
										.getName());
								if (i > 0) {

									// doing a little bit loop for 3 times to
									// make sure that
									// pid already loaded so activity is logged
									// well and also make sure that the
									// validation is work as logic wished
									int counter = 0;
									while (counter < 3) {
										processes = fetchListProcess();
										if (processes.get(i - 1).getPid()
												.equals("-")) {
											processes = fetchListProcess();
										} else {
											break;
										}
										TimeUnit.SECONDS.sleep(2);
										counter++;
									}

									// is higher priority already running
									boolean isHigherPriorityReady = linuxRepository
											.isProcessRunning(
													processes.get(i - 1)
															.getPid(),
													processes.get(i - 1)
															.getName(),
													processes.get(i - 1)
															.getPidFile());

									LOG.info(
											"service with higher priority names {} with pid {} {}",
											processes.get(i - 1).getName(),
											processes.get(i - 1).getPid(),
											isHigherPriorityReady ? "already running"
													: "is not running yet");

									if (isHigherPriorityReady) {
										// higher priority already running run
										// the next
										LOG.info("starting  {} service",
												processes.get(i).getName());
										linuxRepository.startProcess(processes
												.get(i).getStartScript());
										i++;
									} else {
										// wait for higher priority apps running
										LOG.info("wait 2 second for {}",
												processes.get(i - 1).getName());
										// wait for 2 second
										TimeUnit.SECONDS.sleep(2);
									}
								} else {
									// running highest priority on index of 0
									LOG.info(
											"running {} as the highest priority",
											processes.get(i).getName());
									linuxRepository.startProcess(processes.get(
											i).getStartScript());
									i++;
								}
							}
						} else {
							// reload process
							processes = fetchListProcess();
							// check process
							if (windowsRepository.isProcessRunning(processes
									.get(i).getPid())) {
								// current apps already running move to the next
								// index
								i++;
							} else {
								LOG.info("trying to run {}", processes.get(i)
										.getName());
								if (i > 0) {
									// is higher priority already running
									boolean isHigherPriorityReady = windowsRepository
											.isProcessRunning(processes.get(
													i - 1).getPid());
									LOG.info(
											"service with higher priority names {} with pid {} {}",
											processes.get(i - 1).getName(),
											processes.get(i - 1).getPid(),
											isHigherPriorityReady ? "already running"
													: "is not ready yet");

									if (isHigherPriorityReady) {
										// higher priority already running run
										// the next
										LOG.info("starting  :{}", processes
												.get(i).getName());
										windowsRepository
												.startProcess(processes.get(i)
														.getStartScript());
										i++;
									} else {
										// wait for higher priority apps running
										LOG.info("wait 2 second for {}",
												processes.get(i - 1).getName());
										// wait for 2 second
										TimeUnit.SECONDS.sleep(2);
									}
								} else {
									// running highest priority on index of 0
									LOG.info(
											"running {} as the highest priority",
											processes.get(i).getName());
									windowsRepository.startProcess(processes
											.get(i).getStartScript());
									i++;
								}
							}
						}
					}
				}
				TimeUnit.SECONDS.sleep(2);
			} else {
				throw new ObserverException("no application registered ...");
			}

		} catch (Exception exception) {
			throw new ObserverException(exception.getMessage());
		}

	}

	/**
	 *
	 * check if there is Inactive process in the registered process
	 *
	 * @param registeredProcesses
	 *            list of processes
	 * @return
	 */
	public boolean isInactiveProcessExist(
			List<ProcessDomain> registeredProcesses) {
		for (ProcessDomain domain : registeredProcesses) {
			if (domain.getStatus().equals("off")) {
				return true;
			}
		}
		return false;
	}
}
