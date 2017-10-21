package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 20/10/2017.
 */

public class ClubTable {

    int numbersFans;
    String clubName;
    String clubLogo;

    public ClubTable() {
    }

    public ClubTable(int numbersFans, String clubName, String clubLogo) {
        this.numbersFans = numbersFans;
        this.clubName = clubName;
        this.clubLogo = clubLogo;
    }

    public int getNumbersFans() {
        return numbersFans;
    }

    public void setNumbersFans(int numbersFans) {
        this.numbersFans = numbersFans;
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
