/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var teacherA= new Array(
//0
            "(S,a)={S,A} (S,b)={S}\n" + "(A,b)={AB}\n" + "(AB,b)={ABB}\n"
            + "(ABB,a)={ABB} (ABB,b)={ABB}\n" + "final={ABB}",
//1
            "(X,a)=Y (Y,b)=Z (Z,c)=Z\nfinal={Z}",
//2
            "(1,a)=2 (1,b)=3\n" + "(2,a)=4 (2,b)=5\n" + "(3,b)=1\n"
            + "(4,a)=6 (4,b)=6\n" + "(5,a)=4 (5,b)=7\n" + "(6,a)=6 (6,b)=9\n"
            + "(7,a)=4 (7,b)=7\n" + "(8,a)=2 (8,b)=5\n" + "(9,a)=6 (9,b)=9\n"
            + "final={1,3,4,5,6,7,9}\n",
//3
            "(A,m)={C,D}\n" + "(B,n)={C}\n" + "(C,m)={A,D}\n"
            + "(D,n)={B}\n" + "final={B}",
//4
            "(A,x)=D (A,y)=B\n" + "(B,x)=C\n" + "(C,y)=A\n" + "(D,x)=C\n"
            + "final={B,D}",
//5
            "(1,a)={1,2} (1,\\e)={2}\n" + "(2,a)={5} (2,b)={3,5}\n"
            + "(3,b)={6}\n" + "(4,b)={4} (4,\\e)={1,5}\n"
            + "(5,a)={5} (5,c)={3} (5,\\e)={6}\n" + "(6,c)={3,6} (6,\\e)={2}\n"
            + "final={6}",
//6
            "S -> aA| bC| a|\\e\n" + "A -> bB| aA| b | c \n"
            + "B -> aB| bC| aC| cA| c\n" + "C -> a | b | aA| bB",
//7
            "(QQ,0)=0Q (QQ,1)=Q1\n" + "(0Q,0)=QQ (0Q,1)=01\n"
            + "(Q1,0)=01 (Q1,1)=QQ\n" + "(01,0)=Q1 (01,1)=0Q\nfinal={QQ}",
 //8
            "(ab)^*",
//9
            "(A,x)=B (A,y)=B\n" + "(B,x)=C (B,y)=C\n" + "(C,x)=A (C,y)=A\n"
            + "final={A} ",
//10
            "(A,a)=A (A,b)=B (A,c)=B \n" + "(B,a)=B (B,b)=A (B,c)=A \n"+ "final={B}\n"
);

var studentA=new Array(
//0
        "(q0,a)=q1 (q0,b)=q0\n" + "(q1,a)=q1 (q1,b)=q2\n"
            + "(q2,a)=q1 (q2,b)=q3\n" + "(q3,a)=q3 (q3,b)=q3\n" + "final={q3}",
//1
        "(X,a)=Y (X,b)=N (X,c)=N\n" + "(Y,a)=N (Y,b)=Z (Y,c)=N\n"
            + "(Z,a)=N (Z,b)=N (Z,c)=Z\n" + "(N,a)=N (N,b)=N (N,c)=N\n" + "final={Z}",
//2
        "(A,a)=B (A,b)=C\n" + "(B,a)=D (B,b)=D\n" + "(C,a)=E (C,b)=A\n"
            + "(D,a)=D (D,b)=D\n" + "(E,a)=E (E,b)=E\n" + "final={A,C,D}",
//3
        "" + "(A,m)=B\n" + "(B,m)=C (B,n)=D\n" + "(C,m)=B (C,n)=D\n"
            + "(D,n)=E\n" + "(E,m)=C\n" + "final={D}",
//4
        "" + "(A,x)=B (A,y)=C\n" + "(B,x)=D (B,y)=E\n"
            + "(C,x)=D (C,y)=E\n" + "(D,x)=E (D,y)=A\n" + "(E,x)=E (E,y)=E\n"
            + "final={B,C}",
//5
        "(1,a)={1,2,5,6} (1,b)={2,3,5,6}\n"
            + "(2,a)={2,5,6} (2,b)={2,3,5,6}\n" + "(3,b)={2,6}\n"
            + "(4,a)={1,2,5,6} (4,b)={1,2,3,4,5,6} (4,c)={2,3,6}\n"
            + "(5,a)={2,5,6} (5,b)={2,3,5,6} (5,c)={2,3,6}\n"
            + "(6,a)={2,5,6} (6,b)={2,3,5,6} (6,c)={2,3,6}\n" + "final={6}",
//6
        "(S,a)={A,N} (S,b)={C}\n" + "(A,a)={A} (A,b)={B,N} (A,c)={N}\n"
            + "(B,a)={B,C} (B,b)={C} (B,c)={A,N}\n" + "(C,a)={A,N} (C,b)={B,N}\n"
            + "final={S, N}",
//7
        "S -> \\e | 0<0> | 1<1>\n" + "<0> -> 0 | 0<0011> | 1<01>\n"
            + "<1> -> 1 | 1<0011> | 0<01>\n" + "<01> -> 0<1> | 1<0>\n"
            + "<0011> -> 0<0> | 1<1>",
//8
        "S -> \\e | aB \n" + "B -> bA | b \n" + "A -> aB",
//9
        "((x+y)(x+y)(x+y))*",
//10
        "(a+(b+c).a^*.(b+c))^*.(b+c).a^*");


var teacherF=new Array("EFA","DFA","DFA","EFA","DFA","EFA","GRA","DFA","REG","DFA","DFA");
var studentF=new Array("MIN","TOT","MIC","DFA","TOT","NFA","EFA","GRA","GRA","REG","ALL");
function ex(which){
    document.getElementById("t").value=teacherA[which];
    document.getElementById("s").value=studentA[which];
    setCheckedValue(document.forms['equality'].elements['teach'], teacherF[which],'t');
    setCheckedValue(document.forms['equality'].elements['stud'], studentF[which],'s');
    //TODO
    //document.getElementsByName("stud")
}

// set the radio button with the given value as being checked
// do nothing if there are no radio buttons
// if the given value does not exist, all the radio buttons
// are reset to unchecked
// source: http://www.somacon.com/p143.php
function setCheckedValue(radioObj, newValue, elem) {
	if(!radioObj)
		return;
	var radioLength = radioObj.length;
	if(radioLength === undefined) {
		radioObj.checked = (radioObj.value === newValue.toString());
		return;
	}
	for(var i = 0; i < radioLength; i++) {
		radioObj[i].checked = false;
		if(radioObj[i].value === newValue.toString()) {
			radioObj[i].checked = true;
                        invalidate(radioObj[i].value, elem);
		}
	}
}

