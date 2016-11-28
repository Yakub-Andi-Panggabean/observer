var updateInterval = 1000;
var cpuUsage = [];
var memoryUsge = [];
var totalPoints = 100;
var now = new Date().getTime();


/**
 * 
 * get the usage of cpu
 * 
 */
function GetCpuUsage() {
	cpuUsage.shift();

	while (cpuUsage.length < totalPoints) {
		var y = Math.random() * 100;
		var temp = [ now += updateInterval, y ];
		cpuUsage.push(temp);
	}
}

/**
 * 
 * get the usage of memory
 * 
 */
function GetMemoryUsage() {
	memoryUsge.shift();

	while (memoryUsge.length < totalPoints) {
		var y = Math.random() * 100;
		var temp = [ now += updateInterval, y ];
		memoryUsge.push(temp);
	}
}

var options = {
	series : {
		 stack: true,
		lines : {
			show : true,
			lineWidth : 1.2,
			fill : true
		}
	},
	grid : {
		backgroundColor: "#fff",
		tickColor : "#fff"
	},
	yaxis : {
		ticks : 10
	},
	xaxes : [
			{
				mode : "time",
				tickSize : [ 2, "second" ],
				tickFormatter : function(v, axis) {
					var date = new Date(v);

					if (date.getSeconds() % 20 == 0) {
						var hours = date.getHours() < 10 ? "0"
								+ date.getHours() : date.getHours();
						var minutes = date.getMinutes() < 10 ? "0"
								+ date.getMinutes() : date.getMinutes();
						var seconds = date.getSeconds() < 10 ? "0"
								+ date.getSeconds() : date.getSeconds();

						return hours + ":" + minutes + ":" + seconds;
					} else {
						return "";
					}
				},
				axisLabelUseCanvas : true,
				axisLabelFontSizePixels : 12,
				axisLabelFontFamily : 'Verdana, Arial',
				axisLabelPadding : 10
			}, {
				show : false,
				mode : "time",
				tickSize : [ 2, "second" ]
			} ]
};

$(document).ready(function() {
	GetCpuUsage();
	GetMemoryUsage();

	var dataset = [ {
		label : "Cpu",
		data : cpuUsage,
		xaxis : 1
	}, {
		label : "Memory",
		data : memoryUsge,
		xaxis : 2
	} ];

	$.plot($("#flotcontainer"), dataset, options);

	function updateData() {
		GetCpuUsage();
		GetMemoryUsage();

		$.plot($("#flotcontainer"), dataset, options);
		setTimeout(updateData, updateInterval);
	}

	updateData();
});