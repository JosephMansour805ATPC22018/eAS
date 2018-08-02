# eAS - Administrer un système d'exploitation via courriel

eAS est un outil qui permet à l’administrateur de système d’exploitation, au cas où l’accès physique et à distance au serveur n’est pas possible, d’effectuer les tâches d’administration sans qu’il n’y ait aucun impact sur le fonctionnement des applications hébergées sur le serveur.
eAS utilise le service courriel pour transférer les commandes à exécuter vers le serveur. Le fichier flux_des_taches.JPG détaille le fonctionnement du l'outil.

Les prérequis sont:
- Installer Java SE Runtime Environment (version 8 ou superieure) sur le serveur à administrer
- Créer un compte courriel dédié à eAS
- Permettre au serveur de communiquer avec un serveur courriel (e.g. smtp.gmail.com, imap.gmail.com)
 
Installation
- Créer un repertoire de travail sur le serveur dédié exclusivement à eAS (e.g. /eAS_rep)
- Télécharger les fichiers suivants sur /eAS_rep
  - https://github.com/JosephMansour805ATPC22018/eAS/raw/master/com.joseph-mansour.eAS/dist/com.joseph-mansour.eAS.jar 
  - https://raw.githubusercontent.com/JosephMansour805ATPC22018/eAS/master/com.joseph-mansour.eAS/dist/commandespermises.json
  - https://raw.githubusercontent.com/JosephMansour805ATPC22018/eAS/master/com.joseph-mansour.eAS/dist/envoyeursagrees.json
  - https://raw.githubusercontent.com/JosephMansour805ATPC22018/eAS/master/com.joseph-mansour.eAS/dist/registre.json
  - https://raw.githubusercontent.com/JosephMansour805ATPC22018/eAS/master/com.joseph-mansour.eAS/dist/serveurcourriel.json

- Télécharger les fichiers suivants sur /eAS_rep/lib
  - https://github.com/JosephMansour805ATPC22018/eAS/raw/master/com.joseph-mansour.eAS/dist/lib/gson-2.8.0.jar
  - https://github.com/JosephMansour805ATPC22018/eAS/raw/master/com.joseph-mansour.eAS/dist/lib/javax.mail-1.6.0.jar
  
- Modifier les fichiers serveurcourriel.json, commandespermises.json, envoyeursagrees.json et registre.json en fonction de votre environnement
   - serveurcourriel.json contient les paramètres du serveur courriel 
   - commandespermises.json contient les commandes permises à exécuter
   - envoyeursagrees.json contient l'adresse courriel des administrateurs agrées et de leurs modérateurs (s'il y en a)
   - registre.json contient une liste de clef-valeur 
  
Exigences du courriel à envoyer:
- Envoyé par un envoyeur agrée
- Le format du contenu doit être texte brut
- Le contenu est un et un seul mot (i.e. le code de la commande à exécuter)

Exécution:
 - java -jar com.joseph-mansour.eAS.jar /eAS_rep 
 - Pour ordonnancer l'exécution vous pouvez utiliser crontab/Unix ou taskschd.msc/Windows
 - Tous les détails/erreurs des exécutuons sont enregistrés dans les fichiers eAS_execution.log et eAS_erreur.log
