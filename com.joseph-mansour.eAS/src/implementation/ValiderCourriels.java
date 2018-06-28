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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;
import parametres.Params;
import static parametres.Params.A_EXECUTER;
import static parametres.Params.A_MODERER;
import static parametres.Params.LIBELLE_ID;
import static parametres.Params.MAL_FORME;
import static parametres.Params.MODERE;
import static parametres.Params.PAS_RENVOYER;
import static parametres.Params.COMMANDE_NON_PERMISE;
import static parametres.Params.EXECUTE;
import static parametres.Params.NON_MODERE;
import static parametres.Params.PREFIX_ID;

/**
 * Vérifier si les envoyeurs des nouveaux courriels sont agrées. Appeller la
 * Classe TraiterCourriel en fournissant le courriel comme paramètre
 *
 * @author Joseph Mansour
 */
public class ValiderCourriels {

    /**
     * Etablir une connection avec le serveur courriel
     *
     * @author Joseph Mansour
     * @param nomserveur IP ou nom du serveur courriel
     * @param protocole imap/pop3
     * @param port 995/143
     * @param identifiant addresse courriel recevant les courriels
     * @param motdepasse mot de passe
     * @throws java.io.FileNotFoundException si serveurcourriel.json n'existe
     * pas
     * @throws javax.mail.NoSuchProviderException si le serveur courriel n'es
     * pas disponible
     * @throws java.io.IOException si le store du serveur courriel n'es pas
     * disponible
     * @throws java.text.ParseException si serveurcourriel.json est mal formé
     */
    
    
    
    public ValiderCourriels(ServeurCourriel serveurCourriel) throws NoSuchProviderException, ParseException, IOException {
        connecterServeurCourriel(serveurCourriel.getNomServeur(), serveurCourriel.getProtocole(), Integer.parseInt(serveurCourriel.getPort()), serveurCourriel.getIdentifiant(), serveurCourriel.getMotDePasse());
    }

