package splProject;

// ek ekta logic gate er id, type, pos store korteche
import java.awt.Point;

public class LogicGate {
    enum Type {
        AND, OR, NOT, INPUT
    }

    private static int nextId = 0;
    private String id;
    private Type type;
    private Point position;

    public LogicGate(Type type) {
        this.type = type;
        this.id = type.toString() + "_" + nextId++;
        this.position = new Point(0, 0);
    }

    public LogicGate(Type type, String id) {
        this.type = type;
        this.id = id;
        this.position = new Point(0, 0);
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
