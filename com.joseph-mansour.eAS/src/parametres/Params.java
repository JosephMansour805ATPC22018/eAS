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
package parametres;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import entites.ServeurCourriel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 * @author Joseph Mansour
 */
public class Params {

    //Le repertoire où touls les fichiers vont être crees
    public static String REP_TRAVAIL;
    public static String DOSSIER_COURRIELS;
    //Separateur de repertoires
    public final static String SEP_REP = "unix".equals(System.getProperty("os.name").substring(0, 4).toLowerCase()) ? "/" : "\\";

    //Commande shell
    public final static String SHELL = "unix".equals(System.getProperty("os.name").substring(0, 4).toLowerCase()) ? ". " : "cmd /c ";

    //Prefix des fichiers courriel
    public final static String PREFIX_ID = "EAS-";

    //Libelle de l'ID qui va être ajoute au sujet 
    public final static String LIBELLE_ID = " - ID: ";
    public final static String A_MODERER = "A moderer";
    public final static String MODERE = "Modere";

    public final static String EXECUTE = "Execute";

    public final static String A_EXECUTER = "A executer";

    public final static String MAL_CONSTRUIT = "Contenu mal construit";
    public final static String MAL_CONSTRUIT_DESC = "Le contenu de votre courriel est mal construit";

    public final static String COMMANDE_NON_PERMISE = "Commande non permise";
    public final static String COMMANDE_NON_PERMISE_DESC = "La commande suivante n'est pas permise:\r\n";

    public final static String RENVOYER = "\r\n" + String.format("%44s", "Renvoyer le courriel");
    public final static String PAS_RENVOYER = "\r\n" + String.format("%60s", "Pas de besoin de renvoyer le courriel");

    //Lire les informations relatives au Serveur courriel du fichier serveurCourriel.json
    public final static ServeurCourriel serveurCourriel() {
        ServeurCourriel sc = null;
        BufferedReader reader = null;
        File file = new File(Params.REP_TRAVAIL + "serveurcourriel.json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Gson gson = new GsonBuilder().create();
            sc = gson.fromJson(reader, ServeurCourriel.class);
        } catch (FileNotFoundException ex) {
            try {
                BoiteNoire.enregistrer("Fichier serveurcourriel.json n'a pas pu \u00eatre lu \u00e0 cause de: " + ex.getMessage(), "erreur");
            } catch (FileNotFoundException ex1) {
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            try {
                BoiteNoire.enregistrer("Fichier serveurcourriel.json est mal form\u00e9 \u00e0 cause de: " + e.getMessage(), "erreur");
            } catch (FileNotFoundException ex) {
            }
        }
        return sc;
    }

}
