package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 21/10/2017.
 */

public class LivescoreModel {

    String localTeam;
    String localTeamLogo;
    String visitorTeam;
    String visitorTeamLogo;
    String score;
    String status;
    String timeStart;

    public LivescoreModel() {
    }

    public LivescoreModel(String localTeam, String localTeamLogo, String visitorTeam, String visitorTeamLogo, String score, String status, String timeStart) {
        this.localTeam = localTeam;
        this.localTeamLogo = localTeamLogo;
        this.visitorTeam = visitorTeam;
        this.visitorTeamLogo = visitorTeamLogo;
        this.score = score;
        this.status = status;
        this.timeStart = timeStart;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getLocalTeam() {
        return localTeam;
    }

    public void setLocalTeam(String localTeam) {
        this.localTeam = localTeam;
    }

    public String getLocalTeamLogo() {
        return localTeamLogo;
    }

    public void setLocalTeamLogo(String localTeamLogo) {
        this.localTeamLogo = localTeamLogo;
    }

    public String getVisitorTeam() {
        return visitorTeam;
    }

    public void setVisitorTeam(String visitorTeam) {
        this.visitorTeam = visitorTeam;
    }

    public String getVisitorTeamLogo() {
        return visitorTeamLogo;
    }

    public void setVisitorTeamLogo(String visitorTeamLogo) {
        this.visitorTeamLogo = visitorTeamLogo;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
