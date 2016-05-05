var svgns = "http://www.w3.org/2000/svg";
var movingElement = 0;
var renamingTransition = 0;
var transitionPrevName;
var circleSize = 25;
var fadeTime = 100;
var cursorTimer;
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
		var editor = document.createElement("div");
		editor.setAttribute("id", id + "a");
		editor.setAttribute("class", "tab-pane fade in active");
		editor.wp = wp;
		
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

		var button1 = document.createElement("input");
		button1.type = "button";
		button1.value = "Přidat stav";

		var button2 = document.createElement("input");
		button2.type = "button";
		button2.value = "Přidat přechod";

		var button3 = document.createElement("input");
		button3.type = "button";
		button3.value = "Koncový stav";
		
		var button4 = document.createElement("input");
		button4.type = "button";
		button4.value = "Změnit znaky přechodu";
		
		var textBox = document.createElement("input");
		textBox.type = "text";
		textBox.value = "a";
		
		var button5 = document.createElement("input");
		button5.type = "button";
		button5.value = "Smaž zvolené";
		
		var button6 = document.createElement("input");
		button6.type = "button";
		button6.value = "Vygeneruj výstup";

		editor.appendChild(button1);
		editor.appendChild(button2);
		editor.appendChild(button3);
		editor.appendChild(button4);
		editor.appendChild(textBox);
		editor.appendChild(button5);
		editor.appendChild(button6);

		var p1 = document.createElement("p");
		editor.appendChild(p1);


		var mydiv = document.createElement("DIV");
		mydiv.setAttribute("style", "background-color: pink;");
		mydiv.setAttribute("class", "canvas");
		mydiv.setAttributeNS(null, "id", "mydiv");
		$(mydiv).resizable({minHeight: 400, minWidth: 400});
		editor.appendChild(mydiv);
		
		
		var svg = document.createElementNS(svgns, 'svg');
		svg.setAttribute('width', '100%');
		svg.setAttribute('height', '100%');
		svg.selectedElement = 0;
		svg.makingTransition = 0;
		renamingCursor = 0;
		svg.inputBox = textBox;
		svg.parentSvg = svg;
		svg.setAttributeNS(null, "onmousemove", "moveElement(evt)");
		svg.setAttributeNS(null, "onmouseleave", "stopMovingElement(evt);");
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
		rect.mode = modeEnum.SELECT;
		rect.setAttributeNS(null, "onmousedown", "rectClick(evt,this)");
		rect.button1 = button1;
		rect.button2 = button2;
		$(rect).dblclick(rectDblClick);
		svg.rect = rect;
		svg.appendChild(rect);
		
		
		var initState = document.createElementNS(svgns, "circle");
		initState.setAttributeNS(null, "cx", 70);
		initState.setAttributeNS(null, "cy", 50);
		initState.setAttributeNS(null, "r", circleSize);
		initState.setAttributeNS(null, "fill", "white");
		initState.setAttributeNS(null, "stroke", "black");
		initState.setAttributeNS(null, "stroke-width", 1);
		initState.parentSvg = svg;
		initState.parentRect = rect;
		initState.init = 0;
		initState.end = 0;
		initState.lines1 = [];
		initState.lines2 = [];
		initState.name = String.fromCharCode(65 + rect.states.length);
		initState.setAttributeNS(null, "onmousedown", "clickState(evt)");
		initState.setAttributeNS(null, "onmouseup", "stopMovingElement(evt)");
		initState.setAttributeNS(null, 'onmousemove', 'prevent(evt)');
		$(initState).dblclick(stateDblClick);

		var newText = document.createElementNS(svgns, "text");
		newText.setAttributeNS(null, "x", initState.getAttribute("cx"));
		newText.setAttributeNS(null, "y", initState.getAttribute("cy"));
		newText.setAttribute('pointer-events', 'none');
		newText.setAttribute('font-size', 20);
		newText.setAttribute('font-family','Arial, Helvetica, sans-serif');
		newText.setAttribute('dy', ".3em");							// vertical alignment
		newText.setAttribute('text-anchor', "middle");				// horizontal alignment
		newText.setAttribute('class', 'noselect');
		var textNode = document.createTextNode(String.fromCharCode(65 + rect.states.length));
		newText.appendChild(textNode);
		newText.node = textNode;

		initState.text = newText;

		toggleInitState(initState);
		rect.states.push(initState);
		putOnTop(initState);
		rect.initState = initState;

		button1.rect = rect;
		button2.rect = rect;
		button3.rect = rect;
		button4.rect = rect;
		button5.rect = rect;
		button6.rect = rect;
		button1.setAttributeNS(null, "onclick", 'button1Click(rect);');
		button2.setAttributeNS(null, "onclick", 'button2Click(rect);');
		button3.setAttributeNS(null, "onclick", 'button3Click(rect);');
		button4.setAttributeNS(null, "onclick", 'button4Click(rect);');
		button5.setAttributeNS(null, "onclick", 'button5Click(rect);');
		button6.setAttributeNS(null, "onclick", 'button6Click(rect);');
		button1.style.borderStyle = "outset";
		button2.style.borderStyle = "outset";
		button3.style.borderStyle = "outset";
		button4.style.borderStyle = "outset";
		button5.style.borderStyle = "outset";
		button6.style.borderStyle = "outset";
		
		wp.appendChild(editor);
		
		// TABLE TAB
		var tableButton1 = document.createElement("input");
		tableButton1.type = "button";
		tableButton1.value = "Počátečný stav";
		tableButton1.tableTab = tableTab;
		tableButton1.setAttributeNS(null, "onclick", 'tableButton1Click(tableTab);');
		tableButton1.style.borderStyle = "outset";
		tableTab.appendChild(tableButton1);
		
		var tableButton2 = document.createElement("input");
		tableButton2.type = "button";
		tableButton2.value = "Koncový stav";
		tableButton2.tableTab = tableTab;
		tableButton2.setAttributeNS(null, "onclick", 'tableButton2Click(tableTab);');
		tableButton2.style.borderStyle = "outset";
		tableTab.appendChild(tableButton2);
		
		initTable(wp);
		wp.appendChild(tableTab);
		
		// TEXT TAB
		wp.appendChild(textTab);
		
		$('a[data-target="#' + id + 'a"]').on('shown.bs.tab', function (e) {
			updateEditorTab(wp, e.relatedTarget);
		});
		$('a[data-target="#' + id + 'b"]').on('shown.bs.tab', function (e) {
			updateTableTab(wp, e.relatedTarget);
		});
		$('a[data-target="#' + id + 'c"]').on('shown.bs.tab', function (e) {
			updateTextTab(wp, e.relatedTarget);
		});
		
		$('a[data-target="#' + id + 'a"]').on('hide.bs.tab', function (e) {
			wp.svg.div.lastWidth = wp.svg.div.offsetWidth;
			wp.svg.div.lastHeight = wp.svg.div.offsetHeight;
		});
		
		$('a[data-target="#' + id + 'a"]').on('hidden.bs.tab', function (e) {
			deselectElement(svg);
			for (i = 0; i < rect.states.length; i++)
			{
				if (rect.states[i].init == 0 && rect.states[i].end == 0 && rect.states[i].lines1.length == 0 && rect.states[i].lines2.length == 0)
					deleteState(rect.states[i]);
			}
		});
	}
	else if (wp.type == "REG")
	{
		var textArea = document.createElement("input");
		textArea.setAttribute("type", "text");
		textArea.setAttribute("class", "myTextArea");
		$(textArea).on('input',regTextChanged);
		//$(textArea).focusout(tableCellChangedFinal);
		wp.appendChild(textArea);	
		
		var textAreaSyntax = document.createElement("img");
		textArea.syntax = textAreaSyntax;
		wp.appendChild(textAreaSyntax);	
	}
}

