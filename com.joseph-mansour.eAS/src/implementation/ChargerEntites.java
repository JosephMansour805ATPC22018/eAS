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
package implementation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import entites.CommandePermise;
import entites.Courriel;
import entites.EnvoyeurAgree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import parametres.BoiteNoire;
import parametres.Params;

/**
 * Charger les infos des entites EnvoyeurAgree et CommandePermise a partir des fichiers json
 *
 * @author Joseph Mansour
 */
public class ChargerEntites {

    //Lire les informations relatives aux Envoyeurs agres du fichier envoyeursAgrees.json
    HashMap<String, String> envoyeursAgrees() {
        List<EnvoyeurAgree> listeEnvoyeursAgrees = null;
        HashMap<String, String> easMap = new HashMap<>();
        BufferedReader reader = null;
        File file = new File(Params.REP_TRAVAIL + "envoyeursagrees.json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Type listType = new TypeToken<List<EnvoyeurAgree>>() {
            }.getType();
            listeEnvoyeursAgrees = new Gson().fromJson(reader, listType);
            for (EnvoyeurAgree o : listeEnvoyeursAgrees) {
                easMap.put(o.getAdresseEnvoyeur().toLowerCase() + "-" + o.getSujet().toLowerCase(), o.getAdresseModerateur().toLowerCase());
            }
        } catch (FileNotFoundException ex) {
            try {
                BoiteNoire.enregistrer("Fichier envoyeursagrees.json n'a pas pu \u00eatre lu \u00e0 cause de: " + ex.getMessage(), "erreur");
            } catch (FileNotFoundException ex1) {
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            try {
                BoiteNoire.enregistrer("Fichier envoyeursagrees.json est mal form\u00e9 \u00e0 cause de: " + e.getMessage(), "erreur");
            } catch (FileNotFoundException ex) {
            }
        }
        return easMap;
    }

    //Lire la liste des commeandes permises du fichier commandespermises.json
    public HashMap<String, String> commandesPermises() {
        List<CommandePermise> listeCommandesPermises = null;
        HashMap<String, String> cpsMap = new HashMap<>();
        BufferedReader reader = null;
        File file = new File(Params.REP_TRAVAIL + "commandespermises.json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Type listType = new TypeToken<List<CommandePermise>>() {
            }.getType();
            listeCommandesPermises = new Gson().fromJson(reader, listType);
            for (CommandePermise o : listeCommandesPermises) {
                cpsMap.put(o.getClefCommande(), o.getCommande());
            }
        } catch (FileNotFoundException ex) {
            try {
                BoiteNoire.enregistrer("Fichier commandespermises.json n'a pas pu \u00eatre lu \u00e0 cause de: " + ex.getMessage(), "erreur");
            } catch (FileNotFoundException ex1) {
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            try {
                BoiteNoire.enregistrer("Fichier commandespermises.json est mal form\u00e9 \u00e0 cause de: " + e.getMessage(), "erreur");
            } catch (FileNotFoundException ex) {
            }
        }

        return cpsMap;
    }

    //Lire les informations relatives aux courriels pas encore moderes du fichier courriels.json
    Courriel trouverCourriel(String id, String statut) {
        Courriel courriel = null;
        BufferedReader reader = null;
        File file = new File(Params.REP_TRAVAIL + id + ".json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Gson gson = new GsonBuilder().create();
            courriel = gson.fromJson(reader, Courriel.class);
        } catch (FileNotFoundException ex) {
            try {
                BoiteNoire.enregistrer("Fichier " + id + ".json n'a pas pu être lu à cause de: " + ex.getMessage(), "erreur");
            } catch (FileNotFoundException ex1) {
                return null;
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            try {
                BoiteNoire.enregistrer("Fichier " + id + ".json est mal construit à cause de: " + e.getMessage(), "erreur");
            } catch (FileNotFoundException ex) {
                return null;
            }
        }
        System.out.print("line 123 :");
        if (courriel == null || !courriel.getStatut().equalsIgnoreCase(statut)) {
            return null;
        } else {

            return courriel;
        }
    }

}
