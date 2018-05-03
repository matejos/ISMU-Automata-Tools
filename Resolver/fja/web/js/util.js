/* --------------------------------------------------------------------------
 File:	util.js
 Author:	Radim Cebis
 Usage:	functions for assigning the parser to element's events

 You may use, modify and distribute this software under the terms and conditions
 of the Artistic License. Please see ARTISTIC for more information.
 ----------------------------------------------------------------------------- */

/* -FUNCTION--------------------------------------------------------------------
 Function:		addEvent(obj, evType, fn)

 Usage:			adds event listener (fn) to the object (obj) on event (evType)
 ----------------------------------------------------------------------------- */
function addEvent(obj, evType, fn) {
    if (obj.addEventListener) {
        obj.addEventListener(evType, fn, false);
        return true;
    } else if (obj.attachEvent) {
        var r = obj.attachEvent("on" + evType, fn);
        return r;
    } else {
        return false;
    }
}

/* -FUNCTION--------------------------------------------------------------------
 Function:		register(id, func)
 Param elemType: Textarea object
 Usage:			registers func to element with correct question ID
 ----------------------------------------------------------------------------- */
function register(idTextarea, func, elem) {

    //var elem = elemType;
    function test(evt) {
        if (!evt) var evt = window.event;
        var input = (evt.target) ? evt.target : evt.srcElement;

        var result = func(input.value);
        var textAreaClassName = "form-group ";
        var iconClassName = "glyphicon ";
        var helpClassName = "alert ";
        if (elem.value != "") {
            if (result.error_string != "")
                document.getElementById(idTextarea + "-error-text").innerHTML = htmlentities(result.error_string);
            else
                document.getElementById(idTextarea + "-error-text").innerHTML = "Syntax je korektní.";

            if (result.error == 2) {
                textAreaClassName += "has-error";
                iconClassName += "glyphicon-remove";
                helpClassName += "alert-danger";
            }
            else if (result.error == 1) {
                textAreaClassName += "has-warning";
                iconClassName += "glyphicon-alert";
                helpClassName += "alert-warning";
            }
            else {
                textAreaClassName += "has-success";
                iconClassName += "glyphicon-ok";
                helpClassName += "alert-success";
            }
        }
        else {
            iconClassName = "";
            textAreaClassName = "form-group";
            helpClassName += "alert-info";
            document.getElementById(idTextarea + "-error-text").innerHTML = "Zde se zobrazuje nápověda syntaxe.";
        }
        document.getElementById(idTextarea + "-i").className = iconClassName;
        document.getElementById(idTextarea + "-error").className = helpClassName;
        elem.parentElement.className = textAreaClassName;
    }

    addEvent(elem, 'change', test);
    addEvent(elem, 'keyup', test);
    addEvent(elem, 'focus', test);
    addEvent(elem, 'blur', test);
    addEvent(elem, 'mouseup', test);
    elem.focus();
    elem.blur();
}

