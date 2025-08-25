
//store logic gates, variables and their connections 
package splProject;

import java.util.*;

public class LogicCircuit {
    private Map<String, Integer> varPos = new HashMap<>(); //variable er shathe er position ba index store kore 
    private List<LogicGate> gates = new ArrayList<>(); // type logicGAte 
    private List<Connection> connections = new ArrayList<>(); //shob connection store kore mane  A'= A->and->not
    private String outputId;

    public void addVariable(String name) {
        if (!varPos.containsKey(name)) { //variable ta age theke ache kina
            varPos.put(name, varPos.size());
        }
    }

    public void addGate(LogicGate gate) {
        gates.add(gate);
    }

    public void addConnection(String fromId, String toId, int termIndex) { // connection ta kunta theke kuntay jabe r kun term er jate upore likha jay
        connections.add(new Connection(fromId, toId, termIndex)); //new connection constructor 
    }

    public void setOutput(String id) {
        this.outputId = id;
    }

    public Map<String, Integer> getVarPos() {
        return varPos;
    }

    public List<LogicGate> getGates() {
        return gates;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public String getOutputId() {
        return outputId;
    }

    public Set<String> getVariables() {
        return varPos.keySet(); // map er ekta built in function. var name gulu return korbe
    }

    public LogicGate getGateById(String id) { // index diye gate call kore
        for (LogicGate gate : gates) {
            if (gate.getId().equals(id)) {
                return gate;
            }
        }
        return null;
    }
}
