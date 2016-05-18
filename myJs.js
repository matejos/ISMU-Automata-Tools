var svgns = "http://www.w3.org/2000/svg";
var movingElement = 0;
var transitionPrevName;
var circleSize = 25;
var fadeTime = 100;
var maxfont = 24;
var minCellW = 50;
var minx = 100;
var miny = 100;
var dist = 6;
var modeEnum = Object.freeze({
    ADD_STATE: 1,
    ADD_TRANSITION: 2,
    SELECT: 3
});

function init(id, type) {
    var wp = document.getElementById(id);
	wp.realtype = type;
	switch (type) 
	{
		case "NFA":
		case "EFA":
			wp.type = "NFA";
			break;
		case "TOT":
		case "MIN":
		case "CAN":
		case "TOC":
		case "MIC":
			wp.type = "DFA";
			break;
		default:
			wp.type = type;
	}
	
	if (wp.type == "NFA" || wp.type == "DFA")
	{
		var graph = document.createElement("div");
		graph.setAttribute("id", id + "a");
		graph.setAttribute("class", "tab-pane fade in active");
		graph.wp = wp;
		
		var tableTab = document.createElement("div");
		tableTab.setAttribute("id", id + "b");
		tableTab.setAttribute("class", "tab-pane fade");
		tableTab.wp = wp;
		wp.tableTab = tableTab;
		
		var textTab = document.createElement("div");
		textTab.setAttribute("id", id + "c");
		textTab.setAttribute("class", "tab-pane fade");
		textTab.wp = wp;
		wp.textTab = textTab;

		var buttonAddStates = document.createElement("input");
		buttonAddStates.type = "button";
		buttonAddStates.value = "Přidávat stavy";

		var buttonAddTransitions = document.createElement("input");
		buttonAddTransitions.type = "button";
		buttonAddTransitions.value = "Přidávat přechody";

		var buttonInitState = document.createElement("input");
		buttonInitState.type = "button";
		buttonInitState.value = "Počátečný stav";
		
		var buttonEndState = document.createElement("input");
		buttonEndState.type = "button";
		buttonEndState.value = "Koncový stav";
		
		var additionalControls = document.createElement("a");
		additionalControls.href = "#";
		additionalControls.innerHTML = "Další ovládací prvky";
		
		var buttonRenameState = document.createElement("input");
		buttonRenameState.type = "button";
		buttonRenameState.value = "Přejmenovat stav";
		
		var buttonRenameTransition = document.createElement("input");
		buttonRenameTransition.type = "button";
		buttonRenameTransition.value = "Změnit znaky přechodu";
		
		var buttonDeleteSelected = document.createElement("input");
		buttonDeleteSelected.type = "button";
		buttonDeleteSelected.value = "Smaž vybrané";
		

		graph.appendChild(buttonAddStates);
		graph.appendChild(buttonAddTransitions);
		graph.appendChild(buttonInitState);
		graph.appendChild(buttonEndState);
		graph.appendChild(document.createElement("div"));
		graph.appendChild(additionalControls);
		graph.appendChild(document.createElement("div"));
		graph.appendChild(buttonRenameState);
		graph.appendChild(buttonRenameTransition);
		graph.appendChild(buttonDeleteSelected);

		var p1 = document.createElement("p");
		graph.appendChild(p1);


		var mydiv = document.createElement("DIV");
		mydiv.setAttribute("style", "background-color: pink;");
		mydiv.setAttribute("class", "canvas");
		mydiv.setAttributeNS(null, "id", "mydiv");
		$(mydiv).resizable({minHeight: 400, minWidth: 400});
		graph.appendChild(mydiv);
		
		
		var svg = document.createElementNS(svgns, 'svg');
		svg.setAttribute('width', '100%');
		svg.setAttribute('height', '100%');
		svg.selectedElement = 0;
		svg.makingTransition = 0;
		svg.parentSvg = svg;
		svg.setAttributeNS(null, "onmousemove", "moveElement(evt)");
		svg.setAttributeNS(null, "onmouseleave", "stopMovingElement();");
		svg.div = mydiv;
		svg.divId = id;
		wp.svg = svg;
		svg.wp = wp;

		mydiv.appendChild(svg);
		
		var rect = document.createElementNS(svgns, "rect");
		rect.setAttribute("fill", "#eeeeee");
		rect.setAttribute("width", '100%');
		rect.setAttribute("height", '100%');
		rect.parentSvg = svg;
		rect.states = [];
		rect.initState = null;
		rect.mode = modeEnum.SELECT;
		rect.setAttributeNS(null, "onmousedown", "rectClick(evt,this)");
		rect.setAttributeNS(null, "onmouseup", "stopMovingElement();");
		rect.buttonAddStates = buttonAddStates;
		rect.buttonAddTransitions = buttonAddTransitions;
		rect.buttonInitState = buttonInitState;
		rect.buttonEndState = buttonEndState;
		rect.buttonRenameState = buttonRenameState;
		rect.buttonRenameTransition = buttonRenameTransition;
		rect.buttonDeleteSelected = buttonDeleteSelected;
		$(rect).dblclick(rectDblClick);
		svg.rect = rect;
		svg.appendChild(rect);
		
		
		var defs = document.createElementNS(svgns, 'defs');
		var marker = document.createElementNS(svgns, 'marker');
		marker.setAttribute('id', 'Triangle');
		marker.setAttribute('viewBox', '0 0 10 10');
		//marker.setAttribute('refX', '22');
		marker.setAttribute('refY', '5');
		marker.setAttribute('markerUnits', 'strokeWidth');
		marker.setAttribute('markerWidth', '6');
		marker.setAttribute('markerHeight', '6');
		marker.setAttribute('orient', 'auto');
		var markerpath = document.createElementNS(svgns, 'path');
		markerpath.setAttribute('d', 'M 0 0 L 10 5 L 0 10 z');
		markerpath.setAttribute('fill', 'black');
		marker.appendChild(markerpath);
		defs.appendChild(marker);
		
		var marker = document.createElementNS(svgns, 'marker');
		marker.setAttribute('id', 'TriangleSel');
		marker.setAttribute('viewBox', '0 0 10 10');
		marker.setAttribute('refY', '5');
		marker.setAttribute('markerUnits', 'strokeWidth');
		marker.setAttribute('markerWidth', '6');
		marker.setAttribute('markerHeight', '6');
		marker.setAttribute('orient', 'auto');
		var markerpath = document.createElementNS(svgns, 'path');
		markerpath.setAttribute('d', 'M 0 0 L 10 5 L 0 10 z');
		markerpath.setAttribute('fill', 'green');
		marker.appendChild(markerpath);
		defs.appendChild(marker);
		
		var marker = document.createElementNS(svgns, 'marker');
		marker.setAttribute('id', 'TriangleInit');
		marker.setAttribute('viewBox', '0 0 10 10');
		marker.setAttribute('refX', '22');
		marker.setAttribute('refY', '5');
		marker.setAttribute('markerUnits', 'strokeWidth');
		marker.setAttribute('markerWidth', '6');
		marker.setAttribute('markerHeight', '6');
		marker.setAttribute('orient', 'auto');
		var markerpath = document.createElementNS(svgns, 'path');
		markerpath.setAttribute('d', 'M 0 0 L 10 5 L 0 10 z');
		markerpath.setAttribute('fill', 'black');
		marker.appendChild(markerpath);
		defs.appendChild(marker);
		
		svg.appendChild(defs);
		

		buttonAddStates.rect = rect;
		buttonAddTransitions.rect = rect;
		buttonInitState.rect = rect;
		buttonEndState.rect = rect;
		buttonRenameState.rect = rect;
		buttonRenameTransition.rect = rect;
		buttonDeleteSelected.rect = rect;
		additionalControls.rect = rect;
		
		buttonAddStates.setAttributeNS(null, "onclick", 'buttonAddStatesClick(rect);');
		buttonAddTransitions.setAttributeNS(null, "onclick", 'buttonAddTransitionsClick(rect);');
		buttonInitState.setAttributeNS(null, "onclick", 'buttonInitStateClick(rect);');
		buttonEndState.setAttributeNS(null, "onclick", 'buttonEndStateClick(rect);');
		buttonRenameTransition.setAttributeNS(null, "onclick", 'buttonRenameTransitionClick(rect);');
		buttonRenameState.setAttributeNS(null, "onclick", 'buttonRenameStateClick(rect);');
		buttonDeleteSelected.setAttributeNS(null, "onclick", 'buttonDeleteSelectedClick(rect);');
		$(additionalControls).click(additionalControlsClick);
		
		buttonAddStates.style.borderStyle = "outset";
		buttonAddTransitions.style.borderStyle = "outset";
		buttonInitState.style.borderStyle = "outset";
		buttonEndState.style.borderStyle = "outset";
		buttonRenameState.style.borderStyle = "outset";
		buttonRenameTransition.style.borderStyle = "outset";
		buttonDeleteSelected.style.borderStyle = "outset";
		additionalControls.shown = true;
		$(additionalControls).trigger("click");
		
		wp.appendChild(graph);
		
		// TABLE TAB
		
		initTableTab(wp);
		
		
		// TEXT TAB
		wp.appendChild(textTab);
		initTextTab(wp);
		
		$('a[data-target="#' + id + 'a"]').on('shown.bs.tab', function (e) {
			updateGraphTab(wp, e.relatedTarget);
		});
		$('a[data-target="#' + id + 'b"]').on('shown.bs.tab', function (e) {
			updateTableTab(wp, e.relatedTarget);
		});
		$('a[data-target="#' + id + 'c"]').on('shown.bs.tab', function (e) {
			updateTextTab(wp);
		});
		
		$( "form" ).submit(function (e) {
			updateTextTab(wp);
		});
		
		$('a[data-target="#' + id + 'a"]').on('hide.bs.tab', function (e) {
			wp.svg.div.lastWidth = wp.svg.div.offsetWidth;
			wp.svg.div.lastHeight = wp.svg.div.offsetHeight;
		});
		
		$('a[data-target="#' + id + 'a"]').on('hidden.bs.tab', function (e) {
			deselectElement(svg);
			for (i = 0; i < rect.states.length; i++)
			{
				if (!rect.states[i].init && !rect.states[i].end && rect.states[i].lines1.length == 0 && rect.states[i].lines2.length == 0)
				{
					deleteState(rect.states[i]);
					i--;
				}
			}
		});
		deselectElement(svg);
	}
}

function initGraphTab(wp)
{
	console.log("init graph tab");
	var initState = createStateAbs(wp.svg.rect, minx, miny);
	toggleInitState(initState);
}

function initTableTab(wp) {
	var tableButtonInit = document.createElement("input");
	tableButtonInit.type = "button";
	tableButtonInit.value = "Počátečný stav";
	tableButtonInit.tableTab = wp.tableTab;
	tableButtonInit.setAttributeNS(null, "onclick", 'tableButtonInitClick(tableTab);');
	tableButtonInit.style.borderStyle = "outset";
	wp.tableTab.appendChild(tableButtonInit);
	wp.tableTab.buttonInit = tableButtonInit;
	
	var tableButtonEnd = document.createElement("input");
	tableButtonEnd.type = "button";
	tableButtonEnd.value = "Koncový stav";
	tableButtonEnd.tableTab = wp.tableTab;
	tableButtonEnd.setAttributeNS(null, "onclick", 'tableButtonEndClick(tableTab);');
	tableButtonEnd.style.borderStyle = "outset";
	wp.tableTab.appendChild(tableButtonEnd);
	wp.tableTab.buttonEnd = tableButtonEnd;
	
	if (wp.realtype == "EFA")
	{
		var tableButtonEpsilon = document.createElement("input");
		tableButtonEpsilon.type = "button";
		tableButtonEpsilon.value = "Přidat epsilon";
		tableButtonEpsilon.tableTab = wp.tableTab;
		$(tableButtonEpsilon).click(tableButtonEpsilonClick);
		tableButtonEpsilon.style.borderStyle = "outset";
		wp.tableTab.appendChild(tableButtonEpsilon);
		wp.tableTab.buttonEpsilon = tableButtonEpsilon;
	}
	
	wp.tableTab.appendChild(document.createElement('p'));
		
	var table = document.createElement("table");
	table.setAttribute("class", "myTable");
	wp.tableTab.table = table;
	table.selectedCell = 0;
	table.wp = wp;
	table.tableTab = wp.tableTab;
	wp.tableTab.appendChild(table);
	
	table.states = [];
	table.symbols = [];
	table.initState = null;
	table.exitStates = [];
	tableDeselectCell(table);
	
	
	wp.tableTab.statusText = document.createElement("p");
	$(wp.tableTab.statusText).addClass("alert alert-danger", fadeTime);
	wp.tableTab.statusText.style.display = "none";
	wp.tableTab.appendChild(wp.tableTab.statusText);

	wp.appendChild(wp.tableTab);
	
	$('a[data-target="#' + wp.svg.divId + 'b"]').on('hidden.bs.tab', function (e) {
			tableButtonInit.style.borderStyle = "outset";
			tableButtonEnd.style.borderStyle = "outset";
			wp.tableTab.statusText.style.display = "none";
		});
}

