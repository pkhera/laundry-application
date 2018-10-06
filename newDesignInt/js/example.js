$(document).ready(function() {
	var exampleBarChartData = {
		"datasets": {
			"values": [45,55,60,50,70,90,85,70],
			"labels": [
				"Apples", 
				"Oranges", 
				"Berries", 
				"Peaches", 
				"Bananas",
				"Orange",
				"Mango",
				"Others"
			],
			"color": "blue"
		},
		"title": "Statistics",
		"noY": true,
		"height": "300px",
		"width": "700px",
		"background": "#FFFFFF",
		"shadowDepth": "1"
	};
	MaterialCharts.bar("#bar-chart-example", exampleBarChartData)
});