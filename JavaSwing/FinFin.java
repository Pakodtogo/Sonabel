import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;



public class FinFin extends JFrame {
    private JTextField montantField;
    private JTextField codeTarifField;
    private JTextField compteurField;
    private JTextField dateAchatField;
    private JTextField cumulEnergieField;

    public FinFin() {
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
        JLabel dateAchatLabel = new JLabel("Date du Dernier Achat:");
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
        calculerButton.addActionListener(e -> {
          try {
            effectuerCalculs();
          } catch (JSONException e1) {
 
            e1.printStackTrace();
          }
        });
        add(calculerButton);


        

        setVisible(true);
    }



    public static String genererCode() {
        List<String> groupsOfDigits = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String randomNumber = String.format("%04d", new Random().nextInt(10000));
            groupsOfDigits.add(randomNumber);
        }

        return String.join(" ", groupsOfDigits);
    }




    public JSONArray chargerFichierJSON(String nomFichier) {
      JSONArray jsonArray = new JSONArray();
      try {
          // Lecture du contenu du fichier JSON en tant que chaîne de caractères
          String contenu = new String(Files.readAllBytes(Paths.get(nomFichier)));

          // Conversion de la chaîne en JSONArray
          jsonArray = new JSONArray(contenu);
      } catch (IOException  | JSONException e) {
          // Gestion des erreurs d'entrée/sortie (IOException)
          e.printStackTrace();
      } 
      return jsonArray;
  }


    public static int calculTQR(double m) {
      int tqr;
      if (m < 100) {
          tqr = 0;
      } else if (100 <= m && m <= 1000) {
          tqr = 20;
      } else if (1000 < m && m <= 5000) {
          tqr = 30;
      } else if (5000 < m && m <= 10000) {
          tqr = 50;
      } else if (10000 < m && m <= 50000) {
          tqr = 100;
      } else {
          int n = (int) (m / 50000);
          tqr = 50 * (n + 2);
      }
      return tqr;
  }



  private int calculerPeriodeAccTF(String dateAchat) {

    int periodeAccTF = 0;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate dateDernierAchat = LocalDate.parse(dateAchat, formatter);
    LocalDate dateDuJour = LocalDate.now();

    int mois = dateDuJour.getMonthValue() - dateDernierAchat.getMonthValue();
    int annees = dateDuJour.getYear() - dateDernierAchat.getYear();
    periodeAccTF = 12 * annees + mois;
    return periodeAccTF;
  
}



private int calculerTaxesFixesAccumulees(int codeTarifEntre, int periodeAccTF) throws JSONException {

  JSONArray tarifsArray = chargerFichierJSON("tarifs.json");
  
  int taxesFixesAccumules = 0;

  for (int i = 0; i < tarifsArray.length(); i++) {
      JSONObject tarif = tarifsArray.getJSONObject(i);
      Integer codeTarif = tarif.getInt("code_tarif");
      Integer primeFixe = tarif.getInt("prime_fixe");
      Integer redevance = tarif.getInt("redevance");

      if (codeTarif.equals(codeTarifEntre)) {
          Integer primeFixeAccumule = periodeAccTF * primeFixe;
          Integer redevanceAccumule = periodeAccTF * redevance;
          taxesFixesAccumules = primeFixeAccumule + redevanceAccumule;
          break;
      }
  }
  return taxesFixesAccumules;
}