function initTextTab(wp) {
	wp.textTab.textArea = vysledkovePole(wp.svg.divId, "_e_a_1");
	// only for testing in local html
	if(!wp.textTab.textArea)
	{
		wp.textTab.textArea = document.createElement('textarea');
		wp.textTab.appendChild(wp.textTab.textArea);
		initGraphTab(wp);
	}
	else
	{
		wp.textTab.appendChild(wp.textTab.textArea.parentElement.parentElement);
		
		if (wp.textTab.textArea.value != "")
		{
			var textval = wp.textTab.textArea.value
			console.log("generating from text " + textval);
			wp.textTab.textArea.value = "";
			updateTableTabFromText(wp, true);
			wp.textTab.textArea.value = textval;
			updateTableTabFromText(wp);
			updateGraphTab(wp);
		}
		else
		{
			initGraphTab(wp);
		}
	}
}

function invalidStatePosition(state)
{
	console.log("testing " + state.name + " " + state.getAttribute("cx") + " " + state.getAttribute("cy"));
	for (var i = 0; i < state.parentRect.states.length; i++) 
	{
		if (state.parentRect.states[i] == state)
			continue;
		if ((Math.abs(state.parentRect.states[i].getAttribute("cx") - state.getAttribute("cx")) < circleSize * 2)
			&& (Math.abs(state.parentRect.states[i].getAttribute("cy") - state.getAttribute("cy")) < circleSize * 2))
		return true;
	}
	return false;
}

function updateGraphTab(wp, target)
{
	if (target)
	{
		var t = target.getAttribute("data-target");
		var x = t.substr(t.length - 1, 1);
		if (x == "c")
			updateTableTabFromText(wp);
	}
	
	for (i = 0; i < wp.svg.rect.states.length; i++)
	{
		adjustStateWidth(wp.svg.rect.states[i]);
		for (j = 0; j < wp.svg.rect.states[i].lines1.length; j++)
		{
			adjustTransitionWidth(wp.svg.rect.states[i].lines1[j]);
		}
	}
	
	var states = wp.svg.rect.states;
	var x = minx;
	var y = miny;
	var w = 2 * minx + (Math.ceil(Math.sqrt(states.length)) - 1) * (circleSize * dist);
	if (wp.svg.div.offsetWidth < w)
		wp.svg.div.style.width = w;
	console.log("w is " + w);
	for (var v = 0; v < states.length; v++) 
	{
		if (states[v].isNew)
		{
			states[v].setAttribute("cx", x);
			states[v].setAttribute("cy", y);
			
			while (invalidStatePosition(states[v]))
			{
				console.log("testing " + x + " " + y + "is invalid!");
				x += circleSize * dist;
				if (x > wp.svg.div.offsetWidth)
				{
					x = minx;
					y += circleSize * dist;
				}
				if (y + circleSize + miny > wp.svg.div.offsetHeight)
				{
					console.log("height more");
					wp.svg.div.style.height = y + circleSize + miny;
				}
				states[v].setAttribute("cx", x);
				states[v].setAttribute("cy", y);
				console.log(x + " " + y);
			}
			
			moveState(states[v]);
			states[v].isNew = false;
			x += circleSize * dist;
			if (x > wp.svg.div.offsetWidth)
			{
				x = minx;
				y += circleSize * dist;
			}
			if (y + circleSize + miny > wp.svg.div.offsetHeight)
				{
					console.log("height more");
					wp.svg.div.style.height = y + circleSize + miny;
				}
		}
	}
	for (var v = 0; v < states.length; v++) 
	{
		for (var e = 0; e < states[v].lines1.length; e++)
			{
				if (states[v].lines1[e].isNew)
				{
					repositionTransition(states[v].lines1[e]);
					states[v].lines1[e].isNew = false;
				}
			}
	}
	
	deselectElement(wp.svg);
}

function updateTableTab(wp, target)
{
	var t = target.getAttribute("data-target");
	var x = t.substr(t.length - 1, 1);
	if (x == "a")
		updateTableTabFromGraph(wp);
	else
		updateTableTabFromText(wp);
}

function updateTableTabFromGraph(wp)
{
	updateTextTab(wp);
	updateTableTabFromText(wp);
}

function updateTableTabFromText(wp, pure)	// not finished
{
	// saving old data
	var oldTable = wp.tableTab.table;
	
	/*
	// clearing previous table
	var l = table.rows.length;
	for (i = 0; i < l; i++)
	{
		table.deleteRow(0);
	}*/
	
	var table = document.createElement("table");
	table.setAttribute("class", "myTable");
	wp.tableTab.table = table;
	table.selectedCell = 0;
	table.wp = wp;
	table.tableTab = wp.tableTab;
	wp.tableTab.appendChild(table);
	
	var s = wp.textTab.textArea.value;
	var str = s.split(" ");
	table.states = [];
	table.symbols = [];
	table.initState = null;
	table.exitStates = [];
	
	
	// header row and topleft corner cell
	var row = table.insertRow(table.rows.length);
	var cell = row.insertCell(0);
	cell.innerHTML = "";
	cell.setAttribute("class", "myCell noselect tc");
	var div = document.createElement("div");
	div.setAttribute("class", "tc");
	cell.appendChild(div);
	
	var cell = row.insertCell(1);
	cell.innerHTML = "";
	cell.setAttribute("class", "myCell noselect tc");
	var div = document.createElement("div");
	div.setAttribute("class", "tc");
	cell.appendChild(div);
	
	for (i = 0; i < str.length; i++)
	{
		if (/^init=[a-zA-Z0-9]+$/.test(str[i]))
		{
			str[i] = str[i].substring(5, str[i].length);
			console.log("found init state " + str[i]);
			table.initState = str[i];
			if (table.states.indexOf(str[i]) == -1)
				table.states.push(str[i]);
		}
		else if (/^F={[a-zA-Z0-9]+(,[a-zA-Z0-9]+)*}$/.test(str[i]))
		{
			str[i] = str[i].substring(3, str[i].length - 1);
			var exits = str[i].split(",");
			for (j =  0; j < exits.length; j++)
			{
				table.exitStates.push(exits[j]);
				if (table.states.indexOf(exits[j]) == -1)
					table.states.push(exits[j]);
			}
		}
		else
		{
			if ((wp.type == "DFA") && (/^\([a-zA-Z0-9]+,[a-zA-Z0-9]+\)=[a-zA-Z0-9]+$/.test(str[i])))	// DFA
			{
				var state1 = str[i].substring(1, str[i].indexOf(","));
				var symb = str[i].substring(str[i].indexOf(",") + 1, str[i].indexOf(")"));
				var state2 = str[i].substring(str[i].indexOf("=") + 1, str[i].length);
				if (table.states.indexOf(state1) == -1)
				{
					table.states.push(state1);
				}
				if (table.states.indexOf(state2) == -1)
					table.states.push(state2);
				if (table.symbols.indexOf(symb) == -1)
					table.symbols.push(symb);
			}
			else if ( ((wp.type == "NFA") && (/^\([a-zA-Z0-9]+,[a-zA-Z0-9]+\)={[a-zA-Z0-9]+(,[a-zA-Z0-9]+)*}$/.test(str[i])))	// NFA
			|| ((wp.realtype == "EFA") && (/^\([a-zA-Z0-9]+,(([a-zA-Z0-9])+|(\\e))\)={[a-zA-Z0-9]+(,[a-zA-Z0-9]+)*}$/.test(str[i]))) )	// EFA
			{
				var state1 = str[i].substring(1, str[i].indexOf(","));
				var symb = str[i].substring(str[i].indexOf(",") + 1, str[i].indexOf(")"));
				if (symb == "\\e")
					symb = "ε";
				var states2 = str[i].substring(str[i].indexOf("=") + 2, str[i].length - 1).split(",");
				if (table.states.indexOf(state1) == -1)
					table.states.push(state1);
				if (table.symbols.indexOf(symb) == -1)
					table.symbols.push(symb);
				for (j = 0; j < states2.length; j++)
				{
					if (table.states.indexOf(states2[j]) == -1)
						table.states.push(states2[j]);
				}
			}
		}
	}
	
	// header row
	var row2 = table.insertRow(table.rows.length);
	var cell = row2.insertCell(0);
	cell.innerHTML = "";
	cell.setAttribute("class", "myCell noselect tc");
	var div = document.createElement("div");
	div.setAttribute("class", "tc");
	cell.appendChild(div);
	
	var cell = row2.insertCell(1);
	cell.innerHTML = "";
	cell.setAttribute("class", "myCell noselect tc");
	cell.myTable = table;
	$(cell).resizable({
		handles: 'e',
		resize: function() 
		{
			if (parseInt(this.style.width) >= minCellW) 
			{
				this.style.minWidth = this.style.width;
				var ci = this.cellIndex;
				console.log(ci);
				for (var i = 2; i < this.myTable.rows.length - 1; i++)
					this.myTable.rows[i].cells[ci].myDiv.style.width = this.style.width;
			}
		},
	});
	cell.style.minWidth = minCellW;
	var div = document.createElement("div");
	div.setAttribute("class", "tc");
	cell.appendChild(div);
	
	// filling out columns' headers from symbols and delete buttons above them
	table.symbols.sort();
	for (i = 0; i < table.symbols.length; i++)
	{
		tableAddColumnDeleteButton(row, table);
		tableAddColumnHeader(row2, table.symbols[i]);
	}
	
	// column add button
	tableAddColumnAddButton(row, table);
	
	// filling out rows' headers from states
	table.states.sort();
	for (i = 0; i < table.states.length; i++)
	{
		var state = table.states[i];
		console.log(state);
		var row = table.insertRow(table.rows.length);
		
		tableAddRowDeleteButton(row, table);
		
		var headerval = "";
		if (table.initState == state)
		{
			if (table.exitStates.indexOf(state) != -1)
				headerval += '↔';
			else
				headerval += '→';
		}
		else if (table.exitStates.indexOf(state) != -1)
			headerval += '←';
		
		headerval += state;
		tableAddRowHeader(row, headerval);

		for (j = 0; j < table.symbols.length; j++)
		{
			tableAddCell(row);
		}
	}
	
	// row add button
	tableAddRowAddButton(table);
	// filling transitions
	for (i = 0; i < str.length; i++)
	{
		if ((wp.type == "DFA") && (/^\([a-zA-Z0-9]+,[a-zA-Z0-9]+\)=[a-zA-Z0-9]+$/.test(str[i])))	// DFA
		{
			var state1 = str[i].substring(1, str[i].indexOf(","));
			var symb = str[i].substring(str[i].indexOf(",") + 1, str[i].indexOf(")"));
			var state2 = str[i].substring(str[i].indexOf("=") + 1, str[i].length);
			var cell = table.rows[table.states.indexOf(state1) + 2].cells[table.symbols.indexOf(symb) + 2];
			cell.myDiv.value = cell.myDiv.value.substring(0, cell.myDiv.value.length - 1);
			if (cell.myDiv.value.length > 1)
				cell.myDiv.value += ',';
			cell.myDiv.value += state2;
			cell.myDiv.prevValue = cell.myDiv.value;
		}
		else if ( ((wp.type == "NFA") && (/^\([a-zA-Z0-9]+,[a-zA-Z0-9]+\)={[a-zA-Z0-9]+(,[a-zA-Z0-9]+)*}$/.test(str[i])))	// NFA
			|| ((wp.realtype == "EFA") && (/^\([a-zA-Z0-9]+,(([a-zA-Z0-9])+|(\\e))\)={[a-zA-Z0-9]+(,[a-zA-Z0-9]+)*}$/.test(str[i]))) )	// EFA
		{
			var state1 = str[i].substring(1, str[i].indexOf(","));
			var symb = str[i].substring(str[i].indexOf(",") + 1, str[i].indexOf(")"));
			var states2 = str[i].substring(str[i].indexOf("=") + 2, str[i].length - 1).split(",");
			if (symb == "\\e")
					symb = "ε";
			var cell = table.rows[table.states.indexOf(state1) + 2].cells[table.symbols.indexOf(symb) + 2];
			cell.myDiv.value = cell.myDiv.value.substring(0, cell.myDiv.value.length - 1);
			for (j = 0; j < states2.length; j++)
			{
				if (cell.myDiv.value.length > 1)
					cell.myDiv.value += ',';
				cell.myDiv.value += states2[j];
			}
			cell.myDiv.value += '}';
			console.log("added tr " + state1 + " through " + symb + " to " + cell.myDiv.value);
			cell.myDiv.prevValue = cell.myDiv.value;
		}
	}
	
	// sort cells' values
	if (wp.type == "NFA")
	{
		for (i = 2; i < table.rows.length - 1; i++)
		{
			for (j = 2; j < table.rows[i].cells.length; j++)
			{
				var val = table.rows[i].cells[j].myDiv.value;
				val = val.replace(/{|}/g, "");
				var vals = val.split(",");
				vals.sort();
				var q = vals.indexOf(state);
				table.rows[i].cells[j].myDiv.value = "{" + vals.toString() + "}";
				table.rows[i].cells[j].myDiv.prevValue = table.rows[i].cells[j].myDiv.value;
			}
		}
	}
	
	// disable epsilon button if there is an epsilon column
	if (wp.tableTab.buttonEpsilon)
	{
		wp.tableTab.buttonEpsilon.disabled = false;
		for (var i = 2; i < table.rows[1].cells.length; i++)
		{
			if (table.rows[1].cells[i].myDiv.value == 'ε')
			{
				wp.tableTab.buttonEpsilon.disabled = true;
				break;
			}
		}
	}
	
	if (!pure)
	{
		var oldStates = [];
		var oldSymbols = [];
		
		for (var i = 2; i < oldTable.rows[1].cells.length; i++)
		{
			var symbol = oldTable.rows[1].cells[i].myDiv.value;
			oldSymbols.push(symbol);
			if (table.symbols.indexOf(symbol) == -1)
				tableDeleteColumn(oldTable, i);
		}
		
		for (var i = 2; i < oldTable.rows.length - 1; i++)
		{
			var state = removePrefixFromState(oldTable.rows[i].cells[1].myDiv.value);
			oldStates.push(state);
			var foundState = false;
			for (var j = 2; j < table.rows.length - 1; j++)
			{
				if (removePrefixFromState(table.rows[j].cells[1].myDiv.value) == state)
				{
					foundState = true;
					for (var k = 2; k < oldTable.rows[i].cells.length; k++)
					{
						var symbol = oldTable.rows[1].cells[k].myDiv.value;
						for (var l = 2; l < table.rows[j].cells.length; l++)
						{
							if (table.rows[1].cells[l].myDiv.value == symbol)
							{
								console.log("OLD: " + oldTable.rows[i].cells[k].myDiv.value + "NEW: " + table.rows[j].cells[l].myDiv.value);
								table.rows[j].cells[l].myDiv.prevValue = oldTable.rows[i].cells[k].myDiv.value;
								$(table.rows[j].cells[l].myDiv).trigger("input");
								$(table.rows[j].cells[l].myDiv).trigger("focusout");
							}
						}
					}
				}
			}
			if (!foundState)
			{
				deleteState(findState(table.wp.svg.rect, removePrefixFromState(state)));
			}
		}
		for (var j = 2; j < table.rows.length - 1; j++)
		{
			var nullify = false;
			var state = removePrefixFromState(table.rows[j].cells[1].myDiv.value);
			if (oldStates.indexOf(state) == -1)
			{
				if (!findState(table.wp.svg.rect, state))
				{
					createStateAbs(table.wp.svg.rect, -2 * circleSize, -2 * circleSize, state);
				}
				nullify = true;
			}
			for (var l = 2; l < table.rows[j].cells.length; l++)
			{
				var symbol = table.rows[1].cells[l].myDiv.value;
				if (!nullify && oldSymbols.indexOf(symbol) == -1)
					nullify = true;
				if (nullify)
					table.rows[j].cells[l].myDiv.prevValue = "{}";
				$(table.rows[j].cells[l].myDiv).trigger("input");
				$(table.rows[j].cells[l].myDiv).trigger("focusout");
			}
		}
		
		toggleInitStateOff(findState(table.wp.svg.rect, oldTable.initState));
		toggleInitStateOn(findState(table.wp.svg.rect, table.initState));
		for (var i = 0; i < oldTable.exitStates.length; i++)
			toggleEndStateOff(findState(table.wp.svg.rect, oldTable.exitStates[i]));
		for (var i = 0; i < table.exitStates.length; i++)
			toggleEndStateOn(findState(table.wp.svg.rect, table.exitStates[i]));
	}
	wp.tableTab.removeChild(oldTable);
	wp.tableTab.removeChild(wp.tableTab.statusText);
	wp.tableTab.appendChild(wp.tableTab.statusText);
}
function tableDeselectCell(table)
{
	if (table.selectedCell != 0)
	{
		var div = table.selectedCell;
		$(div).switchClass(div.defaultClass + "s", div.defaultClass, fadeTime);
		table.selectedCell = 0;
	}
	table.wp.tableTab.buttonInit.disabled = true;
	table.wp.tableTab.buttonEnd.style.borderStyle = "outset";
	table.wp.tableTab.buttonEnd.disabled = true;
}
function tableEditCellClick(evt)
{
	var cell = evt.target;
	var table = cell.parentElement.parentElement.parentElement.parentElement;
	if (!table.locked)
	{
		tableDeselectCell(table);
	}
}

