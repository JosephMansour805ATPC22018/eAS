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

import static com.google.gson.internal.bind.TypeAdapters.URI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.swing.text.Document;
import org.jsoup.Jsoup;
import static parametres.Params.REP_TRAVAIL;
import static parametres.Params.SYSTEME_EXPLOITATION;
import static parametres.Params.serveurCourriel;

/**
 * La classe principale qui fait appel à la classe ValiderCourriels en passant
 * les attributs du serveur courriel
 *
 * @author Joseph Mansour
 */
public class ConsulterCourriels {

    public static void main(String[] args) throws NoSuchProviderException, ParseException, IOException, FileNotFoundException, MessagingException, URISyntaxException {

        //Separateur de repertoires
        String SEP_REP = SYSTEME_EXPLOITATION.equals("unix") ? "/" : "\\";

        //Assigner une valeur au repertoire du travail
        REP_TRAVAIL = args.length < 1 ? "." + SEP_REP : args[0].substring(args[0].length() - 1, args[0].length()).equals(SEP_REP) ? args[0] : args[0] + SEP_REP;

        //Se connecter au serveur courriel pour valider les courriels
        new ValiderCourriels(serveurCourriel());

    }
}
