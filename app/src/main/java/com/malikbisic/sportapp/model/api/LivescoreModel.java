package com.malikbisic.sportapp.model.api;

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
    String leagueName;
    String idLivescore;
    int localTeamId;
    int visitorTeamId;

    public LivescoreModel() {
    }

    public LivescoreModel(String localTeam, String localTeamLogo, String visitorTeam, String visitorTeamLogo, String score, String status, String timeStart, String leagueName, String idLivescore, int localTeamId, int visitorTeamId) {
        this.localTeam = localTeam;
        this.localTeamLogo = localTeamLogo;
        this.visitorTeam = visitorTeam;
        this.visitorTeamLogo = visitorTeamLogo;
        this.score = score;
        this.status = status;
        this.timeStart = timeStart;
        this.leagueName = leagueName;
        this.idLivescore = idLivescore;
        this.localTeamId = localTeamId;
        this.visitorTeamId = visitorTeamId;
    }

    public int getLocalTeamId() {
        return localTeamId;
    }

    public void setLocalTeamId(int localTeamId) {
        this.localTeamId = localTeamId;
    }

    public int getVisitorTeamId() {
        return visitorTeamId;
    }

    public void setVisitorTeamId(int visitorTeamId) {
        this.visitorTeamId = visitorTeamId;
    }

    public String getIdLivescore() {
        return idLivescore;
    }

    public void setIdLivescore(String idLivescore) {
        this.idLivescore = idLivescore;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
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