function tableCellClick(evt)
{
	var cell = evt.target;
	console.log(cell);
	var table = cell.parentElement.parentElement.parentElement.parentElement;
	if (!table.locked && table.selectedCell != cell)
	{
		var div = table.selectedCell;
		if (table.selectedCell != 0)
		{
			$(div).switchClass(div.defaultClass + "s", div.defaultClass, fadeTime);
		}
		$(cell).switchClass(cell.defaultClass, cell.defaultClass + "s", fadeTime);
		
		table.wp.tableTab.buttonInit.disabled = false;
		table.wp.tableTab.buttonEnd.disabled = false;
		
		var name = cell.prevValue;
		if (name[0] == '↔')
		{
			table.wp.tableTab.buttonInit.disabled = true;
			table.wp.tableTab.buttonEnd.style.borderStyle = "inset";
		}
		else if (name[0] == '←')
		{
			table.wp.tableTab.buttonInit.disabled = false;
			table.wp.tableTab.buttonEnd.style.borderStyle = "inset";
		}
		else if (name[0] == '→')
		{
			table.wp.tableTab.buttonInit.disabled = true;
			table.wp.tableTab.buttonEnd.style.borderStyle = "outset";
		}
		else
		{
			table.wp.tableTab.buttonInit.disabled = false;
			table.wp.tableTab.buttonEnd.style.borderStyle = "outset";
		}
		
		table.selectedCell = cell;
	}
}

function tableDeleteRow(table, index)
{
	if (!table.locked)
	{
		tableDeselectCell(table);
		// Delete state in graph
		var state = table.rows[index].cells[1].myDiv.value;
		state = removePrefixFromState(state);
		table.states.splice(table.states.indexOf(state), 1);
		deleteState(findState(table.wp.svg.rect, state));
		
		// Traverse all transitions cells in table and change the name
		for (i = 2; i < table.rows.length - 1; i++)
		{
			for (j = 2; j < table.rows[i].cells.length; j++)
			{
				var val = table.rows[i].cells[j].myDiv.value;
				if (table.wp.type == "NFA")
				{
					val = val.replace(/{|}/g, "");
					var vals = val.split(",");
					var q = vals.indexOf(state);
					if (q != -1)
					{
						vals.splice(q, 1);
						val = vals.toString();
						table.rows[i].cells[j].myDiv.value = "{" + val + "}";
						table.rows[i].cells[j].myDiv.prevValue = table.rows[i].cells[j].myDiv.value;
					}
				}
				else
				{
					if (state == val)
					{
						table.rows[i].cells[j].myDiv.value = "-";
						table.rows[i].cells[j].myDiv.prevValue = table.rows[i].cells[j].myDiv.value;
					}
				}	
			}
		}
		
		// Delete table row
		table.deleteRow(index);
	}
}

function tableDeleteColumn(table, index)
{
	if (!table.locked)
	{
		tableDeselectCell(table);
		var symbol = table.rows[1].cells[index].myDiv.value;
		table.symbols.splice(table.symbols.indexOf(symbol), 1);
		
		// Delete transitions of this symbol in graph
		for (i = 0; i < table.wp.svg.rect.states.length; i++)
		{
			for (j = table.wp.svg.rect.states[i].lines1.length - 1; j >= 0 ; j--)
			{
				var tr = table.wp.svg.rect.states[i].lines1[j].name;
				if (tr == symbol)
					deleteTransition(table.wp.svg.rect.states[i].lines1[j]);
				else
				{
					var trs = tr.split(",");
					var q = trs.indexOf(symbol);
					if (q != -1)
					{
						trs.splice(q, 1);
						tr = trs.toString();
						renameTransition(table.wp.svg.rect.states[i].lines1[j], tr);
					}
				}
			}
		}
		
		// Delete table column
		for (i = 0; i < table.rows.length - 1; i++)
		{
			table.rows[i].deleteCell(index);
		}
		
		if (table.wp.realtype == "EFA" && symbol == 'ε')
		{
			table.tableTab.buttonEpsilon.disabled = false;
		}
	}
}

function tableAddRowAddButton(table)
{
	var row = table.insertRow(table.rows.length);
	var cell = row.insertCell(0);
	
	var div = document.createElement("div");
	cell.setAttribute("class", "addButton noselect");
	div.innerHTML = "+";
	cell.table = table;
	cell.setAttribute("onclick", "tableAddRow(table);");
	
	cell.appendChild(div);
}

function tableAddRowDeleteButton(row, table)
{
	var cell = row.insertCell(0);
	
	var div = document.createElement("div");
	cell.setAttribute("class", "deleteButton noselect");
	div.innerHTML = "×";
	cell.table = table;
	cell.setAttribute("onclick", "tableDeleteRow(table, this.parentNode.rowIndex);");
	
	cell.appendChild(div);
}

function tableAddRowHeader(row, value)
{
	var cell = row.insertCell(row.cells.length);
	var table = cell.parentElement.parentElement.parentElement;
	var div = document.createElement("input");
	div.value = value;
	div.prevValue = value;
	div.style.width = table.rows[1].cells[cell.cellIndex].style.minWidth;
	div.defaultClass = "rh";
	cell.setAttribute("class", "myCell");
	div.setAttribute("class", "myCellDiv " + div.defaultClass);
	$(div).click(tableCellClick);

	$(div).on('input',tableRhChanged);
	$(div).focusout(tableRhChangedFinal);

	$(div).keypress(function (e) {
		var code = e.keyCode || e.which;
		if(code == 13)
			return false;
		var kc = e.charCode;
		if (kc == 0)
			return true;
		var txt = String.fromCharCode(kc);
		if (!txt.match(stateSyntax())) {
			return false;
		}
	});
	
	cell.myDiv = div;
	div.myCell = cell;
	cell.appendChild(div);
}

