/**
 * 
 */

$(document).ready(function() {
	poll();
	getErrorNotification();
});

/**
 * 
 * creating longpolling in order to make the web page looks realtime
 * 
 */
function poll() {
	var childContainers = "";
	var poll_interval = 0;
	$
			.ajax({
				url : "monitoring/process",
				method : "GET",
				success : function(data) {
					$("#connectionLostModal").modal('hide');
					$
							.each(
									data.contents,
									function(k, v) {
										var trueLocation = v.startScript
												.split();
										childContainers = childContainers
												.concat("<tr>");
										childContainers = childContainers
												.concat("<td>" + v.user
														+ "</td>");
										childContainers = childContainers
												.concat("<td>" + v.pid
														+ "</td>");
										childContainers = childContainers
												.concat("<td>" + v.name
														+ "</td>");
										childContainers = childContainers
												.concat("<td>" + trueLocation
														+ "</td>");
										if (v.status === 'running') {
											childContainers = childContainers
													.concat("<td style=\"color:green;\"> "
															+ v.status
															+ "</td>");
										} else {
											childContainers = childContainers
													.concat("<td style=\"color:red;\"> "
															+ v.status
															+ "</td>");
										}
										childContainers = childContainers
												.concat("<td>" + v.memoryUsage
														+ "</td>");
										childContainers = childContainers
												.concat("<td>" + v.cpuUsage
														+ "</td>");
										childContainers = childContainers
												.concat("<td>"
														+ v.automaticStatus
														+ "</td>");
										childContainers = childContainers
												.concat("<td><button id='data' onClick=\"showProcessWindow('"
														+ v.name
														+ "')\" type=\"button\" class=\"btn btn-default btn-sm\">View</button> &nbsp;");
										childContainers = childContainers
												.concat("<button id='data' onClick=\"showUpdateWindow('"
														+ v.name
														+ "','"
														+ v.startScript
														+ "','"
														+ v.status
														+ "','"
														+ v.stopScript
														+ "','"
														+ v.pidFile
														+ "','"
														+ v.priority
														+ "','"
														+ v.automatic
														+ "')\" type=\"button\" class=\"btn btn-primary btn-sm\">Update</button> &nbsp;");
										childContainers = childContainers
												.concat("<button id='data' onClick=\"showDeleteWindow('"
														+ v.name
														+ "','"
														+ v.startScript
														+ "','"
														+ v.status
														+ "','"
														+ v.stopScript
														+ "','"
														+ v.pidFile
														+ "','"
														+ v.priority
														+ "','"
														+ v.automatic
														+ "')\" type=\"button\" class=\"btn btn-danger btn-sm\">Delete</button> </td>");
										childContainers = childContainers
												.concat("</tr>");
									});
					$("#table_body").html(childContainers);
				},
				error : function(jqXHR, exception) {
					// $("#connection_div").show();
					$("#connectionLostModal").modal();
				},
				dataType : "json",
				complete : poll,
				timeout : 60000
			});
}

