# JMCoin
[![Build Status](https://travis-ci.org/NicodemeB/JMCoin.svg?branch=dev)](https://travis-ci.org/NicodemeB/JMCoin)
 


## Architecture 
Le **Master Node** ainsi que 2 **Relay Nodes** sont déployés sur 3 machines Debian9 hébergées par Azure (Cloud Microsoft) différentes afin d'être joignables par n'importe quel **Miner/Wallet**, et cela n'importe où (voir avertissement). Ces 3 machines sont joignables sur les domaines suivants :

-	master.jmcoin.technology
-	relay-01.jmcoin.technology
- 	relay-02.jmcoin.technology

Si nécessaire, contacter <benjamin.nicodeme@ulb.ac.be> pour avoir un accès SSH à ces machines (CLI only). 

Il est possible de consulter les logs de ces machines en temps réel sur les adresses suivantes : 

-	<http://master.jmcoin.technology/jm.log>
-	<http://relay-01.jmcoin.technology/jm.log>
-	<http://relay-02.jmcoin.technology/jm.log>

Ces logs sont consultables sans la moindre authentification/autorisation.

Tous les tests sont faisables en local mais nécessitent cependant la création d'un base de données MySQL, les tests sont alors plus faciles à réaliser sur les serveurs distants car la base de données est déjà présente sur le Master Node distant. 

## Prérequis : maven

### Ubuntu/Debian 
````
sudo apt install maven
````

### Mac OS 
````
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
brew install maven 
````


## Lancement

#### ⚠️ **Avertissement** ⚠️ 
Les ports **TCP 33333 et 33334** sont utilisés par le JMCoin, ce qui signifie en d'autres termes que le réseau **"eduroam"**, **"Plaine-WiFi"** et ainsi de suite ne permettent pas de réaliser les tests (sauf VPN, proxy, etc permettant de bypass le firewall) car il sera impossible de contacter les serveurs distants sur ces ports du au firewall de l'école.

#### Optionnel : Git

````
git clone https://github.com/NicodemeB/JMCoin.git --branch=dev
cd JMCoin
````

### Compilation 

````
mvn package
````

### Step 0 : create keys

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.CreateMiner" -Dexec.args="wallet_password"
````
Exemple : 

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.CreateMiner" -Dexec.args="MySimplePassword"
````


### Master Node

#### Tests avec le Master Node distant 
Rien n'est à faire, le Master Node tourne en permanence automatiquement. 

#### Tests avec le Master Node local
Dans le dossier du projet, il faut faire :

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runnme.RunMaster" -Dexec.cleanupDaemonThreads=false
````


### Relay Node
#### Tests avec les Relay Nodes distants
Rien n'est à faire, les Relay Node tournent en permanence automatiquement. 

#### Tests avec le Relay Nodes local
#### ⚠️ Attention ⚠️ 
Il n'est possible que de lancer **un seul Relay Node** sur la même machine !! (car ils écoutent sur le même port)

Dans le dossier du projet, il faut faire :

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runnme.RunRelay" -Dexec.cleanupDaemonThreads=false
````


### Miner 
Dans le dossier du projet, il faut faire :

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.CreateMiner" -Dexec.args="wallet_password hostname"
````

Connexion sur le Relay Node 01 : 

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.CreateMiner" -Dexec.args="MySimplePassword relay-01.jmcoin.technology"
````

Connexion sur le Relay Node 02 :

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.CreateMiner" -Dexec.args="MySimplePassword relay-02.jmcoin.technology"
````

### Wallet 

#### Créer une transaction 

Dans le dossier du projet, il faut faire :

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.CreateMiner" -Dexec.args="wallet_password sestination_address amout hostname"
````

Connexion sur le Relay Node 01 : 

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.CreateTransaction" -Dexec.args="MySimplePassword ea2f34e3ce079b942abd420b94d1ab7a0c05e67a 0.01 relay-01.jmcoin.technology"
````

Connexion sur le Relay Node 02 :

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.CreateTransaction" -Dexec.args="MySimplePassword ea2f34e3ce079b942abd420b94d1ab7a0c05e67a 0.01 relay-02.jmcoin.technology"
````

#### Récupérer la blockchain

Dans le dossier du projet, il faut faire :

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.GetBlockchain" -Dexec.args="wallet_password"
````

connexion sur le Relay Node 01 : 

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.GetBlockchain" -Dexec.args="MySimplePassword"
````

connexion sur le Relay Node 02 :

````
mvn exec:java -Dexec.mainClass="com.jmcoin.runme.GetBlockchain" -Dexec.args="MySimplePassword"
````


## Facultatif : installation et configuration de la base de données locale

### Step 0 : install mysql-server

#### Ubuntu/Debian
````
sudo apt install mysql-server
````

#### Mac OS 
````
brew install mysql
````


Le MasterNode stocke la chaine (et tout son contenu) dans une base de données MySQL. Il est donc nécessaire d'en créer une afin de pouvoir lancer le MasterNode
Il faut donc d'abord installer un serveur MySQL
    
Une fois le serveur MySQL créé, il suffit de créer un utilisateur et une base de données

````
CREATE USER 'usename' IDENTIFIED BY 'password';
CREATE DATABASE jmcoin;
````
    
Il suffit ensuite de configurer l'application dans `META-INF/persistance.xml`

````
<properties>
    <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
    <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost/jmcoin"/>
    <property name="javax.persistence.jdbc.user" value="user"/>
    <property name="javax.persistence.jdbc.password" value="password"/>
    <property name="javax.persistence.schema-generation.database.action" value="create"/>
    <property name="eclipselink.logging.level" value="FINE"/>
</properties>
````

où

- `localhost` est le nom de l'hôte sur lequel tourne la base de données
- `value="user"` user est l'utilisateur de la base de données
- `value="password"` password est le mot de passe de l'utilisateur de la base de données
        