function tableStateExists(div, state)
{
	var table = div.parentElement.parentElement.parentElement.parentElement;
	var ri = div.parentElement.parentElement.rowIndex;
	for (var i = 2; i < table.rows.length - 1; i++)
	{
		if (i == ri)
			continue;
		var val = table.rows[i].cells[1].myDiv.value;
		val = removePrefixFromState(val);
		if (val == state)
			return table.rows[i].cells[1].myDiv;
	}
	return -1;
}

function tableSymbolExists(div, symbol)
{
	var table = div.parentElement.parentElement.parentElement.parentElement;
	var ci = div.parentElement.cellIndex;
	for (var i = 2; i < table.rows[1].cells.length; i++)
	{
		if (i == ci)
			continue;
		var val = table.rows[1].cells[i].myDiv.value;
		if (val == symbol)
			return table.rows[1].cells[i].myDiv;
	}
	return -1;
}

function lockTable(table, exc)
{
	console.log("locking table");
	for (var i = 1; i < table.rows.length - 1; i++)
	{
		for (var j = 1; j < table.rows[i].cells.length; j++)
		{
			if (table.rows[i].cells[j].myDiv == exc)
				continue;
			$(table.rows[i].cells[j].myDiv).prop('readonly', true);
		}
	}
	table.locked = true;
}

function unlockTable(table)
{
	console.log("unlocking table");
	for (var i = 1; i < table.rows.length - 1; i++)
	{
		for (var j = 1; j < table.rows[i].cells.length; j++)
		{
			$(table.rows[i].cells[j].myDiv).prop('readonly', false);
		}
	}
	table.locked = false;
}

function tableRhChanged()
{
	var state = this.value;
	var table = this.parentElement.parentElement.parentElement.parentElement;
	var ri = this.parentElement.parentElement.rowIndex;
	state = removePrefixFromState(state);
	if (incorrectStateSyntax(state))
	{
		$(this).addClass("incorrect", fadeTime);
		table.tableTab.statusText.innerHTML = "<strong>Chyba!</strong> Nevyhovující syntax názvu stavu (řetězec znaků z {a-z,A-Z,0-9}). Tabulka je uzamčena dokud nebude chyba opravena.";
		table.tableTab.statusText.style.display = "";
		lockTable(table, this);
	}
	else if (tableStateExists(this, state) != -1)
	{
		$(this).addClass("incorrect", fadeTime);
		table.tableTab.statusText.innerHTML = "<strong>Chyba!</strong> Duplicitní název stavu není povolen. Tabulka je uzamčena dokud nebude chyba opravena.";
		table.tableTab.statusText.style.display = "";
		lockTable(table, this);
	}
	else
	{
		$(this).removeClass("incorrect", fadeTime);
		if (table.locked)
		{
			unlockTable(table);
			table.tableTab.statusText.innerHTML = "";
			table.tableTab.statusText.style.display = "none";
		}
		if (this.value[0] == '→' || this.value[0] == '↔')
		{
			for (i = 2; i < table.rows.length - 1; i++)
			{
				if (i == ri)
					continue;
				if (table.rows[i].cells[1].myDiv.prevValue[0] == '→' || table.rows[i].cells[1].myDiv.prevValue[0] == '↔')
				{
					toggleInitStateOff(findState(table.wp.svg.rect, removePrefixFromState(table.rows[i].cells[1].myDiv.prevValue)));
					table.rows[i].cells[1].myDiv.value = "";
					if (table.rows[i].cells[1].myDiv.prevValue[0] == '↔')
						table.rows[i].cells[1].myDiv.value = "←";
					table.rows[i].cells[1].myDiv.value += table.rows[i].cells[1].myDiv.prevValue.substring(1, table.rows[i].cells[1].myDiv.prevValue.length);
					table.rows[i].cells[1].myDiv.prevValue = table.rows[i].cells[1].myDiv.value;
				}
			}
		}
		if (this.value[0] == '↔')
		{
			table.wp.tableTab.buttonInit.disabled = true;
			table.wp.tableTab.buttonEnd.style.borderStyle = "inset";
		}
		else if (this.value[0] == '←')
		{
			table.wp.tableTab.buttonInit.disabled = false;
			table.wp.tableTab.buttonEnd.style.borderStyle = "inset";
		}
		else if (this.value[0] == '→')
		{
			table.wp.tableTab.buttonInit.disabled = true;
			table.wp.tableTab.buttonEnd.style.borderStyle = "outset";
		}
		else
		{
			table.wp.tableTab.buttonInit.disabled = false;
			table.wp.tableTab.buttonEnd.style.borderStyle = "outset";
		}
	}
	
}

function tableRhChangedFinal()
{
	var table = this.parentElement.parentElement.parentElement.parentElement;
	if ($(this).hasClass("incorrect") == false && !table.locked)
	{
		var div = this;
		var prevName = div.prevValue;
		prevName = removePrefixFromState(prevName);
		var newName = div.value;
		
		table.states.splice(table.states.indexOf(prevName), 1);
		table.states.push(removePrefixFromState(newName));
		
		// Rename the state in graph
		
		var state = findState(table.wp.svg.rect, prevName);
		console.log(state);
		if (newName[0] == '↔')
		{
			toggleInitStateOn(state);
			toggleEndStateOn(state);
		}
		else if (newName[0] == '←')
		{
			toggleInitStateOff(state);
			toggleEndStateOn(state);
		}
		else if (newName[0] == '→')
		{
			toggleInitStateOn(state);
			toggleEndStateOff(state);
		}
		else
		{
			toggleInitStateOff(state);
			toggleEndStateOff(state);
		}
					
		newName = removePrefixFromState(newName);
		
		console.log(prevName + "   " + newName);
		// Rename state in graph
		for (i = 0; i < table.wp.svg.rect.states.length; i++)
		{
			if (table.wp.svg.rect.states[i].name == prevName)
			{
				renameState(table.wp.svg.rect.states[i], newName);
				break;
			}
		}
		
		// Traverse all transitions cells in table and change the name
		
		for (i = 2; i < table.rows.length - 1; i++)
		{
			for (j = 2; j < table.rows[i].cells.length; j++)
			{
				var val = table.rows[i].cells[j].myDiv.value;
				val = val.replace(/{|}/g, "");
				var vals = val.split(",");
				var index = vals.indexOf(prevName);
				if (index != -1)
				{
					vals[index] = newName;
					val = vals.toString();
					if (table.wp.type == "NFA")
						table.rows[i].cells[j].myDiv.value = "{" + val + "}";
					else
						table.rows[i].cells[j].myDiv.value = val;
					table.rows[i].cells[j].myDiv.prevValue = table.rows[i].cells[j].myDiv.value;
				}
			}
		}
		
		div.prevValue = div.value;
	}
}

function tableChChanged()
{
	var symbol = this.value;
	var table = this.parentElement.parentElement.parentElement.parentElement;
	var ci = this.parentElement.cellIndex;
	
	if ( (table.wp.realtype == "EFA" && incorrectEFATransitionSyntax(this.value)) ||
		(table.wp.realtype != "EFA" && (incorrectDFATransitionSyntax(this.value) || this.value == "\\e")) )
		{
		$(this).addClass("incorrect", fadeTime);
		if (table.wp.realtype == "EFA")
			table.tableTab.statusText.innerHTML = "<strong>Chyba!</strong> Nevyhovující syntax symbolu přechodu (řetězec znaků z {a-z,A-Z,0-9} nebo ε). Tabulka je uzamčena dokud nebude chyba opravena.";
		else
			table.tableTab.statusText.innerHTML = "<strong>Chyba!</strong> Nevyhovující syntax symbolu přechodu (řetězec znaků z {a-z,A-Z,0-9}). Tabulka je uzamčena dokud nebude chyba opravena.";
		table.tableTab.statusText.style.display = "";
		lockTable(table, this);
		}
	else if (tableSymbolExists(this, symbol) != -1)
	{
		$(this).addClass("incorrect", fadeTime);
		table.tableTab.statusText.innerHTML = "<strong>Chyba!</strong> Duplicitní název symbolu přechodu není povolen. Tabulka je uzamčena dokud nebude chyba opravena.";
		table.tableTab.statusText.style.display = "";
		lockTable(table, this);
	}
	else
	{
		$(this).removeClass("incorrect", fadeTime);
		if (table.locked)
		{
			table.tableTab.statusText.innerHTML = "";
			table.tableTab.statusText.style.display = "none";
			unlockTable(table);
		}
	}
	console.log("CH: " + this.value);
}

function tableChChangedFinal()
{
	var table = this.parentElement.parentElement.parentElement.parentElement;
	if ($(this).hasClass("incorrect") == false && !table.locked)
	{
		var div = this;
		var prevName = div.prevValue;
		var newName = div.value;
		table.symbols.splice(table.symbols.indexOf(prevName), 1);
		table.symbols.push(newName);
		if (prevName != newName)
		{
			console.log("CH changed, correct");
			// Rename the symbol in graph
			for (i = 0; i < table.wp.svg.rect.states.length; i++)
			{
				for (j = 0; j < table.wp.svg.rect.states[i].lines1.length; j++)
				{
					var val = table.wp.svg.rect.states[i].lines1[j].name;
					var vals = val.split(",");
					var index = vals.indexOf(prevName);
					if (index != -1)
					{
						if (vals.indexOf(newName) == -1)
							vals[index] = newName;
						else
							vals.splice(index, 1);
						renameTransition(table.wp.svg.rect.states[i].lines1[j], vals.toString());
					}
				}
			}
			
			if (table.wp.realtype == "EFA")
			{
				if (newName == 'ε')
					table.tableTab.buttonEpsilon.disabled = true;
				else if (prevName == 'ε')
					table.tableTab.buttonEpsilon.disabled = false;
			}
			
			div.prevValue = div.value;
		}
	}
}

function tableCellChanged()
{
	var table = this.parentElement.parentElement.parentElement.parentElement;
	if (! (table.wp.type == "NFA" && incorrectTableNFATransitionsSyntax(this.value)) ||
		(table.wp.type == "DFA" && incorrectTableDFATransitionsSyntax(this.value)) )
	{
		$(this).removeClass("incorrect", fadeTime);
		if (table.locked)
		{
			table.tableTab.statusText.innerHTML = "";
			table.tableTab.statusText.style.display = "none";
			unlockTable(table);
		}
	}
}

