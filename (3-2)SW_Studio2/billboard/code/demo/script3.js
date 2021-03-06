var t_chart = bb.generate ({
	bindto: "#T_Chart",
	data: {
		columns: [
			['20대', 55.2, 43.3, 42.9, 24.2, 37.9, 65.2],
			['30대', 64.3, 53.2, 51.3, 31, 41.8, 72.5],
			['40대', 76.3, 66, 66.3, 47.9, 52.6, 78.7],
			['50대', 83.7, 74.8, 76.6, 60.3, 62.4, 89.9],
			['60대 이상', 78.7, 71.5, 76.3, 65.5, 68.6, 78.8]
		]
	},
	
	axis: {
    x: {
      type: "category",
      "categories": [
        "16대 대선(2002)",
        "17대 총선(2004)",
        "17대 대선(2007)",
        "18대 총선(2008)",
        "19대 총선(2012)",
        "18대 대선(2012)"
      ]
    }
  },
	
	func: function(chart) {
		chart.timer = [
			setTimeout(function () {
				chart.load({
					columns: [
						['data3', 400, 500, 450, 700, 600, 500]
					]
				});
			}, 1000)
		];
	},
});	
				