# eAS
Administrer un système d'exploitation via courriel

eAS est un outil qui permet à l’administrateur du système d’exploitation, au cas où l’accès physique et à distance au serveur n’est pas possible, d’effectuer les tâches d’administration sans qu’il n’y ait aucun impact sur le fonctionnement des applications hébergées sur le serveur.
eAS utilise le service courriel pour transférer les commandes à exécuter vers le serveur. Le fichier flux_des_taches.JPG détaille le fonctionnement du l'outil.

Les prérequis sont:
- Installer Java SE Runtime Environment (version 8 ou superieure) sur le serveur à administrer
- Créer un compte courriel dédié à eAS
- Permettre au serveur de communiquer avec un serveur courriel (e.g. smtp.gmail.com, imap.gmail.com)
 
Installation
- Créer un repertoire de travail sur le serveur dédié exclusivement à eAS (e.g. /eAS_rep)
- Télécharger le repertoire com.joseph-mansour.eAS/dist sur /eAS_rep
- Modifier les fichiers JSON serveurcourriel.json, commandespermises.json et envoyeursagrees.json en fonction de votre environnement
   - serveurcourriel.json contient les paramètres du serveur courriel 
   - commandespermises.json contient les commandes permises à exécuter
   - envoyeursagrees.json contient l'addresses courriel des administrateurs agrées et de leurs modérateurs (s'il y en a)
  
Exécution:
 - java -jar com.joseph-mansour.eAS.jar param1 param2
    param1: le reportoire de travail (e.g. /eAS_rep)
    param2: le dossier du serveur courriel (par défaut Inbox)
 - Pour ordonnancer l'exécution vous pouvez utiliser crontab/Unix ou taskschd.msc/Windows
 - Tous les détails/erreurs des exécutuons sont enregistrés dans le fichiers eAS_execution.log et eAS_erreur.log
