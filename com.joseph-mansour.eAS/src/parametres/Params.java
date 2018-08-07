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
import com.google.gson.reflect.TypeToken;
import entites.ServeurCourriel;
import entites.Registre;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Définir les variables globales
 *
 * @author Joseph Mansour
 */
public class Params {

    //Le repertoire où touls les fichiers vont être crees
    public static String REP_TRAVAIL;
    public static String DOSSIER_COURRIELS;
    //windows ou unix
    public final static String SYSTEME_EXPLOITATION = System.getProperty("os.name").substring(0, 4).equalsIgnoreCase("wind") == false ? "unix" : "windows";

    //Verifier si l'utilsateur SE existe seulement si le SE est unix
    public final static boolean VERIFIER_UTILISATEUR_SE = SYSTEME_EXPLOITATION == "unix";

    public final static String A_MODERER = "A modérer";
    public final static String MODERE = "Modéré";
    public final static String COMMANDE_NON_PERMISE = "Commande non permise";
    public final static String CONTENU_MAL_CONSTRUIT = "Contenu mal construit";
    public final static String A_EXECUTER = "A exécuter";
    public final static String EXECUTE = "Exécuté";

    /**
     * Lire les informations relatives au Serveur courriel du fichier
     * serveurCourriel.json
     *
     * @return les specs du Serveur Courriel
     * @throws FileNotFoundException si le fichier n'existe pas
     */
    public final static ServeurCourriel serveurCourriel() throws FileNotFoundException {
        ServeurCourriel sc = null;
        BufferedReader reader = null;
        File file = new File(Params.REP_TRAVAIL + "serveurcourriel.json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Gson gson = new GsonBuilder().create();
            sc = gson.fromJson(reader, ServeurCourriel.class);
        } catch (FileNotFoundException ex) {
            BoiteNoire.enregistrerErreur("serveurcourriel.json n'a pas pu être lu à cause de: " + ex.getMessage());
            return null;
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            BoiteNoire.enregistrerErreur("serveurcourriel.json est mal construit à cause de: " + e.getMessage());
            return null;
        }
        return sc;
    }

    /**
     * Charger la liste des clef-valeur du registre.json dans une HashMap
     *
     * @return registre format HashMap
     * @throws FileNotFoundException
     */
    public final static HashMap<String, String> registre() throws FileNotFoundException {
        List<Registre> listeClefs = null;
        HashMap<String, String> registreMap = new HashMap<>();
        BufferedReader reader = null;
        File file = new File(Params.REP_TRAVAIL + "registre.json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Type listType = new TypeToken<List<Registre>>() {
            }.getType();
            listeClefs = new Gson().fromJson(reader, listType);
            for (Registre o : listeClefs) {
                registreMap.put(o.getClef().toLowerCase(), o.getValeur());
            }
        } catch (FileNotFoundException ex) {

            BoiteNoire.enregistrerErreur("registre.json n'a pas pu êre lu à cause de: " + ex.getMessage());

        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {

            BoiteNoire.enregistrerErreur("registre.json est mal construit à cause de: " + e.getMessage());

        }

        return registreMap;

    }

}
