import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingInterfaceExample extends JFrame implements ActionListener {
    
    private JTextField[] textFields;
    private JButton submitButton;

    public SwingInterfaceExample() {
        setTitle("Formulaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2));

        textFields = new JTextField[5];
        for (int i = 0; i < 5; i++) {
            JLabel label = new JLabel("Champ " + (i + 1) + ":");
            textFields[i] = new JTextField(20);
            add(label);
            add(textFields[i]);
        }

        submitButton = new JButton("Valider");
        submitButton.addActionListener(this);

        add(new JLabel()); // Placeholder for an empty cell
        add(submitButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            // Action à effectuer lors du clic sur le bouton
            for (int i = 0; i < 5; i++) {
                String fieldValue = textFields[i].getText();
                // Utilisez les valeurs des champs de saisie comme vous le souhaitez
                System.out.println("Champ " + (i + 1) + ": " + fieldValue);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingInterfaceExample());
    }
}