function showProcessWindow(name) {
	var childContainer = '<table class="table table-bordered">';
	var footerContainer = '';
	var location = '';
	var id = '';
	var stopScript = '';
	$
			.ajax({
				url : "monitoring/process",
				method : "GET",
				dataType : 'json',
				success : function(data) {
					$.each(data.contents, function(k, v) {
						var trueLocation = v.startScript.split();
						if (v.name === name) {
							childContainer = childContainer.concat("<tr>");
							childContainer = childContainer
									.concat("<th>User</th>");
							childContainer = childContainer.concat("<td>"
									+ v.user + "</td>");
							childContainer = childContainer.concat("</tr>");
							childContainer = childContainer.concat("<tr>");
							childContainer = childContainer
									.concat("<th>Process Id</th>");
							childContainer = childContainer.concat("<td>"
									+ v.pid + "</td>");
							childContainer = childContainer.concat("</tr>");
							childContainer = childContainer.concat("<tr>");
							childContainer = childContainer
									.concat("<th>Name </th>");
							childContainer = childContainer.concat("<td>"
									+ v.name + "</td>");
							childContainer = childContainer.concat("</tr>");
							childContainer = childContainer.concat("<tr>");
							childContainer = childContainer
									.concat("<th>Start Script </th>");
							childContainer = childContainer
									.concat("<td><label>" + trueLocation
											+ "</label></td>");
							childContainer = childContainer.concat("</tr>");
							childContainer = childContainer

							childContainer = childContainer
									.concat("<th>Stop Script </th>");
							childContainer = childContainer.concat("<td>"
									+ v.stopScript + "</td>");
							childContainer = childContainer.concat("</tr>");
							childContainer = childContainer

							childContainer = childContainer
									.concat("<th>Pid File </th>");
							childContainer = childContainer.concat("<td>"
									+ v.pidFile + "</td>");
							childContainer = childContainer.concat("</tr>");
							childContainer = childContainer

							childContainer = childContainer
									.concat("<th>Priority </th>");
							childContainer = childContainer.concat("<td>"
									+ v.priority + "</td>");
							childContainer = childContainer.concat("</tr>");
							childContainer = childContainer

							.concat("<tr>");
							childContainer = childContainer
									.concat("<th>CPU</th>");
							childContainer = childContainer.concat("<td>"
									+ v.cpuUsage + "</td>");
							childContainer = childContainer.concat("</tr>");
							childContainer = childContainer.concat("<tr>");
							childContainer = childContainer
									.concat("<th>Memory</th>");
							childContainer = childContainer.concat("<td>"
									+ v.memoryUsage + "</td>");
							childContainer = childContainer.concat("</tr>");
							childContainer = childContainer.concat("<tr>");
							childContainer = childContainer
									.concat("<th>Status</th>");
							if (v.status === 'running') {
								childContainer = childContainer
										.concat("<td style=\"color:green;\">"
												+ v.status + "</td>");
							} else {
								childContainer = childContainer
										.concat("<td style=\"color:red;\">"
												+ v.status + "</td>");
							}
							childContainer = childContainer.concat("</tr>");
							childContainer = childContainer.concat("<tr>");
							childContainer = childContainer
									.concat("<th>Restart Action</th>");
							childContainer = childContainer.concat("<td>"
									+ v.automaticStatus + "</td>");
							childContainer = childContainer.concat("</tr>");
							location = trueLocation;
							id = v.pid;
							stopScript = v.stopScript;
						}

					});
					childContainer = childContainer.concat("</table>");

					if (id !== '') {
						footerContainer = footerContainer
								.concat("<button onClick=\"killProcess('"
										+ id
										+ "','"
										+ stopScript
										+ "')\" type=\"button\" class=\"btn btn-default\" id=\"killProcessButton\">Kill</button>");
					}

					footerContainer = footerContainer
							.concat("<button onClick=\"startProcess('"
									+ location
									+ "')\" type=\"button\" class=\"btn btn-default\" id=\"startProcessButton\">Start</button>");
					footerContainer = footerContainer
							.concat("<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>");

					$("#modal_footer").html(footerContainer);
					$("#modal_body").html(childContainer);
					$("#viewWindow").modal();
				},
				error : function(data) {
					console.log(data.description);
				}
			})

}

function killProcess(pid, stopScript) {
	var deleteConfirm = confirm("Are you sure want to kill this process ?");
	if (deleteConfirm == true) {
		document.getElementById("killProcessButton").disabled = true;
		var object = {
			"pid" : pid,
			"stopScript" : stopScript
		}
		$.ajax({
			url : "monitoring/process",
			method : "DELETE",
			data : object,
			success : function(data) {
				window.alert(data.description);
			},
			error : function(data) {
				var obj = data.responseJson;
			}
		}).done(function(data) {
			document.getElementById("killProcessButton").disabled = false;
			$("#viewWindow").modal("hide");
		});
	}

}