function registerAllParser(idTextarea, elem) {
    var Parsers = new Array(
        DFAParser.parse, EFAParser.parse, GRAParser.parse, REGParser.parse, CFGParser.parse
    )
    var ParserNames = new Array(
        "DFA", "NFA/EFA", "GRA", "REG", "CFG")

    function test(evt) {
        var good = "";
        var textAreaClassName = "";
        var iconClassName = "";
        var helpClassName = "";
        for (var i = 0; i < 5; i++) {
            var func = Parsers[i];
            if (!evt) var evt = window.event;
            var input = (evt.target) ? evt.target : evt.srcElement;

            var result = func(input.value);
            textAreaClassName = "form-group ";
            iconClassName = "glyphicon ";
            helpClassName = "alert ";
            if (elem.value == "") {
                iconClassName = "";
                textAreaClassName = "form-group";
                helpClassName += "alert-info";
                document.getElementById(idTextarea + "-error-text").innerHTML = "Zde se zobrazuje nápověda syntaxe.";
            }
            else {
                if (result.error == 2) {
                    textAreaClassName += "has-error";
                    iconClassName += "glyphicon-remove";
                    helpClassName += "alert-danger";
                }
                else if (result.error == 1) {
                    good = ParserNames[i];
                    textAreaClassName += "has-warning";
                    iconClassName += "glyphicon-alert";
                    helpClassName += "alert-warning";
                    break;
                }
                else {
                    good = ParserNames[i];
                    textAreaClassName += "has-success";
                    iconClassName += "glyphicon-ok";
                    helpClassName += "alert-success";
                    break;
                }
            }
        }
        document.getElementById(idTextarea + "-i").className = iconClassName;
        document.getElementById(idTextarea + "-error").className = helpClassName;
        elem.parentElement.className = textAreaClassName;
        if (result.error_string != "")
            document.getElementById(idTextarea + "-error-text").innerHTML = good + ":" + htmlentities(result.error_string);
        else
            document.getElementById(idTextarea + "-error-text").innerHTML = good + ":" + result.error_string;

    }

    addEvent(elem, 'change', test);
    addEvent(elem, 'keyup', test);
    addEvent(elem, 'focus', test);
    addEvent(elem, 'blur', test);
    addEvent(elem, 'mouseup', test);
    elem.focus();
    elem.blur();
}
/* -FUNCTION--------------------------------------------------------------------
 Function:		htmlentities( s )
 Author:			Kevin van Zonneveld (http://kevin.vanzonneveld.net)
 Usage:			converts special characters in string to its html entities
 ----------------------------------------------------------------------------- */
function htmlentities(s) {
    // http://kevin.vanzonneveld.net
    // +   original by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // *     example 1: htmlentities('Kevin & van Zonneveld');
    // *     returns 1: 'Kevin &amp; van Zonneveld'

    var div = document.createElement('div');
    var text = document.createTextNode(s);
    div.appendChild(text);
    return div.innerHTML;
}
/* -FUNCTION--------------------------------------------------------------------
 Function:		invalidate(textboxstyle, textboxelement )
 Author:			Tomas Pastircak
 Usage:			Changes the parser of the textarea by given argument
 ----------------------------------------------------------------------------- */
function invalidate(textboxinput, textboxelement) {
    switch (textboxinput) {
        case 'TOT':
        case 'DFA':
        case 'MIN':
        case 'MIC':
            register(textboxelement, DFAParser.parse, document.getElementById(textboxelement));
            break;
        case 'NFA':
            register(textboxelement, NFAParser.parse, document.getElementById(textboxelement));
            break;
        case 'EFA':
            register(textboxelement, EFAParser.parse, document.getElementById(textboxelement));
            break;
        case 'GRA':
            register(textboxelement, GRAParser.parse, document.getElementById(textboxelement));
            break;
        case 'REG':
            register(textboxelement, REGParser.parse, document.getElementById(textboxelement));
            break;
        case 'ALL':
            registerAllParser(textboxelement, document.getElementById(textboxelement));
            break;
        case 'NE1':
        case 'NE2':
        case 'RED':
        case 'EPS':
        case 'SRF':
        case 'PRO':
        case 'CNF':
        case 'RLR':
        case 'GNF':
        case 'CFG':
            register(textboxelement, CFGParser.parse, document.getElementById(textboxelement));
            break;
    }
}

/* -FUNCTION--------------------------------------------------------------------
 Function:		printHeader(session, activeMenu )
 Author:		Matej Poklemba
 Usage:			Prints the header with information about current session and with the active menu selected
 ----------------------------------------------------------------------------- */
