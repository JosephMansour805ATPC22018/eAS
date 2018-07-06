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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import entites.ServeurCourriel;
import javax.mail.PasswordAuthentication;
import parametres.BoiteNoire;
import parametres.Params;
import static parametres.Params.A_EXECUTER;
import static parametres.Params.A_MODERER;
import static parametres.Params.COMMANDE_NON_PERMISE;
import static parametres.Params.COMMANDE_NON_PERMISE_DESC;
import static parametres.Params.EXECUTE;
import static parametres.Params.LIBELLE_ID;
import static parametres.Params.CONTENU_MAL_CONSTRUIT;
import static parametres.Params.CONTENU_MAL_CONSTRUIT_DESC;
import static parametres.Params.MODERE;
import static parametres.Params.SHELL;

/**
 * Traiter les courriels valides selon leur catégorie. Il' y en a 5: A exécuter, A modérer, Modéré, Mal construit et Contenant une commande non permise
 * @author Joseph Mansour
 */
public class TraiterCourriel {

    String NOUVEAU_COURRIEL = "Un nouveau courriel est recu, ID: ";

    /**
     * Constructeur pour le courriel à modérer
     *
     * @param msg
     * @param id de format  adresseEnvoyeur + "-" + sujet
     * @param adresseModerateur addresse courriel du modérateur
     * @param clefCommande la clef de commande à exécuter
     * @param commande la commande à exécuter
     * @throws MessagingException si le message ne pouvait pas être envoyé
     * @throws IOException si le fichier ne pouvait pas être créé
     */
    public TraiterCourriel(Message msg, String id, String adresseModerateur, String clefCommande, String commande) throws MessagingException, IOException {
        String adresseEnvoyeur = ((InternetAddress) msg.getFrom()[0]).getAddress();
        String adresseDestinataire = ((InternetAddress) msg.getAllRecipients()[0]).getAddress();
        Date dateEnvoyer = msg.getReceivedDate();
        String sujet = msg.getSubject();
        String sujetID = sujet + LIBELLE_ID + id;
        Courriel courriel = new Courriel.CourrielBuilder(id, adresseEnvoyeur, adresseDestinataire, sujet, dateEnvoyer, clefCommande).build();
        courriel.setRemarque(commande);
        courriel.setAdresseModerateur(adresseModerateur);
        courriel.setStatut(A_MODERER);
        courriel.setSujetModereration(sujetID);
        BoiteNoire.enregistrerJournal(NOUVEAU_COURRIEL + id + ", statut: " + A_MODERER);
        String contenu = adresseEnvoyeur + " voudrait executer la commande suivante. Pour en confirmer, repondre au courriel sans rien modifier, sinon ignorer ce courriel \r\n" + commande;
        //creer le fichier json du courriel recu
        BoiteNoire.creerFichier(new Gson().toJson(courriel), courriel.getId() + ".json");
        envoyerCourriel(adresseModerateur, sujetID, contenu);

    }

    /**
     * Constructeur pour le courriel à exécuter
     *
     * @param msg courriel recu a traiter
     * @param idCourriel
     * @param clefCommande la clef de commande à exécuter
     * @param commande la commande à exécuter
     * @throws MessagingException 
     * @throws IOException
     */
    public TraiterCourriel(Message msg, String idCourriel, String clefCommande, String commande) throws MessagingException, IOException {
        String adresseEnvoyeur = ((InternetAddress) msg.getFrom()[0]).getAddress();
        String adresseDestinataire = ((InternetAddress) msg.getAllRecipients()[0]).getAddress();
        Date dateEnvoyer = msg.getReceivedDate();
        String sujet = msg.getSubject();
        Courriel courriel = new Courriel.CourrielBuilder(idCourriel, adresseEnvoyeur, adresseDestinataire, sujet, dateEnvoyer, clefCommande + ":" + commande).build();
        courriel.setStatut(A_EXECUTER);
        BoiteNoire.enregistrerJournal(NOUVEAU_COURRIEL + idCourriel + ", statut: " + A_EXECUTER);
        String resultat = executerCommande(commande);
        courriel.setStatut(EXECUTE);
        courriel.setDateExecution(new Date());
        courriel.setRemarque(resultat);
        String sujetExe = sujet + LIBELLE_ID + idCourriel + " " + EXECUTE;

        //creer le fichier json du courriel recu
        BoiteNoire.creerFichier(new Gson().toJson(courriel), courriel.getId() + ".json");

        envoyerCourriel(adresseEnvoyeur, sujetExe, resultat);

    }

