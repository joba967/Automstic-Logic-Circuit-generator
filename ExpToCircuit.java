package splProject;

import java.util.*;

public class ExpToCircuit {
    private String formExp;
    private Set<String> var = new HashSet<>();

    public LogicCircuit parse(String exp) { //expression theke logicCircuit type er object return korbe
        
        exp = exp.replaceAll("\\s+", "");//space ,tab baad 
        var.clear();
        
        if (exp.isEmpty()) {
            throw new IllegalArgumentException("Expression can't be empty");
        }

        String[] Terms = exp.split("\\+");
        if (Terms.length == 0) {
            throw new IllegalArgumentException("Invalid expression");
        }

        LogicCircuit circuit = new LogicCircuit();

        for (String term : Terms) {
            findVariables(term); //protita term theke variable alada kore ver set e add kora
        }

        List<String> sortVars = new ArrayList<>(var);
        Collections.sort(sortVars);

        for (String var : sortVars) {
            circuit.addVariable(var);
        }

        List<String> andGateIds = new ArrayList<>(); //andGate er id er jonno arraylist
        for (int t = 0; t < Terms.length; t++) {
            String term = Terms[t];

            LogicGate andGate = new LogicGate(LogicGate.Type.AND); // protita term er jonno ekta kore and gate
            circuit.addGate(andGate);
            andGateIds.add(andGate.getId());

            for (int i = 0; i < term.length(); i++) {
                char ch = term.charAt(i);
                if (Character.isLetter(ch)) {
                    String varName = String.valueOf(ch);
                    boolean isNegative = (i + 1 < term.length() && term.charAt(i + 1) == '\'');
                    
                    if (isNegative) {
                        String notId = "NOT_" + varName + "_" + t;
                        LogicGate notGate = new LogicGate(LogicGate.Type.NOT, notId);
                        circuit.addGate(notGate); // ' thakle not gate

                        circuit.addConnection(varName, notGate.getId(), t);
                        circuit.addConnection(notGate.getId(), andGate.getId(), t); //and gate er shathe not gatee er connection
                        i++; // ' eitake jate variable hishebe input na ney
                    } else {
                        circuit.addConnection(varName, andGate.getId(), t);
                    }
                } else if (ch != '\'') {
                    throw new IllegalArgumentException("Unexpected character: " + ch);
                }
            }
        }

        if (Terms.length > 1) { // term 1 er beshi hole shobgulur and gate ekta or e connect hobe 
            LogicGate orGate = new LogicGate(LogicGate.Type.OR);
            circuit.addGate(orGate);

            for (int i = 0; i < andGateIds.size(); i++) {
                circuit.addConnection(andGateIds.get(i), orGate.getId(), i);
            }

            circuit.setOutput(orGate.getId());
        } else if (Terms.length == 1) { //mane ektai term kunu or gate hobe na
            circuit.setOutput(andGateIds.get(0));
        }

        formExp = exp;
        return circuit;
    }

    private void findVariables(String term) {
        for (int i = 0; i < term.length(); i++) {
            char c = term.charAt(i);
            if (Character.isLetter(c)) {
                var.add(String.valueOf(c));

                if (i + 1 < term.length() && term.charAt(i + 1) == '\'') {
                    i++;
                }
            }
        }
    }
/*
    public String getFormattedExpression()() {
        return formExp;
    }
*/
}
