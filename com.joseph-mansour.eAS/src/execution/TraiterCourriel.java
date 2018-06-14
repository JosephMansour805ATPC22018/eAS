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
package execution;

import com.google.gson.Gson;
import entites.Courriel;
import static implementation.ConsulterCourriels.dirTravail;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import javax.mail.Message;
import javax.mail.MessagingException;
import static parametres.InitialiserParams.commandespermises;

/**
 *
 * @author Administrator
 */
public class TraiterCourriel {

    private final Message msg;
    private final HashMap<String, String> cpsMap;

    public TraiterCourriel(Message msg, HashMap<String, String> cpsMap) throws MessagingException, IOException {
        this.msg = msg;
        this.cpsMap = cpsMap;
        Sauvegarder();

    }

    private void Sauvegarder() throws MessagingException, IOException {

        Courriel courriel = new Courriel.CourrielBuilder("1", Arrays.toString(msg.getFrom()), Arrays.toString(msg.getAllRecipients()), msg.getSubject(), msg.getSentDate(), msg.getContent().toString()).build();

        //Sauvegarder le courriel dans un fichier
        Gson gson = new Gson();
        String json = gson.toJson(courriel);
        try (FileWriter file = new FileWriter(dirTravail + courriel.getId() + ".json")) {
            file.write(json);
            ExecuterCommande(msg.getContent().toString());
        }
    }

    private void ExecuterCommande(String commande) {
        
        //Sil la commande fait partie des commandes valides
        if (cpsMap.containsKey(commande)) {
            cpsMap.get(commande);
        };

    }

}
