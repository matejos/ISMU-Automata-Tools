<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Language" content="cs" />
        <title>Vyhodnocovací služba pro regulární jazyky</title>
        <link rel="stylesheet" type="text/css" href="style/style_reg.css">
  </head>
    <body>
        <div class="header" id="header">
            <div class="topLine">
            </div>
            <div class="headerAuthor">
            </div>
            <div class="menuLine">
                <div class="innerMenu">
                    <ul class="menuServices">
                        <li><a class="current" href="./index.jsp" title="Regulární jazyky">Regulární jazyky</a></li>
                        <li><a href="./indexcfg.jsp" title="Bezkontextové gramatiky">Bezkontextové gramatiky</a></li>
                    </ul>
                    <ul class="menu">
                        <% if ((session.getAttribute("Login") != null)){%>
                            <li>P&#345;ihlá&#353;en jako "<c:out value="${sessionScope.Login}"/>"</li>
                            <li><a href="${pageContext.request.contextPath}/Logout">Odhlásit</a></li>
                        <%}%>
                        <li><a href="./admin.jsp" title="Nastavení">Nastavení</a></li>
                        <li><a class="current" href="./help.jsp" title="Nápověda">Nápověda</a></li>
                        <li><a href="./author.jsp" title="O aplikaci">O aplikaci</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="page">
         <div class="content">
         <div class="window2">
         <div class="innerWindow">
                <h1>Popis formalismu</h1>
                <div class="helpfile">
                <ul>
                  <li><a href="#fa">DFA, NFA a EFA - (ne)deterministicky konečný automat (s ɛ-kroky)</a>
                  <li><a href="#tot">TOT, MIN, MIC - Speciální případy DFA</a>
                  <li><a href="#gra">GRA - regulární gramatika</a>
                  <li><a href="#reg">REG - regulární výraz</a>
                  <li><a href="#all">ALL - libovolný formalismus</a>
                </ul>
                </div>
                <a name="fa"></a><h2>DFA, NFA, EFA - Popis jazyka konečným automatem</h2>
                <p>Jazyk v těchto formalismech je možné zapsat jako počáteční stav (nepovinný), množinu pravidel a množinu koncových
                    stavů. Každý z nich může obsahovat povolené symboly nebo escapované speciální symboly, viz níže.
                <h3>
                    Počáteční stav
                </h3>
                <p>Počáteční stav v automatu definujeme jako <span class="code">init=NazevStavu</span>. Definujeme jej
                    jako první řádek formalismu. Pokud definice jazyka neobsahuje explicitní definici počátečního stavu, pak je považován
                    za počáteční stav ten, který je zapsán jako první.</p>
                <h3>Přechodová funkce</h3>
                <p>Přechodovou funkci definujeme jako pravidla ve tvaru
                    <span class="code">(NazevVstupnihoStavu,Znak)=NazevVystupnihoStavu</span> pro deterministické automaty a
                    <span class="code">(NazevVstupnihoStavu,Znak)={NazevVystupnihoStavu1,NazevVystupnihoStavu2, ... ,NazevVystupnihoStavuN}
                    </span>pro nedeterministické automaty. Pravidel je obvykle více, je nutné je oddělit bílým znakem.
                    Pro automaty s ɛ-kroky můžeme použít jako znak také sekvenci
                     <span class="code">\e</span> jako ɛ-krok.
                
                    <h3>Množina koncových stavů</h3>
                    <p>Množinu koncových stavů definujeme jako
                    <span class="code">final={KoncovyStav1,KoncovyStav2,...KoncovyStavN}</span>.
                    </p>

                    <h3>Speciální znaky v definici automatu</h3>
                    <p>Speciálními znaky jsou symboly {,},(,),=,\,",čárka a bílé znaky. Tyto znaky nemůžeme použít samostatně,
                    musíme je použít po znaku zpětného lomítka \.</p>

                    <h3>Rozšíření přechodové funkce</h3>
                    <p>Jako znak můžeme definovat také sekvenci znaků uzavřených do uvozovek, s nimi se poté pracuje jako s více
                    přechody pod více znaky. V uvozovkách je možné používat speciální symboly mimo symbolu zpětného lomítka a symbolu uvozovek,
                    ty musíme používat jako \" a \\.
                    </p>
                <a name="tot"></a><h2>TOT,MIN,MIC - Speciální případy deterministického automatu</h2>
                <p>Všechny takto zapsané jazyky musí splňovat pravidla deterministického automatu a zároveň další upřesňující pravidla.
                   Splnění upřesňujících pravidel není kontrolováno pomocí kontroly syntaxe.

                <h3>TOT - Totální deterministický konečný automat</h3>
                <p>Totální automat musí mít pro každý stav Q definovány přechody pod všemi znaky z abecedy sigma.</p>
                <h3>MIN - Minimální deterministický konečný automat</h3>
                <p>Minimální automat musí splňovat pravidla totálního automatu a navíc musí platit, že neexistuje žádný automat
                s nižším počtem stavů, který popisuje shodný jazyk jako zadaný automat.</p>
                <h3>MIC - Minimální kanonický deterministický konečný automat</h3>
                <p>Minimální kanonický automat musí splňovat pravidla minimálního automatu a dále musí mít všechny stavy pojmenované 
                nepřerušenou posloupností znaků, kdy počáteční stav se vždy jmenuje A a každý další dosažený stav se jmenuje 
                podle následujícího znaku v kódování UTF-8. Přechody do dalších stavů jsou prováděny jako prohledávání
                grafu do šířky pod všemi znaky z abecedy seřazenými opět podle kódování UTF-8. Tento zápis je pro každý
                automat unikátní.</p>
             <a name="gra"></a><h2>GRA - Popis jazyka gramatikou</h2>
             <p>Jazyk je možné popsat gramatikou, z níž definujeme pouze množinu pravidel, ktrá zapisujeme jako 
             <span class="code">N -> tN | tN | t</span>, kde N je neterminál a t je terminál. Jednotlivé řádky od sebe musí
             být odděleny koncem řádku nebo čárkou.
             
             <h3>Neterminál</h3>
             <p>Neterminál je zapsán buď jedním znakem velké anglické abecedy, nebo jako více znaků uzavřených
             do špičatých závorek &lt; &gt;. V nich mohou být uzavřeny i speciální symboly, viz. níže.</p>

             <h3>Terminál</h3>
             <p>Terminál je zapsán jedním znakem, který není velkým písmenem anglické abecedy ani speciálním
                 symbolem, nebo jako více znaků uzavřených
             do uvozovek "". V nich mohou být uzavřeny i speciální symboly, viz. níže. Pokud je terminální symbol
             zapsán do uvozovek, je s ním zacházeno jako s více jednoznakovými terminály.</p>

             <h3>Speciální symboly</h3>
             <p>Za speciální symboly jsou v zápisu gramatiky považovány <, >, -, |, ", mezera, čárka, konec řádku.
             Pokud chceme uvést, že gramatika přijímá prázdné slovo, můžeme na pravé straně prvního (počátečního) pravidla
             uvést symbol \e jako symbol prázdného slova. Pak se ale počáteční neterminál nesmí vyskytovat na pravé straně
             žádného pravidla.
             <a name="reg"></a><h2>REG - Popis jazyka regulárním výrazem</h2>
             <h3>Základní regulární výrazy</h3>
             <p>Jako základní regulární výrazy můžeme používat kterékoliv znaky abecedy mimo +, ^+, *, ^*, (, ), tečka.
             Regulární výraz může také popisovat prázdné slovo, pak jej zapíšeme jako \e. Může také popisovat prázdný jazyk,
             pak jej popíšeme jako \0.
             <h3>Složené regulární výrazy</h3><p>
                 Jakékoliv regulární výrazy můžeme uzavřít do závorek, tím se vyhodnotí jako první a pracuje se s nimi
                 podobně jako se základním regulárním výrazem. Nejvyšší prioritu má operátor *, ^* a ^+, tedy iterace a pozitivní iterace.
                 Druhou nejvyšší prioritu má operátor . (zřetězení), který nemusíme uvádět (a.b je ekvivalentní ab). Pokud není uvedena
                 tečka, není výpočet považován za těsné zřetězení, tedy <span class="code">ab*</span> není ekvivalentní
                 <span class="code">(ab)*</span> a je ekvivalentní <span class="code">a.b*</span>. Nejnižší prioritu má operátor sjednocení +.

             <a name="all"></a><h2>ALL - možnost zadání více způsoby</h2>
             V tomto režimu je možné jazyk zapsat více způsoby, kdy nápověda syntaxe radí, jak se bude vyhodnocovat.
             Výraz se snaží vyhodnotit postupně jako DFA, EFA, GRA a REG. Pozor - pokud nápověda syntaxe uvádí, že je
             nutno něco doplnit, může se výraz vyhodnocovat jinak, zpravidla jako REG (např. při zadání A bude kontrola syntaxe nabízet rozšíření
             na gramatiku, ale jazyk se správně vyhodnotí jako regulární výraz). Jedná se zejména o triviální případy,
             v těch je možné použít dodatečné ozávorkování celého výrazu pro přepnutí kontroly syntaxe na REG.

        </div>
        </div>
        </div>
        </div>
        <div class="bottomGradient">
            <div class="bottomLine">
                <a href="#header" class="bottomArrow"><img src="./style/toparrow.gif" title="Nahoru" /></a>
            </div>
        </div>
  </body>
</html>
