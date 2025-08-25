package splProject;
//connection details save kora
public class Connection {
    private String fromId;
    private String toId;
    private int termIndex;

    public Connection(String fromId, String toId, int termIndex) {
        this.fromId = fromId;
        this.toId = toId;
        this.termIndex = termIndex;
    }

    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    public int getTermIndex() {
        return termIndex;
    }
}
