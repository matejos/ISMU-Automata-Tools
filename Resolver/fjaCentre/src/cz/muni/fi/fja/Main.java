package cz.muni.fi.fja;

import java.io.IOException;

public class Main {
  // DFA - isCanonic!

  public static void main(String[] args) throws IOException {

    /*
     * String s = "\\0" ; Reg r = new Reg(new RegStream(s.toCharArray(), 0)); //
     * System.out.println(r.toString()); EFA e = r.makeEFA(); //
     * System.out.println(e.toString()); DFA d = e.makeDFA(); //
     * System.out.println(d.toString()); //
     * System.out.println(d.makeMinimalCanonicDFA().toString());
     * 
     * 
     * String s2 = "\\0a" ; Reg r2 = new Reg(new RegStream(s2.toCharArray(),
     * 0)); // System.out.println(r.toString()); EFA e2 = r2.makeEFA(); //
     * System.out.println(e.toString()); DFA d2 = e2.makeDFA(); //
     * System.out.println(d2.toString()); //
     * System.out.println(d2.makeMinimalCanonicDFA().toString());
     * 
     * QuickEqual qe = new QuickEqual(d, d2);
     * System.out.println("QUICK VYTVORENO");
     * 
     * System.out.println("abecedy:" + qe.equalAlphabets());
     * System.out.println("odpoved:" + qe.result());
     * 
     * 
     * // System.out.println(d.toString());
     * 
     * // DFA dfa = new DFA(new FAStream(s.toCharArray(), 0));
     * 
     * / String s = "(D\\e,\\ea  )=  \tD(D,b)=D\n" + "(A,b)=A(A,a)=B\n" +
     * "(B,b)=B(B,a)=B\n" + "(C,a)=C(C,b)=C\n" + "\n" + "init=A";
     * 
     * DFA dfa = new DFA(new FAStream(s.toCharArray(), 0));
     * 
     * 
     * System.out.println("DFA: \n" + dfa); System.out.println("kanonicky? : " +
     * dfa.isCanonic()); System.out.println("DFA inOneRow: \n" +
     * dfa.toStringInOneRow());
     * 
     * / String s = "(aa+bb)^+"; RegReader rr = new RegReader(new
     * RegStream(s.toCharArray(), 0)); // rr.print(); Device r = new Reg(new
     * RegStream(s.toCharArray(), 0)); System.out.println("*** GRAMATIKA ***\n"
     * + r.toString()); Device fa = r.makeEFA();
     * System.out.println("*** EFA ***"); System.out.println(fa.toString()); fa
     * = fa.makeMinimalCanonicDFA(); System.out.println("*** MIN DFA ***");
     * System.out.println(fa.toString());
     */
    /*
     * String pom = "(x,\\e)={y,q1} (y,a)={y} (q1,a)={y} F={y}"; fa = new
     * EFA(new FAStream(pom.toCharArray(), 0));
     * System.out.println("*** EFA ***"); System.out.println(fa.toString()); fa
     * = fa.makeMinimalCanonicDFA(); System.out.println("*** MIN DFA ***");
     * System.out.println(fa.toString());
     */

    // System.out.println(" \n" + g);
    // System.out.println("NFA: \n" + g.);
    /*
     * Centre centre = new Centre(true, true); String teacher = ""; String
     * student = ""; teacher =
     * "DFA-DFA\n (A,a)=A\n (A,b)=B\n (B,b)=B\n (B,c)=C\n (C,c)=C\n F={C}\n";
     * student = " (A,a)=A\n (A,b)=B\n (B,b)=B\n (B,c)=C\n (C,c)=C\n F={C}\n";
     * 
     * 
     * System.out.println(centre.equalDevices(teacher, student));
     * 
     * // System.out.println(centre.equalDevices("NFA", teacher, "NFA",
     * student));
     * 
     * 
     * teacher =
     * "DFA-DFA\n (A,a)=A\n (A,b)=B\n (B,b)=B\n (B,c)=C\n (C,c)=C\n F={C}\n";
     * student = " (A,a)=A\n (A,b)=B\n (B,b)=B\n (B,c)=C\n (C,c)=C\n F={C}\n";
     * student =
     * " (A,a)={A,B}\n (A,b)={A,B}\n (B,a)={C}\n (B,b)={C}\n (C,a)={C} (C,b)={C}\n F={C}\n"
     * ; student =
     * " (A,a)={A,B}\n (A,b)={A}\n (B,b)={C}\n (C,a)={C} (C,b)={C}\n F={C}";
     * //(A,a)={A,B}\n (A,b)={A}\n (B,b)={C}\n (C,a)={C} (C,b)={C}\n F={C} /
     * InStream pokus = new
     * FilterStream("fa1=%2B%B%2B%26%26%26&fa2=%2B%2B%2B%26%26%26"); while
     * (!pokus.isEOF() && pokus.getSymbol()) { System.out.println("SYMBOL:" +
     * pokus.getLastSymbol()); } if (pokus.isEOF()) {
     * System.out.println("KONEC RETEZCE: " + pokus.getError());
     * System.out.println("POSLEDNI SYMBOL: " + pokus.getLastSymbol()); } else {
     * System.out.println("JESTE NENI KONEC RETEZCE: " + pokus.getError());
     * System.out.println("POSLEDNI SYMBOL: " + pokus.getLastSymbol()); }
     * 
     * 
     * DFA dfa1 = DFA.makeMinimalCanonicDFAFromEnteredDFA("dfa"); DFA dfa2 =
     * DFA.makeMinimalCanonicDFAFromEnteredNFA("nfa");
     * 
     * 
     * 
     * System.out.println("*** CANNONIC MODEL 1 ***"); System.out.println(dfa1);
     * System.out.println("*** CANNONIC MODEL 2 ***"); System.out.println(dfa2);
     * 
     * 
     * 
     * 
     * if (dfa1.equals(dfa2)) {
     * System.out.println("prvni FA je ekvivalentni druhemu FA"); } else {
     * System.out.println("prvni FA NENI ekvivalentni druhemu FA"); }
     * 
     * if (dfa2.equals(dfa1)) {
     * System.out.println("druhy FA je ekvivalentni prvnimu FA"); } else {
     * System.out.println("prvni FA NENI ekvivalentni druhemu FA"); }
     */
  }

}