function tableCellChangedFinal()
{
	var table = this.parentElement.parentElement.parentElement.parentElement;
	if ( (table.wp.type == "NFA" && incorrectTableNFATransitionsSyntax(this.value)) ||
		(table.wp.type == "DFA" && incorrectTableDFATransitionsSyntax(this.value)) )
		{
		$(this).addClass("incorrect", fadeTime);
		var statusmsg = "<strong>Chyba!</strong> Nevyhovující syntax výsledku přechodové funkce. ";
		if (table.wp.type == "DFA")
		{
			statusmsg += "Očekávaný řetězec znaků z {a-z,A-Z,0-9}. ";
		}
		else if (table.wp.type == "NFA")
		{
			statusmsg += "Očekávané řetězce znaků z {a-z,A-Z,0-9} oddělené čárkami, uzavřeny do složených závorek. "
		}
		statusmsg += "Tabulka je uzamčena dokud nebude chyba opravena."
		table.tableTab.statusText.innerHTML = statusmsg;
		table.tableTab.statusText.style.display = "";
		lockTable(table, this);
		}
	else
	{
		var div = this;
		var prevName = div.prevValue;
		var newName = div.value;
		if (table.wp.type == "NFA")
		{
			prevName = prevName.substring(1, prevName.length - 1);
			newName = newName.substring(1, newName.length - 1);
		}
		else
		{
			if (newName == "")
				newName = "-";
			div.value = newName;
		}
		
		var stateName = table.rows[div.myCell.parentElement.rowIndex].cells[1].myDiv.value;
		stateName = removePrefixFromState(stateName);
		var state = findState(table.wp.svg.rect, stateName);
		var symbol = table.rows[1].cells[div.myCell.cellIndex].myDiv.prevValue;
		
		var prevStates = prevName.split(",");
		var newStates = newName.split(",");
		
		// Delete the transitions in graph
		for (var i = 0; i < prevStates.length; i++)
		{
			if (newStates.indexOf(prevStates[i]) == -1)
			{
				var state2Name = prevStates[i];
				var state2 = findState(table.wp.svg.rect, state2Name);
				for (var j = 0; j < state.lines1.length; j++)
				{
					if (state.lines1[j].end == state2)
					{
						var trs = state.lines1[j].name.split(",");
						if (trs.length <= 1)
						{
							deleteTransition(state.lines1[j]);
						}
						else
						{
							console.log("renaming " + state.name + " from " + state.lines1[j].name);
							trs.splice(trs.indexOf(symbol), 1);
							renameTransition(state.lines1[j], trs.toString());
							console.log("to " + state.lines1[j].name);
						}
						break;
					}
				}
			}
		}
		
		//Add the transitions in graph
		if (newStates.length == 1 && ((newStates[0] == "") || (table.wp.type == "DFA" && newStates[0] == "-")))
		{
			newStates = [];
		}
		for (var i = 0; i < newStates.length; i++)
		{
			if (prevStates.indexOf(newStates[i]) == -1)
			{
				var state2Name = newStates[i];
				console.log("searching for " + state2Name);
				var state2 = findState(table.wp.svg.rect, state2Name);
				if (!state2)
				{
					console.log("adding state " + state2Name + " and transition " + symbol);
					state2 = tableAddRow(table, state2Name);
					createTransition(state, state2, symbol);
				}
				else
				{
					var found = false;
					for (var j = 0; j < state.lines1.length; j++)
					{
						if (state.lines1[j].end == state2)
						{
							var trs = state.lines1[j].name.split(",");
							if (trs.indexOf(symbol) == -1)
							{
								trs.push(symbol);
								renameTransition(state.lines1[j], trs.toString());
							}
							found = true;
							break;
						}
					}
					if (!found)
					{
						createTransition(state, state2, symbol);
					}
				}
			}
		}

		div.prevValue = div.value;
	}
}



function removePrefixFromState(state)
{
	var first = state.charAt(0);
	if (first == '→' || first == '←' || first == '↔')
		state = state.substring(1, state.length);
	return state;
}

function tableAddColumnAddButton(row, table)
{
	var cell = row.insertCell(row.cells.length);
	
	var div = document.createElement("div");
	cell.setAttribute("class", "addButton noselect");
	div.innerHTML = "+";
	cell.table = table;
	cell.setAttribute("onclick", "tableAddColumn(table);");
	
	cell.appendChild(div);
}

function tableAddColumnDeleteButton(row, table)
{
	var cell = row.insertCell(row.cells.length);
	
	var div = document.createElement("div");
	cell.setAttribute("class", "deleteButton noselect");
	div.innerHTML = "×";
	cell.table = table;
	cell.setAttribute("onclick", "tableDeleteColumn(table, this.cellIndex);");
	div.myCell = cell;
	
	cell.appendChild(div);
}

function tableAddColumnHeader(row, value)
{
	var cell = row.insertCell(row.cells.length);
	var table = cell.parentElement.parentElement.parentElement;
	
	var div = document.createElement("input");
	div.value = value;
	div.prevValue = value;
	div.style.width = minCellW;
	cell.setAttribute("class", "myCell ch");
	div.setAttribute("class", "myCellDiv");
	$(div).click(tableEditCellClick);
	
	$(div).on('input',tableChChanged);
	$(div).focusout(tableChChangedFinal);
	cell.myTable = table;
	$(cell).resizable({
		handles: 'e',
		resize: function() 
		{
			if (parseInt(this.style.width) >= minCellW) 
			{
				this.style.minWidth = this.style.width;
				var ci = this.cellIndex;
				console.log(ci);
				for (var i = 1; i < this.myTable.rows.length - 1; i++)
					this.myTable.rows[i].cells[ci].myDiv.style.width = this.style.width;
			}
		},
	});
	cell.style.minWidth = minCellW;
	
	var regex;
	if (table.wp.realtype == "EFA")
		regex = EFATransitionSyntax();
	else
		regex = DFATransitionSyntax();
	$(div).keypress(function (e) {
		var code = e.keyCode || e.which;
		if(code == 13)
			return false;
		var kc = e.charCode;
		if (kc == 0)
			return true;
		var txt = String.fromCharCode(kc);
		if (!txt.match(regex)) {
			return false;
		}
	});
	
	cell.myDiv = div;
	cell.appendChild(div);
}

function tableAddCell(row)
{
	var cell = row.insertCell(row.cells.length);
	var table = cell.parentElement.parentElement.parentElement;
	var div = document.createElement("input");
	if (table.wp.type == "NFA")
		div.value = "{}";
	else
		div.value = "-";
	div.prevValue = div.value;
	div.style.width = table.rows[1].cells[cell.cellIndex].style.minWidth;
	console.log(table.rows[1].cells[cell.cellIndex].style.minWidth);
	$(div).click(tableEditCellClick);
	cell.setAttribute("class", "myCell");
	div.setAttribute("class", "myCellDiv");
	
	$(div).on('input',tableCellChanged);
	$(div).focusout(tableCellChangedFinal);

	
	var regex;
	if (table.wp.type == "NFA")
		regex = /[a-zA-Z0-9{},]/;
	else
		regex = /[a-zA-Z0-9\-]/;
	$(div).keypress(function (e) {
		var code = e.keyCode || e.which;
		if(code == 13)
			return false;
		var kc = e.charCode;
		if (kc == 0)
			return true;
		var txt = String.fromCharCode(kc);
		if (!txt.match(regex)) {
			return false;
		}
	});
	
	div.myCell = cell;
	cell.myDiv = div;
	cell.appendChild(div);
}

function tableAddColumn(table, symb)
{
	if (!table.locked)
	{
		tableDeselectCell(table);
		table.rows[0].deleteCell(table.rows[0].cells.length - 1);
		tableAddColumnDeleteButton(table.rows[0], table);
		tableAddColumnAddButton(table.rows[0], table);
		
		if (!symb)
		{
			var k = 'a'.charCodeAt(0);
			var symbprefix = "";
			do
			{
				if (k > 'z'.charCodeAt(0))
				{
					symbprefix += "a";
					k = 'a'.charCodeAt(0);
				}
				symb = symbprefix + String.fromCharCode(k);
				k++;
			}
			while (table.symbols.indexOf(symb) != -1)
		}
		
		table.symbols.push(symb);
		tableAddColumnHeader(table.rows[1], symb);
		for (i = 2; i < table.rows.length - 1; i++)
		{
			tableAddCell(table.rows[i]);
		}
	}
}

function tableAddRow(table, name)
{
	if (!table.locked)
	{
		if (!name)
		{
			var k = 'A'.charCodeAt(0);
			var nameprefix = "";
			do
			{
				if (k > 'Z'.charCodeAt(0))
				{
					nameprefix += "A";
					k = 'A'.charCodeAt(0);
				}
				name = nameprefix + String.fromCharCode(k);
				k++;
			}
			while (table.states.indexOf(name) != -1)
		}
		
		tableDeselectCell(table);
		console.log("adding row");
		table.rows[table.rows.length - 1].deleteCell(0);
		tableAddRowDeleteButton(table.rows[table.rows.length - 1], table);
		
		table.states.push(name);
		
		tableAddRowHeader(table.rows[table.rows.length - 1], name);
		for (i = 2; i < table.rows[0].cells.length - 1; i++)
		{
			tableAddCell(table.rows[table.rows.length - 1]);
		}
		tableAddRowAddButton(table);
		
		// Add state to graph
		console.log(table.wp.svg.rect);
		return createStateAbs(table.wp.svg.rect, -2 * circleSize, -2 * circleSize, name);
	}
}

function tableButtonInitClick(tableTab) {
	var table = tableTab.table;
	var cell = table.selectedCell;
	if (cell != 0 && cell.myCell.cellIndex == 1)
	{
		var state = cell.value.replace(/←|→|↔/g, '');
		var on;
		if (/[→|↔]/.test(cell.value)) 
		{
			if (/[↔]/.test(cell.value))
				cell.value = '←' + state;
			else
				cell.value = state;
			console.log(state + " is now not an init state");
			on = false;
		}
		else
		{
			if (/[←]/.test(cell.value))
				cell.value = '↔' + state;
			else
				cell.value = '→' + state;
			console.log(state + " is now an init state");
			on = true;
		}
		cell.prevValue = cell.value;
		
		// Edit init state in graph
		if (on)
			toggleInitStateOn(findState(table.wp.svg.rect, state));
		else
			toggleInitStateOff(findState(table.wp.svg.rect, state));
		
		$(cell).trigger("input");
	}
}

function tableButtonEndClick(tableTab) {
	var table = tableTab.table;
	var cell = table.selectedCell;
	if (cell != 0 && cell.myCell.cellIndex == 1)
	{
		var state = cell.value.replace(/←|→|↔/g, '');
		var on;
		if (/[←|↔]/.test(cell.value))
		{
			if (/[↔]/.test(cell.value))
				cell.value = '→' + state;
			else
				cell.value = state;
			console.log(state + " is now not an exit state");
			on = false;
		}
		else
		{
			if (/[→]/.test(cell.value))
				cell.value = '↔' + state;
			else
				cell.value = '←' + state;
			console.log(state + " is now an exit state");
			on = true;
		}
		cell.prevValue = cell.value;
		
		// Edit exit state in graph
		if (on)
			toggleEndStateOn(findState(table.wp.svg.rect, state));
		else
			toggleEndStateOff(findState(table.wp.svg.rect, state));
		
		$(cell).trigger("input");
	}
}

function doGetCaretPosition (oField) {

  // Initialize
  var iCaretPos = 0;

  // IE Support
  if (document.selection) {

    // Set focus on the element
    oField.focus();

    // To get cursor position, get empty selection range
    var oSel = document.selection.createRange();

    // Move selection start to 0 position
    oSel.moveStart('character', -oField.value.length);

    // The caret position is selection length
    iCaretPos = oSel.text.length;
  }

  // Firefox support
  else if (oField.selectionStart || oField.selectionStart == '0')
    iCaretPos = oField.selectionStart;

  // Return results
  return iCaretPos;
}

function tableButtonEpsilonClick()
{
	var table = this.tableTab.table;
	for (var i = 2; i < table.rows[1].cells.length; i++)
	{
		if (table.rows[1].cells[i].myDiv.value == 'ε')
			return;
	}
	this.disabled = true;
	tableAddColumn(table, 'ε');
}

function updateTextTab(wp)
{
	if (!wp.textTab.textArea)
		initTextTab(wp);
	var textArea = wp.textTab.textArea;
	textArea.value = generateAnswer(wp.svg.rect);
	console.log("updated " + wp.svg.divId + "  to " + textArea.value);
	updateTableTabFromText(wp, true);
	textArea.focus();
	textArea.blur();
}

function textButtonEpsilonClick(textTab)
{
	var pos = doGetCaretPosition (textTab.textArea);
	var val = textTab.textArea.value;
	var str = val.substring(0, pos) + 'ε' + val.substring(pos, val.length);
	textTab.textArea.value = str;
}

function buttonAddStatesClick(rect) {
    if (rect.buttonAddStates.style.borderStyle == "inset") {
        rect.buttonAddStates.style.borderStyle = "outset";
        rect.mode = modeEnum.SELECT;
    } else {
        if (rect.buttonAddTransitions.style.borderStyle == "inset") rect.buttonAddTransitions.style.borderStyle = "outset";
        rect.buttonAddStates.style.borderStyle = "inset";
        rect.mode = modeEnum.ADD_STATE;
        if (rect.parentSvg.makingTransition !== 0) {
            rect.parentSvg.selectedElement = rect.parentSvg.makingTransition;
            rect.parentSvg.makingTransition = 0;
        }
        deselectElement(rect.parentSvg);
    }
}

