package splProject;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class CircuitPanel extends JPanel {
    private LogicCircuit circuit;
    private String outputExp;
    
    // Layout constants
    private static final int left = 80;
    private static final int top = 100;
    private static final int varSpace = 80;
    private static final int gateSpace = 100;
    private static final int horizGap = 200;
    
    // Component positions
    private Map<String, Point> variablePos = new HashMap<>();
    private Map<String, Point> notGatePos = new HashMap<>();
    private Map<String, Point> andGatePos = new HashMap<>();
    private Point orGatePos;
    private Point outputPos;
    
    // Terms and their colors
    private List<String> terms = new ArrayList<>();
    private List<List<String>> termInputs = new ArrayList<>();  //List<String> → One term’s inputs (like ["A", "B'"])  
                                                               //List<List<String>> → All terms in the expression

    private List<Color> termColors = new ArrayList<>();

    public void setCircuit(LogicCircuit circuit, String expression) {
        this.circuit = circuit;
        this.outputExp = expression;
        
        if (circuit != null) {
            parseExpression(expression);
            setupTermColors();
            layoutComponents();
        }
        
        // Force parent container to update scroll bars
        SwingUtilities.invokeLater(() -> {
            Container parent = getParent();
            if (parent instanceof JViewport) {
                parent.revalidate();
            }
        });
        
        repaint();
    }

    private void parseExpression(String expression) {
        terms.clear();
        termInputs.clear();
        
        String cleanExp = expression.replaceAll("[·*]", "");
        String[] termArray = cleanExp.split("\\+");
        
        for (String term : termArray) {
            term = term.trim();
            terms.add(term);
            
            List<String> inputs = new ArrayList<>();
            for (int i = 0; i < term.length(); i++) {
                char c = term.charAt(i);
                if (Character.isLetter(c)) {
                    if (i + 1 < term.length() && term.charAt(i + 1) == '\'') {
                        inputs.add(c + "'");
                        i++;
                    } else {
                        inputs.add(String.valueOf(c));
                    }
                }
            }
            termInputs.add(inputs);
        }
    }

    private void setupTermColors() {
        termColors.clear();
        // Different colors for each term
        Color[] colors = {
            new Color(34, 139, 34),   //  Green
            new Color(30, 144, 255),  //  Blue  
            new Color(220, 20, 60),   //  Red
            new Color(255, 140, 0),   //  Orange
            new Color(138, 43, 226),  //  Violet
            new Color(255, 20, 147)   //  Pink
        };
        
        for (int i = 0; i < terms.size(); i++) {
            termColors.add(colors[i % colors.length]); // unique color assign kora hocche  0%3=0, 1%3=1....
        }
    }

    private void layoutComponents() {
        variablePos.clear();
        notGatePos.clear();
        andGatePos.clear();
        
        // Get all unique variables
        Set<String> allVars = new HashSet<>();
        for (List<String> inputs : termInputs) {
            for (String input : inputs) {
                if (input.endsWith("'")) {
                    allVars.add(input.substring(0, 1)); //0 index er ta nibe 
                } else {
                    allVars.add(input);
                }
            }
        }
        
        List<String> sortedVars = new ArrayList<>(allVars);
        Collections.sort(sortedVars);
        
        //   panel height based on number of terms and variables
        int height = Math.max(
            top + sortedVars.size() * varSpace + 100,
            top + terms.size() * gateSpace + 200
        );
        
        //panel width
        int width = left + 600; // Base width
        if (terms.size() > 1) {
            width += horizGap; // Extra space for OR gate
        }
        
        // Set preferred size dynamically
        setPreferredSize(new Dimension(width, height));
       
        // Position variables vertically on the left
        for (int i = 0; i < sortedVars.size(); i++) {
            String var = sortedVars.get(i);
            int y = top + i * varSpace;
            variablePos.put(var, new Point(left, y)); // y er value change hoye niche niche boshe jacche
        }
        
        // Position NOT gates
        int notGateX = left + 100;
        for (String var : sortedVars) {
            Point varPos = variablePos.get(var);  //point holo java.awt.Point class er ekta object jeita 2d pos bujhay
            notGatePos.put(var + "'", new Point(notGateX, varPos.y));
        }
        
        // Position AND gates with better spacing
        int andGateX = left + 400;
        for (int i = 0; i < terms.size(); i++) {
            int y = top + 50 + i * gateSpace;
            andGatePos.put("AND_" + i, new Point(andGateX, y));
        }
        
        // Position OR gate
        if (terms.size() > 1) {
            int orGateX = andGateX + horizGap;
            int orGateY = top + 50 + (terms.size() - 1) * gateSpace / 2; //or gate shob and gate er majhkhane
            orGatePos = new Point(orGateX, orGateY);
            outputPos = new Point(orGateX + 150, orGateY);
        } else {
            Point andPos = andGatePos.get("AND_0");
            outputPos = new Point(andPos.x + 150, andPos.y);
        }
        
        // Revalidate  to update scroll bars knona notun component add kora and layout e change ana hoyeche
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) { //jpanel e draw kore
        super.paintComponent(g); //paren class er method
        setBackground(Color.WHITE);
        
        if (circuit != null) {
            Graphics2D g2d = (Graphics2D) g; // java er normal drawing system graphics k 2d graphics e neoya
            //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setStroke(new BasicStroke(3.0f));//kotota mota hobe 3 pixel kore  
                                                  // f mane float eikhane. normallyte java te decimal point nile sheita double hishebe kaaj kore
            drawExp(g2d);
            drawVar(g2d);
            drawNotGates(g2d);
            drawAndGates(g2d);
            drawOrGate(g2d);
            drawSeparateConnections(g2d);
            drawOutput(g2d);
            drawColorLegend(g2d);
        }
    }

    private void drawExp(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(outputExp, 20, 40);
    }

    
    
    private void drawVar(Graphics2D g) {
    
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.BOLD, 18));

   
    for (String variable : variablePos.keySet()) { //variablePos namok map theke shudhu variable ta nicche
        Point point = variablePos.get(variable);  // variable position

        g.drawString(variable, point.x - 20, point.y + 6);//variable lekha

        
        g.fillOval(point.x - 4, point.y - 4, 8, 8); // variable er shamne ekta gol aka 8,8 beshardho
    }
}

    

    private void drawNotGates(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        
        for (Map.Entry<String, Point> entry : notGatePos.entrySet()) {
            String notVar = entry.getKey();
            Point pos = entry.getValue();
            String baseVar = notVar.substring(0, 1);
            
            // Check if this NOT gate is needed
            boolean needed = false;
            for (List<String> inputs : termInputs) {
                if (inputs.contains(notVar)) {
                    needed = true;
                    break;
                }
            }
            
            if (needed) {
                //  NOT gate as small triangle 
                int[] xPoints = {pos.x - 15, pos.x - 15, pos.x + 5};
                int[] yPoints = {pos.y - 10, pos.y + 10, pos.y};
                g.fillPolygon(xPoints, yPoints, 3);
                g.drawPolygon(xPoints, yPoints, 3);
                
                // Small bubble for inversion
                g.drawOval(pos.x + 5, pos.y - 4, 8, 8);
                
                // Connection from variable
                Point varPos = variablePos.get(baseVar);
                g.drawLine(varPos.x, varPos.y, pos.x - 15, pos.y);
                
                // Label
                g.drawString(baseVar + "'", pos.x - 10, pos.y - 15);
            }
        }
    }

    private void drawAndGates(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        
        for (int i = 0; i < terms.size(); i++) {
            Point pos = andGatePos.get("AND_" + i);
            String term = terms.get(i);
            
            // Draw AND gate
            int width = 80;
            int height = 50;
            
            // Rectangle part   drawRect(int x, int y, int width, int height)

            g.drawRect(pos.x - width/2, pos.y - height/2, width/2, height);
            
            // drawArc(x, y, width, height, startAngle, arcAngle)
            g.drawArc(pos.x - height/2, pos.y - height/2, height, height, -90, 180); //jei curved ta akbe oita -90 theke 180 porjonto ghurbe
            
            // Term label
            g.setColor(termColors.get(i));
            g.drawString(term, pos.x - 30, pos.y - 30);
            g.setColor(Color.BLACK);
        }
    }

    private void drawOrGate(Graphics2D g) {
        if (orGatePos != null && terms.size() > 1) {
            g.setColor(Color.BLACK);
            
            int width = 80;
            int height = 60;
            
            // Draw OR gate shape
            g.drawArc(orGatePos.x - width, orGatePos.y - height/2, width, height, -90, 180);
            g.drawArc(orGatePos.x - width/2, orGatePos.y - height/2, width/2, height, -90, 180);
        }
    }

    private void drawSeparateConnections(Graphics2D g) {
        g.setStroke(new BasicStroke(3.0f)); //line koto mota
        
        for (int termIndex = 0; termIndex < terms.size(); termIndex++) {
            Color termColor = termColors.get(termIndex);
            g.setColor(termColor);
            
            Point andPos = andGatePos.get("AND_" + termIndex);
            List<String> inputs = termInputs.get(termIndex);
           
            
            
            
            
            
            // completely separate line
            for (int inputIndex = 0; inputIndex < inputs.size(); inputIndex++) {
                String input = inputs.get(inputIndex);
                Point sourcePos; //point java.awt er ekta class (x,y)
                
                if (input.endsWith("'")) {
                    // NOT gate 
                    sourcePos = notGatePos.get(input);
                    if (sourcePos != null) {
                        // Separate line path for each input
                        int offsetY = (inputIndex - inputs.size()/2) * 15; // Vertical offset
                        int midX = sourcePos.x + 50 + (inputIndex * 20); // Horizontal offset
                        
                        // Line from NOT gate to intermediate point
                        g.drawLine(sourcePos.x + 13, sourcePos.y, midX, sourcePos.y);
                        // Vertical line to AND gate level
                        g.drawLine(midX, sourcePos.y, midX, andPos.y + offsetY);
                        // Line to AND gate
                        g.drawLine(midX, andPos.y + offsetY, andPos.x - 40, andPos.y + offsetY);
                        g.drawLine(andPos.x - 40, andPos.y + offsetY, andPos.x - 40, andPos.y);
                        
                        // Connection points
                        g.fillOval(midX - 3, sourcePos.y - 3, 6, 6);
                        g.fillOval(midX - 3, andPos.y + offsetY - 3, 6, 6);
                    }
                } else {
                    // Variable directly
                    sourcePos = variablePos.get(input);
                    if (sourcePos != null) {
                        // Separate line path for each input
                        int offsetY = (inputIndex - inputs.size()/2) * 15; // vertical offset to prevent lines from overlapping 
                        int midX = sourcePos.x + 50 + (inputIndex * 20); // Horizontal offset....
                        
                        // Line from variable to intermediate point
                        g.drawLine(sourcePos.x, sourcePos.y, midX, sourcePos.y); //straight line from point (x1, y1) to point (x2, y2)
                        // Vertical line to AND gate level
                        g.drawLine(midX, sourcePos.y, midX, andPos.y + offsetY);
                        // Line to AND gate
                        g.drawLine(midX, andPos.y + offsetY, andPos.x - 40, andPos.y + offsetY);
                        g.drawLine(andPos.x - 40, andPos.y + offsetY, andPos.x - 40, andPos.y);
                        
                        // Connection points
                        g.fillOval(midX - 3, sourcePos.y - 3, 6, 6); //fillOval(int x, int y, int width, int height)

                        g.fillOval(midX - 3, andPos.y + offsetY - 3, 6, 6);
                    }
                }
            }
            
            // AND gate  OR gate connection (same color)
            if (orGatePos != null && terms.size() > 1) {
                int offsetY = (termIndex - terms.size()/2) * 20; //majhkhane
                g.drawLine(andPos.x + 25, andPos.y, orGatePos.x - 60, andPos.y); // and gate er dan dik  theke shoja  horizontal line create with same y
                g.drawLine(orGatePos.x - 60, andPos.y, orGatePos.x - 60, orGatePos.y + offsetY); // same x so verticle line
                g.drawLine(orGatePos.x - 60, orGatePos.y + offsetY, orGatePos.x - 40, orGatePos.y + offsetY); //horiz
                g.drawLine(orGatePos.x - 30, orGatePos.y + offsetY, orGatePos.x - 30, orGatePos.y);// or gate er center e connect houyar jonno
                
                // Connection points
                g.fillOval(orGatePos.x - 63, andPos.y - 3, 6, 6);
                g.fillOval(orGatePos.x - 63, orGatePos.y + offsetY - 3, 6, 6);
            }
        }
    }

    private void drawOutput(Graphics2D g) {
        if (outputPos != null) {
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(3.0f));
            
            // Output line
            if (terms.size() > 1 && orGatePos != null) {
                g.drawLine(orGatePos.x + 10, orGatePos.y, outputPos.x, outputPos.y);
            } else if (terms.size() == 1) {
                Point andPos = andGatePos.get("AND_0");
                g.drawLine(andPos.x + 30, andPos.y, outputPos.x, outputPos.y);
            }
            
            // Output terminal
            g.fillOval(outputPos.x - 5, outputPos.y - 5, 10, 10);
            
            // Output label
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("OUTPUT", outputPos.x + 15, outputPos.y + 5);
        }
    }

    private void drawColorLegend(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 12));
        int legendY = 60;
        
        for (int i = 0; i < terms.size(); i++) {
            g.setColor(termColors.get(i));
            g.fillRect(20, legendY + i * 25, 15, 15);
            g.setColor(Color.BLACK);
            g.drawRect(20, legendY + i * 25, 15, 15);
            g.drawString(terms.get(i), 45, legendY + i * 25 + 12);
        }
    }
}



