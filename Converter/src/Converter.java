/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author Matej Poklemba
 */
public class Converter {
    static String prefixContentFileName = "Skript_prefix.txt";
    static String parsersLocationFileName = "Skript_parsers.txt";
    static String endingContentFileName = "Skript_ending.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1 || (args.length == 1 && args[0].equals("-?"))) {
            printHelp();
            return;
        }

        String s, s2, parsersLocation = "", prefixContent = "", endingContent = "";

        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(parsersLocationFileName))) {
                String line = reader.readLine();
                parsersLocation += line;
            }
        } catch (Exception e) {
            System.err.format("ERROR: Could not read parsers location from file '%s'. Exporting aborted.\n", parsersLocationFileName);
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
            System.err.format("ERROR: Could not read prefix content from file '%s'. Exporting aborted.\n", prefixContentFileName);
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
            System.err.format("ERROR: Could not read ending content from file '%s'. Exporting aborted.\n", endingContentFileName);
            return;
        }


        String suffix;
        System.out.print("Enter suffix of the export name: ");
        Scanner scanner = new Scanner(System.in);
        suffix = scanner.next();

        long t = System.currentTimeMillis() / 1000;

        ArrayList<String> files = new ArrayList<>();
        boolean reset = false;
        boolean keep = false;
        boolean iso = false;

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
            } else if (args[i].equals("-i") || args[i].equals("-iso") || args[i].equals("-isomorphism")) {
                iso = true;
                invalidArg = false;
            }

            if (invalidArg) {
                System.out.format("Argument %s invalid! Ignoring...\n", args[i]);
            }
        }

        for (int fileNumber = 0; fileNumber < files.size(); fileNumber++) {
            String inputName = files.get(fileNumber);
            String outputName = inputName;
            outputName = new StringBuilder(outputName).insert(outputName.lastIndexOf('.'), suffix).toString();
            if (outputName.equals(prefixContentFileName) || outputName.equals(parsersLocationFileName)) {
                System.err.format("ERROR: Forbidden name! Cannot convert '%s' to '%s'. Skipping...", inputName, outputName);
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
                                    writer.write(":e\n");
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
                    } else {
                        System.err.format("ERROR: Could not read content from file '%s'. Exporting aborted.\n", inputName);
                        return;
                    }
                    continue;
                }
            }

            try {
                FileWriter writer;
                try (BufferedReader reader = new BufferedReader(new FileReader(inputName))) {
                    int questionNumber = 0;
                    File file = new File(outputName);
                    file.createNewFile();
                    writer = new FileWriter(file);
                    writer.write("++\n" + prefixContent);
                    writer.write("<script src=\"" + parsersLocation + "js/utilIS.js\" type=\"text/javascript\"></script>\n");
                    writer.write("<style type=\"text/css\">@import \"" + parsersLocation + "css/parser_style.css\";</style>\n--\n");
                    s = reader.readLine();
                    while (s != null) {
                        if ("++".equals(s.trim()))
                            throw new Exception();
                        boolean dontRead = false;
                        if (s.contains(":e") && !s.contains(":e=")) {
                            s2 = reader.readLine();
                            String formtype = "";
                            String type = s2.substring(s2.indexOf('-') + 1, s2.indexOf('-') + 4);
                            if (type.equals("DFA") || type.equals("MIN") || type.equals("MIC") || type.equals("TOT") || type.equals("TOC") || type.equals("CAN")) {
                                formtype = "DFA";
                            } else if (type.equals("REG") || type.equals("GRA") || type.equals("NFA") || type.equals("EFA")) {
                                formtype = type;
                            }
                            if (!formtype.equals("")) {
                                questionNumber++;
                                String idString = t + "-" + fileNumber + "-" + questionNumber;
                                if (formtype.equals("DFA") || formtype.equals("NFA") || formtype.equals("EFA")) {
                                    writer.write("<input name=\"q" + idString + "\" type=\"hidden\" value=\"\" />"
                                            + "<noscript>(Nemate zapnuty JavaScript, ale pro spravnou funkci otazky je JavaScript nutny. Jako prohlizec je doporuceny Firefox.) </noscript><script src=\"" + parsersLocation + "js/" + formtype + "Parser.js\" type=\"text/javascript\"></script>"
                                            + "<div id=\"q" + idString + "-div\" class=\"parser_text_default\"> :e <span id=\"q" + idString + "-error\" class=\"parser_error\"></span></div><script type=\"text/javascript\">register(\"q" + idString + "\", " + formtype + "Parser.parse)</script>\n");
                                    writer.write("<ul class=\"nav nav-tabs\"><li class=\"myli active\"><a data-toggle=\"tab\" data-target=\"#q" + idString
                                            + "a\">Graf</a></li><li class=\"myli\"><a data-toggle=\"tab\" data-target=\"#q" + idString
                                            + "b\">Tabulka</a></li><li class=\"myli\"><a data-toggle=\"tab\" data-target=\"#q" + idString
                                            + "c\">Text</a></li></ul></ul>\n");
                                    writer.write("<div id=\"q" + idString + "\" class=\"tab-content\"><script>init(\"q" + idString + "\", \"" + type + "\");</script></div>\n");
                                } else {
                                    writer.write("<input name=\"q" + idString + "\" type=\"hidden\" value=\"\" />\n");
                                    writer.write("<noscript>(Nemate zapnuty JavaScript, ale pro spravnou funkci otazky je JavaScript nutny. Jako prohlizec je doporuceny Firefox.) </noscript><script src=\"" + parsersLocation + "js/" + formtype + "Parser.js\" type=\"text/javascript\"></script>\n");
                                    writer.write("<div id=\"q" + idString + "-div\" class=\"parser_text_default\"> :e <br><span id=\"q" + idString + "-error\" class=\"parser_error\"></span></div><script type=\"text/javascript\">register(\"q" + idString + "\", " + formtype + "Parser.parse)</script>\n");
                                }
                            }
                            while (s2 != null && s2.contains(":e=")) {
                                StringBuilder stringBuilder = new StringBuilder(s2.trim());
                                stringBuilder.replace(4, 5, "f");
                                if (stringBuilder.toString().charAt(13) == ':') {
                                    if (iso)
                                        stringBuilder.insert(13, "-Y");
                                    else
                                        stringBuilder.insert(13, "-N");
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
                } else {
                    System.err.format("ERROR: Could not read content from file '%s'. Exporting aborted.\n", inputName);
                    System.err.format("Does this file contains older version of editor? In that case use argument -r\n");
                    return;
                }
            }
        }
        System.out.println("Finished.");
        return;
    }

    static void printHelp() {
        System.out.format("Usage: %s [-options] fileName…\n", Converter.class.getName());
        System.out.format("where options include:\n");
        System.out.format("\t-r | -reset\tperform reset on files (use if they contain older version of the editor)\n");
        System.out.format("\t-k | -keep\tkeep reset versions of files (…Reset.qdef)\n");
        System.out.format("\t-i | -iso | -isomorphism\tadd isomorphism condition to the questions\n");
    }
}