function startProcess(location) {
	document.getElementById("startProcessButton").disabled = true;
	var object = {
		"location" : location
	}
	$.ajax({
		url : "monitoring/process",
		method : "POST",
		data : object,
		success : function(data) {
			window.alert(data.description);
		},
		error : function(data) {
			var obj = data.responseJson;
		}
	}).done(function(data) {
		document.getElementById("startProcessButton").disabled = false;
		$("#viewWindow").modal("hide");
	});
}

function addNewProcess() {
	var name = document.getElementById("process_name").value = "";
	var location = document.getElementById("process_location").value = "";
	var automatic = document.getElementById("automatic_restart").checked = false;
	var stopScript = document.getElementById("stop_process_location").value = "";
	var pidFile = document.getElementById("pid_location").value = "";
	var priority = document.getElementById("priority_").value = "";

	$("#viewAddWindow").modal();

	document.getElementById("process_name").readOnly = false;
	document.getElementById("modal_title").innerHTML = "Add New Process ...";
	document.getElementById("modal_action").innerHTML = "Save";
	document.getElementById("modal_action").setAttribute("onClick",
			"javascript: addNewProcessRecord();");
}

function addNewProcessRecord() {
	var name = document.getElementById("process_name").value;
	var location = document.getElementById("process_location").value;
	var stopScript = document.getElementById("stop_process_location").value;
	var pidFile = document.getElementById("pid_location").value;
	var priority = document.getElementById("priority_").value;
	var automatic = document.getElementById("automatic_restart").checked;

	var object = {
		"name" : name,
		"startScript" : location,
		"stopScript" : stopScript,
		"pidFile" : pidFile,
		"priority" : priority,
		"automatic" : automatic
	}

	$.ajax({
		url : "monitor/registry",
		method : "POST",
		dataType : "JSON",
		contentType : "application/json",
		data : JSON.stringify(object),
		success : function(data) {
			window.alert(data.description);
		},
		error : function(data) {
			var obj = data.responseJson;
			console.log(data);
		}
	}).done(function(data) {
		if (data.code != -1) {
			$("#viewAddWindow").modal('hide');
			document.getElementById("process_name").value = "";
			document.getElementById("process_location").value = "";
			document.getElementById("stop_process_location").value = "";
			document.getElementById("pid_location").value = "";
			document.getElementById("priority_").value = "";
		}
	});

}

function startAllProcess() {
	document.getElementById("startAllButton").disabled = true;
	$.ajax({
		url : "monitoring/process/all",
		method : "POST",
		success : function(data) {
			window.alert(data.description);
		},
		error : function(data) {
			var obj = data.responseJson;
		}
	}).done(function(data) {
		document.getElementById("startAllButton").disabled = false;
	});
}

function killAllProcess() {
	var deleteConfirm = confirm("Are you sure wanto to kill all process ?");
	if (deleteConfirm == true) {
		document.getElementById("killAllButton").disabled = true;
		$.ajax({
			url : "monitoring/process/all",
			method : "DELETE",
			success : function(data) {
				window.alert(data.description);
			},
			error : function(data) {
				var obj = data.responseJson;
			}
		}).done(function(data) {
			document.getElementById("killAllButton").disabled = false;
		});
	}
}

function showUpdateWindow(name, location, status, stopScript, pidLocation,
		priority, automatic) {
	document.getElementById("modal_title").innerHTML = "Update Process ...";

	$("#viewAddWindow").modal();

	document.getElementById("process_name").value = name;
	document.getElementById("process_name").readOnly = true;
	document.getElementById("process_location").value = location;
	document.getElementById("stop_process_location").value = stopScript;
	document.getElementById("pid_location").value = pidLocation;
	document.getElementById("priority_").value = priority;

	// console.log("status {} ", automatic);

	if (automatic === "true") {
		document.getElementById("automatic_restart").checked = true;
	} else {
		document.getElementById("automatic_restart").checked = false;
	}

	document.getElementById("modal_action").innerHTML = "update";
	document.getElementById("modal_action").setAttribute("onClick",
			"javascript: updateProcess();");

}

