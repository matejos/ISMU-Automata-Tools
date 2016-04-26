var svgns = "http://www.w3.org/2000/svg";
var movingElement = 0;
var renamingTransition = 0;
var transitionPrevName;
var circleSize = 25;
var cursorTimer;
var modeEnum = Object.freeze({
    ADD_STATE: 1,
    ADD_TRANSITION: 2,
    SELECT: 3
});

function init(id) {
    var wp = document.getElementById(id);


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

    wp.appendChild(button1);
    wp.appendChild(button2);
    wp.appendChild(button3);
    wp.appendChild(button4);
    wp.appendChild(textBox);
	wp.appendChild(button5);
    wp.appendChild(button6);

    var p1 = document.createElement("p");
    wp.appendChild(p1);


	var mydiv = document.createElement("DIV");
	mydiv.setAttribute("style", "background-color: pink;");
	mydiv.setAttribute("class", "canvas");
	mydiv.setAttributeNS(null, "id", "mydiv");
	$(mydiv).resizable();
	wp.appendChild(mydiv);
	
	
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
    initState.setAttributeNS(null, "cx", 50);
    initState.setAttributeNS(null, "cy", 50);
    initState.setAttributeNS(null, "r", circleSize);
    initState.setAttributeNS(null, "fill", "white");
    initState.setAttributeNS(null, "stroke", "black");
    initState.setAttributeNS(null, "stroke-width", 1);
    initState.parentSvg = svg;
    initState.parentRect = rect;
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

function button3Click(rect) {
    var svg = rect.parentSvg;
    if ((svg.selectedElement !== 0) && (svg.selectedElement.tagName == "circle")) {
        if (svg.selectedElement.end === 0) {
            var shape = document.createElementNS(svgns, "circle");
            shape.setAttributeNS(null, "cx", svg.selectedElement.getAttribute("cx"));
            shape.setAttributeNS(null, "cy", svg.selectedElement.getAttribute("cy"));
            shape.setAttributeNS(null, "r", circleSize - 5);
            shape.setAttributeNS(null, "fill", "lightgreen");
            shape.setAttributeNS(null, "stroke", "black");
            shape.setAttributeNS(null, "stroke-width", 1);
            shape.parentSvg = rect.parentSvg;
            shape.parentRect = rect;
            shape.setAttribute('pointer-events', 'none');
            svg.selectedElement.end = shape;
            putOnTop(svg.selectedElement);
        } else {
            rect.parentSvg.removeChild(svg.selectedElement.end);
            svg.selectedElement.end = 0;
        }
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

function button6Click(rect) {
    var svg = rect.parentSvg;
    var finalStates = [];
    var out = "init=A ";
    for (i = 0; i < rect.states.length; i++)
    {
        if (rect.states[i].end !== 0)
            finalStates.push(rect.states[i]);
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
    if (finalStates.length === 0)
    {
        alert("Žádný koncový stav!");
    }
    else
    {
        out += "F={";
        for (i = 0; i < finalStates.length; i++)
    	{
            out += finalStates[i].name;
            if (i < finalStates.length - 1)
                out += ",";
        }
        out +="}";
    	//alert(out);
		var x = parseInt(svg.divId.substring(1, svg.divId.length)) - 1;
		console.log(document.getElementsByTagName('textarea'));
		document.getElementsByTagName('textarea')[x].value = out; 
    }
}

function createState(evt) 
{
	var el = evt.target;
	if (el.parentSvg.selectedElement !== 0) deselectElement(el.parentSvg);
	var shape = document.createElementNS(svgns, "circle");
	var x = evt.offsetX;
	var y = evt.offsetY;
	var width = el.parentSvg.div.offsetWidth;
	var height = el.parentSvg.div.offsetHeight;
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
	shape.parentSvg = el.parentSvg;
	shape.parentRect = el;
	shape.end = 0;
	shape.lines1 = [];
	shape.lines2 = [];
	var names = [];
	for (k = 65; k < 91; k++)
		names.push(String.fromCharCode(k));
	for (k = 0; k < el.states.length; k++)
		names.splice(names.indexOf(el.states[k].name), 1);
	var name = names[0];
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

	el.states.push(shape);
	putOnTop(shape);
	el.button1.style.borderStyle = "outset";
	el.mode = modeEnum.SELECT;
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
                var x1 = state.parentSvg.makingTransition.getAttribute("cx");
                var y1 = state.parentSvg.makingTransition.getAttribute("cy");
                var x2 = state.getAttribute("cx");
                var y2 = state.getAttribute("cy");
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
                aLine.parentSvg = state.parentSvg;
                aLine.name = 'a';
                aLine.start = state.parentSvg.makingTransition;
                aLine.end = state;
                
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
                newText.parentSvg = state.parentSvg;
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
				newRect.parentSvg = state.parentSvg;
				newRect.setAttributeNS(null, 'onmousedown', 'selectElement(evt)');
                newRect.setAttributeNS(null, 'onmouseup', 'stopMovingElement(evt)');
                newRect.setAttributeNS(null, 'onmousemove', 'prevent(evt)');
				newRect.line = aLine;
				$(newRect).dblclick(transitionDblClick);

                aLine.text = newText;
				aLine.rect = newRect;
                newText.line = aLine;
                
                state.parentSvg.appendChild(aLine);
				state.parentSvg.appendChild(newRect);
                state.parentSvg.appendChild(aLine.text);
                putOnTop(state);
                putOnTop(state.parentSvg.makingTransition);
                whitenState(state.parentSvg.makingTransition);
                state.parentSvg.makingTransition.lines1.push(aLine);
                state.lines2.push(aLine);
                state.parentRect.mode = modeEnum.SELECT;
                state.parentRect.button2.style.borderStyle = "outset";
                
                /*var debug = state.parentSvg.makingTransition.name + " -> ";
                for (i = 0; i < state.parentSvg.makingTransition.lines1.length; i++)
                    debug += state.parentSvg.makingTransition.lines1[i].end.name + ",";
                console.log(debug);*/
				
                state.parentSvg.makingTransition = 0;
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
function renameTransition(rect, str)
{
	var line = rect.line;
	line.name = str;
	line.text.node.nodeValue = str;
	rect.setAttribute("width", line.text.getComputedTextLength() + 8);
	moveTextRect(rect, line.text.getAttribute('x'), line.text.getAttribute('y'));
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
		renameTransition(renamingTransition, renamingTransition.line.name.replace("|", ""));
		if (renamingTransition.line.name == "")
			renameTransition(renamingTransition, transitionPrevName);
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
	renameTransition(rect, newname);
	cursorTimer = setInterval(toggleCursor, 500);
	rect.setAttribute('class', 'editable');
	$(document).keypress(function( event ) {
		
		var key = event.keyCode || event.which || event.charCode;
		var s_key = String.fromCharCode(key);
		if (/[a-z,]/.test(s_key))
		{
			var newname = line.name.substring(0, renamingCursor) + s_key + line.name.substring(renamingCursor, line.name.length);
			renameTransition(rect, newname);
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
			renameTransition(rect, transitionPrevName);
			stopTyping();
		}
		else if (key == 8)	// backspace
		{
			event.preventDefault();
			var newname = line.name.substring(0, renamingCursor - 1) + line.name.substring(renamingCursor, line.name.length);
			renameTransition(rect, newname);
			if (renamingCursor > 0)
				renamingCursor--;
		}
		else if (key == 46)	// delete
		{
			var newname = line.name.substring(0, renamingCursor + 1) + line.name.substring(renamingCursor + 2, line.name.length);
			renameTransition(rect, newname);
		}
		else if (key == 35)	// end
		{
			event.preventDefault();
			if (renamingCursor < line.name.length - 1)
			{
				renameTransition(rect, line.name.replace("|", ""));
				renamingCursor = line.name.length;
				var newname = line.name.substring(0, renamingCursor) + '|' + line.name.substring(renamingCursor, line.name.length);
				renameTransition(rect, newname);
			}
		}
		else if (key == 36)	// home
		{
			event.preventDefault();
			if (renamingCursor > 0)
			{
				renameTransition(rect, line.name.replace("|", ""));
				renamingCursor = 0;
				var newname = line.name.substring(0, renamingCursor) + '|' + line.name.substring(renamingCursor, line.name.length);
				renameTransition(rect, newname);
			}
		}
		else if (key == 37)	// left arrow
		{
			if (renamingCursor > 0)
			{
				renameTransition(rect, line.name.replace("|", ""));
				renamingCursor--;
				var newname = line.name.substring(0, renamingCursor) + '|' + line.name.substring(renamingCursor, line.name.length);
				renameTransition(rect, newname);
			}
		}
		else if (key == 39)	// right arrow
		{
			if (renamingCursor < line.name.length - 1)
			{
				renameTransition(rect, line.name.replace("|", ""));
				renamingCursor++;
				var newname = line.name.substring(0, renamingCursor) + '|' + line.name.substring(renamingCursor, line.name.length);
				renameTransition(rect, newname);
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