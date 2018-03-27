package com.malikbisic.sportapp.model.api;

/**
 * Created by korisnik on 20/10/2017.
 */

public class ClubTable {

    long numberClubFan;
    String clubName;
    String clubLogo;
    int clubPos;

    public ClubTable(long numberClubFan, String clubName, String clubLogo, int clubPos) {
        this.numberClubFan = numberClubFan;
        this.clubName = clubName;
        this.clubLogo = clubLogo;
        this.clubPos = clubPos;
    }

    public long getNumberClubFan() {
        return numberClubFan;
    }

    public void setNumberClubFan(long numberClubFan) {
        this.numberClubFan = numberClubFan;
    }

    public int getClubPos() {
        return clubPos;
    }

    public void setClubPos(int clubPos) {
        this.clubPos = clubPos;
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
