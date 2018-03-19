package cz.muni.fi.pda.automaton;

import java.util.*;

/**
 * @author      Daniel Pelisek <dpelisek@gmail.com>
 * @version     1.0              
 * @since       2011-12-4
 */
public class PushDownAutomaton {

    private Set<String> states; // a-z
    private Set<String> inputAlphabet; // a-z nebo 0-9
    private Set<String> stackAlphabet; // A-Z nebo <A-Z nebo a-z nebo 0-9 nebo A-Z' a-z' 0-9'> nebo A-Z' a-z' 0-9'
    private Map<List<List<String>>, Set<List<List<String>>>> transitionRelation;
    private String startState; // a-z
    private String initialStackSymbol; // A-Z nebo <A-Z nebo a-z nebo 0-9> nebo A-Z' a-z' 0-9'
    private Set<String> acceptingStates; // a-z

    public PushDownAutomaton(Set<String> states, Set<String> inputAlphabet, Set<String> stackAlphabet,
            Map<List<List<String>>, Set<List<List<String>>>> transitionRelation, String startState,
            String initialStackSymbol, Set<String> acceptingStates) {
        this.states = states;
        this.inputAlphabet = inputAlphabet;
        this.stackAlphabet = stackAlphabet;
        this.transitionRelation = transitionRelation;
        this.startState = startState;
        this.initialStackSymbol = initialStackSymbol;
        this.acceptingStates = acceptingStates;
    }
    
    /**
     * constructor of PushDownAutomaton 
     * 
     * @param input given string (example: (a,\e, <a''B>A'8) = {(a, b'<a>),(b, /e)})
     */
    public PushDownAutomaton(String input) {
                
        input = input.replaceAll("[\\r\\n]", ";");
        input = input.replaceAll("[;]+", ";");
        
        String firstRow = input.substring(0, input.indexOf(";")).replaceAll("(\\(|\\{|\\}\\)| )", "");
        
        input = input.substring(input.indexOf(";")+1).replaceAll("[ \\{\\}]", "");
        
        String[] items = firstRow.split("},");
        if (items.length != 4)
            throw new IllegalArgumentException(firstRow + " není správně zadaná sedmice.");
        
        states = new HashSet<String>(Arrays.asList(items[0].split(",")));
        inputAlphabet = new HashSet<String>(Arrays.asList(items[1].split(",")));
        stackAlphabet = new HashSet<String>(Arrays.asList(items[2].split(",")));
        transitionRelation = parseText(input);
        acceptingStates = new HashSet<String>();
        
        String[] others = items[3].split(",");
        for (int i = 1; i < others.length; i++) {
            if (i == 1)
                startState = others[i];
            else if (i == 2)
                initialStackSymbol = others[i];
            else
                acceptingStates.add(others[i]);
        }
    }
        
