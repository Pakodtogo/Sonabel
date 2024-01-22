from datetime import datetime
import random
T1 = 96
T2 = 108
T3 = 114
T4 = 60
T5 = 96
Ttva = 18/100
Seuil = 0
TQR = 0
#serie = ''.join([str(random.randint(0, 9)) for _ in range(20)])
groups_of_digits = [str(random.randint(0, 9999)).zfill(4) for _ in range(5)]
combinaison = ' '.join(groups_of_digits)


def est_premier_achat_du_mois(date_dernier_achat):
    date_actuelle = datetime.now()
    return date_dernier_achat.month != date_actuelle.month or date_dernier_achat.year != date_actuelle.year

def calcul_TQR(M):
    if M < 100:
        TQR = 0
    elif 100 <= M <= 1000:
        TQR = 20
    elif 1000 < M <= 5000:
        TQR = 30
    elif 5000 < M <= 10000:
        TQR = 50
    elif 10000 < M <= 50000:
        TQR = 100
    else:
        n = M // 50000
        TQR = 50 * (n + 2)
    return TQR


def client_pam(M, PF, Rd, dd):
    if (M) / (T1 + 3) <= 50:
        E = (M) / (T1 + 3)
        CE = T1 * E
        Tde = 2 * E
        Tsdaae = E
        TVA = 0
    
    
    elif 50 < (M + (50 * T2) - (50 * T1)) / (T2 + 5) <= 150:
        E = (M + (50 * T2) - (50 * T1)) / (T2 + 5)
        CE = (T1 * 50) + (E - 50) * T2
        Tde = 2 * E
        Tsdaae = 3 * E
        TVA = 0
    
    elif 150 < (M + (Ttva * (5 + T2) * 150) + (50 * T2) - (50 * T1) - (Ttva * ((PF + Rd) / dd))) / ((T2 + 5) + Ttva * (5 + T2)) <= 200:
        E = (M + (Ttva * (5 + T2) * 150) + (50 * T2) - (50 * T1) - (Ttva * ((PF + Rd) / dd))) / ((T2 + 5) + Ttva * (5 + T2))
        CE = (T1 * 50) + (E - 50) * T2
        Tde = 2 * E
        Tsdaae = 3 * E
        TVA = Ttva * ((PF + Rd) / dd + (E - 150) * T2 + (E - 150) * 5)
        M1 = ((T2 + 5) + Ttva*(T2 + 5))*E + PF + Rd + 50*T1 - 50*T2 + Ttva*((PF+Rd)/dd - 150*T2 -750)
        Ms = M - M1  
        print("\nMonnaie = ", Ms, " FCFA")
           
    else:
        E = (M + (Ttva * (200 * T3 + 750)) + (200 * T3) - (150 * T2) - (50 * T1) - Ttva * ((PF + Rd) / dd + (50 * T2))) / ((T3 + 5) + Ttva * (5 + T3))
        CE = (T1 * 50) + (150 * T2) + (E - 200) * T3
        Tde = 2 * E
        Tsdaae = 3 * E
        TVA = Ttva * ((PF + Rd) / dd + (E - 200) * T3 + (50 * T2) + (E - 150) * 5)
        M1 = ((T3 + 5) + Ttva * (5 + T3))*E
        Ms = M - M1
        print("\nMonnaie = ", Ms - TQR, " FCFA")
    
    return CE, E, Tde, Tsdaae, TVA