function printHeader(session, activeMenu) {
    var str = "";
    str += "<nav class='navbar navbar-default'>\n";
    str += "<div class='container-fluid'>\n";
    str += "<div class='navbar-header'>\n";
    str += "<img src='style/logotyp.png' class='logo'>";
    str += "</div>\n";
    str += "<ul class='nav navbar-nav'>\n";
    str += "<li class='dropdown" + (activeMenu == "reg" ? " active" : "") + "'>\n";
    str += "<a class='dropdown-toggle' data-toggle='dropdown' href='#'>\n";
    str += "Regulární jazyky\n";
    str += "<span class='caret'></span></a>\n";
    str += "<ul class='dropdown-menu'>\n";
    str += "<li><a href='./index.jsp'>Simulace odpovědníku</a></li>\n";
    str += "<li><a href='./convert.jsp'>Převod</a></li>\n";
    str += "</ul>\n";
    str += "</li>\n";
    str += "<li class='dropdown" + (activeMenu == "cfg" ? " active" : "") + "'>\n";
    str += "<a class='dropdown-toggle' data-toggle='dropdown' href='#'>\n";
    str += "Bezkontextové gramatiky\n";
    str += "<span class='caret'></span></a>\n";
    str += "<ul class='dropdown-menu'>\n";
    str += "<li><a href='./indexcfg.jsp'>Simulace odpovědníku</a></li>\n";
    str += "<li><a href='./convertcfg.jsp'>Převod</a></li>\n";
    str += "<li><a href='./cyk.jsp'>C-Y-K</a></li>\n";
    str += "</ul>\n";
    str += "</li>\n";
    str += "</ul>\n";
    str += "<ul class='nav navbar-nav navbar-right'>\n";
    if (session != "") {
        str += "<li><a>P&#345;ihlá&#353;en jako " + session + "</a></li>\n";
        str += "<li><a href='Logout'>Odhlásit</a></li>\n";
    }
    str += "<li" + (activeMenu == "admin" ? " class='active'" : "") + "><a href='./admin.jsp' title='Nastavení'>Nastavení</a></li>\n";
    if (activeMenu == "reg" || activeMenu == "cfg") {
        str += "<li" + (activeMenu == "help" ? " class='active'" : "") + "><a href='#' data-toggle='modal' data-target='#myModal' title='Nápověda'>Nápověda</a></li>\n";
    }
    str += "<li" + (activeMenu == "about" ? " class='active'" : "") + "><a href='./author.jsp' title='O aplikaci'>O aplikaci</a></li>\n";
    str += "</ul>\n";
    str += "</div>\n";
    str += "</nav>\n";
    if (activeMenu == "reg") {
        str += printHelpReg();
    }
    else if (activeMenu == "cfg") {
        str += printHelpCfg();
    }
    return str;
}

/* -FUNCTION--------------------------------------------------------------------
 Function:		printHelpReg()
 Author:		Matej Poklemba
 Usage:			Prints help modal for regular languages
 ----------------------------------------------------------------------------- */