private double calculerMontantCumulEnergie(int codeTarifEntre, double cumulEnergie, double tvaTarif) throws JSONException {

  // ... chargement des tranches et autres opérations nécessaires
  JSONArray tranchesArray = chargerFichierJSON("tranches.json");
  
  double montantCumulEnergie = 0.0;
  double restCumulEnergie = cumulEnergie;

  // Parcourir les tranches et calculer le montant cumulé d'énergie en fonction du code tarif
  for (int a = 0; a < tranchesArray.length(); a++) {
      JSONObject tranche = tranchesArray.getJSONObject(a);
      Integer codeTarifTranche = tranche.getInt("CodeTarif");
      Integer finTranche = tranche.getInt("Fin");
      Integer debutTranche = tranche.getInt("Debut");
      Double coutTTCtranche = tranche.getDouble("CoutTTC");
      Double tvaTranche = tranche.getDouble("TvaTrch");
      Double coutMaxTranche = coutTTCtranche * finTranche;

      if (codeTarifTranche.equals(codeTarifEntre)) {
          if (finTranche != 0){
            if (restCumulEnergie >= finTranche) {
              montantCumulEnergie += coutMaxTranche + tvaTranche*tvaTarif;
            }
            else if(restCumulEnergie>=debutTranche) {
              montantCumulEnergie += (restCumulEnergie+1-debutTranche) * coutTTCtranche + tvaTranche*tvaTarif;
              restCumulEnergie = 0.0;
            }
            else{break;}
          }
          else{
            if(restCumulEnergie>=debutTranche) {
              montantCumulEnergie += (restCumulEnergie+1-debutTranche) * coutTTCtranche;
              restCumulEnergie = 0.0;
            }
            else{break;}
          }
      }
  }

  return montantCumulEnergie;
}




private double calculerTdeCumulEnergie(int codeTarifEntre, double cumulEnergie) throws JSONException {

  // ... chargement des tranches et autres opérations nécessaires
  JSONArray tranchesArray = chargerFichierJSON("tranches.json");
  
  double tdeCumulEnergie = 0.0;
  double restCumulEnergie = cumulEnergie;

  // Parcourir les tranches et calculer le montant cumulé d'énergie en fonction du code tarif
  for (int a = 0; a < tranchesArray.length(); a++) {
      JSONObject tranche = tranchesArray.getJSONObject(a);
      Integer codeTarifTranche = tranche.getInt("CodeTarif");
      Integer finTranche = tranche.getInt("Fin");
      Integer debutTranche = tranche.getInt("Debut");
      Double tdeTranche = tranche.getDouble("TdeTrch");

      if (codeTarifTranche.equals(codeTarifEntre)) {
            if (finTranche != 0 && restCumulEnergie >= finTranche) {
              tdeCumulEnergie = tdeTranche*finTranche;
            }
            else if(restCumulEnergie>=debutTranche) {
              tdeCumulEnergie += (restCumulEnergie+1-debutTranche) * tdeTranche;
              restCumulEnergie = 0.0;
            }
            else{break;}
      }
  }

  return tdeCumulEnergie;
}



private double calculerTsdaaeCumulEnergie(int codeTarifEntre, double cumulEnergie) throws JSONException {

  // ... chargement des tranches et autres opérations nécessaires
  JSONArray tranchesArray = chargerFichierJSON("tranches.json");
  
  double tsdaaeCumulEnergie = 0.0;
  double restCumulEnergie = cumulEnergie;

  // Parcourir les tranches et calculer le montant cumulé d'énergie en fonction du code tarif
  for (int a = 0; a < tranchesArray.length(); a++) {
      JSONObject tranche = tranchesArray.getJSONObject(a);
      Integer codeTarifTranche = tranche.getInt("CodeTarif");
      Integer finTranche = tranche.getInt("Fin");
      Integer debutTranche = tranche.getInt("Debut");
      Double tsdaaeTranche = tranche.getDouble("TsdaaeTrch");
      

      if (codeTarifTranche.equals(codeTarifEntre)) {
            if (finTranche != 0 && restCumulEnergie >= finTranche) {
              tsdaaeCumulEnergie = tsdaaeTranche*finTranche;
            }
            else if(restCumulEnergie>=debutTranche) {
              tsdaaeCumulEnergie = restCumulEnergie * tsdaaeTranche;
              restCumulEnergie = 0.0;
            }
            else{break;} 
      }
  }

  return tsdaaeCumulEnergie;
}