function initTable(wp) {
	var table = document.createElement("table");
	table.setAttribute("class", "myTable");
	wp.tableTab.table = table;
	table.selectedCell = 0;
	table.wp = wp;
	wp.tableTab.appendChild(table);
	console.log(table);
}

function initTextTab(wp) {
	console.log("initializing text tab");
	var x = parseInt(wp.svg.divId.substring(1, wp.svg.divId.length)) - 1;
	console.log(x);
	wp.textTab.textArea = document.getElementsByTagName('textarea')[x];
	console.log(wp.textTab.textArea);
}

function updateEditorTab(wp, target)
{
	var t = target.getAttribute("data-target");
	var x = t.substr(t.length - 1, 1);
	if (x == "b")
		updateEditorTabFromTable(wp);
	else
		updateEditorTabFromText(wp);
	
	for (i = 0; i < wp.svg.rect.states.length; i++)
	{
		for (j = 0; j < wp.svg.rect.states[i].lines1.length; j++)
		{
			adjustTransitionWidth(wp.svg.rect.states[i].lines1[j]);
		}
	}
}

function updateEditorTabFromTable(wp)	// not finished
{

}

function updateEditorTabFromText(wp)
{
	updateTableTabFromText(wp);
	updateEditorTabFromTable(wp);
}

function updateTableTab(wp, target)
{
	var t = target.getAttribute("data-target");
	var x = t.substr(t.length - 1, 1);
	if (x == "a")
		updateTableTabFromEditor(wp);
	else
		updateTableTabFromText(wp);
}

function updateTableTabFromEditor(wp)
{
	updateTextTabFromEditor(wp);
	updateTableTabFromText(wp);
}

