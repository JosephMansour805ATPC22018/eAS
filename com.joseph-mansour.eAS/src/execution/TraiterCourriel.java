/*
 * Copyright (C) 2018 Administrator
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package execution;

import com.google.gson.Gson;
import entites.Courriel;
import static implementation.ConsulterCourriels.dirTravail;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import static parametres.InitialiserParams.serveurcourriel;
import parametres.ServeurCourriel;
import parametres.BoiteNoire;
import static parametres.InitialiserParams.shell;

/**
 *
 * @author Administrator
 */
public class TraiterCourriel {

    private final Message msg;
    private final HashMap<String, String> cpsMap;

    public TraiterCourriel(Message msg, HashMap<String, String> cpsMap) throws MessagingException, IOException {
        this.msg = msg;
        this.cpsMap = cpsMap;
        Traiter();

    }

    

    private void Traiter() throws MessagingException, IOException {

        //Construire l'objet courriel à partir du courriel reçu
        String adresseEnvoyeur = Arrays.toString(msg.getFrom());
        int nb = nbFichiersJsonEnvoyeur(adresseEnvoyeur);
        String sujet = msg.getSubject();
        String id = adresseEnvoyeur + "-" + sujet + "-" + nb;
        String adresseDestinataire = Arrays.toString(msg.getAllRecipients());

        Date dateEnvoyer = msg.getSentDate();
        String contenu = msg.getContent().toString();
        Courriel courriel = new Courriel.CourrielBuilder(id, adresseEnvoyeur, adresseDestinataire, sujet, dateEnvoyer, contenu).build();

        BoiteNoire.enregistrer("Un nouveau courriel est reçu: ID:" + id, "info");
        //Executer la commande inclue dans le contenu du courriel
        String resultat = ExecuterCommande(contenu);
        BoiteNoire.enregistrer("Courriel " + id + " est en train de traitement", "info");

        //Changer le statut du courriel à Exécuté
        courriel.setStatut("Executé");
        courriel.setRemarque(resultat);
        BoiteNoire.enregistrer("Courriel " + id + " est exécuté", "info");

        //Sauvegarder le courriel dans un fichier Json
        Gson gson = new Gson();
        String json = gson.toJson(courriel);
        try (FileWriter file = new FileWriter(dirTravail + courriel.getId() + ".json")) {
            file.write(json);
        }
        //Repondre au courriel dont le contenu est le resultat de la commande exéecutée
        EnvoyerCourriel(adresseEnvoyeur, sujet + "-ID:" + id, resultat);
        BoiteNoire.enregistrer("Courriel " + id + " a été répondu", "info");
    }

    // @SuppressWarnings("empty-statement")
    private String ExecuterCommande(String commande) throws FileNotFoundException {

        String resultat="" ;

        //Si la commande fait partie des commandes valides
        if (cpsMap.containsKey(commande)) {
            String commandeaexecuter = shell+cpsMap.get(commande);

            try {
                Runtime rt = Runtime.getRuntime();
                //Process pr = rt.exec("cmd /c dir");
                Process pr = rt.exec(commandeaexecuter);

                BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String ligne;
                while ((ligne = input.readLine()) != null) {
                    resultat += ligne+"\r\n";
                }

                int exitVal = pr.waitFor();
                resultat = "La commande " + commande + " a été exécutée avec le resultat suivant: " + resultat ;

            } catch (IOException | InterruptedException e) {
                resultat = e.toString();
            }

        } else {
            resultat = "La commande " + commande + " n'est pas permise";
        }
        BoiteNoire.enregistrer(resultat, "info");
        return (resultat);

    }

    private void EnvoyerCourriel(String adresseDestinataire, String sujet, String contenu) throws AddressException, MessagingException {
        ServeurCourriel sc = serveurcourriel();
        Session getMailSession;
        MimeMessage generateMailMessage;
        Properties mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", sc.getSmtpPort());
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(adresseDestinataire));
        generateMailMessage.setSubject(sujet);
        generateMailMessage.setContent(contenu, "text/html");
        Transport transport = getMailSession.getTransport("smtp");
        transport.connect("smtp.gmail.com", sc.getIdentifiant(), sc.getMotDePasse());
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        transport.close();
    }

    private int nbFichiersJsonEnvoyeur(String envoyeur) throws IOException {

        FilenameFilter filefilter = (File file, String filename) -> filename.toLowerCase().endsWith(".json") && filename.startsWith(envoyeur);
        File[] fList = new File(dirTravail).listFiles(filefilter);
        return fList.length;
    }

}