function updateProcess() {
	var name = document.getElementById("process_name").value;
	var location = document.getElementById("process_location").value;
	var automatic = document.getElementById("automatic_restart").checked;
	var stopScript = document.getElementById("stop_process_location").value;
	var pidFile = document.getElementById("pid_location").value;
	var priority = document.getElementById("priority_").value;

	var object = {
		"name" : name,
		"startScript" : location,
		"stopScript" : stopScript,
		"pidFile" : pidFile,
		"priority" : priority,
		"automatic" : automatic
	}

	$.ajax({
		url : "monitor/registry",
		method : "PUT",
		dataType : "JSON",
		contentType : "application/json",
		data : JSON.stringify(object),
		success : function(data) {
			window.alert(data.description);
		},
		error : function(data) {
			var obj = data.responseJson;
		}
	}).done(function(data) {
		document.getElementById("process_name").value = "";
		document.getElementById("process_location").value = "";
		document.getElementById("stop_process_location").value = "";
		document.getElementById("pid_location").value = "";
		document.getElementById("priority_").value = "";
		$("#viewAddWindow").modal('hide');
	});

}

function deleteProcessDetail() {
	var name = document.getElementById("process_name").value;
	var location = document.getElementById("process_location").value;
	var automatic = document.getElementById("automatic_restart").checked;
	var stopScript = document.getElementById("stop_process_location").value;
	var pidFile = document.getElementById("pid_location").value;
	var priority = document.getElementById("priority_").value;

	var object = {
		"name" : name,
		"startScript" : location,
		"stopScript" : stopScript,
		"pidFile" : pidFile,
		"priority" : priority,
		"automatic" : automatic
	}

	$.ajax({
		url : "monitor/registry",
		method : "DELETE",
		dataType : "JSON",
		contentType : "application/json",
		data : JSON.stringify(object),
		success : function(data) {
			window.alert(data.description);
		},
		error : function(data) {
			var obj = data.responseJson;
		}
	}).done(function(data) {
		document.getElementById("process_name").value = "";
		document.getElementById("process_location").value = "";
		document.getElementById("stop_process_location").value = "";
		document.getElementById("pid_location").value = "";
		document.getElementById("priority_").value = "";
		$("#viewAddWindow").modal('hide');
	});
}

function deleteProcess() {
	var deleteConfirm = confirm("Are you sure to delete this data ?");
	if (deleteConfirm == true) {
		deleteProcessDetail();
	}
}

function showDeleteWindow(name, location, status, stopScript, pidLocation,
		priority, automatic) {
	document.getElementById("modal_title").innerHTML = "Delete Process ...";

	$("#viewAddWindow").modal();

	document.getElementById("process_name").value = name;
	location
	document.getElementById("process_name").readOnly = true;
	document.getElementById("process_location").value = location;
	document.getElementById("stop_process_location").value = stopScript;
	document.getElementById("pid_location").value = pidLocation;
	document.getElementById("priority_").value = priority;

	if (automatic === "true") {
		document.getElementById("automatic_restart").checked = true;
	} else {
		document.getElementById("automatic_restart").checked = false;
	}

	document.getElementById("modal_action").innerHTML = "delete";
	document.getElementById("modal_action").setAttribute("onClick",
			"javascript: deleteProcess();");

}



function getErrorNotification() {
	$.ajax({
		url : "notification/error",
		method : "GET",
		success : function(data) {
			//console.log(data);
			toastr.error(data.description, 'Observer Error !')
		},
		error : function(data) {
			var obj = data.responseJson;
		},
		complete : getErrorNotification,
		timeout : 60000
	}).done(function(data) {
		
	});
}