function updateTableTabFromText(wp)	// not finished
{
	wp.textTab.textArea.style.display = "none";	// hide answer textarea
	
	var table = wp.tableTab.table;
	var s = wp.textTab.textArea.value;
	var str = s.split(" ");
	table.states = [];
	table.symbols = [];
	initStates = [];
	exitStates = [];
	
	// clearing previous table
	var l = table.rows.length;
	for (i = 0; i < l; i++)
		table.deleteRow(0);
	
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
		if (/^init=[^ ]+$/.test(str[i]))
		{
			str[i] = str[i].substring(5, str[i].length);
			initStates.push(str[i]);
			if (table.states.indexOf(str[i]) == -1)
				table.states.push(str[i]);
		}
		else if (/^F={[^ ,]+(,[^ ,]+)*}$/.test(str[i]))
		{
			str[i] = str[i].substring(3, str[i].length - 1);
			var exits = str[i].split(",");
			for (j =  0; j < exits.length; j++)
			{
				exitStates.push(exits[j]);
				if (table.states.indexOf(exits[j]) == -1)
					table.states.push(exits[j]);
			}
		}
		else
		{
			if ((wp.type == "DFA") && (/^\([^ ,]+,[^ ,]+\)=[^ ]+$/.test(str[i])))	// DFA
			{
				var state1 = str[i].substring(1, str[i].indexOf(","));
				var symb = str[i].substring(str[i].indexOf(",") + 1, str[i].indexOf(")"));
				var state2 = str[i].substring(str[i].indexOf("=") + 1, str[i].length);
				if (table.states.indexOf(state1) == -1)
					table.states.push(state1);
				if (table.states.indexOf(state2) == -1)
					table.states.push(state2);
				if (table.symbols.indexOf(symb) == -1)
					table.symbols.push(symb);
			}
			else if ((wp.type == "NFA") && (/^\([^ ,]+,[^ ,]+\)={[^ ,]+(,[^ ,]+)*}$/.test(str[i])))	// NFA
			{
				var state1 = str[i].substring(1, str[i].indexOf(","));
				var symb = str[i].substring(str[i].indexOf(",") + 1, str[i].indexOf(")"));
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
	var div = document.createElement("div");
	div.setAttribute("class", "tc");
	cell.appendChild(div);
	
	// filling out columns' headers from symbols and delete buttons above them
	for (i = 0; i < table.symbols.length; i++)
	{
		tableAddColumnDeleteButton(row, table);
		tableAddColumnHeader(row2, table.symbols[i]);
	}
	
	// column add button
	tableAddColumnAddButton(row, table);
	
	console.log(table.states.length);
	// filling out rows' headers from states
	for (i = 0; i < table.states.length; i++)
	{
		var state = table.states[i];
		console.log(state);
		var row = table.insertRow(table.rows.length);
		
		tableAddRowDeleteButton(row, table);
		
		var headerval = "";
		if (initStates.indexOf(state) != -1)
		{
			if (exitStates.indexOf(state) != -1)
				headerval += '↔';
			else
				headerval += '→';
		}
		else if (exitStates.indexOf(state) != -1)
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
		if ((wp.type == "DFA") && (/^\([^ ,]+,[^ ,]+\)=[^ ,]+$/.test(str[i])))	// DFA
		{
			var state1 = str[i].substring(1, str[i].indexOf(","));
			var symb = str[i].substring(str[i].indexOf(",") + 1, str[i].indexOf(")"));
			var state2 = str[i].substring(str[i].indexOf("=") + 1, str[i].length);
			var cell = table.rows[table.states.indexOf(state1) + 2].cells[table.symbols.indexOf(symb) + 2];
			cell.myDiv.value = cell.myDiv.value.substring(0, cell.myDiv.value.length - 1);
			if (cell.myDiv.value.length > 1)
				cell.myDiv.value += ',';
			cell.myDiv.value += state2 + '}';
			cell.myDiv.prevValue = cell.myDiv.value;
		}
		else if ((wp.type == "NFA") && (/^\([^ ,]+,[^ ,]+\)={[^ ,]+(,[^ ,]+)*}$/.test(str[i])))	// NFA
		{
			var state1 = str[i].substring(1, str[i].indexOf(","));
			var symb = str[i].substring(str[i].indexOf(",") + 1, str[i].indexOf(")"));
			var states2 = str[i].substring(str[i].indexOf("=") + 2, str[i].length - 1).split(",");
			console.log(states2);
			var cell = table.rows[table.states.indexOf(state1) + 2].cells[table.symbols.indexOf(symb) + 2];
			cell.myDiv.value = cell.myDiv.value.substring(0, cell.myDiv.value.length - 1);
			for (j = 0; j < states2.length; j++)
			{
				if (cell.myDiv.value.length > 1)
					cell.myDiv.value += ',';
				cell.myDiv.value += states2[j];
			}
			cell.myDiv.value += '}';
			cell.myDiv.prevValue = cell.myDiv.value;
		}
	}
}

function tableEditCellClick(evt)
{
	var cell = evt.target;
	var table = cell.parentElement.parentElement.parentElement.parentElement;
	if (!table.statesLocked && table.selectedCell != 0)
	{
		var div = table.selectedCell;
		$(div).switchClass(div.defaultClass + "s", div.defaultClass, fadeTime);
		table.selectedCell = 0;
	}
}

function tableCellClick(evt)
{
	var cell = evt.target;
	console.log(cell);
	var table = cell.parentElement.parentElement.parentElement.parentElement;
	if (!table.statesLocked && table.selectedCell != cell)
	{
		var div = table.selectedCell;
		if (table.selectedCell != 0)
		{
			$(div).switchClass(div.defaultClass + "s", div.defaultClass, fadeTime);
		}
		$(cell).switchClass(cell.defaultClass, cell.defaultClass + "s", fadeTime);
		table.selectedCell = cell;
	}
}

function tableDeleteRow(table, index)
{
	//table.states.splice(table.states.indexOf(cell.parentNode.cells[1].myDiv.innerHTML), 1);
	
	// Deselect this row's header, if it was selected
	if (table.selectedCell != 0 && index == table.selectedCell.myCell.parentNode.rowIndex)
		table.selectedCell = 0;
	
	// Delete state in editor
	var state = table.rows[index].cells[1].myDiv.value;
	state = removePrefixFromState(state);
	deleteState(findState(table.wp.svg.rect, state));
	
	// Traverse all transitions cells in table and change the name
	for (i = 2; i < table.rows.length - 1; i++)
	{
		for (j = 2; j < table.rows[i].cells.length; j++)
		{
			var val = table.rows[i].cells[j].myDiv.value;
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
	}
	
	// Delete table row
	table.deleteRow(index);
}

function tableDeleteColumn(table, index)
{
	var symbol = table.rows[1].cells[index].myDiv.value;
	console.log(symbol);
	
	// Delete transitions of this symbol in editor
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
	var div = document.createElement("input");
	div.value = value;
	div.prevValue = value;
	div.setAttribute("size", 1);
	div.defaultClass = "rh";
	cell.setAttribute("class", "myCell");
	div.setAttribute("class", "myCellDiv " + div.defaultClass);
	$(div).click(tableCellClick);

	$(div).on('input',tableRhChanged);
	
	$(div).focusout(tableRhChangedFinal);

	$(div).keypress(function (e) {
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

function tableRhChanged()
{
	var state = this.value;
	var table = this.parentElement.parentElement.parentElement.parentElement;
	var ri = this.parentElement.parentElement.rowIndex;
	state = removePrefixFromState(state);
	console.log(state);
	if (incorrectStateSyntax(state))
		$(this).addClass("incorrect", fadeTime);
	else if (tableStateExists(this, state) != -1)
	{
		$(this).addClass("incorrect", fadeTime);
		for (var i = 2; i < table.rows.length - 1; i++)
		{
			if (i == ri)
				continue;
			$(table.rows[i].cells[1].myDiv).prop('readonly', true);
		}
		table.statesLocked = true;
	}
	else
	{
		$(this).removeClass("incorrect", fadeTime);
		if (table.statesLocked)
		{
			for (var i = 2; i < table.rows.length - 1; i++)
			{
				if (i == ri)
					continue;
				$(table.rows[i].cells[1].myDiv).prop('readonly', false);
			}
			table.statesLocked = false;
		}
	}
	
}

function tableRhChangedFinal()
{
	if ($(this).hasClass("incorrect") == false)
	{
		// Rename the state in editor
		var table = $(this).parent().parent().parent().parent().get(0);
		var div = $(this).get(0);
		var prevName = div.prevValue;
		prevName = removePrefixFromState(prevName);
		var newName = div.value;
		newName = removePrefixFromState(newName);
		
		console.log(prevName + "   " + newName);
		// Rename state in editor
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
					table.rows[i].cells[j].myDiv.value = "{" + val + "}";
					table.rows[i].cells[j].myDiv.prevValue = table.rows[i].cells[j].myDiv.value;
				}
			}
		}
		
		div.prevValue = div.value;
	}
}

function tableChChanged()
{
	if (incorrectTransitionSyntax(this.value))
		$(this).addClass("incorrect", fadeTime);
	else
		$(this).removeClass("incorrect", fadeTime);
	console.log("CH: " + this.value);
}

function tableChChangedFinal()
{
	if ($(this).hasClass("incorrect") == false)
	{
		var table = $(this).parent().parent().parent().parent().get(0);
		var div = $(this).get(0);
		var prevName = div.prevValue;
		var newName = div.value;
		
		// Rename the symbol in editor
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
		
		div.prevValue = div.value;
	}
}

function tableCellChanged()
{
	if (incorrectTableTransitionsSyntax(this.value))
		$(this).addClass("incorrect", fadeTime);
	else
		$(this).removeClass("incorrect", fadeTime);
	console.log("Cell: " + this.value);
}

function tableCellChangedFinal()
{
	if ($(this).hasClass("incorrect") == false)
	{
		var table = $(this).parent().parent().parent().parent().get(0);
		var div = $(this).get(0);
		var prevName = div.prevValue;
		var newName = div.value;
		prevName = prevName.substring(1, prevName.length - 1);
		newName = newName.substring(1, newName.length - 1);
		
		var stateName = table.rows[div.myCell.parentElement.rowIndex].cells[1].myDiv.value;
		stateName = removePrefixFromState(stateName);
		var state = findState(table.wp.svg.rect, stateName);
		var symbol = table.rows[1].cells[div.myCell.cellIndex].myDiv.value;
		
		var prevStates = prevName.split(",");
		var newStates = newName.split(",");
		/*
		console.log(prevName);
		console.log(newName);
		console.log(stateName);
		console.log(symbol);
		*/
		
		// Delete the transitions in editor
		console.log(prevStates);
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
		
		//Add the transitions in editor
		if (newStates.length == 1 && newStates[0] == "")
		{
			newStates = [];
		}
		console.log(newStates);
		for (var i = 0; i < newStates.length; i++)
		{
			if (prevStates.indexOf(newStates[i]) == -1)
			{
				var state2Name = newStates[i];
				console.log("searching for " + state2Name);
				var state2 = findState(table.wp.svg.rect, state2Name);
				if (state2 == -1)
				{
					console.log("adding state " + state2Name + " and transition " + symbol);
					state2 = createStateAbs(table.wp.svg.rect, 100, 100, state2Name);
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
	
	var div = document.createElement("input");
	div.value = value;
	div.prevValue = value;
	div.setAttribute("size", 1);
	cell.setAttribute("class", "myCell ch");
	div.setAttribute("class", "myCellDiv");
	$(div).click(tableEditCellClick);
	
	$(div).on('input',tableChChanged);
	$(div).focusout(tableChChangedFinal);
	
	$(div).keypress(function (e) {
		var kc = e.charCode;
		if (kc == 0)
			return true;
		var txt = String.fromCharCode(kc);
		if (incorrectTransitionSyntax(txt)) {
			return false;
		}
	});
	
	cell.myDiv = div;
	cell.appendChild(div);
}

function tableAddCell(row)
{
	var cell = row.insertCell(row.cells.length);
	
	var div = document.createElement("input");
	div.value = "{}";
	div.prevValue = div.value;
	div.setAttribute("size", 1);
	$(div).click(tableEditCellClick);
	cell.setAttribute("class", "myCell");
	div.setAttribute("class", "myCellDiv");
	
	$(div).on('input',tableCellChanged);
	$(div).focusout(tableCellChangedFinal);

	$(div).keypress(function (e) {
		var kc = e.charCode;
		if (kc == 0)
			return true;
		var txt = String.fromCharCode(kc);
		if (!txt.match(/[^ =()]/)) {
			return false;
		}
	});
	
	div.myCell = cell;
	cell.myDiv = div;
	cell.appendChild(div);
}

function tableAddColumn(table)
{
	table.rows[0].deleteCell(table.rows[0].cells.length - 1);
	tableAddColumnDeleteButton(table.rows[0], table);
	tableAddColumnAddButton(table.rows[0], table);
	
	var names = [];
	for (k = 'a'.charCodeAt(0); k < 'z'.charCodeAt(0); k++)
		names.push(String.fromCharCode(k));
	for (k = 0; k < table.symbols.length; k++)
		names.splice(names.indexOf(table.symbols[k]), 1);
	var name = names[0];
	table.symbols.push(name);
	
	tableAddColumnHeader(table.rows[1], name);
	for (i = 2; i < table.rows.length - 1; i++)
	{
		tableAddCell(table.rows[i]);
	}
}

function tableAddRow(table)
{
	console.log("adding row");
	table.rows[table.rows.length - 1].deleteCell(0);
	tableAddRowDeleteButton(table.rows[table.rows.length - 1], table);
	
	var names = [];
	for (k = 65; k < 91; k++)
		names.push(String.fromCharCode(k));
	for (k = 0; k < table.wp.svg.rect.states.length; k++)
		names.splice(names.indexOf(table.wp.svg.rect.states[k].name), 1);
	var name = names[0];
	table.states.push(name);
	
	tableAddRowHeader(table.rows[table.rows.length - 1], name);
	for (i = 2; i < table.rows[0].cells.length - 1; i++)
	{
		tableAddCell(table.rows[table.rows.length - 1]);
	}
	tableAddRowAddButton(table);
	
	// Add state to editor
	console.log(table.wp.svg.rect);
	createStateAbs(table.wp.svg.rect, 200, 100);
}

function tableButton1Click(tableTab) {
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
		
		// Edit init state in editor
		for (i = 0; i < table.wp.svg.rect.states.length; i++)
		{
			if (table.wp.svg.rect.states[i].name == state)
			{
				if (on)
					toggleInitStateOn(table.wp.svg.rect.states[i]);
				else
					toggleInitStateOff(table.wp.svg.rect.states[i]);
				break;
			}
		}
		
		$(cell).trigger("input");
	}
}

function tableButton2Click(tableTab) {
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
		
		// Edit exit state in editor
		for (i = 0; i < table.wp.svg.rect.states.length; i++)
		{
			if (table.wp.svg.rect.states[i].name == state)
			{
				if (on)
					toggleEndStateOn(table.wp.svg.rect.states[i]);
				else
					toggleEndStateOff(table.wp.svg.rect.states[i]);
				break;
			}
		}
		
		$(cell).trigger("input");
	}
}

function updateTextTab(wp, target)
{
	var t = target.getAttribute("data-target");
	var x = t.substr(t.length - 1, 1);
	if (x == "b")
		updateTextTabFromTable(wp);
	else
		updateTextTabFromEditor(wp);
}

function updateTextTabFromTable(wp)
{
	updateEditorTabFromTable(wp);
	updateTextTabFromEditor(wp);
}

function updateTextTabFromEditor(wp)
{
	if (!wp.textTab.textArea)
		initTextTab(wp);
	wp.textTab.textArea.style.display = "";	// show answer textarea
	var textArea = wp.textTab.textArea;
	textArea.value = generateAnswer(wp.svg.rect);
}

function button1Click(rect) {
    if (rect.button1.style.borderStyle == "inset") {
        rect.button1.style.borderStyle = "outset";
        rect.mode = modeEnum.SELECT;
    } else {
        if (rect.button2.style.borderStyle == "inset") rect.button2.style.borderStyle = "outset";
        rect.button1.style.borderStyle = "inset";
        rect.mode = modeEnum.ADD_STATE;
        if (rect.parentSvg.makingTransition !== 0) {
            rect.parentSvg.selectedElement = rect.parentSvg.makingTransition;
            rect.parentSvg.makingTransition = 0;
        }
        deselectElement(rect.parentSvg);
    }
}

function button2Click(rect) {
    if (rect.button2.style.borderStyle == "inset") {
        rect.button2.style.borderStyle = "outset";
        rect.mode = modeEnum.SELECT;
        if (rect.parentSvg.makingTransition !== 0) {
            rect.parentSvg.selectedElement = rect.parentSvg.makingTransition;
            rect.parentSvg.makingTransition = 0;
            deselectElement(rect.parentSvg);
        }
    } else {
        if (rect.button1.style.borderStyle == "inset") rect.button1.style.borderStyle = "outset";
        rect.button2.style.borderStyle = "inset";
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
	var x2 = state.getAttribute("cx");
	var x1 = x2 - circleSize * 2.5;
	var y = state.getAttribute("cy");
	var aLine = document.createElementNS(svgns, 'path');
	var att = "M "+x1+" "+y+" L ";
	att += x2+" "+y;
	aLine.setAttribute('d', att);
	aLine.setAttribute('stroke', 'black');
	aLine.setAttribute('stroke-width', 3);
	aLine.setAttribute('fill', 'none');
	aLine.parentSvg = state.parentSvg;
	
	var defs = document.createElementNS(svgns, 'defs');
	var marker = document.createElementNS(svgns, 'marker');
	marker.setAttribute('id', 'Triangle');
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
	
	state.parentSvg.appendChild(defs);
	defs.appendChild(marker);
	aLine.setAttribute('marker-end', 'url(#Triangle)');
	
	state.parentSvg.appendChild(aLine);
	putOnTop(state);
	state.init = aLine;
}

function toggleInitStateOff(state)
{
	state.parentSvg.removeChild(state.init);
	state.init = 0;
}

function toggleInitState(state)
{
	if (state.init === 0) {
		toggleInitStateOn(state)
	} else {
		toggleInitStateOff(state)
	}
}

function toggleEndStateOn(state)
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
}

function toggleEndStateOff(state)
{
	state.parentSvg.removeChild(state.end);
	state.end = 0;
}

function toggleEndState(state)
{
	if (state.end === 0) {
		toggleEndStateOn(state)
	} else {
		toggleEndStateOff(state)
	}
}

function button3Click(rect) {
    var svg = rect.parentSvg;
    if ((svg.selectedElement !== 0) && (svg.selectedElement.tagName == "circle")) {
		toggleEndState(svg.selectedElement);
    }
}

function button4Click(rect) {
    var svg = rect.parentSvg;
    if ((svg.selectedElement !== 0) && (svg.selectedElement.tagName == "path")) {
        if (svg.inputBox.value === "")
        {
            alert("Nelze prázdný přechod");
        }
        else if (/[^a-z,]/.test(svg.inputBox.value))
        {
            alert("Chybná syntax! (pouze a-z oddělené čárkami)");
        }
        else
        {
        	svg.selectedElement.name = svg.inputBox.value;
        	svg.selectedElement.text.node.nodeValue = svg.inputBox.value;
			svg.selectedElement.rect.setAttribute("width", svg.selectedElement.text.getComputedTextLength() + 8);
			moveTextRect(svg.selectedElement.rect, svg.selectedElement.text.getAttribute('x'), svg.selectedElement.text.getAttribute('y'));
        }
    }
}

function button5Click(rect) {
	var svg = rect.parentSvg;
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
function generateAnswer(rect)
{
	var finalStates = [];
    var out = "";
	var type = rect.parentSvg.wp.type;
	for (i = 0; i < rect.states.length; i++)
    {
		if (rect.states[i].init !== 0)
		{
			if (out == "")
				out += "init=";
            out += rect.states[i].name + " ";
			break; // replace break with adding more init states, if they can be
		}
	}
    for (i = 0; i < rect.states.length; i++)
    {
        if (rect.states[i].end !== 0)
            finalStates.push(rect.states[i]);
		if (type == "DFA")
		{
			for (j = 0; j < rect.states[i].lines1.length; j++)
			{
				var str = rect.states[i].lines1[j].name;
				str = str.split(',');
				for (k = 0; k < str.length; k++)
				{
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
				out += "(" + rect.states[i].name + "," + keys[j] +
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
function button6Click(rect) {
    var svg = rect.parentSvg;
    var out = generateAnswer(rect);
    
	var x = parseInt(svg.divId.substring(1, svg.divId.length)) - 1;
	document.getElementsByTagName('textarea')[x].value = out; 
}

function createStateAbs(rect, x, y, name)
{
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
	shape.init = 0;
	shape.end = 0;
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
	shape.setAttributeNS(null, "onmouseup", "stopMovingElement(evt)");
	$(shape).dblclick(stateDblClick);

	var newText = document.createElementNS(svgns, "text");
	newText.setAttributeNS(null, "x", shape.getAttribute("cx"));
	newText.setAttributeNS(null, "y", shape.getAttribute("cy"));
	newText.setAttribute('pointer-events', 'none');
	newText.setAttribute('font-size', 20);
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
	rect.button1.style.borderStyle = "outset";
	rect.mode = modeEnum.SELECT;
	return shape;
}

function createState(evt) 
{
	var rect = evt.target;
	var x = evt.offsetX;
	var y = evt.offsetY;
	console.log(rect);
	console.log(x + " " + y);
	createStateAbs(rect, x, y);
}
function rectDblClick(evt) {
	evt.preventDefault();
	createState(evt);
}

function rectClick(evt, rect) {
	evt.preventDefault();
	stopTyping();
    switch (rect.mode) {
        case modeEnum.ADD_STATE:
            createState(evt);
            break;
		case modeEnum.ADD_TRANSITION:
			rect.button2.style.borderStyle = "outset";
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
    if (state.end !== 0) state.parentSvg.appendChild(state.end);
    state.parentSvg.appendChild(state.text);
}
function controlPoint(x1, y1, x2, y2){
    var str;
    var x = ((+x2 + (+x1))/2) + ((+y2 - +y1)/5);
    var y = ((+y2 + (+y1))/2) - ((+x2 - +x1)/5);
    str = x + " " + y;
    return str;
}
function selectStateForTransition(state)
{
	state.setAttributeNS(null, "fill", "lightblue");
	if (state.end !== 0)
		state.end.setAttributeNS(null, "fill", "lightblue");
	state.parentSvg.makingTransition = state;
}
function stateDblClick(evt)
{
	evt.preventDefault();
	var state = evt.target;
	var rect = state.parentRect;
	rect.button2.style.borderStyle = "inset";
	rect.mode = modeEnum.ADD_TRANSITION;
	selectStateForTransition(state);
}
function createTransition(state1, state2, symbols)
{
	var x1 = state1.getAttribute("cx");
	var y1 = state1.getAttribute("cy");
	var x2 = state2.getAttribute("cx");
	var y2 = state2.getAttribute("cy");
	var aLine = document.createElementNS(svgns, 'path');
	var att = "M "+x1+" "+y1+" Q ";
	att += controlPoint(x1, y1, x2, y2);
	att += " "+x2+" "+y2;
	aLine.setAttribute('d', att);
	aLine.setAttribute('stroke', 'black');
	aLine.setAttribute('stroke-width', 3);
	aLine.setAttribute('fill', 'none');
	aLine.setAttributeNS(null, 'onmousedown', 'selectElement(evt)');
	aLine.setAttributeNS(null, 'onmouseup', 'stopMovingElement(evt)');
	aLine.parentSvg = state2.parentSvg;
	aLine.name = symbols;
	aLine.start = state1;
	aLine.end = state2;
	
	var defs = document.createElementNS(svgns, 'defs');
	var marker = document.createElementNS(svgns, 'marker');
	marker.setAttribute('id', 'Triangle');
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
	
	state2.parentSvg.appendChild(defs);
	defs.appendChild(marker);
	aLine.setAttribute('marker-end', 'url(#Triangle)');
	
	var str = aLine.getAttribute('d').split(' ');
	var tx = (+str[4] + (+((+str[1] + (+str[6]))/2)))/2;
	var ty = (+str[5] + (+((+str[2] + (+str[7]))/2)))/2;
	
	var newText = document.createElementNS(svgns, 'text');
	newText.setAttributeNS(null, "x", tx);
	newText.setAttributeNS(null, "y", ty);
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
	moveTextRect(newRect, tx, ty);
	newRect.setAttributeNS(null, "stroke", "black");
	newRect.setAttributeNS(null, "stroke-width", 1);
	newRect.parentSvg = state2.parentSvg;
	newRect.setAttributeNS(null, 'onmousedown', 'selectElement(evt)');
	newRect.setAttributeNS(null, 'onmouseup', 'stopMovingElement(evt)');
	newRect.setAttributeNS(null, 'onmousemove', 'prevent(evt)');
	newRect.line = aLine;
	$(newRect).dblclick(transitionDblClick);

	aLine.text = newText;
	aLine.rect = newRect;
	newText.line = aLine;
	
	state2.parentSvg.appendChild(aLine);
	state2.parentSvg.appendChild(newRect);
	state2.parentSvg.appendChild(aLine.text);
	putOnTop(state2);
	putOnTop(state1);
	whitenState(state1);
	state1.lines1.push(aLine);
	state2.lines2.push(aLine);
	state2.parentRect.mode = modeEnum.SELECT;
	state2.parentRect.button2.style.borderStyle = "outset";
	
	state2.parentSvg.makingTransition = 0;
	state2.parentSvg.selectedElement = 0;
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
                for (i = 0; i < state.parentSvg.makingTransition.lines1.length; i++)
                    if (state.parentSvg.makingTransition.lines1[i].end == state)
					{
						state.parentRect.button2.style.borderStyle = "outset";
						state.parentRect.mode = modeEnum.SELECT;
						state.parentRect.parentSvg.selectedElement = state.parentRect.parentSvg.makingTransition;
						state.parentRect.parentSvg.makingTransition = 0;
						deselectElement(state.parentRect.parentSvg);
						return;
					}
                createTransition(state.parentSvg.makingTransition, state, "a");
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
    if (state.end !== 0)
        state.end.setAttributeNS(null, "fill", "white");
}

function selectElement(evt) {
	evt.preventDefault();
	stopTyping();
	var svg = evt.target.parentSvg;
    deselectElement(svg);
    svg.selectedElement = evt.target;
    movingElement = svg.selectedElement;
    switch (svg.selectedElement.tagName)
    {
        case "circle":
            svg.selectedElement.setAttributeNS(null, "fill", "lightgreen");
    		if (svg.selectedElement.end !== 0) 
                svg.selectedElement.end.setAttributeNS(null, "fill", "lightgreen");
    		//putOnTop(svg.selectedElement);	// breaks doubleclicking on Chrome
            break;
		case "text":
		case "rect":
			svg.selectedElement = svg.selectedElement.line;
        case "path":
            svg.selectedElement.setAttribute('stroke',"lightgreen");
            svg.inputBox.value = svg.selectedElement.text.node.nodeValue;
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
	return -1;
}

function deleteState(state)
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
    if (state.end !== 0) svg.removeChild(state.end);
	if (state.init !== 0) svg.removeChild(state.init);
	svg.removeChild(state);
	state.parentRect.states.splice(index, 1);
	deselectElement(svg);
}

function deleteTransition(tr)
{
	console.log("deleting tr " + tr);
	var svg = tr.parentSvg;
	tr.start.lines1.splice(tr.start.lines1.indexOf(tr), 1);
	tr.end.lines2.splice(tr.end.lines2.indexOf(tr), 1);
	svg.removeChild(tr.text);
	svg.removeChild(tr.rect);
	svg.removeChild(tr);
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
	//adjustTransitionWidth(state);
}

function deselectElement(svg) {
	//stopTyping();
    if (svg.selectedElement !== 0) {
        switch (svg.selectedElement.tagName)
    	{
        	case "circle":
        		whitenState(svg.selectedElement);
                break;
            case "path":
                svg.selectedElement.setAttribute('stroke',"black");
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
                var str, temp, tx;
				var width = svg.div.offsetWidth;
				var height = svg.div.offsetHeight;
                if ((mouseX > circleSize) && (mouseX < width - circleSize)) {
                    svg.selectedElement.setAttribute("cx", mouseX);
                    svg.selectedElement.text.setAttribute("x", mouseX);
                    if (svg.selectedElement.end !== 0) svg.selectedElement.end.setAttribute("cx", mouseX);
					if (svg.selectedElement.init !== 0) 
					{
						var x2 = mouseX;
						var x1 = x2 - circleSize * 2.5;
						var y = mouseY;
						var att = "M "+x1+" "+y+" L ";
						att += x2+" "+y;
						svg.selectedElement.init.setAttribute("d", att);
					}
                    for (i = 0; i < svg.selectedElement.lines1.length; i++)
                    {
                        str = svg.selectedElement.lines1[i].getAttribute("d").split(" ");
                        str[1] = mouseX;
                        
                        //temp = controlPoint(str[1], str[2], str[6], str[7]);
                        //temp = temp.split(' ');
                        //str[4] = temp[0];
                        
                        tx = (+str[4] + (+((+str[1] + (+str[6]))/2)))/2;
                        svg.selectedElement.lines1[i].text.setAttributeNS(null, "x", tx);
						moveTextRect(svg.selectedElement.lines1[i].rect, tx, -1);
                        str = str.join(" ");
                        svg.selectedElement.lines1[i].setAttribute("d", str);
                    }
                    for (i = 0; i < svg.selectedElement.lines2.length; i++)
                    {
                        str = svg.selectedElement.lines2[i].getAttribute("d").split(" ");
                        str[6] = mouseX;
                        
                        //temp = controlPoint(str[1], str[2], str[6], str[7]);
                        //temp = temp.split(' ');
                        //str[4] = temp[0];
                        
                        tx = (+str[4] + (+((+str[1] + (+str[6]))/2)))/2;
                        svg.selectedElement.lines2[i].text.setAttributeNS(null, "x", tx);
						moveTextRect(svg.selectedElement.lines2[i].rect, tx, -1);
                        str = str.join(" ");
                        svg.selectedElement.lines2[i].setAttribute("d", str);
                    }
                }
                if ((mouseY > circleSize) && (mouseY < height - circleSize)) {
                    svg.selectedElement.setAttribute("cy", mouseY);
                    svg.selectedElement.text.setAttribute("y", mouseY);
                    if (svg.selectedElement.end !== 0) svg.selectedElement.end.setAttribute("cy", mouseY);
                    for (i = 0; i < svg.selectedElement.lines1.length; i++)
                    {
                        str = svg.selectedElement.lines1[i].getAttribute("d").split(" ");
                        str[2] = mouseY;
                        
                        //temp = controlPoint(str[1], str[2], str[6], str[7]);
                        //temp = temp.split(' ');
                        //str[5] = temp[1];
                        
                        ty = (+str[5] + (+((+str[2] + (+str[7]))/2)))/2;
                        svg.selectedElement.lines1[i].text.setAttributeNS(null, "y", ty);
						moveTextRect(svg.selectedElement.lines1[i].rect, -1, ty);
                        str = str.join(" ");
                        svg.selectedElement.lines1[i].setAttribute("d", str);
                    }
                    for (i = 0; i < svg.selectedElement.lines2.length; i++)
                    {
                        str = svg.selectedElement.lines2[i].getAttribute("d").split(" ");
                        str[7] = mouseY;
                        
                        //temp = controlPoint(str[1], str[2], str[6], str[7]);
                        //temp = temp.split(' ');
                        //str[5] = temp[1];
                        
                        ty = (+str[5] + (+((+str[2] + (+str[7]))/2)))/2;
                        svg.selectedElement.lines2[i].text.setAttributeNS(null, "y", ty);
						moveTextRect(svg.selectedElement.lines2[i].rect, -1, ty);
                        str = str.join(" ");
                        svg.selectedElement.lines2[i].setAttribute("d", str);
                    }
                }
                break;
            case "path":
				svg.selectedElement.rect.setAttribute('class', 'movable');
                movePath(svg.selectedElement, mouseX, mouseY);
                break;
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

function movePath(line, mouseX, mouseY) {
    var str = line.getAttribute("d").split(" ");
    str[4] = ((+str[1] + (+str[6]))/2)+(2*(mouseX-((+str[1] + (+str[6]))/2)));
    str[5] = ((+str[2] + (+str[7]))/2)+(2*(mouseY-((+str[2] + (+str[7]))/2)));

    var tx = (+str[4] + (+((+str[1] + (+str[6]))/2)))/2;
    var ty = (+str[5] + (+((+str[2] + (+str[7]))/2)))/2;
    line.text.setAttributeNS(null, "x", tx);
    line.text.setAttributeNS(null, "y", ty);
	moveTextRect(line.rect, tx, ty);

    str = str.join(" ");
    line.setAttribute("d", str);
}

function toggleCursor()
{
	var str = renamingTransition.line.name;
	if (renamingTransition.line.text.node.nodeValue.indexOf("|") != -1)
	{
		renamingTransition.line.text.node.nodeValue = str.replace("|", " ");
	}
	else
	{
		renamingTransition.line.text.node.nodeValue = str;
	}
}
function stopTyping()
{
	if (renamingTransition !== 0)
	{
		clearInterval(cursorTimer);
		$(document).unbind("keypress");
		$(document).unbind("keydown");
		renamingTransition.setAttribute("fill", "white");
		renameTransition(renamingTransition.line, renamingTransition.line.name.replace("|", ""));
		if (renamingTransition.line.name == "")
			renameTransition(renamingTransition.line, transitionPrevName);
		/*else if (renamingTransition.line.name == "abc")
		{
			renamingTransition.setAttribute("fill", "red");
		}*/
		renamingTransition.setAttribute("stroke", "black");
		renamingTransition.setAttribute('class', '');
		renamingTransition = 0;
	}
}
function transitionDblClick(evt)
{
	var rect = evt.target;
	var line = rect.line;
	var svg = rect.parentSvg;
	$(document.activeElement).blur();
	renamingTransition = rect;
	transitionPrevName = line.name;
	stopTyping();
	rect.setAttribute("stroke", "lightgreen");
	renamingTransition = rect;
	renamingCursor = line.name.length;
	var newname = line.name.substring(0, renamingCursor) + '|' + line.name.substring(renamingCursor, line.name.length);
	renameTransition(rect.line, newname);
	cursorTimer = setInterval(toggleCursor, 500);
	rect.setAttribute('class', 'editable');
	$(document).keypress(function( event ) {
		
		var key = event.keyCode || event.which || event.charCode;
		var s_key = String.fromCharCode(key);
		if (!incorrectEditorTransitionsCharsSyntax(s_key))
		{
			var newname = line.name.substring(0, renamingCursor) + s_key + line.name.substring(renamingCursor, line.name.length);
			renameTransition(rect.line, newname);
			renamingCursor++;
		}
		event.preventDefault();
	});
	$(document).keydown(function( event ) {
		var key = event.keyCode || event.which || event.charCode;
		console.log(key);
		if (key == 13)	// enter or escape
		{
			stopTyping();
		}
		else if (key == 27)
		{
			renameTransition(rect.line, transitionPrevName);
			stopTyping();
		}
		else if (key == 8)	// backspace
		{
			event.preventDefault();
			var newname = line.name.substring(0, renamingCursor - 1) + line.name.substring(renamingCursor, line.name.length);
			renameTransition(rect.line, newname);
			if (renamingCursor > 0)
				renamingCursor--;
		}
		else if (key == 46)	// delete
		{
			var newname = line.name.substring(0, renamingCursor + 1) + line.name.substring(renamingCursor + 2, line.name.length);
			renameTransition(rect.line, newname);
		}
		else if (key == 35)	// end
		{
			event.preventDefault();
			if (renamingCursor < line.name.length - 1)
			{
				renameTransition(rect.line, line.name.replace("|", ""));
				renamingCursor = line.name.length;
				var newname = line.name.substring(0, renamingCursor) + '|' + line.name.substring(renamingCursor, line.name.length);
				renameTransition(rect.line, newname);
			}
		}
		else if (key == 36)	// home
		{
			event.preventDefault();
			if (renamingCursor > 0)
			{
				renameTransition(rect.line, line.name.replace("|", ""));
				renamingCursor = 0;
				var newname = line.name.substring(0, renamingCursor) + '|' + line.name.substring(renamingCursor, line.name.length);
				renameTransition(rect.line, newname);
			}
		}
		else if (key == 37)	// left arrow
		{
			if (renamingCursor > 0)
			{
				renameTransition(rect.line, line.name.replace("|", ""));
				renamingCursor--;
				var newname = line.name.substring(0, renamingCursor) + '|' + line.name.substring(renamingCursor, line.name.length);
				renameTransition(rect.line, newname);
			}
		}
		else if (key == 39)	// right arrow
		{
			if (renamingCursor < line.name.length - 1)
			{
				renameTransition(rect.line, line.name.replace("|", ""));
				renamingCursor++;
				var newname = line.name.substring(0, renamingCursor) + '|' + line.name.substring(renamingCursor, line.name.length);
				renameTransition(rect.line, newname);
			}
		}
	});
	stopMovingElement(evt);
	moving = false;
}
function stopMovingElement(evt) {
	evt.preventDefault();
    if (movingElement !== 0) {
        movingElement.setAttribute('class', 'none');
		if (movingElement.tagName == "path")
			movingElement.rect.setAttribute('class', 'none');
        movingElement = 0;
    }
}

function editorTransitionsCharsSyntax()
{
	return /[^ =()]/;
}

function incorrectEditorTransitionsCharsSyntax(val)
{
	return (!editorTransitionsCharsSyntax().test(val))
}

function editorTransitionsSyntax()
{
	return /^[^ ,]+(,[^ ,]+)*$/;
}

function incorrectEditorTransitionsSyntax(val)
{
	return (!editorTransitionsSyntax().test(val))
}

function tableTransitionsSyntax()
{
	return /^\{\}$|^\{[^ ,]+(,[^ ,]+)*\}$/;
}

function incorrectTableTransitionsSyntax(val)
{
	return (!tableTransitionsSyntax().test(val))
}

function transitionSyntax()
{
	return /^[^ =(),]+$/;
}

function incorrectTransitionSyntax(val)
{
	return (!transitionSyntax().test(val))
}
function stateSyntax()
{
	return /^[^ =(),]+$/;
}
function incorrectStateSyntax(val)
{
	return (!stateSyntax().test(val));
}