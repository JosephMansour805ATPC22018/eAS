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
import static parametres.InitialiserParams.serveurcourriel;
import parametres.ServeurCourriel;
import com.google.gson.JsonIOException;
import com.google.gson.stream.MalformedJsonException;
import entites.Courriel;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.mail.NoSuchProviderException;

/**
 *
 * @author Administrator
 */
public class ConsulterCourriels {

    public static String dirTravail;

    public static void main(String[] args) throws FileNotFoundException, MalformedJsonException, JsonIOException, NoSuchProviderException, IOException, ParseException {
        //Assigner la directoire du travail
        dirTravail = args.length == 0 ? ".\\" : args[0];

// try {
//                Runtime rt = Runtime.getRuntime();
//                //Process pr = rt.exec("cmd /c dir");
//                Process pr = rt.exec("cmd /c dir");
// 
//                BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
// 
//                String line=null;
// 
//                while((line=input.readLine()) != null) {
//                    System.out.println(line);
//                }
// 
//                int exitVal = pr.waitFor();
//                System.out.println("Exited with error code "+exitVal);
// 
//            } catch(Exception e) {
//                System.out.println(e.toString());
//                e.printStackTrace();
//            }
        

 Courriel courriel = new Courriel.CourrielBuilder("1", "envoyeur", "recepteur", "sujet", new SimpleDateFormat("dd/MM/yyyy").parse("14/06/2018"), "cont").build();
       Gson gson = new Gson();
        String json = gson.toJson(courriel);
        try (FileWriter file = new FileWriter(dirTravail+courriel.getId()+".json"))
            {
                file.write(json);
    }
      //  ServeurCourriel sc = serveurcourriel();
        //Se connecter au serveur courriel
      //  ValiderCourriels.ConnSrvCourriel(sc.getNomServeur(), sc.getProtocole(), Integer.parseInt(sc.getPort()), sc.getIdentifiant(), sc.getMotDePasse());

    }
}
