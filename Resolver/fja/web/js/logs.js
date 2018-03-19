logs = [];
filters = [];
now = new Date();
dateTo = new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours() + 1);
dateFrom = new Date(dateTo.getTime() - 14 * 24 * 60 * 60 * 1000);

setInterval(timerMethod, 60 * 1000);
function timerMethod() {
	refreshLogs();
}

$(document).ready(function() {

  $("input[name=levels]").each(function() {
		
		setFilter($(this).val(), $(this).is(':checked'));

		$(this).click(function() {
			setFilter($(this).val(), $(this).is(':checked'));
			refreshTable();
		});
  });

	$('#slider').slider({
		min: dateFrom.getTime(),
		max: dateTo.getTime(),
		step: 60 * 60 * 1000,
		range: true,
		values: [dateFrom.getTime(), dateTo.getTime()],
		slide: function(event, ui) {
			dateFrom.setTime(ui.values[0]);
			dateTo.setTime(ui.values[1]);
			$('#dateFrom').text(toDateString(dateFrom));
			$('#dateTo').text(toDateString(dateTo));
		},
		change: function(event, ui) {
			refreshTable();
		}
	});
	$('#dateFrom').text(toDateString(dateFrom));
	$('#dateTo').text(toDateString(dateTo));

	refreshLogs();
});

function setFilter(name, selected) {
		if (selected) {
			filters.push(name);
		}
		else {
			filters.splice($.inArray(name, filters), 1 );
		}
}

function toDateString(date) {
	var d = date.toISOString().slice(0, 10);
	var t = date.toTimeString().slice(0, 5);
	return d + " " + t;
}

function refreshLogs() {
	$.ajax({
		url: 'utils',
		data: 'request=logs_json',
		success: function(result) {
			logs = $.parseJSON(result);
			refreshTable();
		}
	});
}

function refreshTable() {
	clearTable();
	fillTable();
}

function clearTable() {
	$("#data table").empty();
}

function fillTable() {	
	$.each(logs, function() {
		
		var time = Date.parse(this.date);
		var date = new Date(time - 2 * 60 * 60 * 1000); 
		if ($.inArray(this.level, filters) > -1 && date > dateFrom && date < dateTo) {

			// create table row
			tableRow = $("<tr>")
				.append($("<td>").append(this.date.replace('T', ' ')))
				.append($("<td>").append(this.level))
				.append($("<td>").append(this.logger))
				.append($("<td>").append(this.message));

			// if exception stack is available, show it on click
			if (this.stack) {
				tableRow.children().eq(3)
					.addClass("exception")
					.attr("data-stack", this.stack)
					.click(function() {
						alert($(this).attr("data-stack"));
				});
			}

			$("#data table").prepend(tableRow);
		}
	});
}
