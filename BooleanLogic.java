

package splProject;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;


public class BooleanLogic extends JFrame {
    private static final Color basic = new Color(121, 101, 241);
    private static final Color success = new Color(76, 175, 80);
    private static final Color error = new Color(244, 67, 54);
    
    final JPanel mainPanel;
    final CardLayout layout;
    private JPanel welcomePanel;
    private JPanel inpPanel;
    private JPanel circuitOrigPanel;
    private JPanel circuitSimpPanel;
    
    JTextField expField;
    JLabel simpExpLabel;
    private CircuitPanel origCirPanel;
    private CircuitPanel simpCirPanel;
    
    String origExp = "";
    String simpExp = "";
    Simplifier simplifier;

    public BooleanLogic() {
        setTitle("Automatic Logic Circuit Generator");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);//eita dile majhkhane ashbe
        mainPanel = new JPanel();
        layout = new CardLayout();
        mainPanel.setLayout(layout);
        createWelcomePanel();
        createInpPanel();
        createCircuitOrigPanel();
        createCircuitSimpPanel();
        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(inpPanel, "input");
        mainPanel.add(circuitOrigPanel, "circuitOriginal");
        mainPanel.add(circuitSimpPanel, "circuitSimplified");
        add(mainPanel);
        layout.show(mainPanel, "welcome");
        
        simplifier = new Simplifier(this);
       
    }

    private void createWelcomePanel() {
        welcomePanel = new JPanel(new BorderLayout(20, 20));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        welcomePanel.setBackground(new Color(240, 248, 255));
        JLabel titleLabel = new JLabel("Automatic Logic Circuit Generator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(basic);
        welcomePanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea descrip = new JTextArea();
        descrip.setText(
            "This tool helps you to simplify Boolean expressions and generate logic circuits.\n\n" +
            "Supported operators:\n" +
            "• + for OR\n" +
            "• · or * for AND\n" +
            "• ' for NOT (complement)\n\n" +
            "Example: A·B + A'·C"
        );
        descrip.setEditable(false);
        descrip.setFont(new Font("Arial", Font.PLAIN, 20));
        descrip.setBackground(Color.WHITE);
        descrip.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        welcomePanel.add(descrip, BorderLayout.CENTER);

        JButton startButton = new JButton("Start Using Tool");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setBackground(basic);
        startButton.setForeground(Color.WHITE);
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.addActionListener(e -> {
            layout.show(mainPanel, "input");
            SwingUtilities.invokeLater(() -> expField.requestFocusInWindow()); //jate expField e cursor thake
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        welcomePanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createInpPanel() {
        inpPanel = new JPanel(new BorderLayout(60, 60));
        inpPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        inpPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Automatic Logic Circuit Generator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(basic);
        inpPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inpSec = new JPanel(new BorderLayout(10, 10));
        inpSec.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            "Input Section. Enter a Boolean Expression:",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 25),
            success
        ));
        inpSec.setBackground(Color.WHITE);
      
        expField = new JTextField(40);//input nicche
        expField.setFont(new Font("Arial", Font.PLAIN, 20));
        expField.setPreferredSize(new Dimension(200, 70));

        simpExpLabel = new JLabel("Simplified Expression: ");
        simpExpLabel.setFont(new Font("Arial", Font.BOLD, 30));
        simpExpLabel.setForeground(success);

        inpSec.add(expField, BorderLayout.NORTH);
        inpSec.add(simpExpLabel, BorderLayout.CENTER);
        inpPanel.add(inpSec, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));//eker por ek boshbe
        buttonPanel.setOpaque(false);
        JButton simplifyButton = createButton("Simplify Expression", success);
        simplifyButton.addActionListener(e -> {
            origExp = expField.getText().trim();
            simplifier.simplify();
        });

        JButton genOrigCirButton = createButton("Generate Original Circuit", basic);
        genOrigCirButton.addActionListener(e -> {
            origExp = expField.getText().trim();
            if (!origExp.isEmpty()) {
                genOrigCircuit();
                layout.show(mainPanel, "circuitOriginal");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter an expression first!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton genSimpCirButton = createButton("Generate Simplified Circuit", basic);
        genSimpCirButton.addActionListener(e -> {
            if (!simpExp.isEmpty()) {
                genSimpCircuit();
                layout.show(mainPanel, "circuitSimplified");
            } else {
                JOptionPane.showMessageDialog(this, "Please simplify the expression first!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = createButton("Back", Color.GRAY);
        backButton.addActionListener(e -> layout.show(mainPanel, "welcome"));
        
        JButton clearButton = createButton("Clear", error); // Clear 
           clearButton.addActionListener(e -> {
           expField.setText(""); // expression field clear
           simpExpLabel.setText("Simplified Expression: "); // simpExpLabel
           simpExp = ""; // simplified expression variable clear
    });


        buttonPanel.add(simplifyButton);
        buttonPanel.add(genOrigCirButton);
        buttonPanel.add(genSimpCirButton);
        buttonPanel.add(backButton);
        buttonPanel.add(clearButton);

        inpPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createCircuitOrigPanel() {
        circuitOrigPanel = new JPanel(new BorderLayout(20, 20));
        circuitOrigPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        circuitOrigPanel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("Logic Circuit For Original Expression", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(basic);
        circuitOrigPanel.add(headerLabel, BorderLayout.NORTH);

        origCirPanel = new CircuitPanel();
        origCirPanel.setPreferredSize(new Dimension(900, 400));
        JScrollPane scrollPane = new JScrollPane(origCirPanel);
        circuitOrigPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton backButton = createButton("Back to Input", Color.GRAY);
        backButton.addActionListener(e -> layout.show(mainPanel, "input"));
        buttonPanel.add(backButton);

        circuitOrigPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createCircuitSimpPanel() {
        circuitSimpPanel = new JPanel(new BorderLayout(20, 20));
        circuitSimpPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        circuitSimpPanel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("Logic Circuit For Simplified Expression", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(basic);
        circuitSimpPanel.add(headerLabel, BorderLayout.NORTH);

        simpCirPanel = new CircuitPanel();
        simpCirPanel.setPreferredSize(new Dimension(900, 400));
        JScrollPane scrollPane = new JScrollPane(simpCirPanel);
        circuitSimpPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton backButton = createButton("← Back to Input", Color.GRAY);
        backButton.addActionListener(e -> layout.show(mainPanel, "input"));
        buttonPanel.add(backButton);

        circuitSimpPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }

    public void genSimpCircuit() {
        try {
            ExpToCircuit parser = new ExpToCircuit();
            LogicCircuit circuit = parser.parse(simpExp);
            simpCirPanel.setCircuit(circuit, simpExp);
            simpCirPanel.repaint();//ager shob muche ekn ja deoya hocche ta akte
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating simplified circuit: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void genOrigCircuit() {
        try {
            ExpToCircuit parser = new ExpToCircuit();
            LogicCircuit circuit = parser.parse(origExp);
            origCirPanel.setCircuit(circuit, origExp);
            origCirPanel.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating original circuit: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setSimpExp(String simplified) {
        this.simpExp = simplified;
        simpExpLabel.setText("Simplified Expression: " + simplified);
    }

    public static void main(String[] args) {
        
            BooleanLogic bl = new BooleanLogic();
            bl.setVisible(true);
        
    }
}
