# JMCoin
[![Build Status](https://travis-ci.com/NicodemeB/JMCoin.svg?token=CMaSjYW1VJT7FyWUsiJw&branch=dev)](https://travis-ci.com/NicodemeB/JMCoin)


## Structure :
Le fichier fourni contient différents dossiers :

-	Crypto: comprend les classes implémentant la cryptographique.
-	Database : méthodes de façades pour lecture, update et modification de la base de données.
-	Model : contient les objets métiers de l’application (Block, Bundle, Chain, Input, KeyGenerator, Output, Transaction et Wallet).
-	Network : contient les classes nécessaires à la mise en place des protocoles réseaux, les fichiers se terminant par Node contiennent la machine elle-même, voir même l’utilisateur qui utilise la machine.

## Fonctionnement :
Il faut tout d’abord créer des clés liées au Wallet, pour se faire exécutez la classe createKey.java en passant en argument un mot de passe (String) pour la création des clés publiques et privées.

Pour effectuer une transaction : exécutez la classe createTransaction.java avec comme arguments le mot de passe du Wallet d’où provient l’argent, l’adresse du destinataire et la quantité à transférer. Lors de la première transaction la classe UserNode va se charger de créer le Wallet associé aux clés créées dans la première étape. Le mot de passe du Wallet permet de déverrouiller toutes les clés associées à celui-ci.

Pour obtenir la liste de la chaine de blocs, lancer la classe getBlockchain.java avec en paramètre le mot de passe du Wallet.
Pour miner : lancez la classe createMiner.java avec en paramètres le mot de passe lié au Wallet de ce mineur et l’hôte du serveur distant : relay-02.jmcoin.technology ou relay-01.jmcoin.technology (nécessite une connexion internet) en fonction du relais désiré. 

Le Wallet du mineur lui permet également de toucher sa récompense, il est considéré comme un utilisateur un peu particulier. 
