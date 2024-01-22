# Initialisation des variables
montantAchat = 4500
energiePayee = 0
monnaieRendue = 0

# Définition des tranches
tranches = [
    {"Debut": 0, "Fin": 50, "CoutHT": 96, "TdeTach": 2, "TsdaaeTrch": 1, "TvaTrch": 0, "CoutTTC": 99, "CodeTarif": 17032},
    {"Debut": 51, "Fin": 150, "CoutHT": 108, "TdeTach": 2, "TsdaaeTrch": 3, "TvaTrch": 0, "CoutTTC": 113, "CodeTarif": 17032},
    {"Debut": 151, "Fin": 200, "CoutHT": 108, "TdeTach": 2, "TsdaaeTrch": 3, "TvaTrch": 2150.82, "CoutTTC": 2263.82, "CodeTarif": 17032},
    {"Debut": 201, "Fin": None, "CoutHT": 114, "TdeTach": 2, "TsdaaeTrch": 3, "TvaTrch": 2151.9, "CoutTTC": 2264.9, "CodeTarif": 17032}
]

# Parcours des tranches
for tranche in tranches:
    coutMaxTranche = tranche['CoutTTC'] * tranche['Fin']
    
    if montantAchat > coutMaxTranche:
        energiePayee += tranche['Fin']
        montantAchat -= coutMaxTranche
    elif montantAchat > tranche['TvaTrch']:
        energiePayee += montantAchat / tranche['CoutTTC']
        montantAchat -= montantAchat
        break  # Sortir de la boucle à la dernière tranche
    else:
        monnaieRendue = montantAchat - coutMaxTranche
        break  # Sortir de la boucle à la dernière tranche

# Affichage des résultats
print("Énergie payée :", energiePayee)
print("Montant d'achat restant :", montantAchat)
print("Monnaie rendue :", monnaieRendue)
