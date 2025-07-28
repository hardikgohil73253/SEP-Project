import javax.swing.*;
import java.awt.*;

/**
 * TanCalculator — computes tan(x) without Java's trig library.
 * 
 * FUNCTIONAL REQUIREMENTS (ISO/IEC/IEEE 29148)
 * FR‑1  The system shall present a single text‑input field that accepts an angle in degrees.                [6]
 * FR‑2  The system shall convert that degree value to radians using radians = degrees × π / 180.           [6]
 * FR‑3  The system shall compute sin x and cos x via a Maclaurin series containing at least 15 terms.       [5][7]
 * FR‑4  The system shall calculate tan x as the quotient sin x / cos x.                                     [1]
 * FR‑5  The system shall display UNDEFINED whenever |cos x| < 1×10^(-12).                                    [1]
 * FR‑6  The system shall display INVALID INPUT when the entry is empty, non‑numeric, NaN, or ∞.           [2]
 * FR‑7  The system shall provide Compute and Clear buttons and shall terminate when the user types exit.  [2]
 * FR‑8  The system shall format each numeric result with exactly six (6) decimal places.                    [4]
 * FR‑9  Before series evaluation, the system shall normalise radians into the range −π … π.                [5]
 * FR‑10 The system shall catch all unexpected runtime errors and display ERROR instead of terminating.    [3][2]
 */
public class TanCalculator extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final double PI = 3.1415926535897932384626433832795;
    private static final double EPS = 1e-12;

    private final JTextField inputField = new JTextField(10);
    private final JLabel resultLabel = new JLabel("Result:");

    public TanCalculator() {
        super("tan(x) Calculator");
        buildUI();
    }

    private void buildUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,8,8,8);

        g.gridx=0; g.gridy=0; g.gridwidth=2;
        add(new JLabel("Enter angle in degrees:"), g);

        g.gridy=1;
        add(inputField, g);

        JButton compute = new JButton("Compute");
        JButton clear   = new JButton("Clear");
        JPanel panel = new JPanel();
        panel.add(compute);
        panel.add(clear);

        g.gridy=2;
        add(panel, g);

        g.gridy=3;
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD,14f));
        add(resultLabel, g);

        compute.addActionListener(e->compute());
        clear.addActionListener(e->{inputField.setText(""); resultLabel.setText("Result:");});

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void compute(){
        try{
            String txt = inputField.getText().trim();
            if(txt.equalsIgnoreCase("exit")){dispose();return;}
            double deg = parse(txt);
            double rad = normalize(toRad(deg));
            double t = tan(rad);
            resultLabel.setText(String.format("Result: %.6f", t));
        }catch(UndefinedTangentException u){
            resultLabel.setText("Result: UNDEFINED");
        }catch(InvalidInputException i){
            resultLabel.setText("Result: INVALID INPUT");
        }catch(Exception ex){
            resultLabel.setText("Result: ERROR");
        }
    }

    private double parse(String s) throws InvalidInputException{
        double v;
        try{v=Double.parseDouble(s);}catch(NumberFormatException e){throw new InvalidInputException();}
        if(Double.isNaN(v)||Double.isInfinite(v)) throw new InvalidInputException();
        return v;
    }

    private double toRad(double d){return d*PI/180.0;}

    private double normalize(double r){
        double twoPi=2*PI;
        r%=twoPi;
        if(r>PI) r-=twoPi;
        if(r<-PI) r+=twoPi;
        return r;
    }

    private double tan(double x) throws UndefinedTangentException{
        double c=cos(x);
        if(Math.abs(c)<EPS) throw new UndefinedTangentException();
        return sin(x)/c;
    }

    private double sin(double x){
        double term=x,sum=x;
        for(int n=1;n<15;n++){
            term*=-x*x/((2*n)*(2*n+1));
            sum+=term;
        }
        return sum;
    }

    private double cos(double x){
        double term=1,sum=1;
        for(int n=1;n<15;n++){
            term*=-x*x/((2*n-1)*(2*n));
            sum+=term;
        }
        return sum;
    }

    private static class InvalidInputException extends Exception{}
    private static class UndefinedTangentException extends Exception{}

    public static void main(String[] args){EventQueue.invokeLater(() -> new TanCalculator().setVisible(true));}
}