function printHelpReg() {
    var str = "";
    str += printHelpStart("Popis formalismu");

    str += '<div class="helpfile"><ul><li><a href="#fa">DFA, NFA a EFA - (ne)deterministicky konečný automat (s ɛ-kroky)</a>\n';
    str += '<li><a href="#tot">TOT, MIN, MIC - Speciální případy DFA</a>\n';
    str += '<li><a href="#gra">GRA - regulární gramatika</a>\n';
    str += '<li><a href="#reg">REG - regulární výraz</a>\n';
    str += '<li><a href="#all">ALL - libovolný formalismus</a>\n';
    str += '</ul></div>\n';
    str += '<a name="fa"></a><h2>DFA, NFA, EFA - Popis jazyka konečným automatem</h2>\n';
    str += '<p>Jazyk v těchto formalismech je možné zapsat jako počáteční stav (nepovinný), množinu pravidel a množinu koncových stavů. Každý z nich může obsahovat povolené symboly nebo escapované speciální symboly, viz níže.\n';
    str += '<h4>Počáteční stav</h4>\n';
    str += '<p>Počáteční stav v automatu definujeme jako <code>init=NazevStavu</code>. Definujeme jej jako první řádek formalismu. Pokud definice jazyka neobsahuje explicitní definici počátečního stavu, pak je považován za počáteční stav ten, který je zapsán jako první.</p>\n';
    str += '<h4>Přechodová funkce</h4>\n';
    str += '<p>Přechodovou funkci definujeme jako pravidla ve tvaru <code>(NazevVstupnihoStavu,Znak)=NazevVystupnihoStavu</code> pro deterministické automaty a <code>(NazevVstupnihoStavu,Znak)={NazevVystupnihoStavu1,NazevVystupnihoStavu2, ... ,NazevVystupnihoStavuN}</code>pro nedeterministické automaty.<br>\n';
    str += 'Pro automaty s ɛ-kroky můžeme použít jako znak také sekvenci <code>\\e</code> jako ɛ-krok.</p>\n';
    str += '<h4>Množina koncových stavů</h4>\n';
    str += '<p>Množinu koncových stavů definujeme jako <code>final={KoncovyStav1,KoncovyStav2,...KoncovyStavN}</code>.</p>\n';
    str += '<h4>Speciální znaky v definici automatu</h4>\n';
    str += '<p>Speciálními znaky jsou symboly {,},(,),=,\\,",čárka a bílé znaky. Tyto znaky nemůžeme použít samostatně, musíme je použít po znaku zpětného lomítka \\.</p>\n';
    str += '<h4>Rozšíření přechodové funkce</h4>\n';
    str += '<p>Jako znak můžeme definovat také sekvenci znaků uzavřených do uvozovek, s nimi se poté pracuje jako s více přechody pod více znaky. V uvozovkách je možné používat speciální symboly mimo symbolu zpětného lomítka a symbolu uvozovek, ty musíme používat jako \\" a \\\\.</p>\n';
    str += '<a name="tot"></a><h2>TOT,MIN,MIC - Speciální případy deterministického automatu</h2>\n';
    str += '<p>Všechny takto zapsané jazyky musí splňovat pravidla deterministického automatu a zároveň další upřesňující pravidla. Splnění upřesňujících pravidel není kontrolováno pomocí kontroly syntaxe.\n';
    str += '<h4>TOT - Totální deterministický konečný automat</h4>\n';
    str += '<p>Totální automat musí mít pro každý stav definovány přechody pod všemi znaky z abecedy.</p>\n';
    str += '<h4>MIN - Minimální deterministický konečný automat</h4>\n';
    str += '<p>Minimální automat musí splňovat pravidla totálního automatu a navíc musí platit, že neexistuje žádný automat s nižším počtem stavů, který popisuje shodný jazyk jako zadaný automat.</p>\n';
    str += '<h4>MIC - Minimální kanonický deterministický konečný automat</h4>\n';
    str += '<p>Minimální kanonický automat musí splňovat pravidla minimálního automatu a dále musí mít všechny stavy pojmenované nepřerušenou posloupností znaků, kdy počáteční stav se vždy jmenuje A a každý další dosažený stav se jmenuje podle následujícího znaku v kódování UTF-8. Přechody do dalších stavů jsou prováděny jako prohledávání grafu do šířky pod všemi znaky z abecedy seřazenými opět podle kódování UTF-8. Tento zápis je pro každý automat unikátní.</p>\n';
    str += '<a name="gra"></a><h2>GRA - Popis jazyka gramatikou</h2>\n';
    str += '<p>Jazyk je možné popsat gramatikou, z níž definujeme pouze množinu pravidel, kterou zapisujeme jako <code>N -> tN | tN | t</code>, kde N je neterminál a t je terminál. Jednotlivé řádky od sebe musí být odděleny koncem řádku.\n';
    str += '<h4>Neterminál</h4>\n';
    str += '<p>Neterminál je zapsán buď jedním znakem velké anglické abecedy, nebo jako více znaků uzavřených do špičatých závorek &lt; &gt;. V nich mohou být uzavřeny i speciální symboly, viz. níže.</p>\n';
    str += '<h4>Terminál</h4>\n';
    str += '<p>Terminál je zapsán jedním znakem, který není velkým písmenem anglické abecedy ani speciálním symbolem, nebo jako více znaků uzavřených do uvozovek "". V nich mohou být uzavřeny i speciální symboly, viz. níže. Pokud je terminální symbol zapsán do uvozovek, je s ním zacházeno jako s více jednoznakovými terminály.</p>\n';
    str += '<h4>Speciální symboly</h4>\n';
    str += '<p>Za speciální symboly jsou v zápisu gramatiky považovány <, >, -, |, ", mezera, čárka, konec řádku. Pokud chceme uvést, že gramatika přijímá prázdné slovo, můžeme na pravé straně prvního (počátečního) pravidla uvést symbol \\e jako symbol prázdného slova. Pak se ale počáteční neterminál nesmí vyskytovat na pravé straně žádného pravidla.\n';
    str += '<a name="reg"></a><h2>REG - Popis jazyka regulárním výrazem</h2>\n';
    str += '<h4>Základní regulární výrazy</h4>\n';
    str += '<p>Jako základní regulární výrazy můžeme používat kterékoliv znaky abecedy mimo +, ^+, *, ^*, (, ), tečka. Regulární výraz může také popisovat prázdné slovo, pak jej zapíšeme jako \\e. Může také popisovat prázdný jazyk, pak jej popíšeme jako \\0.\n';
    str += '<h4>Složené regulární výrazy</h4><p>\n';
    str += 'Jakékoliv regulární výrazy můžeme uzavřít do závorek, tím se vyhodnotí jako první a pracuje se s nimi podobně jako se základním regulárním výrazem. Nejvyšší prioritu má operátor *, ^* a ^+, tedy iterace a pozitivní iterace. Druhou nejvyšší prioritu má operátor . (zřetězení), který nemusíme uvádět (a.b je ekvivalentní ab). Pokud není uvedena tečka, není výpočet považován za těsné zřetězení, tedy <code>ab*</code> není ekvivalentní <code>(ab)*</code> a je ekvivalentní <code>a.b*</code>. Nejnižší prioritu má operátor sjednocení +.\n';
    str += '<a name="all"></a><h2>ALL - možnost zadání více způsoby</h2>\n';
    str += 'V tomto režimu je možné jazyk zapsat více způsoby, kdy nápověda syntaxe radí, jak se bude vyhodnocovat. Výraz se snaží vyhodnotit postupně jako DFA, EFA, GRA a REG. Pozor - pokud nápověda syntaxe uvádí, že je nutno něco doplnit, může se výraz vyhodnocovat jinak, zpravidla jako REG (např. při zadání A bude kontrola syntaxe nabízet rozšíření na gramatiku, ale jazyk se správně vyhodnotí jako regulární výraz). Jedná se zejména o triviální případy, v těch je možné použít dodatečné ozávorkování celého výrazu pro přepnutí kontroly syntaxe na REG.\n';

    str += printHelpEnd();
    return str;
}

