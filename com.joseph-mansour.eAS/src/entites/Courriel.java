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

import java.util.Date;

/**
 *
 * @author Administrator
 */
public class Courriel {

    private final String id;
    private final String adresseEnvoyeur;
    private final String adresseDestinataire;
    private final String sujet;
    private final Date dateEnvoyer;
    private final String contenu;
    private String statut;
    private String adresseModerateur;
    private Date dateModeration;
    private String sujetModereration;
    private Date dateExecution;
    private String remarque;

//    public Courriel(String id, String adresseEnvoyeur, String adresseDestinataire, String sujet, Date dateEnvoyer, String contenu, String statut, String adresseModerateur, Date dateModeration, String sujetModereration, Date dateExecution, String remarque) {
//        this.id = id;
//        this.adresseEnvoyeur = adresseEnvoyeur;
//        this.adresseDestinataire = adresseDestinataire;
//        this.sujet = sujet;
//        this.dateEnvoyer = dateEnvoyer;
//        this.contenu = contenu;
//        this.statut = statut;
//        this.adresseModerateur = adresseModerateur;
//        this.dateModeration = dateModeration;
//        this.sujetModereration = sujetModereration;
//        this.dateExecution = dateExecution;
//        this.remarque = remarque;
//    }

    private Courriel(CourrielBuilder cb) {

        id = cb.id;
        adresseEnvoyeur = cb.adresseEnvoyeur;
        adresseDestinataire = cb.adresseDestinataire;
        sujet = cb.sujet;
        dateEnvoyer = cb.dateEnvoyer;
        contenu = cb.contenu;
        statut = cb.statut;
        adresseModerateur = cb.adresseModerateur;
        dateModeration = cb.dateModeration;
        sujetModereration = cb.sujetModereration;
        dateExecution = cb.dateExecution;
        remarque = cb.remarque;

    }

    public String getId() {
        return id;
    }

    public String getAdresseEnvoyeur() {
        return adresseEnvoyeur;
    }

    public String getAdresseDestinataire() {
        return adresseDestinataire;
    }

    public String getSujet() {
        return sujet;
    }

    public Date getDateEnvoyer() {
        return dateEnvoyer;
    }

    public String getContenu() {
        return contenu;
    }

    public String getStatut() {
        return statut;
    }

    public String getAdresseModerateur() {
        return adresseModerateur;
    }

    public Date getDateModeration() {
        return dateModeration;
    }

    public String getSujetModereration() {
        return sujetModereration;
    }

    public Date getDateExecution() {
        return dateExecution;
    }

    public String getRemarque() {
        return remarque;
    }

    
    
    //Classe de CourrielBuilder
    public static class CourrielBuilder {

        private String id;
        private String adresseEnvoyeur;
        private String adresseDestinataire;
        private String sujet;
        private Date dateEnvoyer;
        private String contenu;
        private String statut;
        private String adresseModerateur;
        private Date dateModeration;
        private String sujetModereration;
        private Date dateExecution;
        private String remarque;

        public CourrielBuilder(String id, String adresseEnvoyeur, String adresseDestinataire, String sujet, Date dateEnvoyer, String contenu) {
            this.id = id;
            this.adresseEnvoyeur = adresseEnvoyeur;
            this.adresseDestinataire = adresseDestinataire;
            this.sujet = sujet;
            this.dateEnvoyer = dateEnvoyer;
            this.contenu = contenu;
        }

        public CourrielBuilder statut(String statut) {
            this.statut = statut;
            return this;
        }

        public CourrielBuilder adresseModerateur(String adresseModerateur) {
            this.adresseModerateur = adresseModerateur;
            return this;
        }

        public CourrielBuilder dateModeration(Date dateModeration) {
            this.dateModeration = dateModeration;
            return this;
        }

        public CourrielBuilder sujetModereration(String sujetModereration) {
            this.sujetModereration = sujetModereration;
            return this;
        }

        public CourrielBuilder dateExecution(Date dateExecution) {
            this.dateExecution = dateExecution;
            return this;
        }

        public CourrielBuilder remarque(String remarque) {
            this.remarque = remarque;
            return this;
        }

        public Courriel build() {
            return new Courriel(this);
        }

    }

}
