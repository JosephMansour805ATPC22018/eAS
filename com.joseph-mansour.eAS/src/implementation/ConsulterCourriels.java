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

import com.google.gson.JsonIOException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import javax.mail.NoSuchProviderException;
import static parametres.Params.REP_TRAVAIL;
import static parametres.Params.SEP_REP;
import static parametres.Params.serveurCourriel;
import static parametres.Params.DOSSIER_COURRIELS;

/**
 * La fonction principale qui appelle la classe ValiderCourriels
 *
 * @author Joseph Mansour
 */
public class ConsulterCourriels {

    public static void main(String[] args) throws NoSuchProviderException, ParseException, IOException {
        // System.out.println("\r\n" + String.format("%60s", "Pas de besoin de renvoyer le courriel"));
        //Assigner une valeur au repertoire du travail
        REP_TRAVAIL = args.length < 1 ? "." + SEP_REP : args[0];
        
        //Assigner une valeur au dossier des courriels
        DOSSIER_COURRIELS = args.length < 2 ? "Inbox"  : args[1];
        //Se connecter au serveur courriel pour valider les courriels
        new ValiderCourriels(serveurCourriel());
    }
}
