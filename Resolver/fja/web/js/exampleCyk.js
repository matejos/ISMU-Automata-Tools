/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var grammarExample = [
//0
    "S -> BB | b,\nA -> a,\nB -> CA | SA,\nC -> c ",
//1
    "S -> BB | b,\nA -> a,\nB -> CA | SA,\nC -> c "
];

var wordExample = [
//0
    "baca",
//1
    "baca"
];

var answerExample = [
//0
    "t0-9=S t0-8= t1-8= t0-7=B t1-7= t2-7=B t0-6=S t1-6=A t2-6=C t3-6=A",
//1
    "t0-9=S t0-8=B t1-8= t0-7=B t1-7= t2-7= t0-6=S t1-6=C t2-6=C t3-6=A"
];

function ex(which){
    var index = document.getElementById(which).selectedIndex - 1;
    document.getElementById("generate").value=grammarExample[index];
    document.getElementById("generate").focus();
    document.getElementById("generate").blur();
    document.getElementById("word").value=wordExample[index];
    document.getElementById("word").focus();
    document.getElementById("word").blur();
    fillTheTable(answerExample[index]);
}

function fillTheTable(answer){
    var tokens = answer.split(" ");
    for (i = 0; i < tokens.length; i++)
    {
        var token = tokens[i];
        var cellName = token.substr(0, token.indexOf('='));
        var val = token.substr(token.indexOf('=') + 1);
        if (document.getElementById(cellName) != "undefined") {
            document.getElementById(cellName).value = val;
        }
    }
}