/* -FUNCTION--------------------------------------------------------------------
 Function:		printHelpCfg()
 Author:		Matej Poklemba
 Usage:			Prints help modal for context-free grammars
 ----------------------------------------------------------------------------- */
function printHelpCfg() {
    var str = "";
    str += printHelpStart("Popis formalismu");

    str += '<h1>CFG - Bezkontextová gramatika, syntaxe</h1>\n';
    str += '<p>Gramatika je typu 2, jestliže každé její pravidlo je tvaru A -> α, kde |α|>=1 s eventuální výjimkou pravidla S -> \\e, pokud se S nevyskytuje na pravé straně žádného pravidla.<br>\n';
    str += '<h4>Iniciální neterminál</h4>\n';
    str += '<p>Iniciálním neterminálem je zvolen první nalezený neterminál.</p>\n';
    str += '<h4>Neterminál</h4>\n';
    str += '<p>Neterminál je symbol z množiny <code>{A, ..., Z}</code>, popř. sekvence povolených symbolů uzavřených do zobáčků: &lt;cokoli&gt; popř. terminál nebo symbol množiny <code>{A, ..., Z}</code> s libovolným počtem apostrofů.</p>\n';
    str += '<h4>Terminál</h4>\n';
    str += '<p>Terminál je libovolný symbol různý od neterminálního symbolu a speciálních symbolů.</p>\n';
    str += '<h4>Pravidla</h4>\n';
    str += '<p>Pravidla jsou v takovém tvaru, aby odpovídala pravidlům gramatik typu 2. Jednotlivá pravidla jsou od sebe oddělena čárkami nebo novým řádkem nebo čárkou a novým řádkem.</p>\n';
    str += '<h4>Speciální symboly</h4>\n';
    str += '<p>Speciální symboly jsou "\\n", ",", "<", ">", "|", "\\", " \' ". Epsilon se značí jako <code>\\e</code>.</p>\n';

    str += '<h2>Simulace odpovědníku</h2>\n';
    str += '<p>Simulace odpovědníku reprezentuje vyhodnocování odpovědí úkolů.<br>\n';
    str += 'Do pravého formuláře se zadává studentovo řešení úkolu. Do levého formuláře se zadává zadání úkolu, nikoli jeho správné řešení. V případě zadání správného řešení by totiž mohlo dojít k opětovné transformaci zadané gramatiky, což by ve výsledku mohlo vést ke špatnému vyhodnocení příkladu.<br>\n';
    str += 'Ke korektnímu vyhodnocení odpovědi je také nutno zvolit patřičný druh transformace, který se po studentovi v zadání požadoval.</p>\n';
    str += '<h4>Módy odpovědí</h4>\n';
    str += '<p>Lze zvolit celkem dva módy odpovědí. Normální mód vypisuje informace o chybách, kdy např. při odstranění jednoduchých pravidel je nejdříve gramatika studenta kontrolována, zda byla odstraněna epsilon pravidla, v případě, že nikoli, je student na daný fakt upozorněn. V případě, že je studentova gramatika zkontrolována, že neobsahuje epsilon pravidla, ani jednoduchá pravidla, teprve až pak je porovnána s modelem správné odpovědi. Student má tedy k dispozici obsáhlou kontrolu řešení.<br>\n';
    str += 'IS mód pouze vypisuje true, je-li odpověď správná, jinak vypisuje false.</p>\n';

    str += '<h2>Převody gramatik</h2>\n';
    str += '<p>V sekci převod jde převést bezkontextovou gramatiku do všech možných forem. Konkrétně lze provést převody odstranění nenormovaných symbolů, odstranění nedosažitelných symbolů, redukce, odstranění epsilon kroků, odstranění jednoduchých pravidel, převod na vlastní gramatiku, převod do Chomského normální formy, odstranění levé rekurze a převod do Greibachové normální formy.</p>\n';
    str += '<h4>Módy odpovědí</h4>\n';
    str += '<p>Opět lze vybrat ze dvou módu odpovědí - normální a detailní. V normálním módu je zobrazena pouze výsledná transformace gramatiky, kdežto v módu detailním je vypsána celá posloupnost transformací gramatiky až do její požadované formy.</p>\n';
    str += '<h4>Řetězec pro odpovědník</h4>\n';
    str += '<p>Učitel může zvolit možnost vygenerování řetězce reprezentující model zadání úkolu do odpovědníku ISu.</p>\n';

    str += printHelpEnd();
    return str;
}

