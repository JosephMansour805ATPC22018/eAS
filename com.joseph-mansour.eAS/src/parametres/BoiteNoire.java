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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Enregistrer les traces et les erreurs de l'exécution Créer des fichier
 *
 * @author Joseph Mansour
 */
public class BoiteNoire {

    /**
     * Enregistrer le journal des exécutions dans le fichier eAS_execution.log
     *
     * @param entree la description d'une entrée d'un journal
     * @throws FileNotFoundException
     */
    public static void enregistrerJournal(String entree) throws FileNotFoundException {
        PrintStream o = new PrintStream(new FileOutputStream(Params.REP_TRAVAIL + "eAS_execution.log", true));
        System.setOut(o);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        Date date = new Date();
        System.out.print(formatter.format(date) + " : ");
        System.out.println(entree);
    }

    /**
     * Enregistrer les erreurs des exécutions dans le fichier eAS_erreur.log
     *
     * @param texteErreur la description de l'erreur
     * @throws FileNotFoundException
     */
    public static void enregistrerErreur(String texteErreur) throws FileNotFoundException {
        PrintStream o = new PrintStream(new FileOutputStream(Params.REP_TRAVAIL + "eAS_erreur.log", true));
        System.setOut(o);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        Date date = new Date();
        System.out.print(formatter.format(date) + " : ");
        System.out.println(texteErreur);
    }

    /**
     * Creer un fichier en utilisant le Printstream
     *
     * @param contenu contenu du fichier
     * @param nomFichier le nom du fichier a creer
     * @throws FileNotFoundException
     */
    public static void creerFichier(String contenu, String nomFichier) throws FileNotFoundException {
        PrintStream o = new PrintStream(new FileOutputStream(Params.REP_TRAVAIL + nomFichier, false));
        System.setOut(o);
        System.out.println(contenu);
    }

}