private double calculerTvaCumulEnergie(int codeTarifEntre, double cumulEnergie, double tvaTarif) throws JSONException {

  // ... chargement des tranches et autres opérations nécessaires
  JSONArray tranchesArray = chargerFichierJSON("tranches.json");
  
  double tvaCumulEnergie = 0.0;
  double restCumulEnergie = cumulEnergie;

  // Parcourir les tranches et calculer le montant cumulé d'énergie en fonction du code tarif
  for (int a = 0; a < tranchesArray.length(); a++) {
      JSONObject tranche = tranchesArray.getJSONObject(a);
      Integer codeTarifTranche = tranche.getInt("CodeTarif");
      Integer finTranche = tranche.getInt("Fin");
      Integer debutTranche = tranche.getInt("Debut");
      Double coutHTTranche = tranche.getDouble("CoutHT");
      Double tvaTranche = tranche.getDouble("TvaTrch");

      if (codeTarifTranche.equals(codeTarifEntre)) {
             if (finTranche != 0){
            if (restCumulEnergie >= finTranche) {
              tvaCumulEnergie += tvaTranche*(coutHTTranche+5)*(finTranche+1-debutTranche);
            }
            else if(restCumulEnergie>=debutTranche) {
              tvaCumulEnergie += tvaTranche*(restCumulEnergie+1-debutTranche)*(coutHTTranche+5);
              restCumulEnergie = 0.0;
            }
            else{break;}
          }
          else{
            if(restCumulEnergie>=debutTranche) {
              tvaCumulEnergie += tvaTranche*(restCumulEnergie+1-debutTranche)*(coutHTTranche+5);
              restCumulEnergie = 0.0;
            }
            else{break;}
          }
      }
  }

  if(tvaCumulEnergie>0){tvaCumulEnergie += 18*tvaTarif/100;}

  return tvaCumulEnergie;
}