function buttonAddTransitionsClick(rect) {
    if (rect.buttonAddTransitions.style.borderStyle == "inset") {
        rect.buttonAddTransitions.style.borderStyle = "outset";
        rect.mode = modeEnum.SELECT;
        if (rect.parentSvg.makingTransition !== 0) {
            rect.parentSvg.selectedElement = rect.parentSvg.makingTransition;
            rect.parentSvg.makingTransition = 0;
            deselectElement(rect.parentSvg);
        }
    } else {
        if (rect.buttonAddStates.style.borderStyle == "inset") rect.buttonAddStates.style.borderStyle = "outset";
        rect.buttonAddTransitions.style.borderStyle = "inset";
        rect.mode = modeEnum.ADD_TRANSITION;
        deselectElement(rect.parentSvg);
    }
}

function regTextChanged()
{
	checkSyntax(this, this.syntax);
}

function toggleInitStateOn(state)
{
	if (state && !state.init) 
	{
		var x2 = state.getAttribute("cx");
		var x1 = x2 - circleSize * 2.5;
		var y = state.getAttribute("cy");
		var aLine = document.createElementNS(svgns, 'path');
		var att = "M "+x1+" "+y+" L ";
		att += x2+" "+y;
		aLine.setAttribute('d', att);
		aLine.setAttribute('stroke', 'red');
		aLine.setAttribute('stroke-width', 3);
		aLine.setAttribute('fill', 'none');
		aLine.parentSvg = state.parentSvg;
		aLine.setAttribute('marker-end', 'url(#TriangleInit)');
		
		state.parentSvg.appendChild(aLine);
		putOnTop(state);
		state.init = aLine;
		
		state.parentRect.initState = state;
	}
}

function toggleInitStateOff(state)
{
	if (state && state.init) 
	{
	state.parentSvg.removeChild(state.init);
	state.init = null;
	if (state.parentRect.initState == state)
		state.parentRect.initState = null;
	}
}

function toggleInitState(state)
{
	if (state.parentRect.initState)
		toggleInitStateOff(state.parentRect.initState);
	toggleInitStateOn(state);
	state.parentRect.initState = state;
}

function toggleEndStateOn(state)
{
	if (state && !state.end) 
	{
		var shape = document.createElementNS(svgns, "circle");
		shape.setAttributeNS(null, "cx", state.getAttribute("cx"));
		shape.setAttributeNS(null, "cy", state.getAttribute("cy"));
		shape.setAttributeNS(null, "r", circleSize - 5);
		if (state.parentSvg.selectedElement == state)
			shape.setAttributeNS(null, "fill", "lightgreen");
		else
			shape.setAttributeNS(null, "fill", "white");
		shape.setAttributeNS(null, "stroke", "black");
		shape.setAttributeNS(null, "stroke-width", 1);
		shape.parentSvg = state.parentSvg;
		shape.parentRect = state.parentRect;
		shape.setAttribute('pointer-events', 'none');
		state.end = shape;
		putOnTop(state);
		
		state.parentRect.buttonEndState.style.borderStyle = "inset";
	}
}

function toggleEndStateOff(state)
{
	if (state && state.end) 
	{
		state.parentSvg.removeChild(state.end);
		state.end = null;
		
		state.parentRect.buttonEndState.style.borderStyle = "outset";
	}
}

function toggleEndState(state)
{
	if (!state.end) {
		toggleEndStateOn(state)
	} else {
		toggleEndStateOff(state)
	}
}

function buttonInitStateClick(rect) {
    var svg = rect.parentSvg;
    if ((svg.selectedElement !== 0) && (svg.selectedElement.tagName == "circle")) {
		rect.buttonInitState.disabled = true;
		toggleInitState(svg.selectedElement);
    }
}

function buttonEndStateClick(rect) {
    var svg = rect.parentSvg;
    if ((svg.selectedElement !== 0) && (svg.selectedElement.tagName == "circle")) {
		toggleEndState(svg.selectedElement);
    }
}

function additionalControlsClick() {
	if (this.shown)
	{
		this.setAttribute("class", "nedurazne rozbal");
		$(this.rect.buttonRenameState).hide();
		$(this.rect.buttonRenameTransition).hide();
		$(this.rect.buttonDeleteSelected).hide();
	}
	else
	{
		this.setAttribute("class", "nedurazne sbal");
		$(this.rect.buttonRenameState).show();
		$(this.rect.buttonRenameTransition).show();
		$(this.rect.buttonDeleteSelected).show();
	}
	this.shown = !this.shown;
	return false;
}

function buttonRenameStateClick(rect) {
    var svg = rect.parentSvg;
    if ((svg.selectedElement !== 0) && (svg.selectedElement.tagName == "circle")) {
		$(svg.selectedElement).trigger("dblclick");
    }
}

function buttonRenameTransitionClick(rect) {
    var svg = rect.parentSvg;
    if ((svg.selectedElement !== 0) && (svg.selectedElement.tagName == "path")) {
		$(svg.selectedElement.rect).trigger("dblclick");
    }
}

function buttonDeleteSelectedClick(rect) {
	var svg = rect.parentSvg;
	if (svg.selectedElement)
	{
		switch (svg.selectedElement.tagName)
		{
			case "circle":
				deleteState(svg.selectedElement);
				break;
			case "path":
				deleteTransition(svg.selectedElement);
				break;
		}
		$(document).unbind("keydown");
	}
}
function generateAnswer(rect)
{
	var finalStates = [];
    var out = "";
	var type = rect.parentSvg.wp.type;
	if (rect.initState)
		out += "init=" + rect.initState.name + " ";
    for (i = 0; i < rect.states.length; i++)
    {
        if (rect.states[i].end)
            finalStates.push(rect.states[i]);
		if (type == "DFA")
		{
			for (j = 0; j < rect.states[i].lines1.length; j++)
			{
				var str = rect.states[i].lines1[j].name;
				str = str.split(',');
				for (k = 0; k < str.length; k++)
				{
					if (str[k] == "ε")
						str[k] = "\\e";
					out += "(" + rect.states[i].name + "," + str[k] +
						")=" + rect.states[i].lines1[j].end.name + " ";
				}
			}
		}
		else if (type == "NFA")
		{
			var transitions = {};
			for (j = 0; j < rect.states[i].lines1.length; j++)
			{
				var str = rect.states[i].lines1[j].name;
				str = str.split(',');
				for (k = 0; k < str.length; k++)
				{
					if (!transitions[str[k]])
						transitions[str[k]] = [];
					transitions[str[k]].push(rect.states[i].lines1[j].end.name);
				}
			}
			var keys = [];
			for (var key in transitions) 
			{
				if (transitions.hasOwnProperty(key)) 
				{
				keys.push(key);
				}
			}
			for (j = 0; j < keys.length; j++)
			{
				var keyout = keys[j];
				if (keyout == "ε")
					keyout = "\\e";
				out += "(" + rect.states[i].name + "," + keyout +
						")={" + transitions[keys[j]] + "} ";
			}
		}
    }
    if (finalStates.length !== 0)
    {
        out += "F={";
        for (i = 0; i < finalStates.length; i++)
    	{
            out += finalStates[i].name;
            if (i < finalStates.length - 1)
                out += ",";
        }
        out +="}";
	}
	return out;
}

function createStateAbs(rect, x, y, name)
{
	console.log("creating state " + name);
	if (rect.parentSvg.selectedElement !== 0) deselectElement(rect.parentSvg);
	var shape = document.createElementNS(svgns, "circle");
	var width = rect.parentSvg.div.offsetWidth;
	var height = rect.parentSvg.div.offsetHeight;
	if (width == 0)
		width = rect.parentSvg.div.lastWidth;
	if (height == 0)
		height = rect.parentSvg.div.lastHeight;
	if (x < circleSize) x = circleSize;
	if (x > width - circleSize) x = width - circleSize;
	if (y < circleSize) y = circleSize;
	if (y > height - circleSize) y = height - circleSize;
	shape.setAttributeNS(null, "cx", x);
	shape.setAttributeNS(null, "cy", y);
	shape.setAttributeNS(null, "r", circleSize);
	shape.setAttributeNS(null, "fill", "white");
	shape.setAttributeNS(null, "stroke", "black");
	shape.setAttributeNS(null, "stroke-width", 1);
	shape.parentSvg = rect.parentSvg;
	shape.parentRect = rect;
	shape.init = null;
	shape.end = null;
	shape.isNew = true;
	shape.lines1 = [];
	shape.lines2 = [];
	if (!name)
	{
		var names = [];
		for (k = 65; k < 91; k++)
			names.push(String.fromCharCode(k));
		for (k = 0; k < rect.states.length; k++)
			names.splice(names.indexOf(rect.states[k].name), 1);
		name = names[0];
	}
	shape.name = name;
	shape.setAttributeNS(null, "onmousedown", "clickState(evt)");
	shape.setAttributeNS(null, "onmouseup", "stopMovingElement()");
	$(shape).dblclick(stateDblClick);

	var newText = document.createElementNS(svgns, "text");
	newText.setAttributeNS(null, "x", shape.getAttribute("cx"));
	newText.setAttributeNS(null, "y", shape.getAttribute("cy"));
	newText.setAttribute('pointer-events', 'none');
	newText.setAttribute('font-size', maxfont);
	newText.setAttribute('font-family','Arial, Helvetica, sans-serif');
	newText.setAttribute('dy', ".3em");							// vertical alignment
	newText.setAttribute('text-anchor', "middle");				// horizontal alignment
	newText.setAttribute('class', 'noselect');
	var textNode = document.createTextNode(name);
	newText.appendChild(textNode);
	newText.node = textNode;

	shape.text = newText;

	rect.states.push(shape);
	putOnTop(shape);
	if (rect.buttonAddStates.style.borderStyle == "outset")
		rect.mode = modeEnum.SELECT;
	return shape;
}

function createState(evt) 
{
	var rect = evt.target;
	var x = evt.offsetX;
	var y = evt.offsetY;
	var state = createStateAbs(rect, x, y);
	state.isNew = false;
}
function rectDblClick(evt) {
	evt.preventDefault();
	evt.target.buttonAddTransitions.style.borderStyle = "outset";
	createState(evt);
}

function rectClick(evt, rect) {
	if (evt)
		evt.preventDefault();
    switch (rect.mode) {
        case modeEnum.ADD_STATE:
            createState(evt);
            break;
		case modeEnum.ADD_TRANSITION:
			if (rect.buttonAddTransitions.style.borderStyle == "outset")
				rect.mode = modeEnum.SELECT;
			if (rect.parentSvg.makingTransition !== 0) {
				rect.parentSvg.selectedElement = rect.parentSvg.makingTransition;
				rect.parentSvg.makingTransition = 0;
				deselectElement(rect.parentSvg);
			}
			break;
        case modeEnum.SELECT:
            deselectElement(rect.parentSvg);
            break;
    }
}

function putOnTop(state) {
    state.parentSvg.appendChild(state);
    if (state.end) state.parentSvg.appendChild(state.end);
    state.parentSvg.appendChild(state.text);
}
function cubicControlPoints(x, y, d){
	var mult = 100;
	var div = 8;
    var x1 = +x + (Math.cos(d + Math.PI / div) * mult);
	var y1 = +y - (Math.sin(d + Math.PI / div) * mult);
	var x2 = +x + (Math.cos(d - Math.PI / div) * mult);
	var y2 = +y - (Math.sin(d - Math.PI / div) * mult);
    var str = x1 + " " + y1 + " " + x2 + " " + y2;
    return str;
}
function controlPoint(x1, y1, x2, y2){
    var x = ((+x2 + (+x1))/2) + ((+y2 - +y1)/5);
    var y = ((+y2 + (+y1))/2) - ((+x2 - +x1)/5);
    var str = x + " " + y;
    return str;
}
function selectStateForTransition(state)
{
	state.setAttributeNS(null, "fill", "lightblue");
	if (state.end)
		state.end.setAttributeNS(null, "fill", "lightblue");
	state.parentSvg.makingTransition = state;
}
function stateDblClick(evt)
{
	evt.preventDefault();
	var svg = this.parentSvg;
	this.setAttribute("stroke", "green");
	stopMovingElement();
	var name = getValidStateName(this, this.name);
	console.log(name);
	if (name != null)
	{
		renameState(this, name);
	}

	this.setAttribute("stroke", "black");
}

