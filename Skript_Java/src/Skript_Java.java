/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.util.Scanner;

/**
 *
 * @author Matej
 */
public class Skript_Java {
    static String prefixContentFileName = "Skript_prefix.txt";
    static String parsersLocationFileName = "Skript_parsers.txt";
    /**
     * @param args the command line arguments 
     */
    public static void main(String[] args) {
        if (args.length < 1) 
	{
            System.out.println("ERROR: Use at least one file name as argument!");
            System.out.println("Program closed.");
            return;
	}

	String s, s2, parsersLocation = "", prefixContent = "";
        
        try
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(parsersLocationFileName))) 
            {
                String line = reader.readLine();
                parsersLocation += line;
            }
        }
        catch (Exception e)
        {
            System.err.format("ERROR: Could not read parsers location from file '%s'. Exporting aborted.\n", parsersLocationFileName);
            return;
        }

        try
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(prefixContentFileName))) 
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    prefixContent += line + "\n";
                } 
            }
        }
        catch (Exception e)
        {
            System.err.format("ERROR: Could not read prefix content from file '%s'. Exporting aborted.\n", prefixContentFileName);
            return;
        }
        

	String suffix;
	System.out.print("Enter suffix of the export name: ");
        Scanner scanner = new Scanner(System.in);
        suffix = scanner.next();
        
        long t = System.currentTimeMillis() / 1000;
	
        for (int fileNumber = 0; fileNumber < args.length; fileNumber++) {
            String inputName = args[fileNumber];
            String outputName = inputName;
            outputName = new StringBuilder(outputName).insert(outputName.lastIndexOf('.'), suffix).toString();
            if (outputName.equals(prefixContentFileName) || outputName.equals(parsersLocationFileName))
            {
                System.err.format("ERROR: Forbidden name! Cannot convert '%s' to '%s'. Skipping...", inputName, outputName);
                continue;
            }
            System.out.format("Converting '%s' to '%s'\n", inputName, outputName);

            try
            {
                FileWriter writer;
                try (BufferedReader reader = new BufferedReader(new FileReader(inputName))) {
                    int questionNumber = 0;
                    File file = new File(outputName);
                    file.createNewFile();
                    writer = new FileWriter(file);
                    writer.write("++\n" + prefixContent);
                    writer.write("<script src=\"" + parsersLocation + "utilIS.js\" type=\"text/javascript\"></script>\n");
                    writer.write("<style type=\"text/css\">@import \"" + parsersLocation + "parser_style.css\";</style>\n--\n");
                    while ((s = reader.readLine()) != null)
                    {
                        if (s.contains(":e"))
                        {
                            s2 = reader.readLine();
                            String formtype = "";
                            String type = s2.substring(s2.indexOf('-') + 1, s2.indexOf('-') + 4);
                            if (type.equals("DFA") || type.equals("MIN") || type.equals("MIC") || type.equals("TOT") || type.equals("TOC") || type.equals("CAN"))
                            {
                                formtype = "DFA";
                            }
                            else if (type.equals("REG") || type.equals("GRA") || type.equals("NFA") || type.equals("EFA"))
                            {
                                formtype = type;
                            }
                            if (!formtype.equals(""))
                            {
                                questionNumber++;
                                String formtypelower = formtype;
                                String idString = t + "-" + fileNumber + "-" + questionNumber;
                                formtypelower = formtypelower.toLowerCase();
                                if (formtype.equals("DFA") || formtype.equals("NFA") || formtype.equals("EFA"))
                                {
                                    writer.write("<input name=\"q" + idString + "\" type=\"hidden\" value=\"\" />"
                                            + "<noscript>(Nemate zapnuty JavaScript, ale pro spravnou funkci otazky je JavaScript nutny. Jako prohlizec je doporuceny Firefox.) </noscript><script src=\"" + parsersLocation + formtypelower + "parserN.js\" type=\"text/javascript\"></script>"
                                            + "<div id=\"q" + idString + "-div\" class=\"parser_text_default\"> :e <span id=\"q" + idString + "-error\" class=\"parser_error\"></span></div><script type=\"text/javascript\">register(\"q" + idString + "\", " + formtypelower + "Parser.parse)</script>\n");
                                    writer.write("<ul class=\"nav nav-tabs\"><li class=\"myli active\"><a data-toggle=\"tab\" data-target=\"#q" + idString
                                            + "a\">Graf</a></li><li class=\"myli\"><a data-toggle=\"tab\" data-target=\"#q" + idString
                                            + "b\">Tabulka</a></li><li class=\"myli\"><a data-toggle=\"tab\" data-target=\"#q" + idString
                                            + "c\">Text</a></li></ul></ul>\n");
                                    writer.write("<div id=\"q" + idString + "\" class=\"tab-content\"><script>init(\"q" + idString + "\", \"" + type + "\");</script></div>\n");
                                }
                                else
                                {
                                    writer.write("<input name=\"q" + idString + "\" type=\"hidden\" value=\"\" />\n");
                                    writer.write("<noscript>(Nemate zapnuty JavaScript, ale pro spravnou funkci otazky je JavaScript nutny. Jako prohlizec je doporuceny Firefox.) </noscript><script src=\"" + parsersLocation + formtypelower + "parserN.js\" type=\"text/javascript\"></script>\n");
                                    writer.write("<div id=\"q" + idString + "-div\" class=\"parser_text_default\"> :e <br><span id=\"q" + idString + "-error\" class=\"parser_error\"></span></div><script type=\"text/javascript\">register(\"q" + idString + "\", " + formtypelower + "Parser.parse)</script>\n");
                                }
                            }
                            writer.write(s2 + "\n");
                        }
                        else if (!s.contains("<input"))
                        {
                            writer.write(s + "\n");
                        }
                    }   System.out.println("-Successfully converted " + questionNumber + " questions.\n");
                }
                writer.flush();
                writer.close();
            }
            catch (Exception e)
            {
                System.err.format("ERROR: Could not read content from file '%s'. Exporting aborted.\n", inputName);
                return;
            }
        }
	System.out.println("Finished. Press Enter to continue.");
        try 
        {
            System.in.read();
        } 
        catch (IOException ex) 
        {
        }
    }
}
