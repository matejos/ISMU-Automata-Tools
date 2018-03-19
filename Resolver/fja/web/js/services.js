$(document).ready(function() {
	refreshData();
});

function set(name, allowed) {
	$.ajax({
		url: 'utils',
		data: 'request=set&service=' + name + '&allowed=' + allowed
	});
}

function refreshData() {

	// transformations
	$.ajax({
		url: 'utils',
		data: 'request=transformations_json',
		success: function(result) {
			settings = $.parseJSON(result);
			fill("transformations", settings);
		}
	});

	// conversions
	$.ajax({
		url: 'utils',
		data: 'request=conversions_json',
		success: function(result) {
			settings = $.parseJSON(result);
			fill("conversions", settings);
		}
	});
}


function fill(divId, data) {
	var div = $("#" + divId);
	div.empty();

	$.each(data, function() {

		var input = $("<input>")
			.attr("type", "checkbox")
			.attr("value", this.name)
			.attr("id", this.name)
			.click(function() {
				set($(this).attr("value"), $(this).is(':checked'));
			});

		if(this.allowed == "true") {
			input.attr("checked", "checked");
		}

		var label = $("<label>")
			.attr("for", this.name)
			.append($("<strong>").append(this.name))
			.append(" - " + this.description);

		div.append(input)
			.append(label)
			.append($("<br>"));
	});
}