function repositionTransition(line)
{
	var x1 = +line.start.getAttribute("cx");
	var y1 = +line.start.getAttribute("cy");
	var att;
	if (line.start == line.end)
	{
		line.angle = 0;
		att = "M " + x1 + " " + y1 + " C "
			+ cubicControlPoints(x1, y1, line.angle)
			+ " " + x1 +" " + y1;
			
		var atts = att.split(" ");
		var tx = (+atts[4] + +atts[6] + +atts[1]) / 3;
		var ty = (+atts[5] + +atts[7] + +atts[2]) / 3;
	}
	else
	{
		var x2 = +line.end.getAttribute("cx");
		var y2 = +line.end.getAttribute("cy");
		
		var z = Math.max(50, Math.sqrt(sqr(x2-x1) + sqr(y2-y1)) / 2);
		console.log(line.start.name + "->" + line.end.name + " " + z);
		var sqrtt = Math.sqrt( sqr(x2 - x1) + sqr(y2 - y1) );
		if (sqrtt == 0)
			sqrtt = 0.001;
		var angle = Math.acos( (x2 - x1) / sqrtt );
		if (y2 > y1)
			angle = -angle;
		angle += Math.PI/2;
		line.dx = (Math.cos(angle) * z);
		line.dy = (-Math.sin(angle) * z);
		var cpx = ((x1 + x2)/2) + line.dx;
		var cpy = ((y1 + y2)/2) + line.dy;
		att = "M "+x1+" "+y1+" Q "
			+ cpx + " " + cpy
			+ " " + x2 + " " + y2;
		var str = att.split(' ');
		var tx = (+str[4] + (+((+str[1] + (+str[6]))/2)))/2;
		var ty = (+str[5] + (+((+str[2] + (+str[7]))/2)))/2;
	}
	line.setAttribute('d', att);
	line.text.setAttributeNS(null, "x", tx);
	line.text.setAttributeNS(null, "y", ty);
	moveTextRect(line.rect, tx, ty);
	repositionMarker(line);
}

function createTransition(state1, state2, symbols)
{
	var aLine = document.createElementNS(svgns, 'path');
	aLine.setAttribute('stroke', 'black');
	aLine.setAttribute('stroke-width', 3);
	aLine.setAttribute('fill', 'none');
	aLine.setAttributeNS(null, 'onmousedown', 'selectElement(evt)');
	aLine.setAttributeNS(null, 'onmouseup', 'stopMovingElement()');
	aLine.parentSvg = state2.parentSvg;
	aLine.name = symbols;
	aLine.start = state1;
	aLine.end = state2;
	aLine.isNew = true;
	
	var line = document.createElementNS(svgns, 'path');
	line.setAttribute('stroke-width', 3);
	line.setAttribute('pointer-events', 'none');
	line.setAttribute('marker-end', 'url(#Triangle)');
	state2.parentSvg.appendChild(line);
	
	
	aLine.markerline = line;
	
	
	var newText = document.createElementNS(svgns, 'text');
	newText.setAttribute('pointer-events', 'none');
	newText.setAttribute('cursor', 'default');
	newText.setAttribute('font-size', 20);
	newText.setAttribute('font-family','Arial, Helvetica, sans-serif');
	newText.setAttribute('dy', ".3em");							// vertical alignment
	newText.setAttribute('text-anchor', "middle");				// horizontal alignment
	newText.setAttribute('class', 'noselect');
	newText.parentSvg = state2.parentSvg;
	var textNode = document.createTextNode(aLine.name);
	newText.appendChild(textNode);
	newText.node = textNode;
	
	var newRect = document.createElementNS(svgns, 'rect');
	newRect.setAttribute("fill", "#ffffff");
	newRect.setAttribute("width", 30);
	newRect.setAttribute("height", 30);
	newRect.setAttributeNS(null, "stroke", "black");
	newRect.setAttributeNS(null, "stroke-width", 1);
	newRect.parentSvg = state2.parentSvg;
	newRect.setAttributeNS(null, 'onmousedown', 'selectElement(evt)');
	newRect.setAttributeNS(null, 'onmouseup', 'stopMovingElement()');
	newRect.setAttributeNS(null, 'onmousemove', 'prevent(evt)');
	newRect.line = aLine;
	$(newRect).dblclick(transitionDblClick);

	aLine.text = newText;
	aLine.rect = newRect;
	newText.line = aLine;
	
	repositionTransition(aLine);
	
	state2.parentSvg.appendChild(aLine);
	state2.parentSvg.appendChild(newRect);
	state2.parentSvg.appendChild(aLine.text);
	putOnTop(state2);
	putOnTop(state1);
	whitenState(state1);
	state1.lines1.push(aLine);
	state2.lines2.push(aLine);
	if (state2.parentRect.buttonAddTransitions.style.borderStyle == "outset")
		state2.parentRect.mode = modeEnum.SELECT;
	
	state2.parentSvg.makingTransition = 0;
	deselectElement(state2.parentSvg);
	
	adjustTransitionWidth(aLine);
	
	return aLine;
}

function getValidStateName(state, sname)
{
	var promptmsg = "Zadej nový název stavu (řetězec znaků z {a-z,A-Z,0-9}).";
	var name = prompt(promptmsg, sname);
	if (name == null)
		return null;
	do
	{
		incorrect = false;
		if (incorrectStateSyntax(name))
		{
			name = prompt("Chyba: Nevyhovující syntax! " + promptmsg, name);
			incorrect = true;
		}
		for (var i = 0; i < state.parentRect.states.length; i++)
			if (state.parentRect.states[i].name == name && state.parentRect.states[i] != state)
			{
				name = prompt("Chyba: Takto pojmenovaný stav již existuje! " + promptmsg, name);
				incorrect = true;
			}
	}
	while (incorrect && name != null);
	return name;
}

function getValidTransitionName(state1, state2, sname)
{
	var promptmsg = "Zadej symboly přechodu (řetězce znaků z {a-z,A-Z,0-9}";
	if (state1.parentSvg.wp.realtype == "EFA")
	{
		promptmsg += " nebo \\e";
	}
	promptmsg += ") oddělené čárkou.";
	var name = prompt(promptmsg, sname);
	if (name == null)
		return null;
	do
	{
		incorrect = false;
		var names = name.split(",");
		names = names.filter(function(item, pos) {return names.indexOf(item) == pos;});
		names.forEach(function(item, i) { if (item == "\\e") names[i] = "ε"; });
		name = names.toString();
		if (state1.parentSvg.wp.realtype == "EFA")
		{
			if (name == "")
				name = "ε";
		}
		else
		{
			if ((name == "") || (names.indexOf("ε") != -1))
			{
				name = prompt("Chyba: Nelze přidat prázdný přechod! " + promptmsg, name);
				incorrect = true;
			}
		}
		if (!incorrect)
		{
			if (incorrectGraphTransitionsSyntax(name))
			{
				name = prompt("Chyba: Nevyhovující syntax! " + promptmsg, name);
				incorrect = true;
			}
			else
			{
				if (state1.parentSvg.wp.type == "DFA")
				{
					for (var i = 0; i < names.length; i++)
					{
						for (var j = 0; j < state1.lines1.length; j++)
						{
							if (state1.lines1[j].end == state2)
								continue;
							var temp = state1.lines1[j].name.split(",");
							if (temp.indexOf(names[i]) != -1)
							{
								name = prompt("Chyba: Existuje přechod z tohoto stavu pod alespoň jedním z těchto symbolů do jiného stavu, zadání vyžaduje determinizmus. " + promptmsg, name);
								incorrect = true;
								// break
								j = state1.lines1.length;
								i = names.length;
							}
						}
					}
				}
			}
		}
	}
	while (incorrect && name != null);
	return name;
}

function clickState(evt) {
	evt.preventDefault();
    var state = evt.target;
    switch (state.parentRect.mode) {
        case modeEnum.SELECT:
            selectElement(evt);
            break;
        case modeEnum.ADD_TRANSITION:
            if (state.parentSvg.makingTransition !== 0) {
                for (var i = 0; i < state.parentSvg.makingTransition.lines1.length; i++)
                    if (state.parentSvg.makingTransition.lines1[i].end == state)
					{
						if (state.parentRect.buttonAddTransitions.style.borderStyle == "outset")
							state.parentRect.mode = modeEnum.SELECT;
						state.parentSvg.selectedElement = state.parentSvg.makingTransition;
						state.parentSvg.makingTransition = 0;
						deselectElement(state.parentSvg);
						return;
					}
				var name = getValidTransitionName(state.parentSvg.makingTransition, state, "");
				
				if (name != null)
				{
					var line = createTransition(state.parentSvg.makingTransition, state, name);
					line.isNew = false;
				}
				else
					rectClick(null, state.parentRect);
            } else {
                selectStateForTransition(state);
            }
            break;
    }
}

function prevent(evt) {
    evt.preventDefault();
}

function whitenState(state) {
    state.setAttributeNS(null, "fill", "white");
    if (state.end)
        state.end.setAttributeNS(null, "fill", "white");
}

function selectElement(evt) {
	evt.preventDefault();
	var svg = evt.target.parentSvg;
	if (svg.rect.mode != modeEnum.SELECT)
		return;
    deselectElement(svg);
    svg.selectedElement = evt.target;
	svg.makingTransition = 0;
    movingElement = svg.selectedElement;
	svg.rect.buttonDeleteSelected.disabled = false;
	svg.rect.buttonRenameState.disabled = true;
	svg.rect.buttonRenameTransition.disabled = false;
    switch (svg.selectedElement.tagName)
    {
        case "circle":
            svg.selectedElement.setAttributeNS(null, "fill", "lightgreen");
			svg.rect.buttonInitState.disabled = false;
			svg.rect.buttonEndState.disabled = false;
			svg.rect.buttonRenameState.disabled = false;
			svg.rect.buttonRenameTransition.disabled = true;
    		if (svg.selectedElement.end)
			{
                svg.selectedElement.end.setAttributeNS(null, "fill", "lightgreen");
				svg.rect.buttonEndState.style.borderStyle = "inset";
			}
			if (svg.selectedElement == svg.rect.initState)
				svg.rect.buttonInitState.disabled = true;
    		//putOnTop(svg.selectedElement);	// breaks doubleclicking on Chrome
            break;
		case "text":
		case "rect":
			svg.selectedElement = svg.selectedElement.line;
        case "path":
            svg.selectedElement.setAttribute('stroke',"green");
			svg.selectedElement.markerline.setAttribute('marker-end', "url(#TriangleSel)");
            break;
    }
	$(document).unbind("keydown");
	$(document).keydown(function( event ) {
		var key = event.keyCode || event.which || event.charCode;
		if (key == 46)	// delete
		{
			switch (svg.selectedElement.tagName)
			{
				case "circle":
					deleteState(svg.selectedElement);
					break;
				case "path":
					deleteTransition(svg.selectedElement);
					break;
			}
			$(document).unbind("keydown");
		}
	});
}

function findState(rect, state)
{
	for (var i = 0; i < rect.states.length; i++)
	{
		if (rect.states[i].name == state)
			return rect.states[i];
	}
	return null;
}

function deleteState(state)
{
	if (state)
	{
		console.log("deleting state " + state.name);
		var svg = state.parentSvg;
		var target = state.lines1.length;
		
		// Delete transitions FROM and TO this state
		for (i = 0; i < target; i++)
		{
			deleteTransition(state.lines1[0]);
		}
		target = state.lines2.length;
		for (i = 0; i < target; i++)
		{
			deleteTransition(state.lines2[0]);
		}
		
		var index = state.parentRect.states.indexOf(state);
		state.text.removeChild(state.text.node);
		//svg.removeChild(state.text);		// this line causes all transitions on Microsoft Edge to disappear until resize of the window
		if (state.end) svg.removeChild(state.end);
		if (state.init) 
		{
			svg.removeChild(state.init);
			state.parentRect.initState = null;
		}
		svg.removeChild(state);
		state.parentRect.states.splice(index, 1);
		deselectElement(svg);
	}
}

