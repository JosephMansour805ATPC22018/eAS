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

import Parametres.BoiteNoire;
import Parametres.EnvoyeurAgree;
import Parametres.InitialiserParams;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;

/**
 *
 * @author Administrator
 */
public class ValiderCourriels {

    public static void ConnSrvCourriel(String nomserveur, String protocole, int port, String identifiant, String motdepasse) throws FileNotFoundException {
        String renvoyer = "\r\n" + String.format("%44s", "Renvoyer le courriel");
        String pasrenvoyer = "\r\n" + String.format("%60s", "Pas de besoin de renvoyer le courriel");
        Store store = null;
        Folder folder = null;                                                                                                                                     //    int iport = Integer.parseInt(sport);                                                                                           
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
            //System.out.println("b40 connected");
            try {
                store.connect(nomserveur, port, identifiant, motdepasse);
                folder = store.getDefaultFolder();
                if (folder == null) {
                    throw new Exception("No default folder");
                }
                folder = folder.getFolder("DISCARD");
                if (folder == null) {
                    throw new Exception("No INBOX");
                }
                folder.open(Folder.READ_WRITE);
                List<EnvoyeurAgree> eas = new InitialiserParams().envoyeursagrees();

                SearchTerm st = new SearchTerm() {
                    @Override
                    public boolean match(Message message) {
                        try {
                            if (eas.contains((Arrays.toString(message.getFrom()) + "-" + message.getSubject()))) {
                                return true;
                            } else {
                            }
                        } catch (MessagingException ex) {
                            try {
                                BoiteNoire.enregistrer("La boite de reception n'a pas pu être lue proprement " + ex.getMessage() + pasrenvoyer, "erreur");
                            } catch (FileNotFoundException ex1) {
                                Logger.getLogger(ValiderCourriels.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        }
                        return false;
                    }
                };
                Message[] msgs = folder.search(st);
                for (Message msg : msgs) {
                    msg.setFlag(Flags.Flag.DELETED, true);

                }
            } catch (MessagingException ex) {
                BoiteNoire.enregistrer("La connexion au serveur courriel a échoué à cause de " + ex.getMessage() + pasrenvoyer, "erreur");
            }

        } catch (Exception ex) {
            BoiteNoire.enregistrer("La connexion au serveur courriel a échoué2 à cause de " + ex.getMessage(), "erreur");

        } finally {
            try {
                if (folder != null) {
                    folder.close(true);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException ex) {
                BoiteNoire.enregistrer("La connexion au serveur courriel a échoué3 à cause de " + ex.getMessage(), "erreur");
            }
        }
    }

}