      void connecterServeurCourriel(String nomserveur, String protocole, int port, String identifiant, String motdepasse) throws FileNotFoundException, NoSuchProviderException, ParseException, IOException {
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
                BoiteNoire.enregistrer("La connexion au serveur courriel a échoué à cause de " + ex.getMessage() + PAS_RENVOYER, "erreur");
            }

        } finally {
            try {

                if (store.isConnected()) {
                    store.close();
                    BoiteNoire.enregistrer("La connexion au serveur courriel est terminée proprement", "success");
                }
            } catch (MessagingException ex) {
                BoiteNoire.enregistrer("La connexion au serveur courriel n'a pas été terminée proprement à cause de " + store.toString() + ex.getMessage(), "erreur");
            }

        }
    }

    /**
     * Vérifier s'il y en a des courriels venant des envoyeurs/modérateurs
     * agrées et puis passer le courriel vers la classe TraiterCourriel pour
     * traitement
     *
     * @param store
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     * @author Joseph Mansour
     */
     void lireDossier(Store store) throws MessagingException, FileNotFoundException, IOException {

        Folder dossier = store.getFolder("inbox");
        dossier.open(Folder.READ_WRITE);
        BoiteNoire.enregistrer("La connexion au serveur courriel est établie et la directoire " + dossier.toString() + " est ouverte", "info");
        int nb = nbFichiersCourriel();

        //Construire la liste des envoyeurs agrés
        final HashMap<String, String> easMap = new ChargerEntites().envoyeursAgreesMap();
        final HashMap<String, String> commandespermisesMap = new ChargerEntites().commandesPermisesMap();

        //Construire le filtre des courriels à consulter
        SearchTerm searchTerm = new SearchTerm() {

            @Override
            public boolean match(Message message) {
                Boolean envoyeurAgree = false;
                Boolean courrielModere = false;

                try {

                    //Ignorer les courriels déjà repondus ou conteneant plus qu'un texte simple
                    if (message.isSet(Flags.Flag.ANSWERED)) {
                        return false;
                    }

                    //Voir si un nouveau courriel est envoyé par un envoyeur agrée
                    String envoyeur = Arrays.toString(message.getFrom());
                    String sujet = message.getSubject();
                    String eaID = envoyeur + "-" + sujet;

                    if (envoyeurAgree = easMap.containsKey(eaID)) {
                        message.setDescription(MAL_FORME);
                        //Vérifier si le contenu du courriel contient une clefCommande permise
                        if (message.isMimeType("text/*")) {

                            String clefCommande = message.getContent().toString().trim();
                            if (commandespermisesMap.containsKey(clefCommande)) {
                                String commande = easMap.get(eaID);
                                if (easMap.get(eaID).isEmpty() || easMap.get(eaID) == null) {
                                    message.setDescription(A_EXECUTER + " " + clefCommande + " " + commande);
                                    //+commandespermisesMap.get(clefCommande)
                                    //message.setDescription(commandespermisesMap.get(clefCommande));
                                } else {
                                    message.setDescription(NON_MODERE + " " + easMap.get(eaID) + " " + clefCommande + " " + commande);
                                }
                            } else {
                                message.setDescription(COMMANDE_NON_PERMISE + " " + clefCommande);
                            }
                        }
                    }

                    //Voir si un courriel existant a été modéré
                    int placementLibelle = sujet.indexOf(LIBELLE_ID);
                    if (placementLibelle >= 0) {
                        String idCourriel = message.getSubject().substring(placementLibelle + LIBELLE_ID.length());
                        Courriel courriel = new ChargerEntites().trouverCourriel(idCourriel, NON_MODERE);
                        if (courriel != null) {
                            courrielModere = courriel.getAdresseModerateur().toLowerCase().equals(envoyeur.toLowerCase());
                            if (courrielModere) {
                                message.setDescription(MODERE + " " + courriel);
                            }
                        }
                    }

                    if (!envoyeurAgree || !courrielModere) {
                    } else {
                        message.saveChanges();
                        return true;
                    }

                } catch (MessagingException | IOException ex) {

                    try {
                        BoiteNoire.enregistrer("Un des courriels n'a pas pu être lu à cause de " + store.toString() + ex.getMessage(), "erreur");
                    } catch (FileNotFoundException ex1) {

                    }
                }

                return false;
            }

        };

        //Itérer à travers le dossier des courriels valides
        Message[] msgs = dossier.search(searchTerm);
        for (Message msg : msgs) {
            String idCourriel;
            String[] description = msg.getDescription().split(" ");
            TraiterCourriel traiterCourriel;
            switch (description[0]) {
                case MAL_FORME:
                    traiterCourriel = new TraiterCourriel(msg);
                    break;
                case COMMANDE_NON_PERMISE:
                    traiterCourriel = new TraiterCourriel(msg, description[1]);
                    break;
                case A_MODERER:
                    nb += 1;
                    idCourriel = PREFIX_ID + nb;
                    traiterCourriel = new TraiterCourriel(msg, idCourriel, description[1], description[2], description[3]);
                    break;

                case A_EXECUTER:
                    nb += 1;
                    idCourriel = PREFIX_ID + nb;
                    traiterCourriel = new TraiterCourriel(msg, idCourriel, description[1], description[2]);
                    break;

                case MODERE:
                    Courriel courriel=new GsonBuilder().create().fromJson(description[1], Courriel.class);
                    traiterCourriel = new TraiterCourriel(msg, courriel);
                    break;
            }
            msg.setFlag(Flags.Flag.ANSWERED, true);
            BoiteNoire.enregistrer("Courriel est marqué ANSWERED", "info");
        }
    }

    private static int nbFichiersCourriel() throws IOException {

        FilenameFilter filefilter = (File file, String filename) -> filename.toLowerCase().endsWith(".json") && filename.toLowerCase().startsWith(PREFIX_ID.toLowerCase());
        File[] fList = new File(Params.REP_TRAVAIL).listFiles(filefilter);
        return fList.length;
    }
}
