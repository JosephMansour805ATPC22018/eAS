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
 * GNU General Public License for more daaaetails.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package implementation;

import com.google.gson.GsonBuilder;
import entites.Courriel;
import entites.ServeurCourriel;
import execution.TraiterCourriel;
import java.io.File;
import parametres.BoiteNoire;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.SearchTerm;
import parametres.Params;
import static parametres.Params.A_EXECUTER;
import static parametres.Params.A_MODERER;
import static parametres.Params.LIBELLE_ID;
import static parametres.Params.CONTENU_MAL_CONSTRUIT;
import static parametres.Params.MODERE;
import static parametres.Params.PAS_RENVOYER;
import static parametres.Params.COMMANDE_NON_PERMISE;
import static parametres.Params.DOSSIER_COURRIELS;
import static parametres.Params.PREFIX_ID;

/**
 * Identifier les courriels valides (i.e. envoyés par les envoyeurs agrées) et
 * puis les catégoriser pour les traiter par conséquent
 *
 *
 * @author Joseph Mansour
 */
public final class ValiderCourriels {

    /**
     * Etablir une connection avec le serveur courriel
     *
     * @author Joseph Mansour
     * @param serveurCourriel contient les infos du serveur courriel
     * @throws java.io.FileNotFoundException si serveurcourriel.json n'existe
     * pas
     * @throws javax.mail.NoSuchProviderException si le serveur courriel n'est
     * pas disponible
     * @throws java.io.IOException si le store du serveur courriel n'est pas
     * disponible
     * @throws java.text.ParseException si serveurcourriel.json est mal
     * construit
     */
    public ValiderCourriels(ServeurCourriel serveurCourriel) throws NoSuchProviderException, ParseException, IOException, FileNotFoundException, MessagingException {

        if (serveurCourriel != null) {
            connecterServeurCourriel(serveurCourriel.getNomServeur(), serveurCourriel.getProtocole(), Integer.parseInt(serveurCourriel.getPort()), serveurCourriel.getIdentifiant(), serveurCourriel.getMotDePasse());
        }

    }

