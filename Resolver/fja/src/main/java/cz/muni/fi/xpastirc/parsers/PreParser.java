/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tomas Pastircak - 324693@mail.muni.cz, Adrian Elgyutt
 * @version 10.5.2011
 */
public class PreParser {
    public static String parse(String original){
        Pattern p1 = Pattern.compile("[\u0020\r\n\t]+");
        Matcher m1 = p1.matcher(original);
        String toReturn = m1.replaceAll(" ");
        Pattern p2 = Pattern.compile("init\u0020=+\u0020+");
        Matcher m2 = p2.matcher(toReturn);
        toReturn = m2.replaceAll("init=");
        Pattern p3 = Pattern.compile("final\u0020=+\u0020+");
        Matcher m3 = p3.matcher(toReturn);
        toReturn = m3.replaceAll("F=");
        Pattern p4 = Pattern.compile("final=");
        Matcher m4 = p4.matcher(toReturn);
        toReturn = m4.replaceAll("F=");
        System.out.println(toReturn);
        return toReturn;

    }
}
