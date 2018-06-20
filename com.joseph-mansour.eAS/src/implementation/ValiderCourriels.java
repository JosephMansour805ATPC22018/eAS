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

import execution.TraiterCourriel;
import parametres.BoiteNoire;
import static parametres.InitialiserParams.commandespermises;
import java.io.FileNotFoundException;
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
import static parametres.InitialiserParams.envoyeursagrees;

/**
 *
 * @author Administrator
 */
public class ValiderCourriels {

    static String renvoyer = "\r\n" + String.format("%44s", "Renvoyer le courriel");
    static String pasrenvoyer = "\r\n" + String.format("%60s", "Pas de besoin de renvoyer le courriel");

    public static void ConnSrvCourriel(String nomserveur, String protocole, int port, String identifiant, String motdepasse) throws FileNotFoundException, NoSuchProviderException, IOException, ParseException {

        //Se connecter au serveur courriel
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
                LireDossier(store);

            } catch (MessagingException ex) {
                BoiteNoire.enregistrer("La connexion au serveur courriel a échoué à cause de " + ex.getMessage() + pasrenvoyer, "erreur");
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

//    Itérer à travers le dossier Inbox et filrer les courriels venant des envoyeurs agrées
    static void LireDossier(Store store) throws MessagingException, FileNotFoundException, IOException {
        Folder dossier = store.getFolder("inbox");
        dossier.open(Folder.READ_WRITE);
        BoiteNoire.enregistrer("La connexion au serveur courriel est établie et la directoire " + dossier.toString() + " est ouverte", "info");
        HashMap<String, String> easMap = envoyeursagrees();

        SearchTerm st;
        st = new SearchTerm() {
            @Override
            public boolean match(Message message) {

                try {
                    if (!easMap.containsKey((Arrays.toString(message.getFrom()) + "-" + message.getSubject()))) {
                    } else {
                        return true;
                    }
                } catch (MessagingException ex) {
                    try {
                        BoiteNoire.enregistrer("Un des courriels n'a pas pu être lu à cause de " + store.toString() + ex.getMessage(), "erreur");
                    } catch (FileNotFoundException ex1) {

                    }
                }

                return false;
            }
        };

        
        HashMap<String, String> cpsMap = null;

        //Itérer à travers le dossier des courriels valides
        Message[] msgs = dossier.search(st);

        for (Message msg : msgs) {
            
            //Construire la liste des commandes permises si elle est vide
            if (cpsMap == null) {
                cpsMap = commandespermises();
            }
            
            TraiterCourriel tc = new TraiterCourriel(msg, cpsMap);
            // System.out.println(msg.getContent().toString());
            msg.setFlag(Flags.Flag.DELETED, true);
            BoiteNoire.enregistrer("Courriel est supprimé","info");

        }
    }

    
}