    /**
     * Se connecter au serveur courriel
     *
     * @param nomserveur
     * @param protocole
     * @param port
     * @param identifiant
     * @param motdepasse
     * @throws FileNotFoundException
     * @throws NoSuchProviderException
     * @throws ParseException
     * @throws IOException
     * @throws MessagingException
     */
    void connecterServeurCourriel(String nomserveur, String protocole, int port, String identifiant, String motdepasse) throws FileNotFoundException, NoSuchProviderException, ParseException, IOException, MessagingException {
        Store store = null;

        try {
            Properties prop = new Properties();
            prop.setProperty("mail.store.protocol", protocole);
            if (port == 993 || port == 995) {
                prop.setProperty("mail." + protocole + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                prop.setProperty("mail." + protocole + ".socketFactory.fallback", "false");
            } else {
            }
            Session session = Session.getDefaultInstance(prop);
            store = session.getStore();

            try {
                store.connect(nomserveur, port, identifiant, motdepasse);
                lireDossier(store);

            } catch (MessagingException ex) {
                BoiteNoire.enregistrerErreur("La connexion au serveur courriel a échoué à cause de " + ex.getMessage() + PAS_RENVOYER);
            }

        } finally {

            if (store.isConnected()) {
                store.close();
                BoiteNoire.enregistrerJournal("La connexion au serveur courriel est proprement terminée ");
            }
        }
    }

    /**
     * Verifier s'il y en a des courriels venant des envoyeurs/moderateurs
     * agrees et puis passer le courriel vers la classe TraiterCourriel pour
     * traitement
     *
     * @param store
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     * @author Joseph Mansour
     */
    void lireDossier(Store store) throws MessagingException, FileNotFoundException, IOException {

        Folder dossier = store.getFolder(DOSSIER_COURRIELS);
        dossier.open(Folder.READ_WRITE);
        BoiteNoire.enregistrerJournal("La connexion au serveur courriel est établie et le dossier " + dossier.toString() + " est ouverte");
        int nb = nbFichiersCourriel();
        final String SEPARATEUR = "::";
        //Construire la liste des envoyeurs agres
        final HashMap<String, String> easMap = new ChargerEntites().envoyeursAgrees();
        final HashMap<String, String> commandespermisesMap = new ChargerEntites().commandesPermises();
        HashMap<String, String> infoSupplCourriel = new HashMap<>();
        //Construire le filtre des courriels à consulter
        SearchTerm searchTerm = new SearchTerm() {

            @Override
            public boolean match(Message message) {
                Boolean envoyeurAgree = false;
                Boolean courrielModere = false;
                Boolean contenuValide;
                try {

                    //Ignorer les courriels dejà repondus ou conteneant plus qu'un texte simple
                    if (message.isSet(Flags.Flag.ANSWERED)) {
                        return false;
                    }
                    //Voir si un nouveau courriel est envoye par un envoyeur agree
                    Address[] froms = message.getFrom();
                    String envoyeur = ((InternetAddress) froms[0]).getAddress();
                    String sujet = message.getSubject();
                    String eaID = (envoyeur + "-" + sujet).toLowerCase();
                    String numMessage = Integer.toString(message.getMessageNumber());
                    if (envoyeurAgree = easMap.containsKey(eaID)) {
                        String clefCommande = message.getContent().toString().trim().toLowerCase();
                        //Verifier si le contenu du courriel contient un seul mot de mode texte brut
                        contenuValide = message.isMimeType("text/plain") && !clefCommande.contains(" ") && !clefCommande.contains(System.getProperty("line.separator")) && !clefCommande.isEmpty();
                        if (contenuValide) {
                            if (commandespermisesMap.containsKey(clefCommande)) {
                                String commande = commandespermisesMap.get(clefCommande);

                                if (easMap.get(eaID).isEmpty() || easMap.get(eaID) == null) {
                                    infoSupplCourriel.put(numMessage, A_EXECUTER + SEPARATEUR + clefCommande + SEPARATEUR + commande);

                                } else {
                                    infoSupplCourriel.put(numMessage, A_MODERER + SEPARATEUR + easMap.get(eaID) + SEPARATEUR + clefCommande + SEPARATEUR + commande);
                                }

                            } else {
                                infoSupplCourriel.put(numMessage, COMMANDE_NON_PERMISE + SEPARATEUR + envoyeur + SEPARATEUR + clefCommande + SEPARATEUR + commandespermisesMap.keySet());

                            }

                        } else {
                            infoSupplCourriel.put(numMessage, CONTENU_MAL_CONSTRUIT + SEPARATEUR + envoyeur);
                        }
                        return true;
                    }

                    //Voir si un courriel existant a ete modere
                    int placementLibelle = sujet.indexOf(LIBELLE_ID);
                    if (placementLibelle >= 0) {
                        String idCourriel = message.getSubject().substring(placementLibelle + LIBELLE_ID.length());
                        Courriel courriel = new ChargerEntites().trouverCourriel(idCourriel, A_MODERER);
                        if (courriel != null) {
                            courrielModere = courriel.getAdresseModerateur().toLowerCase().equals(envoyeur.toLowerCase());
                            if (courrielModere) {
                                infoSupplCourriel.put(numMessage, MODERE + SEPARATEUR + courriel);
                                return true;
                            }
                        }
                    }

                } catch (MessagingException | IOException ex) {

                    try {
                        BoiteNoire.enregistrerErreur("Un des courriels n'a pas pu être lu à cause de " + ex.getMessage() + PAS_RENVOYER);
                    } catch (FileNotFoundException ex1) {

                    }
                }

                return false;
            }

        };

        //Iterer à travers le dossier des courriels valides
        Message[] msgs = dossier.search(searchTerm);
        BoiteNoire.enregistrerJournal("Nombre total de courriels valides est " + msgs.length);
        for (Message msg : msgs) {
            String idCourriel;
            String numMessage = Integer.toString(msg.getMessageNumber());
            String[] infoSuppl = infoSupplCourriel.get(numMessage).split(SEPARATEUR);
            TraiterCourriel traiterCourriel;
            msg.setFlag(Flags.Flag.ANSWERED, true);
            switch (infoSuppl[0]) {
                case CONTENU_MAL_CONSTRUIT:
                    traiterCourriel = new TraiterCourriel(infoSuppl[1]);
                    break;
                case COMMANDE_NON_PERMISE:
                    traiterCourriel = new TraiterCourriel(infoSuppl[1], infoSuppl[2], infoSuppl[3]);
                    break;
                case A_MODERER:
                    nb += 1;
                    idCourriel = PREFIX_ID + nb;
                    traiterCourriel = new TraiterCourriel(msg, idCourriel, infoSuppl[1], infoSuppl[2], infoSuppl[3]);
                    break;

                case A_EXECUTER:
                    nb += 1;
                    idCourriel = PREFIX_ID + nb;
                    traiterCourriel = new TraiterCourriel(msg, idCourriel, infoSuppl[1], infoSuppl[2]);
                    break;

                case MODERE:
                    Courriel courriel = new GsonBuilder().create().fromJson(infoSuppl[1], Courriel.class);
                    traiterCourriel = new TraiterCourriel(msg, courriel);
                    break;
            }

        }
        if (msgs.length > 0) {
            BoiteNoire.enregistrerJournal("Tous les courriels valides sont marqués ANSWERED");
        }
    }

    /**
     *
     * @return le nombre de fichiers couriel déjà existents *
     */
    private static int nbFichiersCourriel() {

        FilenameFilter filefilter = (File file, String filename) -> filename.toLowerCase().endsWith(".json") && filename.toLowerCase().startsWith(PREFIX_ID.toLowerCase());
        File[] fList = new File(Params.REP_TRAVAIL).listFiles(filefilter);
        return fList.length;
    }
}
