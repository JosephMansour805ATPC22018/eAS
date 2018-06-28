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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
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
import parametres.BoiteNoire;
import parametres.Params;
import static parametres.Params.A_EXECUTER;
import static parametres.Params.A_MODERER;
import static parametres.Params.COMMANDE_NON_PERMISE;
import static parametres.Params.COMMANDE_NON_PERMISE_DESC;
import static parametres.Params.EXECUTE;
import static parametres.Params.LIBELLE_ID;
import static parametres.Params.MAL_FORME;
import static parametres.Params.MAL_FORME_DESC;
import static parametres.Params.MODERE;
import static parametres.Params.PREFIX_ID;
import static parametres.Params.NON_MODERE;

/**
 *
 * @author Administrator
 */
public class TraiterCourriel {
String NOUVEAU_COURRIEL="\"Un nouveau courriel est reçu, ID: ";
    
    
    /**
     * Constructeur pour le courriel à modérer
     *
     * @param msg
     * @param id format dresseEnvoyeur + "-" + sujet
     * @param adresseModerateur
     * @param clefCommande
     * @param commande
     * @throws MessagingException
     * @throws IOException
     */
    public TraiterCourriel(Message msg, String id, String adresseModerateur, String clefCommande, String commande) throws MessagingException, IOException {
        String adresseEnvoyeur = Arrays.toString(msg.getFrom());
        String adresseDestinataire = Arrays.toString(msg.getAllRecipients());
        Date dateEnvoyer = msg.getReceivedDate();
        String sujet = msg.getSubject();
        String sujetID = sujet + LIBELLE_ID + id;
        Courriel courriel = new Courriel.CourrielBuilder(id, adresseEnvoyeur, adresseDestinataire, sujet, dateEnvoyer, clefCommande ).build();
        courriel.setRemarque(commande);
        courriel.setAdresseModerateur(adresseModerateur);
        courriel.setStatut(NON_MODERE);
        courriel.setSujetModereration(sujetID);
        BoiteNoire.enregistrer(NOUVEAU_COURRIEL + id + ", statut: " + A_MODERER, "info");
        String contenu = adresseEnvoyeur + " voudrait exécuter la commande suivante. Pour confirmer répondre au courriel sans rien modifier, sinon ignorer ce courriel" + "\r\n" + commande;
        envoyerCourriel(adresseModerateur, sujetID, contenu);
        sauvegarderCourrielJson(courriel);

    }

    /**
     * Constructeur pour le courriel à exécuter
     *
     * @param msg courriel reçu à traiter
     * @param idCourriel
     * @param clefCommande
     * @param commande
     * @throws MessagingException
     * @throws IOException
     */
    public TraiterCourriel(Message msg, String idCourriel, String clefCommande, String commande) throws MessagingException, IOException {
        String adresseEnvoyeur = Arrays.toString(msg.getFrom());
        String adresseDestinataire = Arrays.toString(msg.getAllRecipients());
        Date dateEnvoyer = msg.getReceivedDate();
        String sujet = msg.getSubject();
        Courriel courriel = new Courriel.CourrielBuilder(idCourriel, adresseEnvoyeur, adresseDestinataire, sujet, dateEnvoyer, clefCommande + ":" + commande).build();
        courriel.setStatut(A_EXECUTER);
        BoiteNoire.enregistrer(NOUVEAU_COURRIEL + idCourriel + ", statut: " + A_EXECUTER, "info");
        String resultat = executerCommande(commande);
        courriel.setStatut(EXECUTE);
        courriel.setDateExecution(new Date());
        courriel.setRemarque(resultat);
        String sujetExe = sujet + LIBELLE_ID + idCourriel + " " + EXECUTE;
        envoyerCourriel(adresseEnvoyeur, sujetExe, resultat);
        sauvegarderCourrielJson(courriel);

    }
    
    /**
     * Constructeur pour le courriel modéré à exécuter
     *
     * @param msg courriel reçu à traiter
     * @param idCourriel
     * @throws MessagingException
     * @throws IOException
     */
    public TraiterCourriel(Message msg, Courriel courriel) throws MessagingException, IOException {
        String adresseEnvoyeur = courriel.getAdresseEnvoyeur()+","+courriel.getAdresseModerateur();
        BoiteNoire.enregistrer(NOUVEAU_COURRIEL  + courriel.getId() + ", statut: " + MODERE , "info");
        String resultat = executerCommande(courriel.getRemarque());
        courriel.setStatut(courriel.getStatut()+", "+EXECUTE);
        courriel.setDateModeration(msg.getReceivedDate());
        courriel.setDateExecution(new Date());
        courriel.setRemarque(resultat);
        String sujetExe = LIBELLE_ID + courriel.getId() + " " + EXECUTE;
        envoyerCourriel(adresseEnvoyeur, sujetExe, resultat);
        sauvegarderCourrielJson(courriel);

    }

    /**
     * Constructeur pour le courriel dont le contenu contient une commande non
     * permise
     *
     *
     * @param msg
     * @param clefCommande
     *
     * @throws MessagingException
     * @throws IOException
     */
    public TraiterCourriel(Message msg, String clefCommande) throws MessagingException, IOException {

        BoiteNoire.enregistrer("Un courriel, reçu de " + Arrays.toString(msg.getFrom()) + ", contient une commande non permise", "info");
        envoyerCourriel(Arrays.toString(msg.getFrom()), COMMANDE_NON_PERMISE, COMMANDE_NON_PERMISE_DESC);
    }

    /**
     * Constructeur pour le courriel mal formé
     *
     * @param msg
     *
     * @throws MessagingException
     * @throws IOException
     */
    public TraiterCourriel(Message msg) throws MessagingException, IOException {
        envoyerCourriel(Arrays.toString(msg.getFrom()), MAL_FORME, MAL_FORME_DESC);
        BoiteNoire.enregistrer("Un courriel mal formé est reçu de " + Arrays.toString(msg.getFrom()), "info");
    }

    

    private void sauvegarderCourrielJson(Courriel courriel) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(courriel);
        try (FileWriter file = new FileWriter(Params.REP_TRAVAIL + courriel.getId() + ".json")) {
            file.write(json);
        }

    }

    private String executerCommande(String commande) throws FileNotFoundException {
        String resultat = "";
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(commande);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String ligne;
            while ((ligne = input.readLine()) != null) {
                resultat += ligne + "\r\n";
            }
            resultat = "La commande " + commande + " a été exécutée avec le resultat suivant: " + resultat;
        } catch (IOException e) {
            resultat = e.toString();
        }
        BoiteNoire.enregistrer(resultat, "info");
        return (resultat);

    }

    private void envoyerCourriel(String adresseDestinataire, String sujet, String contenu) throws AddressException, MessagingException {
        ServeurCourriel sc = Params.serveurCourriel();
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
        generateMailMessage.setContent(contenu, "text/plain");
        Transport transport = getMailSession.getTransport("smtp");
        transport.connect("smtp.gmail.com", sc.getIdentifiant(), sc.getMotDePasse());
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        transport.close();
    }

    private int nbFichiersCourriel() throws IOException {

        FilenameFilter filefilter = (File file, String filename) -> filename.toLowerCase().endsWith(".json") && filename.toLowerCase().startsWith(PREFIX_ID.toLowerCase());
        File[] fList = new File(Params.REP_TRAVAIL).listFiles(filefilter);
        return fList.length;
    }

}
