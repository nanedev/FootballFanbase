package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 20/10/2017.
 */

public class ClubTable {

    int numberClubFan;
    String clubName;
    String clubLogo;

    public ClubTable() {
    }

    public ClubTable(int numbersFans, String numberClubFan, String clubLogo) {
        this.numberClubFan = numbersFans;
        this.clubName = numberClubFan;
        this.clubLogo = clubLogo;
    }

    public int getNumberClubFan() {
        return numberClubFan;
    }

    public void setNumberClubFan(int numberClubFan) {
        this.numberClubFan = numberClubFan;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }
}
