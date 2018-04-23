/*
 * Downloaded source
 */

package cz.muni.fi.xpastirc.fja.servlet;

/**
 * @author http://www.rgagnon.com/javadetails/java-0306.html
 */
public class HTMLEscaper {
        public static String escapeHTML(String s){
           StringBuilder sb = new StringBuilder();
           int n = s.length();
           for (int i = 0; i < n; i++) {
              char c = s.charAt(i);
              switch (c) {
                 case '<': sb.append("&lt;"); break;
                 case '>': sb.append("&gt;"); break;
                 case '&': sb.append("&amp;"); break;
                 case '"': sb.append("&quot;"); break;
                 case 'à': sb.append("&agrave;");break;
                 case 'À': sb.append("&Agrave;");break;
                 case 'â': sb.append("&acirc;");break;
                 case 'Â': sb.append("&Acirc;");break;
                 case 'ä': sb.append("&auml;");break;
                 case 'Ä': sb.append("&Auml;");break;
                 case 'å': sb.append("&aring;");break;
                 case 'Å': sb.append("&Aring;");break;
                 case 'æ': sb.append("&aelig;");break;
                 case 'Æ': sb.append("&AElig;");break;
                 case 'ç': sb.append("&ccedil;");break;
                 case 'Ç': sb.append("&Ccedil;");break;
                 case 'é': sb.append("&eacute;");break;
                 case 'É': sb.append("&Eacute;");break;
                 case 'è': sb.append("&egrave;");break;
                 case 'È': sb.append("&Egrave;");break;
                 case 'ê': sb.append("&ecirc;");break;
                 case 'Ê': sb.append("&Ecirc;");break;
                 case 'ë': sb.append("&euml;");break;
                 case 'Ë': sb.append("&Euml;");break;
                 case 'ï': sb.append("&iuml;");break;
                 case 'Ï': sb.append("&Iuml;");break;
                 case 'ô': sb.append("&ocirc;");break;
                 case 'Ô': sb.append("&Ocirc;");break;
                 case 'ö': sb.append("&ouml;");break;
                 case 'Ö': sb.append("&Ouml;");break;
                 case 'ø': sb.append("&oslash;");break;
                 case 'Ø': sb.append("&Oslash;");break;
                 case 'ß': sb.append("&szlig;");break;
                 case 'ù': sb.append("&ugrave;");break;
                 case 'Ù': sb.append("&Ugrave;");break;
                 case 'û': sb.append("&ucirc;");break;
                 case 'Û': sb.append("&Ucirc;");break;
                 case 'ü': sb.append("&uuml;");break;
                 case 'Ü': sb.append("&Uuml;");break;
                 case '®': sb.append("&reg;");break;
                 case '©': sb.append("&copy;");break;
                 case '€': sb.append("&euro;"); break;
                 // be carefull with this one (non-breaking whitee space)
                 case ' ': sb.append("<br>");break;
                 default:  sb.append(c); break;
              }
           }
           return sb.toString();
        }
        public static String removeWhiteSpace(String s){
           StringBuilder sb = new StringBuilder();
           int n = s.length();
           for (int i = 0; i < n; i++) {
              char c = s.charAt(i);
              switch (c) {
                 case '\n': sb.append(""); break;
                 case ' ': sb.append("");break;
                 case '\r': sb.append("");break;
                 default:  sb.append(c); break;
              }
              if (i < n - 6) {if ((s.charAt(i+1))=='f'&&(s.charAt(i+2))=='i' &&(s.charAt(i+3))=='n'
                      &&(s.charAt(i+4))=='a' &&(s.charAt(i+5))=='l' &&(s.charAt(i+6))=='=') sb.append(" ");};
          }
              return sb.toString();
        }
}