def agent_pam(M, TQR, PF, Rd, Seuil):
    if (M) / 3 <= 50:
        E = (M) / 3
        Tde = 2 * E
        Tsdaae = E
        TVA = 0
        CE = 0
    
    elif (M) / 3 > 50    and    (M) / 5 <= 50:
        Ep =50
        Tdep = 2 * Ep
        Tsdaaep = Ep
        TVAp = 0
        CEp = 0
        M1 = 3*Ep
        TQR = calcul_TQR(M1)
        Ms = M - M1
        TQRs = calcul_TQR(Ms)
        #if (Ms) <= 2*(Ep): print("Monnaie =", Ms, "FCFA")    #dans tous les cas Ms <= 2*(Ep)
        # SA = (2*Ep - MS)+1
        # print("Vous ajouter", SA)
        #MA = float(input("Entrer la somme ajoutee: "))
        # if MA > = SA : M = M+MA et TQR = calculTQR(M)
        '''Es = (Ms-TQRs-2*Ep)/5
        Tdes = 2*Es
        Tsdaaes = 3*Es + 2*Ep
        TVAs = 0
        CEs = 0'''
        # if MA = 0 : et il ya monnaie print("Monnaie =", Ms, "FCFA")
        E = Ep + Es
        TVA =TVAp + TVAs
        Tsdaae = Tsdaaep + Tsdaaes
        Tde = Tdep + Tdes
        CE = CEp + CEs
        

    elif 50 < (M) / 5 <= 150:
        E = (M) / 5
        Tde = 2 * E
        Tsdaae = 3 * E
        TVA = 0
        CE = 0
    
    elif (M) / 5 > 150 and (M + Ttva*750) / (5 + (Ttva * 5)) <= 150:
        Ep =150
        Tdep = 2 * Ep
        Tsdaaep = 3*Ep
        TVAp = 0
        CEp = 0
        M1 = 5*Ep  #+ calcul_TQR(5*Ep)
        #TQR = calcul_TQR(M1)
        M2 = 5*Ep
        Ms = M - M2
        #TQRs = calcul_TQR(Ms)
        # Taxe = Ttva*(PF+Rd) + calcul_TQR(Ttva*(PF+Rd)+1
        #if (Ms - Ttva*(PF + Rd)) <= 0: print("Votre montant vous soumet a la TVA Mais ne suffit pas pour payer la TVA. Si vous reprenez ", Ms , " Fcfa, vous serez exempté de la TVA et vous aurez ", Es, " KWh. Mais pour votre prochain achat dans ce mois, vous devrez d'abord payer ", Taxe , " Fcfa!")
        Es = (Ms) / (5 + (Ttva * 5))
        Tdes = 2*Es
        Tsdaaes = 3*Es
        TVAs = Ttva * Es * 5
        CEs = 0
        E = Ep + Es
        TVA =TVAp + TVAs
        Tsdaae = Tsdaaep + Tsdaaes
        Tde = Tdep + Tdes
        CE = CEp + CEs

    elif 150 < (M + Ttva*750) / (5 + (Ttva * 5)) <= Seuil:
        E = (M + Ttva*750) / (5 + (Ttva * 5))
        Tde = 2 * E
        Tsdaae = 3 * E
        TVA = Ttva * (E - 150) * 5
        CE = 0
    
    elif (M + Ttva *750) / (5 + (Ttva * 5)) > Seuil and (M + Ttva * (Seuil*T4 + 750) + (Seuil * T4)) / ((T4 + 5) + Ttva * (T4 + 5)) <= Seuil:
        Ep =Seuil
        Tdep = 2 * Ep
        Tsdaaep = 3*Ep
        TVAp = Ttva * (Ep - 150) * 5
        CEp = 0
        M1 = Ep*(5 + Ttva*5) - Ttva*750
        #TQR = calcul_TQR(M1)
        M2 = Ep*(5 + Ttva*5) - Ttva*750
        Ms = M - M2
        #TQRs = calcul_TQR(Ms)
        Es = (Ms) / ((T4+5)+Ttva*(T4+5))
        Tdes = 2*Es
        Tsdaaes = 3*Es
        TVAs = Ttva * Es * (T4 + 5)
        CEs = Es * T4
        E = Ep + Es
        TVA =TVAp + TVAs
        Tsdaae = Tsdaaep + Tsdaaes
        Tde = Tdep + Tdes
        CE = CEp + CEs

    elif Seuil < (M + Ttva * (Seuil*T4 + 750) + (Seuil * T4)) / ((T4 + 5) + Ttva * (T4 + 5)) <= Seuil + 150:
        E = (M + Ttva * (Seuil*T4 + 750) + (Seuil * T4)) / ((T4 + 5) + Ttva * (T4 + 5))
        Tde = 2 * E
        Tsdaae = 3 * E
        TVA = Ttva * (E * (T4 + 5) - (Seuil*T4 + 750))
        CE = (E * T4) - (Seuil * T4)
    
    elif (M + Ttva * (Seuil*T4 + 750) + (Seuil * T4)) / ((T4 + 5) + Ttva * (T4 + 5)) > (Seuil + 150)   and   (M - Ttva * ((150 * T4) - ((Seuil + 150) * T5) - 750) - (150 * T4) + ((Seuil + 150) * T5)) / ((T5 + 5) + Ttva * (T5 + 5)) <= (Seuil+ 150):
        Ep =(Seuil+150)
        Tdep = 2 * Ep
        Tsdaaep = 3*Ep
        TVAp =  Ttva * (Ep * (T4 + 5) - (Seuil*T4 - 750))
        CEp = (Ep * T4) - (Seuil * T4)
        M1 = Ep*((T4+5) + Ttva*(T4+5)) - Ttva*(Seuil*T4 + 750) +  Seuil*T4
        #TQR = calcul_TQR(M1)
        M2 = Ep*((T4+5)+ Ttva*(T4+5)) - Ttva*(Seuil*T4 + 750) +  Seuil*T4
        Ms = M - M2
        #TQRs = calcul_TQR(Ms)
        Es = (Ms) / ((T5+5)+Ttva*(T5+5))
        Tdes = 2*Es
        Tsdaaes = 3*Es
        TVAs = Ttva * Es * (T5 + 5)
        CEs = Es * T5
        E = Ep + Es
        TVA =TVAp + TVAs
        Tsdaae = Tsdaaep + Tsdaaes
        Tde = Tdep + Tdes
        CE = CEp + CEs
        

    else:
        E = (M - Ttva * ((150 * T4) - ((Seuil + 150) * T5) - 750) - (150 * T4) + ((Seuil + 150) * T5)) / ((T5 + 5) + Ttva * (T5 + 5))
        Tde = 2 * E
        Tsdaae = 3 * E
        TVA = Ttva * (E * (T5 + 5) + ((150 * T4) - ((Seuil + 150) * T5) - 750))
        CE = (E * T5) - ((Seuil + 150) * T5) + (150 * T4)
    return CE, E, Tde, Tsdaae, TVA


