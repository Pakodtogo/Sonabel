import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;


public class CashPower extends JFrame {
    private JTextField montantField;
    private JTextField codeTarifField;
    private JTextField compteurField;
    private JTextField dateAchatField;
    private JTextField cumulEnergieField;

    public CashPower() {
        setTitle("SONABEL VENTE");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2));

        Font labelFont = new Font("Arial", Font.BOLD, 20);


        JLabel montantLabel = new JLabel("Montant:");
        montantField = new JTextField();
        JLabel codeTarifLabel = new JLabel("Code Tarif:");
        codeTarifField = new JTextField();
        JLabel compteurLabel = new JLabel("Numéro de Compteur:");
        compteurField = new JTextField();
        JLabel dateAchatLabel = new JLabel("Date du Dernier Achat (jj-mm-yy):");
        dateAchatField = new JTextField();
        JLabel cumulEnergieLabel = new JLabel("Cumul d'Énergie Payée:");
        cumulEnergieField = new JTextField();
        montantLabel.setForeground(Color.BLACK);
        codeTarifLabel.setForeground(Color.BLACK);
        compteurLabel.setForeground(Color.BLACK);
        dateAchatLabel.setForeground(Color.BLACK);
        cumulEnergieLabel.setForeground(Color.BLACK);

        montantField.setBackground(Color.LIGHT_GRAY);
        codeTarifField.setBackground(Color.LIGHT_GRAY);
        compteurField.setBackground(Color.LIGHT_GRAY);
        dateAchatField.setBackground(Color.LIGHT_GRAY);
        cumulEnergieField.setBackground(Color.LIGHT_GRAY);

        montantLabel.setFont(labelFont);
        codeTarifLabel.setFont(labelFont);
        compteurLabel.setFont(labelFont);
        dateAchatLabel.setFont(labelFont);
        cumulEnergieLabel.setFont(labelFont);

        montantField.setFont(labelFont);
        codeTarifField.setFont(labelFont);
        compteurField.setFont(labelFont);
        dateAchatField.setFont(labelFont);
        dateAchatField.setText("04-12-2023");
        cumulEnergieField.setFont(labelFont);
        cumulEnergieField.setText("0.0");

        add(montantLabel);
        add(montantField);
        add(codeTarifLabel);
        add(codeTarifField);
        add(compteurLabel);
        add(compteurField);
        add(dateAchatLabel);
        add(dateAchatField);
        add(cumulEnergieLabel);
        add(cumulEnergieField);

        JButton calculerButton = new JButton("Calculer");
        calculerButton.setFont(labelFont);
        calculerButton.setBackground(Color.ORANGE);
        calculerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calculerButton.addActionListener(e -> effectuerCalculs());
        add(calculerButton);

        setVisible(true);

         // Charger et utiliser une image comme icône pour la fenêtre
         ImageIcon icon = new ImageIcon("Sonabel.png");
         Image image = icon.getImage();
         setIconImage(image);
         Image newImage = image.getScaledInstance(1000, 250, Image.SCALE_SMOOTH); // Nouvelle taille: 50x50
        setIconImage(newImage);
         pack();
         setLocationRelativeTo(null);
         setVisible(true);
    }

    private void effectuerCalculs() {

        // Récupérer les valeurs saisies
        double montantEntre = Double.parseDouble(montantField.getText());
        int codeTarifEntre = Integer.parseInt(codeTarifField.getText());
        int numeroCompteur = Integer.parseInt(compteurField.getText());
        double cumulEnergie = Double.parseDouble(cumulEnergieField.getText());

        // Récupérer la date d'achat
        String dateAchatStr = dateAchatField.getText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dateDernierAchat = LocalDate.parse(dateAchatStr, formatter);

        // Obtenir la date actuelle
        LocalDate dateDuJour = LocalDate.now();

        JFrame frame = new JFrame("Reçu de l'achat");
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JTextArea resultatArea = new JTextArea();
        resultatArea.setEditable(false);
      

        // Calcul de la période d'accumulutation des taxes fixes
        int mois = dateDuJour.getMonthValue() - dateDernierAchat.getMonthValue();
        int annees = dateDuJour.getYear() - dateDernierAchat.getYear();
        int periodeAccTF = 12 * annees + mois;

        Double energiePayee = 0.0;
        Double monnaieRendue = 0.0;

        try {
          // Charger le contenu du fichier JSON de tarifs
          String tarifsContent = new String(Files.readAllBytes(Paths.get("tarifs.json")));
          JSONArray tarifsArray = new JSONArray(tarifsContent);
         
          

          // Parcourir les tarifs pour trouver celui correspondant au code saisi
          
          for (int i = 0; i < tarifsArray.length(); i++) {
              JSONObject tarif = tarifsArray.getJSONObject(i);
              Integer codeTarif = tarif.getInt("code_tarif");

              

              

             
              if (codeTarif.equals(codeTarifEntre)) {
                  // Afficher l'objet tarif correspondant
                  Integer primeFixe = tarif.getInt("prime_fixe"); 
                  Integer tqr = CalculTQR.calculTQR(montantEntre); // Calcul du TQR
                  Integer redevance = tarif.getInt("redevance");


                  //calcul dde la valeur du cumul des taxes fixes.
                  Integer primeFixeAccumule = periodeAccTF*primeFixe;
                  Integer redevanceAccumule = periodeAccTF*redevance;
                  Integer taxesFixesAccumules = primeFixeAccumule + redevanceAccumule; 
                  Double montantATF = montantEntre - tqr - taxesFixesAccumules;


                  resultatArea.append("code tarif : " + codeTarif + "\n");
                  resultatArea.append("Prime Fixe: " + primeFixe + "\n");
                  resultatArea.append("Redevance: " + redevance + "\n");
                

                  // Charger la liste des tranches associées à ce tarif
                  String tranchesContent = new String(Files.readAllBytes(Paths.get("tranches.json")));
                  JSONArray tranchesArray = new JSONArray(tranchesContent);
                  
                  

                  
                  // Parcourir les tranches
                  for (int a = 0; a < tranchesArray.length(); a++) {
                    JSONObject tranche = tranchesArray.getJSONObject(a);
                    Integer codeTarifTranche = tranche.getInt("CodeTarif");
                    Integer finTranche = tranche.getInt("Fin");
                    Double coutTTCtranche = tranche.getDouble("CoutTTC");
                    Double tvaTranche = tranche.getDouble("TvaTrch");
                    Double coutMaxTranche = coutTTCtranche * finTranche;

                    Double montantCumulEnergie = 0.0;
                    Double restCumulEnergie = cumulEnergie;

                    if (codeTarifTranche.equals(codeTarif)) {

                      if (finTranche > 0 && restCumulEnergie >= finTranche) {
                        montantCumulEnergie += coutMaxTranche;
                        restCumulEnergie -= finTranche;
                    } else {
                        montantCumulEnergie += restCumulEnergie * coutTTCtranche;
                        break; // Fin de la comparaison, on a parcouru les tranches pour le cumulEnergie
                    }


                    // Calcul de la valeur du montant qui doit parcourir les tranches

                    Double montantAchat = montantATF + montantCumulEnergie;
                        
                       

                         if (coutMaxTranche > 0){
                          if ( montantAchat > coutMaxTranche) {
                            energiePayee += finTranche;
                            montantAchat = montantAchat-coutMaxTranche;

                            
                          } 
                          
                          else if(montantAchat > tvaTranche) {
                          energiePayee += montantAchat / coutTTCtranche;
                          montantAchat = 0.00;
     
                            break; // Sortir de la boucle à la dernière tranche
                          } 
                          else {
                            monnaieRendue = montantAchat;
                            break; // Sortir de la boucle à la dernière tranche
                          }
                         } 

                         else{
                          if(montantAchat > tvaTranche) {
                          energiePayee += montantAchat / coutTTCtranche;
                          montantAchat = 0.00;
     
                            break; // Sortir de la boucle à la dernière tranche
                        } else {
                            monnaieRendue = montantAchat;
                            break; // Sortir de la boucle à la dernière tranche
                        }
                         }
                         
                         
                    }
                }

                

              }

              // Afficher les valeurs
         
                resultatArea.append("Energie Payée : " + energiePayee + "\n");
                resultatArea.append("Monnaie Rendue : " + monnaieRendue + "\n");
                break; // Arrêter la recherche une fois le tarif trouvé
              
          }
           
         

      } catch (IOException | JSONException e) {
          e.printStackTrace();
      }
        
        // Affichage des résultats dans la console pour démonstration
        resultatArea.append("\n Montant: " + montantEntre + "\n");
        resultatArea.append("Numéro de Compteur: " + numeroCompteur + "\n");
        resultatArea.append("Résultat de la formule: " + periodeAccTF + "\n");
        
        // Ajout du composant JTextArea dans un JScrollPane pour permettre le défilement si nécessaire
        JScrollPane scrollPane = new JScrollPane(resultatArea);

        // Ajout du JScrollPane au cadre
        frame.add(scrollPane, BorderLayout.CENTER);

        // Rendre la fenêtre visible
        frame.setVisible(true);
   
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(CashPower::new);
    }
}
