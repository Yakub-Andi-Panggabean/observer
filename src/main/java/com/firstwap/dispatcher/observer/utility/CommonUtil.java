package com.firstwap.dispatcher.observer.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstwap.dispatcher.observer.domain.Application;

/**
 *
 * @author yakub
 * @email yakub.jobs@gmail.com
 * @createdOn Oct 4, 2016 1:36:53 PM
 *
 */
public class CommonUtil {

	private static final Logger log = LoggerFactory.getLogger(CommonUtil.class);

	/**
	 *
	 * grab last segment of directory example if the directory is :
	 * /x/y/z/file.txt or c:\x\y\z\file.txt the result of this method will be
	 * "z" which is the last child directory
	 *
	 * @param path
	 * @return
	 */
	public static String grabLastSegment(String path) {

		String segments[] = null;
		File file = new File(path);
		String finalResult = "";

		if (file.getPath().contains("\\")) {
			String fixPath = path.replace("\\", "\\\\");
			segments = fixPath.split("\\\\");
		} else {
			segments = file.getPath().split("/");
		}
		int i = segments.length - 1;
		finalResult = path;
		if (i > 1)
			finalResult = !segments[i - 1].equals("")
			&& !segments[i - 1].isEmpty()
			&& !segments[i - 1].equals("bin") ? segments[i - 1]
					: !segments[i - 2].equals("") && !segments[i - 2].isEmpty()
					&& ((i - 2) - i > 0)
					&& !segments[i - 2].equals("bin") ? segments[i - 2]
							: (i - 3) > 0 && !segments[i - 3].equals("bin") ? segments[i - 3]
									: segments[0];
							return finalResult;
	}

	/**
	 *
	 * make sure that the application run folder is exist after starting
	 * application
	 *
	 * @param applications
	 * @return
	 */
	public static List<Application> validateApplications(
			List<Application> applications) {
		StringBuilder builder = new StringBuilder();
		List<Application> validApps = new ArrayList<>();
		for (Application application : applications) {
			File file = new File(application.getStartScript()).getParentFile();
			if (file.exists() && file.isDirectory()) {
				ApplicationContextAccessor.resetErrorMessage();
				validApps.add(application);
			} else {
				ApplicationContextAccessor
				.updateErrorMessage(builder
						.append(application.getName())
						.append(" application file location is not valid, please check it")
						.toString());
				continue;
			}
		}
		return validApps;
	}
}