def main():
    M = int(input("\nEntrez un Montant : "))

    TQR = calcul_TQR(M)

    CT = int(input("\nEntrez le code tarif du compteur : "))
    
    if CT != 17032 and CT != 17042 and CT != 17052 and CT != 17062 and CT != 17072 and CT != 17030 and CT != 17040 and CT != 17050 and CT != 17060 and CT != 17070:
        print("\nLa valeur que vous avez saisie ne correspond\na aucun code tarif de compteur triphase!\nVous n'etes ni client ni agent de la SONABEL")
        
    else:
        if CT == 17030 or CT == 17040 or CT == 17050 or CT == 17060 or CT == 17070:
            print("\nVous etes un agent de la SONABEL\n")
            Cat = int(input("\nQuelle est votre categorie (1 à 9) ? "))
            if Cat < 1 or  Cat > 9:
                print("\nErreur! Veuiller entrer une valeur entre 1 et 9 \n")
            elif Cat <= 5:
                Seuil = 600
            else:
                Seuil =650
        else: print ("\nVous etes un client!")


        date_dernier_achat_str = input("\nEntrez la date du dernier achat (JJ/MM/AAAA) : ")
        date_dernier_achat = datetime.strptime(date_dernier_achat_str, "%d/%m/%Y")

        if est_premier_achat_du_mois(date_dernier_achat):
            date_actuelle = datetime.now()
            y = date_actuelle.year - date_dernier_achat.year
            m = date_actuelle.month - date_dernier_achat.month
            dd = (12 * y) + m


            if CT == 17032:
                PF = 10610 * dd
                Rd = 1226 * dd
                M = M - TQR - PF - Rd
                CE, E, Tde, Tsdaae, TVA = client_pam(M, PF, Rd, dd)
            
            elif CT == 17042:
                PF = 15918 * dd
                Rd = 1226 * dd
                M = M - TQR - PF - Rd
                CE, E, Tde, Tsdaae, TVA = client_pam(M, PF, Rd, dd) 
         
            elif CT == 17052:
                PF = 21224 * dd
                Rd = 1373 * dd
                M = M - TQR - PF - Rd
                CE, E, Tde, Tsdaae, TVA = client_pam(M, PF, Rd, dd)  
         
            elif CT == 17062:
                PF = 26531 * dd
                Rd = 1373 * dd
                M = M - TQR - PF - Rd
                CE, E, Tde, Tsdaae, TVA = client_pam(M, PF, Rd, dd)  
                
            elif CT == 17072:
                PF = 31837 * dd
                Rd = 1373  * dd
                M = M - TQR - PF - Rd
                CE, E, Tde, Tsdaae, TVA = client_pam(M, PF, Rd, dd)




            elif CT == 17030:
                PF = 10613
                Rd = 1226
                M = M - TQR - PF - Rd
                CE, E, Tde, Tsdaae, TVA = agent_pam(M, TQR, PF, Rd, Seuil)
                PF = 0
                Rd = 0    
            
            elif CT == 17040:
                PF = 15918
                Rd = 1226
                M = M - TQR - PF - Rd
                CE, E, Tde, Tsdaae, TVA = agent_pam(M, TQR, PF, Rd, Seuil)
                PF = 0
                Rd = 0
            
            elif CT == 17050:
                PF = 21224
                Rd = 1373
                M = M - TQR - PF - Rd
                CE, E, Tde, Tsdaae, TVA = agent_pam(M, TQR, PF, Rd, Seuil)
                PF = 0
                Rd = 0
            
            elif CT == 17060:
                PF = 26531
                Rd = 1373
                M = M - TQR - PF - Rd
                CE, E, Tde, Tsdaae, TVA = agent_pam(M, TQR, PF, Rd, Seuil)
                PF = 0
                Rd = 0

            elif CT == 17070:
                PF = 31837
                Rd = 1373
                M = M - TQR - PF - Rd
                CE, E, Tde, Tsdaae, TVA = agent_pam(M, TQR, PF, Rd, Seuil)
                PF = 0
                Rd = 0
            
            

        else:

            Ec = float(input("\nEntrez Le cumul d'énergie payee anterieurement dans le mois: "))
        

        if M < 0:
            print("\nVous ne pouvez pas acheter des unites!\nVous avez un credit de" ,  (PF + Rd) , "FCFA \navant d'avoir droit à des unites\n")
        else:
            print("\nRedevance : ", Rd, "\n")
            print("Prime Fixe : ", PF, "\n")
            print("Tsdaae : ", Tsdaae, "\n")
            print("Tde : ", Tde, "\n")
            print("TVA : ", TVA, "\n")
            print("TQR : ", TQR, "\n")
            print("Cout Energie : ", CE, "\n")
            print("Energie payée : ", E, "\n")
            print ("Part Client: ", M, "\n")
            print("Code à saisir sur vore compteur:", combinaison, "\n")

if __name__ == "__main__":
    main()
