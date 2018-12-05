/*
 * Copyright (C) 2018 Administrator
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * adminsys published by the Free Software Foundation; either version 2
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
import entites.AdministrateurSysteme;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import parametres.BoiteNoire;
import parametres.Params;
import static parametres.Params.SYSTEME_EXPLOITATION;
import static parametres.Params.registre;

/**
 * Charger les infos des entites AdministrateurSysteme et CommandePermise a
 * partir des fichiers json
 *
 * @author Joseph Mansour
 */
public class ChargerEntites {

    String NOT_READ;
    String WRONG_FORMAT;

    public ChargerEntites() throws FileNotFoundException {
        this.NOT_READ = registre().get("pas_lu");
        this.WRONG_FORMAT = registre().get("mal_construit");
    }

    /**
     * @return liste HashMap es administrateurs agrées à partir du fichier
     * administrateurssysteme.json Si le SE est unix les lignes où
     * AdresseCourriel, Sujet et UtilisateurSE ne sont pas définis sont ignorées
     * Si le SE est windows les lignes où AdresseCourriel et Sujet ne sont pas
     * définis sont ignorées
     */
    HashMap<String, AdministrateurSysteme> administrateursSysteme() throws IOException {
        List<AdministrateurSysteme> listeAdministrateursSysteme = null;
        HashMap<String, AdministrateurSysteme> adminsysMap = new HashMap<>();
        BufferedReader reader = null;
        File file = new File(Params.REP_TRAVAIL + "administrateurssysteme.json");
        boolean adminsysIDPasNul;
        boolean agree;
        boolean VERIFIER_UTILISATEUR_SE = SYSTEME_EXPLOITATION == "unix";
        try {
            reader = new BufferedReader(new FileReader(file));
            Type listType = new TypeToken<List<AdministrateurSysteme>>() {
            }.getType();
            listeAdministrateursSysteme = new Gson().fromJson(reader, listType);
            for (AdministrateurSysteme adminsys : listeAdministrateursSysteme) {
                adminsysIDPasNul = !adminsys.getAdresseCourriel().isEmpty() && !adminsys.getSujet().isEmpty();
                agree = VERIFIER_UTILISATEUR_SE == true ? adminsysIDPasNul && !adminsys.getUtilisateurSE().isEmpty() && existeUtilsateurSE(adminsys.getUtilisateurSE()) : adminsysIDPasNul;
                if (agree) {
                    adminsysMap.put(adminsys.getAdresseCourriel().toLowerCase() + "-" + adminsys.getSujet().toLowerCase(), adminsys);
                }
            }
        } catch (FileNotFoundException ex) {
            BoiteNoire.enregistrerErreur("administrateurssysteme.json " + NOT_READ + ex.getMessage());

        } catch (JsonIOException | JsonSyntaxException | NumberFormatException | java.lang.NullPointerException e) {
            BoiteNoire.enregistrerErreur("administrateurssysteme.json " + WRONG_FORMAT + e.getMessage());

        }
        return adminsysMap;
    }

    /**
     *
     * @param utilisateurSE associé à l'administrateur système
     * @return si existe ou pas
     * @throws IOException
     */
    private boolean existeUtilsateurSE(String utilisateurSE) throws IOException {

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("sudo su - " + utilisateurSE + " -c env");
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        long nblignes = input.lines().count();
        return nblignes > 0;

    }

    /**
     * @return liste HashMap des commeandes permises à partir du fichier
     * commandespermises.json Les lignes où idCommande et Commande ne sont pas
     * définis sont ignorées
     * @throws java.io.FileNotFoundException
     */
    public HashMap<String, String> commandesPermises() throws FileNotFoundException {
        List<CommandePermise> listeCommandesPermises = null;
        HashMap<String, String> cpsMap = new HashMap<>();
        BufferedReader reader = null;
        File file = new File(Params.REP_TRAVAIL + "commandespermises.json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Type listType = new TypeToken<List<CommandePermise>>() {
            }.getType();
            listeCommandesPermises = new Gson().fromJson(reader, listType);
            for (CommandePermise commperm : listeCommandesPermises) {
                if (!commperm.getIdCommande().isEmpty() && !commperm.getCommande().isEmpty()) {
                    cpsMap.put(commperm.getIdCommande().toLowerCase(), commperm.getCommande());
                }
            }
        } catch (FileNotFoundException ex) {
            BoiteNoire.enregistrerErreur("commandespermises.json " + NOT_READ + ex.getMessage());

        } catch (JsonIOException | JsonSyntaxException | NumberFormatException | java.lang.NullPointerException e) {
            BoiteNoire.enregistrerErreur("commandespermises.json " + WRONG_FORMAT + e.getMessage());

        }

        return cpsMap;
    }

    /**
     * Trouver le fichier .json qui correspond à un courriel spécifique
     *
     * @param id
     * @param statut A modérer
     * @return les informations relatives au courriel trouvé
     * @throws FileNotFoundException
     */
    Courriel trouverCourriel(String id, String statut) throws FileNotFoundException {
        Courriel courriel = null;
        BufferedReader reader = null;
        File file = new File(Params.REP_TRAVAIL + id + ".json");
        try {
            reader = new BufferedReader(new FileReader(file));
            Gson gson = new GsonBuilder().create();
            courriel = gson.fromJson(reader, Courriel.class);
        } catch (FileNotFoundException ex) {
            BoiteNoire.enregistrerErreur(id + ".json " + NOT_READ + ex.getMessage());

        } catch (JsonIOException | JsonSyntaxException | NumberFormatException e) {
            BoiteNoire.enregistrerErreur(id + ".json " + WRONG_FORMAT + e.getMessage());

        }
        if (courriel == null || !courriel.getStatut().equalsIgnoreCase(statut)) {
            return null;
        } else {

            return courriel;
        }
    }

}
