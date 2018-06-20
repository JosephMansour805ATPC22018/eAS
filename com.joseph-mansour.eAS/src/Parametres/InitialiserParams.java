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
import static implementation.ConsulterCourriels.dirTravail;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class InitialiserParams {

    //Separateur de directoires
    public  final static String SEP_DIR="windows".equals(System.getProperty("os.name").substring(0,7).toLowerCase()) ? "\\":"/";
    public  final static String shell = "windows".equals(System.getProperty("os.name").substring(0,7).toLowerCase()) ? "cmd /c ":". ";
    //Lire les informations relatives au Serveur courriel du fichier serveurcourriel.json
    public final static ServeurCourriel serveurcourriel() {
        ServeurCourriel sc = null;
        BufferedReader reader = null;
        File file = new File(dirTravail + "serveurcourriel.json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Gson gson = new GsonBuilder().create();
            sc = gson.fromJson(reader, ServeurCourriel.class);

        } catch (FileNotFoundException ex) {
            try {
                BoiteNoire.enregistrer("Fichier serveurcourriel.json n'a pas pu être lu à cause de: " + ex.getMessage(), "erreur");
            } catch (FileNotFoundException ex1) {
               
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            try {
                BoiteNoire.enregistrer("Fichier serveurcourriel.json est mal formé à cause de: " + e.getMessage(), "erreur");
            } catch (FileNotFoundException ex) {
               
            }
        }
        return sc;

    }

    //Lire les informations relatives aux Envoyeurs agrés du fichier envoyeursagrees.json
    public  static HashMap<String, String> envoyeursagrees() {
        List<EnvoyeurAgree> eas = null;
        HashMap<String, String> easMap = new HashMap<>();
        BufferedReader reader = null;
        File file = new File(dirTravail + "envoyeursagrees.json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Type listType = new TypeToken<List<EnvoyeurAgree>>() {
            }.getType();
            eas = new Gson().fromJson(reader, listType);

            for (EnvoyeurAgree o : eas) {
                easMap.put(o.getAdresseEnvoyeur() + "-" + o.getSujet(), o.getAdresseModerateur());
            }

        } catch (FileNotFoundException ex) {
            try {
                BoiteNoire.enregistrer("Fichier envoyeursagrees.json n'a pas pu être lu à cause de: " + ex.getMessage(), "erreur");
            } catch (FileNotFoundException ex1) {
               
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            try {
                BoiteNoire.enregistrer("Fichier envoyeursagrees.json est mal formé à cause de: " + e.getMessage(), "erreur");
            } catch (FileNotFoundException ex) {
               
            }
        }

        return easMap;

    }
     //Lire la liste des commeandes permises du fichier commandespermises.json
    public  static HashMap<String, String> commandespermises() {
        List<CommandePermise> cps = null;
        HashMap<String, String> cpsMap = new HashMap<>();
        BufferedReader reader = null;
        File file = new File(dirTravail + "commandespermises.json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Type listType = new TypeToken<List<CommandePermise>>() {
            }.getType();
            cps = new Gson().fromJson(reader, listType);

            for (CommandePermise o : cps) {
                cpsMap.put(o.getClefCommande(), o.getCommande());
            }

        } catch (FileNotFoundException ex) {
            try {
                BoiteNoire.enregistrer("Fichier commandespermises.json n'a pas pu être lu à cause de: " + ex.getMessage(), "erreur");
            } catch (FileNotFoundException ex1) {
                
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            try {
                BoiteNoire.enregistrer("Fichier commandespermises.json est mal formé à cause de: " + e.getMessage(), "erreur");
            } catch (FileNotFoundException ex) {
                
            }
        }

        return cpsMap;

    }
}