    /**
     * Constructeur pour le courriel modéré à exécuter
     *
     * @param msg courriel recu a traiter
     * @param courriel contient les infos du courriel modéré
     * @throws MessagingException
     * @throws IOException
     */
    public TraiterCourriel(Message msg, Courriel courriel) throws MessagingException, IOException {
        String adresseEnvoyeur = courriel.getAdresseEnvoyeur() + "," + courriel.getAdresseModerateur();
        BoiteNoire.enregistrerJournal(NOUVEAU_COURRIEL + courriel.getId() + ", statut: " + MODERE);
        String resultat = executerCommande(courriel.getRemarque());
        courriel.setStatut(courriel.getStatut() + ", " + EXECUTE);
        courriel.setDateModeration(msg.getReceivedDate());
        courriel.setDateExecution(new Date());
        courriel.setRemarque(resultat);
        String sujetExe = LIBELLE_ID + courriel.getId() + " " + EXECUTE;

        //Modifier le fichier json du courriel modéré
        BoiteNoire.creerFichier(new Gson().toJson(courriel), courriel.getId() + ".json");

        envoyerCourriel(adresseEnvoyeur, sujetExe, resultat);

    }

    /**
     * Constructeur pour le courriel dont le contenu contient une commande non
     * permise
     *
     *
     * @param adresseEnvoyeur
     * @param clefCommande
     * @param listeDesCommandesPermises
     *
     * @throws MessagingException
     * @throws java.io.FileNotFoundException
     */
    public TraiterCourriel(String adresseEnvoyeur, String clefCommande, String listeDesCommandesPermises) throws MessagingException, FileNotFoundException {

        BoiteNoire.enregistrerJournal("Un courriel, recu de " + adresseEnvoyeur + ", contient une commande non permise ("+clefCommande+")" );
        envoyerCourriel(adresseEnvoyeur, COMMANDE_NON_PERMISE, COMMANDE_NON_PERMISE_DESC+clefCommande+listeDesCommandesPermises);
    }

    /**
     * Constructeur pour le courriel mal construit
     *
     * @param adresseEnvoyeur
     *
     * @throws MessagingException
     * @throws IOException
     */
    public TraiterCourriel(String adresseEnvoyeur) throws MessagingException, IOException {
        envoyerCourriel(adresseEnvoyeur, CONTENU_MAL_CONSTRUIT, CONTENU_MAL_CONSTRUIT_DESC);
        BoiteNoire.enregistrerJournal("Un courriel mal construit est recu de " + adresseEnvoyeur);
    }

    /**
     * Executer la commande
     *
     * @param commande
     * @return
     * @throws FileNotFoundException
     */
    private String executerCommande(String commande) throws FileNotFoundException {
        String resultat = "";
        try {

            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec( SHELL + commande);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String ligne;
            while ((ligne = input.readLine()) != null) {
                resultat += ligne;
            }
            resultat = "La commande " + commande + " a ete executee avec le resultat suivant: \r\n" + resultat;
        } catch (IOException e) {
            resultat = e.toString();
        }
        BoiteNoire.enregistrerJournal(resultat);
        return (resultat);

    }

    /**
     * Envoyer courriel en utilisant JavaMail smtp
     *
     * @param adresseDestinataire
     * @param sujet
     * @param contenu
     * @throws AddressException
     * @throws MessagingException
     * @throws FileNotFoundException
     */
    private void envoyerCourriel(String adresseDestinataire, String sujet, String contenu) throws AddressException, MessagingException, FileNotFoundException {
        ServeurCourriel sc = Params.serveurCourriel();
        Properties mailServerProperties = new Properties();
        mailServerProperties.put("mail.smtp.host", sc.getSmtp());
        mailServerProperties.put("mail.smtp.port", sc.getSmtpPort());
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(mailServerProperties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sc.getIdentifiant(), sc.getMotDePasse());
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(adresseDestinataire));
            message.setSubject(sujet);
            message.setContent(contenu, "text/plain");
            message.setFrom(sc.getAddresseCourriel());
            Transport transport = session.getTransport("smtp");
            transport.connect(sc.getSmtp(), sc.getIdentifiant(), sc.getMotDePasse());
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException ex) {
            BoiteNoire.enregistrerErreur("Un message n'a pas pu être envoyé à cause de: " + ex.getMessage());
        }

    }

}