private void calculerEnergieEtMonnaie(int codeTarifEntre, Double montantATF, Double montantCumulEnergie, Double tvaTarif,double tdeCumulEnergie, double tsdaaeCumulEnergie, double tvaCumulEnergie) throws JSONException {

  double cumulEnergie = Double.parseDouble(cumulEnergieField.getText());
  double energiePayee = 0.0;
  double monnaieRendue = 0.0;
  Double taxeTele = 0.0;
  Double tde = 0.0;
  Double tva = 0.0;
  
  double montantAchat = montantATF+montantCumulEnergie;
  

  // ... chargement des tranches et autres opérations nécessaires
  JSONArray tranchesArray = chargerFichierJSON("tranches.json");


  
  
  // Parcourir les tranches et effectuer les calculs
  for (int a = 0; a < tranchesArray.length(); a++) {
      JSONObject tranche = tranchesArray.getJSONObject(a);


      // ... récupération des valeurs de la tranche
      Integer codeTarifTranche = tranche.getInt("CodeTarif");
      Integer debutTranche = tranche.getInt("Debut");
      Integer finTranche = tranche.getInt("Fin");
      Double tsdaaeTranche = tranche.getDouble("TsdaaeTrch");
      Double tdeTranche = tranche.getDouble("TdeTrch");
      Double coutTTCtranche = tranche.getDouble("CoutTTC");
      Double tvaTranche = tranche.getDouble("TvaTrch");
      Double coutHTTranche = tranche.getDouble("CoutHT");
      Double coutMaxTranche = coutTTCtranche*(finTranche+1-debutTranche);
      

      
      if (codeTarifTranche.equals(codeTarifEntre)) {

        if (finTranche != 0){

            if (montantAchat-tvaTarif*tvaTranche > coutMaxTranche) {
                    energiePayee += finTranche;
                    tde = tdeTranche*energiePayee;
                    taxeTele =  tsdaaeTranche*energiePayee;
                    tva = tvaTranche * (tvaTarif + (coutHTTranche+5)*(finTranche+1-debutTranche));
                    montantAchat -=  ((coutHTTranche+5)*(energiePayee + tvaTranche) + tvaTranche*tvaTarif);

                }
                else if(montantAchat-tvaTarif*tvaTranche >= 0) {
                    energiePayee += (montantAchat-tvaTarif*tvaTranche) / coutTTCtranche;
                    tva = tvaTranche * (tvaTarif + (coutHTTranche+5)*(finTranche-debutTranche));
                    montantAchat = 0.00;

                    break; // Sortir de la boucle à la dernière tranche
                }
                else {
                    monnaieRendue = montantAchat;
                    montantAchat = 0.0;
                    break; // Sortir de la boucle à la dernière tranche
                }
            
            }
            else{
                energiePayee += montantAchat / coutTTCtranche;
                montantAchat = 0.00;

                break; // Sortir de la boucle à la dernière tranche

            }

      
   }
      
  }

  // Affichage des résultats ou autres actions souhaitées avec energiePayee et monnaieRendue
  energiePayee -= cumulEnergie;
  System.out.println("\n Energie payee: " + (energiePayee-cumulEnergie)  + "\n");
  System.out.println("\n tde :" + (tde-tdeCumulEnergie) +"\n");
  System.out.println("\n Tsdaae :" + (taxeTele-tsdaaeCumulEnergie) +"\n");
  System.out.println("\n TVA :" + (tva-tvaCumulEnergie) +"\n");
  System.out.println("\n Monnaie rendue :" + monnaieRendue +"\n");
}




    private void effectuerCalculs() throws JSONException {

        // Récupérer les valeurs saisies
        double montantEntre = Double.parseDouble(montantField.getText());
        int codeTarifEntre = Integer.parseInt(codeTarifField.getText());
        int numeroCompteur = Integer.parseInt(compteurField.getText());
        double cumulEnergie = Double.parseDouble(cumulEnergieField.getText());
        String dateAchatStr = dateAchatField.getText();

        JSONArray tarifsArray = chargerFichierJSON("tarifs.json");
        for(int i = 0; i < tarifsArray.length(); i++){
            JSONObject tarif = tarifsArray.getJSONObject(i);
            Integer codeTarif = tarif.getInt("code_tarif");
            Integer pf = tarif.getInt("prime_fixe");
            Integer rd = tarif.getInt("redevance");
            double tvaTarif = pf+rd;
            if (codeTarif.equals(codeTarifEntre)) {

                int periodeAccTF = calculerPeriodeAccTF(dateAchatStr);
                int taxesFixesAccumules = calculerTaxesFixesAccumulees(codeTarifEntre, periodeAccTF);
                int tqr = calculTQR(montantEntre);
                double montantATF = montantEntre - tqr - taxesFixesAccumules;
                double montantCumulEnergie = calculerMontantCumulEnergie(codeTarifEntre, cumulEnergie, tvaTarif);
                double tdeCumulEnergie = calculerTdeCumulEnergie(codeTarifEntre, montantCumulEnergie);
                double tsdaaeCumulEnergie = calculerTsdaaeCumulEnergie(codeTarifEntre, montantCumulEnergie);
                double tvaCumulEnergie = calculerTvaCumulEnergie(codeTarifEntre, montantCumulEnergie, tvaTarif);
                System.out.println("\n Numero du Compteur: " + numeroCompteur + "\n");
                System.out.println("\n Montant Payé: " + montantEntre + "\n");
                String codeEnergie = genererCode();
                if (montantATF > 0){
                    calculerEnergieEtMonnaie( codeTarifEntre, montantATF, montantCumulEnergie, tvaTarif, tdeCumulEnergie, tsdaaeCumulEnergie, tvaCumulEnergie);
                    System.out.println("\n Code à saisir sur le compteur: " + codeEnergie + "\n");
                } else{
                    System.out.println("\nMontant insuffisant, vous devez d'abord payer " + taxesFixesAccumules + "\n");
                }
                
                break;
      }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(FinFin::new);
    }
}
