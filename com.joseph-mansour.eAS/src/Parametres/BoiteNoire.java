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

import implementation.ConsulterCourriels;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class BoiteNoire {

    public static void enregistrer(String texte, String typetexte) throws FileNotFoundException {
        // Rediriger le Printstream vers le fichier eAS_execution.log ou eAS_erreur.log
        String nomFichier="erreur".equals(typetexte.toLowerCase())? "eAS_erreur.log":"eAS_execution.log";
        PrintStream o = new PrintStream(new FileOutputStream(ConsulterCourriels.dirTravail + nomFichier, true));
        System.setOut(o);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        Date date = new Date();
        System.out.print(formatter.format(date)+" : ");
        System.out.println(texte);
    }

}
