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

import Parametres.BoiteNoire;
import Parametres.EnvoyeurAgree;
import Parametres.InitialiserParams;
import Parametres.ServeurCourriel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class ConsulterCourriels {

    public  static  String dirTravail;

    public static void main(String[] args) throws FileNotFoundException, MalformedJsonException, JsonIOException {
        //Assigner la directoire du travail
        dirTravail = args.length == 0 ? ".\\" : args[0];

       
       
            ServeurCourriel sc = new InitialiserParams().serveurcourriel();
            List<EnvoyeurAgree> eas =new InitialiserParams().envoyeursagrees();
            System.out.println(eas.contains("mansourjo@mea.com.lb-eSA1"));
            //System.out.println(eas.toString().contains("mansourjo@mea.com.lb-eSA1"));
            //System.out.println(eas.("mansourjo@mea.com.lb-eSA1"));
            //Se connecter au serveur courriel
        // ValiderCourriels.ConnSrvCourriel(sc.getNomServeur(), sc.getProtocole(), Integer.parseInt(sc.getPort()), sc.getIdentifiant(), sc.getMotDePasse());
       
    }
}