/* -FUNCTION--------------------------------------------------------------------
 Function:		printHelpStart(title)
 Author:		Matej Poklemba
 Usage:			Prints the beginning of the help modal with the given title
 ----------------------------------------------------------------------------- */
function printHelpStart(title) {
    var str = "";
    str += '<div class="modal fade" id="myModal" role="dialog">\n';
    str += '<div class="modal-dialog help-modal">\n';
    str += '<div class="modal-content">\n';
    str += '<div class="modal-header">\n';
    str += '<button type="button" class="close" data-dismiss="modal">&times;</button>\n';
    str += '<h4 class="modal-title">' + title + '</h4>\n';
    str += '</div>\n';
    str += '<div class="modal-body">\n';
    return str;
}

/* -FUNCTION--------------------------------------------------------------------
 Function:		printHelpEnd()
 Author:		Matej Poklemba
 Usage:			Prints the end of the help modal
 ----------------------------------------------------------------------------- */
function printHelpEnd() {
    var str = "";
    str += '</div>\n';
    str += '<div class="modal-footer">\n';
    str += '<button type="button" class="btn btn-default" data-dismiss="modal">Zavřít</button>\n';
    str += '</div>\n';
    str += '</div>\n';
    str += '</div>\n';
    str += '</div>\n';
    return str;
}