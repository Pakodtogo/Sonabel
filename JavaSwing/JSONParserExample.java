import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;



public class JSONParserExample {
    public static void main(String[] args) {
        // Code tarif entré par l'utilisateur
        Integer codeTarifEntre = 17032; // Remplacez par le code tarif saisi
        Double montantEntre = 5000.0;

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
                    Integer pF = tarif.getInt("prime_fixe"); 
                    Integer rd = tarif.getInt("redevance");
                    System.out.println("code tarif : " + codeTarif + "\n");
                    System.out.println("Prime Fixe du tarif : " + pF + "\n");
                    System.out.println("Redevance du tarif: " + rd + "\n");

                    // Charger la liste des tranches associées à ce tarif
                    String tranchesContent = new String(Files.readAllBytes(Paths.get("tranches.json")));
                    JSONArray tranchesArray = new JSONArray(tranchesContent);
                    

                    Double montantAchat = montantEntre;
                    // Parcourir les tranches
                    for (int a = 0; a < tranchesArray.length(); a++) {
                      JSONObject tranche = tranchesArray.getJSONObject(a);
                      Integer codeTarifTranche = tranche.getInt("CodeTarif");
                      Integer finTranche = tranche.getInt("Fin");
                      Double coutTTCtranche = tranche.getDouble("CoutTTC");
                      Double tvaTranche = tranche.getDouble("TvaTrch");
                      Double coutMaxTranche = coutTTCtranche * finTranche;

                      if (codeTarifTranche.equals(codeTarif)) {
                          
                         
 
                           if (coutMaxTranche != 0){
                            if ( montantAchat > coutMaxTranche) {
                              energiePayee += finTranche;
                              montantAchat -= coutMaxTranche;

                              
                          } else if(montantAchat > tvaTranche) {
                            energiePayee += montantAchat / coutTTCtranche;
                            montantAchat = 0.00;
       
                              break; // Sortir de la boucle à la dernière tranche
                          } else {
                              monnaieRendue = montantAchat;
                              montantAchat = 0.0;
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
                              montantAchat = 0.0;
                              break; // Sortir de la boucle à la dernière tranche
                          }
                           }
 
                      }
                  }
                  // Afficher les valeurs
                    String code = RandomNumberGroups.genererCode();
                    System.out.println("Energie Payée : " + energiePayee +  "\n");
                    System.out.println("Monnaie Rendue : " + monnaieRendue + "\n");
                    System.out.println("Code : " + code + "\n");
                  break; // Arrêter la recherche une fois le tarif trouvé

                }

                
            }


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
