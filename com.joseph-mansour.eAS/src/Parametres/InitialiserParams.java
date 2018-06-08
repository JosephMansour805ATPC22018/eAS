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
package Parametres;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import static implementation.ConsulterCourriels.dirTravail;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class InitialiserParams {

    //Lire les informations relatives au Serveur courriel du fichier serveurcourriel.json
    public ServeurCourriel serveurcourriel() {
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
                Logger.getLogger(InitialiserParams.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            try {
                BoiteNoire.enregistrer("Fichier serveurcourriel.json est mal formé à cause de: " + e.getMessage(), "erreur");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(InitialiserParams.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sc;

    }

    //Lire les informations relatives aux Envoyeurs agrés du fichier envoyeursagrees.json
    public List<EnvoyeurAgree> envoyeursagrees() {
        List<EnvoyeurAgree> eas = null;

        BufferedReader reader = null;
        File file = new File(dirTravail + "envoyeursagrees.json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Type listType = new TypeToken<List<EnvoyeurAgree>>(){}.getType();
            eas = new Gson().fromJson(reader, listType);
            

        } catch (FileNotFoundException ex) {
            try {
                BoiteNoire.enregistrer("Fichier envoyeursagrees.json n'a pas pu être lu à cause de: " + ex.getMessage(), "erreur");
            } catch (FileNotFoundException ex1) {
                Logger.getLogger(InitialiserParams.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            try {
                BoiteNoire.enregistrer("Fichier envoyeursagrees.json est mal formé à cause de: " + e.getMessage(), "erreur");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(InitialiserParams.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return eas;

    }
}