function deleteTransition(tr)
{
	console.log("deleting tr " + tr);
	var svg = tr.parentSvg;
	tr.start.lines1.splice(tr.start.lines1.indexOf(tr), 1);
	tr.end.lines2.splice(tr.end.lines2.indexOf(tr), 1);
	svg.removeChild(tr.markerline);
	svg.removeChild(tr.text);
	svg.removeChild(tr.rect);
	svg.removeChild(tr);
	deselectElement(svg);
}

function adjustStateWidth(state)
{
	var shortened = false;
	var padding = 8;
	var minfont = 12;
	
	state.text.setAttribute('font-size', maxfont);
	while (state.text.getComputedTextLength() > circleSize * 2 - padding && parseInt(state.text.getAttribute('font-size')) > minfont)
	{
		state.text.setAttribute('font-size', parseInt(state.text.getAttribute('font-size')) - 1);
	}
	while (state.text.getComputedTextLength() > circleSize * 2 - padding)
	{
		state.text.node.nodeValue = state.text.node.nodeValue.substring(0, state.text.node.nodeValue.length - 1);
		shortened = true;
	}
	if (shortened)
		state.text.node.nodeValue = state.text.node.nodeValue.substring(0, state.text.node.nodeValue.length - 1) + "..";
	state.text.setAttribute("dy", "0.3em");
}

function adjustTransitionWidth(line)
{
	line.rect.setAttribute("width", line.text.getComputedTextLength() + 8);
	moveTextRect(line.rect, line.text.getAttribute('x'), line.text.getAttribute('y'));
}

function renameTransition(line, str)
{
	console.log("renaming transition '"+line.name+"' to '"+str+"'");
	line.name = str;
	line.text.node.nodeValue = str;
	adjustTransitionWidth(line);
}

function renameState(state, str)
{
	state.name = str;
	state.text.node.nodeValue = str;
	adjustStateWidth(state);
}

function deselectElement(svg) {
	svg.rect.buttonInitState.disabled = true;
	svg.rect.buttonEndState.style.borderStyle = "outset";
	svg.rect.buttonEndState.disabled = true;
	svg.rect.buttonRenameState.disabled = true;
	svg.rect.buttonRenameTransition.disabled = true;
	svg.rect.buttonDeleteSelected.disabled = true;
    if (svg.selectedElement !== 0) {
        switch (svg.selectedElement.tagName)
    	{
        	case "circle":
        		whitenState(svg.selectedElement);
                break;
            case "path":
                svg.selectedElement.setAttribute('stroke',"black");
				svg.selectedElement.markerline.setAttribute('marker-end', "url(#Triangle)");
                break;
            case "text":
                svg.selectedElement.line.setAttribute('stroke',"black");
                break;
        }
    }
	svg.selectedElement = 0;
}

function moveElement(evt) {
	evt.preventDefault();
	var svg = evt.target.parentSvg;
	
	var target  = evt.target.parentSvg,
              rect    = svg.getBoundingClientRect(),
              offsetX = evt.clientX - rect.left,
              offsetY  = evt.clientY - rect.top;
	var mouseX = offsetX;
	var mouseY = offsetY;
	//console.log("mousemove at "+mouseX+","+mouseY);
    if (movingElement !== 0) {
        movingElement.setAttribute('class', 'movable');
        switch (svg.selectedElement.tagName)
    	{
            case "circle":
                moveState(svg.selectedElement, mouseX, mouseY);
                break;
            case "path":
				svg.selectedElement.rect.setAttribute('class', 'movable');
                movePath(svg.selectedElement, mouseX, mouseY);
                break;
        }
    }
}

function moveState(state, x, y)
{
	var svg = state.parentSvg;
	var str, temp, tx;
	var width = svg.div.offsetWidth;
	var height = svg.div.offsetHeight;
	if (!x)
		x = state.getAttribute("cx");
	if (!y)
		y = state.getAttribute("cy");
	if (x < circleSize)
		x = circleSize;
	else if (x > width - circleSize)
		x = width - circleSize;
	if (y < circleSize)
		y = circleSize;
	else if (y > height - circleSize)
		y = height - circleSize;
	state.setAttribute("cx", x);
	state.text.setAttribute("x", x);
	state.setAttribute("cy", y);
	state.text.setAttribute("y", y);
	if (state.end) 
	{
		state.end.setAttribute("cx", x);
		state.end.setAttribute("cy", y);
	}
	if (state.init) 
	{
		var x2 = x;
		var x1 = x2 - circleSize * 2.5;
		var y = y;
		var att = "M "+x1+" "+y+" L ";
		att += x2+" "+y;
		state.init.setAttribute("d", att);
	}
	for (i = 0; i < state.lines1.length; i++)
	{
		str = state.lines1[i].getAttribute("d").split(" ");
		if (state.lines1[i].start == state.lines1[i].end)
		{
			var att = "M " + x + " " + y + " C "
				+ cubicControlPoints(x, y, state.lines1[i].angle)
				+ " " + x +" " + y;
			var atts = att.split(" ");
			var tx = (+atts[4] + +atts[6] + +atts[1]) / 3;
			var ty = (+atts[5] + +atts[7] + +atts[2]) / 3;
			state.lines1[i].text.setAttributeNS(null, "x", tx);
			state.lines1[i].text.setAttributeNS(null, "y", ty);
			moveTextRect(state.lines1[i].rect, tx, ty);
			str = att;
		}
		else
		{
			str[1] = x;
			str[2] = y;
			
			str[4] = ((+str[1] + (+str[6]))/2) + state.lines1[i].dx;
			str[5] = ((+str[2] + (+str[7]))/2) + state.lines1[i].dy;
			
			tx = (+str[4] + (+((+str[1] + (+str[6]))/2)))/2;
			ty = (+str[5] + (+((+str[2] + (+str[7]))/2)))/2;
			state.lines1[i].text.setAttributeNS(null, "x", tx);
			state.lines1[i].text.setAttributeNS(null, "y", ty);
			moveTextRect(state.lines1[i].rect, tx, ty);
			str = str.join(" ");
		}
		state.lines1[i].setAttribute("d", str);
		repositionMarker(state.lines1[i]);
	}
	for (i = 0; i < state.lines2.length; i++)
	{
		if (state.lines2[i].start != state.lines2[i].end)
		{
		str = state.lines2[i].getAttribute("d").split(" ");
		str[6] = x;
		str[7] = y;

		str[4] = ((+str[1] + (+str[6]))/2) + state.lines2[i].dx;
		str[5] = ((+str[2] + (+str[7]))/2) + state.lines2[i].dy;
		
		tx = (+str[4] + (+((+str[1] + (+str[6]))/2)))/2;
		ty = (+str[5] + (+((+str[2] + (+str[7]))/2)))/2;
		state.lines2[i].text.setAttributeNS(null, "x", tx);
		state.lines2[i].text.setAttributeNS(null, "y", ty);
		moveTextRect(state.lines2[i].rect, tx, ty);
		str = str.join(" ");
		state.lines2[i].setAttribute("d", str);
		repositionMarker(state.lines2[i]);
		}
	}
}

function moveTextRect(rect, x, y) {
	if (x != -1)
	{
		var w = rect.getAttributeNS(null, "width");
		rect.setAttributeNS(null, "x", x-(w / 2));
	}
	if (y != -1)
	{
		var h = rect.getAttributeNS(null, "height");
		rect.setAttributeNS(null, "y", y-(h / 2));
	}
}
function sqr(x)
{
	return (x * x);
}
function movePath(line, mouseX, mouseY) {
	var str = line.getAttribute("d").split(" ");
	if (line.start == line.end)
	{
		var dx = mouseX - str[1];
		var dy = mouseY - str[2];
		var sqrtt = Math.sqrt( sqr(dx) + sqr(dy) );
		if (sqrtt == 0)
			sqrtt = 0.001;
		var angle = Math.acos( dx / sqrtt );
		if (mouseY > str[2])
			angle = -angle;
		
		var att = "M " + str[1] + " " + str[2] + " C "
			+ cubicControlPoints(str[1], str[2], angle)
			+ " " + str[1] +" " + str[2];
		
		var atts = att.split(" ");
		var tx = (+atts[4] + +atts[6] + +atts[1]) / 3;
		var ty = (+atts[5] + +atts[7] + +atts[2]) / 3;
		line.text.setAttributeNS(null, "x", tx);
		line.text.setAttributeNS(null, "y", ty);
		moveTextRect(line.rect, tx, ty);
		str = att;
		line.angle = angle;
	}
	else
	{
		line.dx = (2*(mouseX-((+str[1] + (+str[6]))/2)));
		line.dy = (2*(mouseY-((+str[2] + (+str[7]))/2)));
		str[4] = ((+str[1] + (+str[6]))/2) + line.dx;
		str[5] = ((+str[2] + (+str[7]))/2) + line.dy;

		var tx = (+str[4] + (+((+str[1] + (+str[6]))/2)))/2;
		var ty = (+str[5] + (+((+str[2] + (+str[7]))/2)))/2;
		line.text.setAttributeNS(null, "x", tx);
		line.text.setAttributeNS(null, "y", ty);
		moveTextRect(line.rect, tx, ty);

		str = str.join(" ");
	}
	line.setAttribute("d", str);
	repositionMarker(line);
}

function repositionMarker(line)
{
	var pathLength = line.getTotalLength();
	var pathPoint = line.getPointAtLength(pathLength - circleSize - 15);
	var pathPoint2 = line.getPointAtLength(pathLength - circleSize - 15.01);
	line.markerline.setAttribute("d", "M" + pathPoint2.x + " " + pathPoint2.y + " L " + pathPoint.x +" "+ pathPoint.y);
}

function transitionDblClick()
{
	var rect = this;
	var line = rect.line;
	var svg = rect.parentSvg;
	rect.setAttribute("stroke", "lightgreen");
	stopMovingElement();
	var name = getValidTransitionName(line.start, line.end, line.name);
	
	if (name != null)
	{
		renameTransition(line, name);
	}
	
	rect.setAttribute("fill", "white");
	if (rect.line.name == "")
	{
		if (rect.parentSvg.wp.realtype == "EFA")
		{
			renameTransition(rect.line, "ε");
		}
		else
		{
			renameTransition(rect.line, transitionPrevName);
		}
	}
	rect.setAttribute("stroke", "black");
	rect.setAttribute('class', '');
}
function stopMovingElement() {
    if (movingElement !== 0) {
        movingElement.setAttribute('class', 'none');
		if (movingElement.tagName == "path")
			movingElement.rect.setAttribute('class', 'none');
        movingElement = 0;
    }
}

function graphTransitionsCharsSyntax()
{
	return /[a-zA-Z0-9]/;
}

function incorrectGraphTransitionsCharsSyntax(val)
{
	return (!graphTransitionsCharsSyntax().test(val))
}

function graphTransitionsSyntax()
{
	return /^(([a-zA-Z0-9]+)|(ε)|(\\e))(,(([a-zA-Z0-9]+)|(ε)|(\\e)))*$/;
}

function incorrectGraphTransitionsSyntax(val)
{
	return (!graphTransitionsSyntax().test(val))
}

function tableNFATransitionsSyntax()
{
	return /^\{\}$|^\{[a-zA-Z0-9]+(,[a-zA-Z0-9]+)*\}$/;
}

function incorrectTableNFATransitionsSyntax(val)
{
	return (!tableNFATransitionsSyntax().test(val))
}

function tableDFATransitionsSyntax()
{
	return /^$|^-$|^[a-zA-Z0-9]+$/;
}

function incorrectTableDFATransitionsSyntax(val)
{
	return (!tableDFATransitionsSyntax().test(val))
}

function EFATransitionSyntax()
{
	return /^ε$|^[a-zA-Z0-9]+$/;
}

function incorrectEFATransitionSyntax(val)
{
	return (!EFATransitionSyntax().test(val))
}

function DFATransitionSyntax()
{
	return /^[a-zA-Z0-9]+$/;
}

function incorrectDFATransitionSyntax(val)
{
	return (!DFATransitionSyntax().test(val))
}

function stateSyntax()
{
	return /^[a-zA-Z0-9]+$/;
}
function incorrectStateSyntax(val)
{
	return (!stateSyntax().test(val));
}