    /**
     * method parses automaton from string to map of transition relation
     * 
     * @param in given string (example: (a,\e, <a''B>A'8) = {(a, b'<a>),(b, /e)})
     * @return   map of transition relation (example: {([[a],,[<a''B>, A', 8]], {[[a], [b', <a>]], [[b], []]})})
     */
    public static Map<List<List<String>>, Set<List<List<String>>>> parseText(String in) {
        
        String[] rows = in.split(";");
        Map<List<List<String>>, Set<List<List<String>>>> relation = new HashMap<List<List<String>>, Set<List<List<String>>>>(rows.length);
        
        for (String row : rows) {
            
            String[] sides = row.split("=");
            if (sides.length != 2)
                throw new IllegalArgumentException(row + " není správně zadaná přechodová funkce.");
            
            // left side
            String[] left = sides[0].replaceAll("[()]", "").split(",");
            if (left.length != 3 || !left[0].matches("[a-z]") || !left[1].matches("[a-z]||\\\\e"))
                throw new IllegalArgumentException(row + " není správně zadaná přechodová funkce.");
            List<List<String>> leftSide = new ArrayList<List<String>>(3);
            
            for (int i = 0; i < 2; i++) {
                leftSide.add(new ArrayList<String>(1));
                if (!left[i].equals("\\e"))
                    leftSide.get(i).add(left[i]);
            }
            leftSide.add(parseStackSymbols(left[2]));
            
            // right side
            String[] rights = sides[1].replaceAll("^\\(", "").replaceAll("\\)$", "").split("\\),\\(");
            Set<List<List<String>>> rightSide = new HashSet<List<List<String>>>(rights.length);
            
            for (String right : rights) {
                String[] items = right.split(",");
                if (items.length != 2 || !items[0].matches("[a-z]"))
                    throw new IllegalArgumentException(row + " není správně zadaná přechodová funkce.");
                List<List<String>> itemsList = new ArrayList<List<String>>(2);
                itemsList.add(new ArrayList<String>(1));
                itemsList.get(0).add(items[0]);
                itemsList.add(parseStackSymbols(items[1]));
                rightSide.add(itemsList);
            }
            
            // add both sides to map
            relation.put(leftSide, rightSide);
        }
        return relation;
    }
    
    /**
     * method parses stack symbols from string  to list
     * 
     * @param in given string (example: "a''Z0<abba>")
     * @return   list of stack symbols (example: [a'', Z, 0, <abba>])
     */
    private static List<String> parseStackSymbols(String in) {
        
        List<String> stackSymbols = new ArrayList<String>();
        
        while (!in.isEmpty() && !in.equals("\\e")) {
            int start = in.length()-1;
            while ((in.endsWith(">") && in.charAt(start) != '<') || (in.endsWith("\'") && in.charAt(start) == '\'')) {
                if (start == 0)
                    throw new IllegalArgumentException(in + "není správný řetězec symbolů zásobníkové abecedy.");
                start--;
            }
            if (in.endsWith("\\t"))
                start = start-1;
            String symbol = in.substring(start);
            in = in.substring(0, start);
            if (!symbol.matches("(<([A-Za-z0-9][\\']*)+>|\\\\t|[A-Za-z0-9][\\']*)"))
                throw new IllegalArgumentException(in + "není správný řetězec symbolů zásobníkové abecedy.");
            stackSymbols.add(0, symbol);
        }
        return stackSymbols;
    }

    @Override
    public String toString() {
        
        StringBuilder output = new StringBuilder();
        
        output.append("(").append(states.toString().replace("[", "{").replace("]", "}")).append(", ");
        output.append(inputAlphabet.toString().replace("[", "{").replace("]", "}")).append(", ");
        output.append(stackAlphabet.toString().replace("[", "{").replace("]", "}")).append(", \\d, ");
        output.append(startState).append(", ").append(initialStackSymbol).append(", ");
        output.append(acceptingStates.toString().replace("[", "{").replace("]", "}")).append(")\n\n");
        
        for (List<List<String>> leftSide : transitionRelation.keySet()) {
            
            output.append("(").append(leftSide.get(0).get(0)).append(", ");
            output.append((leftSide.get(1).isEmpty()) ? "\\e" : leftSide.get(1).get(0)).append(", ");
            output.append(leftSide.get(2).toString().replaceAll("[\\[\\], ]", ""));
            output.append((leftSide.get(2).isEmpty()) ? "\\e) = {" : ") = {");
            
            for (List<List<String>> rightSide : transitionRelation.get(leftSide)) {
                output.append("(").append(rightSide.get(0).get(0)).append(", ");
                output.append(rightSide.get(1).toString().replaceAll("[\\[\\], ]", ""));
                output.append((rightSide.get(1).isEmpty()) ? "\\e), " : "), ");
            }
            
            if (output.substring(output.length()-2).equals(", "))
                output.replace(output.length()-2, output.length(), "");
            output.append("}\n");
        }
        return output.toString();
    }
}

