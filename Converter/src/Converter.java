/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Matej Poklemba
 */
public class Converter {
    static String prefixContentFileName = "Skript_prefix.txt";
    static String serverLocationFileName = "Skript_server.txt";
    static String endingContentFileName = "Skript_ending.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1 || (args.length == 1 && args[0].equals("-?"))) {
            printHelp();
            return;
        }

        String s, s2, serverLocation = "", prefixContent = "", endingContent = "";

        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(serverLocationFileName))) {
                String line = reader.readLine();
                serverLocation += line;
            }
        } catch (Exception e) {
            System.err.format("ERROR: Could not read server location from file '%s'. Converting aborted.\n", serverLocationFileName);
            return;
        }

        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(prefixContentFileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    prefixContent += line + "\n";
                }
            }
        } catch (Exception e) {
            System.err.format("ERROR: Could not read prefix content from file '%s'. Converting aborted.\n", prefixContentFileName);
            return;
        }

        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(endingContentFileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    endingContent += line + "\n";
                }
            }
        } catch (Exception e) {
            System.err.format("ERROR: Could not read ending content from file '%s'. Converting aborted.\n", endingContentFileName);
            return;
        }

        ArrayList<String> files = new ArrayList<>();
        boolean reset = false;
        boolean keep = false;
        boolean iso = false;
        int isoPercent = 0;

        for (int i = 0; i < args.length; i++) {
            boolean invalidArg = true;

            if (Pattern.matches("\\S+.qdef", args[i])) {
                files.add(args[i]);
                invalidArg = false;
            } else if (args[i].equals("-r") || args[i].equals("-reset")) {
                reset = true;
                invalidArg = false;
            } else if (args[i].equals("-k") || args[i].equals("-keep")) {
                keep = true;
                invalidArg = false;
            } else if (args[i].matches("-i=\\d+") || args[i].matches("-isomorphism=\\d+")) {
                isoPercent = Integer.parseInt(args[i].substring(args[i].indexOf('=') + 1));
                if (isoPercent < 0 || isoPercent > 100) {
                    System.err.format("ERROR: Isomorphism partial points number not in <0,100>.\n");
                    return;
                }
                iso = true;
                invalidArg = false;
            }

            if (invalidArg) {
                System.out.format("Argument %s invalid! Converting aborted.\n", args[i]);
                return;
            }
        }

        String suffix;
        System.out.print("Enter suffix of the export name: ");
        Scanner scanner = new Scanner(System.in);
        suffix = scanner.next();

        long t = System.currentTimeMillis() / 1000;
        int errorCount = 0;

        for (int fileNumber = 0; fileNumber < files.size(); fileNumber++) {
            String inputName = files.get(fileNumber);
            String outputName = inputName;
            outputName = new StringBuilder(outputName).insert(outputName.lastIndexOf('.'), suffix).toString();
            if (outputName.equals(prefixContentFileName) || outputName.equals(serverLocationFileName)) {
                System.err.format("ERROR: Forbidden name! Cannot convert '%s' to '%s'. Skipping...", inputName, outputName);
                errorCount++;
                continue;
            }
            System.out.format("Converting '%s' to '%s'\n", inputName, outputName);

            if (reset) {
                try {
                    FileWriter writer;
                    String resetName = new StringBuilder(outputName).insert(outputName.lastIndexOf('.'), "Reset").toString();
                    try (BufferedReader reader = new BufferedReader(new FileReader(inputName))) {
                        File file = new File(resetName);
                        file.createNewFile();
                        if (!keep) {
                            file.deleteOnExit();
                        }
                        writer = new FileWriter(file);
                        boolean writeMinusMinus = false;
                        while ((s = reader.readLine()) != null) {
                            if (s.trim().equals("")) {
                                continue;
                            }
                            if (writeMinusMinus) {
                                writeMinusMinus = false;
                                if (!s.trim().equals("++")) {
                                    writer.write("--\n");
                                }
                            }
                            if (s.trim().equals("--")) {
                                writeMinusMinus = true;
                            } else {
                                if (s.trim().equals("++")) {
                                    while ((s = reader.readLine()) != null && !s.equals("--")) {
                                    }
                                } else if (s.contains(":e") && !s.contains(":e=")) {
                                    writer.write(" :e\n");
                                } else if (!s.contains("<ul") && !s.contains("<div")
                                        && !s.contains("<input")
                                        && !s.contains("</script>")) {
                                    writer.write(s + "\n");
                                }
                            }
                        }
                    }
                    writer.flush();
                    writer.close();
                    System.out.format("Successfully reseted %s to %s\n", inputName, resetName);
                    inputName = resetName;
                } catch (Exception e) {
                    if (e instanceof java.io.FileNotFoundException) {
                        System.err.format("ERROR: Could not find file '%s'.\n", inputName);
                        errorCount++;
                    } else {
                        System.err.format("ERROR: Could not read content from file '%s'. Converting aborted.\n", inputName);
                        return;
                    }
                    continue;
                }
            }

            try {
                HashSet<String> types = new HashSet<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(inputName))) {
                    while ((s = reader.readLine()) != null) {
                        if (s.contains(":e=")) {
                            String type = getFormType(s).type;
                            types.add(type);
                        }
                    }
                }
                FileWriter writer;
                try (BufferedReader reader = new BufferedReader(new FileReader(inputName))) {
                    int questionNumber = 0;
                    File file = new File(outputName);
                    file.createNewFile();
                    writer = new FileWriter(file);
                    prefixContent = prefixContent.replaceAll("%SERVER%", serverLocation);
                    writer.write("++\n" + prefixContent);

                    // Parsers importing
                    if (types.size() > 0) {
                        Iterator<String> iterator = types.iterator();
                        while(iterator.hasNext()) {
                            String type = iterator.next();
                            if (!validParserFilename(type))
                                continue;
                            writer.write("<script src=\"" + serverLocation + "/js/" + type + "Parser.js\" type=\"text/javascript\"></script>\n");
                            // Fallback if serverLocation importing fails
                            writer.write("<script>if (typeof " + type + "Parser == 'undefined') {\n");
                            writer.write("    document.write('<script src=\"//rawgit.com/matejos/ISMU-Automata-Tools/master/Resolver/fja/web/js/" + type + "Parser.js\">\\<\\/script>');\n");
                            writer.write("}</script>\n");
                        }
                    }

                    writer.write("--\n");
                    s = reader.readLine();
                    while (s != null) {
                        if ("++".equals(s.trim()))
                            throw new Exception();
                        boolean dontRead = false;
                        if (s.contains(":e") && !s.contains(":e=")) {
                            s2 = reader.readLine();
                            TypePair p = getFormType(s2);
                            String formtype = p.formtype;
                            String type = p.type;
                            if (!formtype.equals("")) {
                                questionNumber++;
                                String idString = t + "-" + fileNumber + "-" + questionNumber;
                                writer.write(getBasicWrapper(idString, formtype));
                                if (formtype.equals("DFA") || formtype.equals("NFA") || formtype.equals("EFA")) {
                                    writer.write("<ul class=\"nav nav-tabs\"><li class=\"myli active\"><a data-toggle=\"tab\" data-target=\"#q" + idString
                                            + "a\">Graf</a></li><li class=\"myli\"><a data-toggle=\"tab\" data-target=\"#q" + idString
                                            + "b\">Tabulka</a></li><li class=\"myli\"><a data-toggle=\"tab\" data-target=\"#q" + idString
                                            + "c\">Text</a></li></ul></ul>\n");
                                    writer.write("<div id=\"q" + idString + "\" class=\"tab-content\"><script>init(\"q" + idString + "\", \"" + type + "\");</script></div>\n");
                                }
                                else if (formtype.equals("CYK")) {
                                    String word = (s2.substring(s2.indexOf(':') + 1));
                                    word = (word.substring(word.indexOf(':') + 1));
                                    word = (word.substring(word.indexOf(':') + 1));
                                    word = (word.substring(0, word.indexOf(':')));
                                    writer.write("<div id=\"q" + idString + "\" class=\"tab-content\"><script>initCYK(\"q" + idString + "\", \"" + word + "\");</script></div>\n");
                                }
                            }
                            while (s2 != null && s2.contains(":e=")) {
                                StringBuilder stringBuilder = new StringBuilder(s2.trim());
                                stringBuilder.replace(4, 5, "f");
                                if (stringBuilder.toString().charAt(13) == ':') {
                                    if (iso) {
                                        StringBuilder stringBuilder2 = new StringBuilder(stringBuilder.toString());
                                        stringBuilder.insert(13, "-Y");
                                        if (isoPercent > 0) {
                                            stringBuilder.append(" " + (100 - isoPercent) + "%");
                                            stringBuilder2.insert(13, "-N");
                                            stringBuilder2.append(" " + isoPercent + "%");
                                            stringBuilder.append("\n" + stringBuilder2);
                                        }
                                    }
                                    else {
                                        stringBuilder.insert(13, "-N");
                                    }
                                }
                                if (stringBuilder.indexOf("F=") != -1) {
                                    stringBuilder.replace(stringBuilder.indexOf("F="), stringBuilder.indexOf("F=") + 2, "final=");
                                }
                                writer.write(stringBuilder.toString() + "\n");
                                s2 = reader.readLine();
                                dontRead = true;
                                s = s2;
                            }
                        } else if (!s.contains("<input")) {
                            writer.write(s + "\n");
                        }
                        if (!dontRead)
                            s = reader.readLine();
                    }
                    System.out.format("-Successfully converted %d questions.\n", questionNumber);
                }
                writer.write(endingContent);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                if (e instanceof java.io.FileNotFoundException) {
                    System.err.format("ERROR: Could not find file '%s'.\n", inputName);
                    errorCount++;
                } else {
                    System.err.format("ERROR: Could not read content from file '%s'. Converting aborted.\n", inputName);
                    if (!reset)
                        System.err.format("Does this file contains older version of editor? In that case use argument -r\n");
                    return;
                }
            }
        }
        System.out.format("Program finished with %d errors.\n", errorCount);
        return;
    }

    static void printHelp() {
        System.out.format("Usage: %s [-options] fileName...\n", Converter.class.getName());
        System.out.format("where options include:\n");
        System.out.format("\t-r | -reset\tPerform reset on files (use if they contain older version of the editor)\n");
        System.out.format("\t-k | -keep\tKeep reset versions of files (...Reset.qdef)\n");
        System.out.format("\t-i=<percent> | -isomorphism=<percent>\tAdd isomorphism condition to the questions with <percent> partial points for nonisomorphic answer\n");
    }

    static String getBasicWrapper(String idString, String formtype) {
        String s = "<input name=\"q" + idString + "\" type=\"hidden\" value=\"\" />"
                + "<noscript>(Nemate zapnuty JavaScript, ale pro spravnou funkci otazky je JavaScript nutny. Jako prohlizec je doporuceny Firefox.) </noscript>"
                + "<div id=\"q" + idString + "-div\"> :e ";
        if (typeHasSyntaxHelp(formtype)) {
            s += "<p></p><div id=\"q" + idString + "-error\" class=\"alert alert-info\" title=\"Nápověda syntaxe učitele.\">"
                    + "<div id=\"q" + idString + "-i\" class=\"\"></div>"
                    + "<div id=\"q" + idString + "-error-text\">Zde se zobrazuje nápověda syntaxe.</div></div>";
            s += "<script type=\"text/javascript\">register(\"q" + idString + "\", " + formtype + "Parser.parse)</script>";
        }
        s += "</div>\n";
        return s;
    }

    static TypePair getFormType(String s) {
        String formtype = "";
        String type = s.substring(s.indexOf('-') + 1, s.indexOf('-') + 4);
        if (type.equals("DFA") || type.equals("MIN") || type.equals("MIC") || type.equals("TOT") || type.equals("TOC") || type.equals("CAN")) {
            formtype = "DFA";
        } else if (type.equals("REG") || type.equals("GRA") || type.equals("NFA") || type.equals("EFA") || type.equals("CYK")) {
            formtype = type;
        }
        return new TypePair(formtype, type);
    }

    static boolean typeHasSyntaxHelp(String type) {
        return typesWithSyntaxHelp.contains(type);
    }

    static boolean validParserFilename(String type) {
        return validParsers.contains(type);
    }

    static List<String> typesWithSyntaxHelp = Arrays.asList("REG", "GRA", "NFA", "EFA", "DFA", "CFG");
    static List<String> validParsers = Arrays.asList("REG", "GRA", "NFA", "EFA", "DFA", "CFG", "CYK");

    static class TypePair {
        public String formtype;
        public String type;
        public TypePair(String formtype, String type) {
            this.formtype = formtype;
            this.type = type;
        }
    }
}
