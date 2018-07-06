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
package entites;

/**
 * Contient les paramêtres du serveur courriel
 * @author Joseph Mansour
 */
public class ServeurCourriel {

    private  String nomServeur;
    private  String protocole;
    private  String port;
    private  String identifiant;
    private  String motDePasse;
    private String smtp;
    private String smtpPort;
    private String addresseCourriel;

    public String getAddresseCourriel() {
        return addresseCourriel;
    }

    public String getSmtp() {
        return smtp;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

   
  

    public String getNomServeur() {
        return nomServeur;
    }

    public String getProtocole() {
        return protocole;
    }

    public String getPort() {
        return port;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public String getMotDePasse() {
        return motDePasse;
    }
}
