package com.malikbisic.sportapp.model;

/**
 * Created by korisnik on 23/10/2017.
 */

public class AllFixturesModel {

    String localTeamName;
    String localTeamLogo;
    String visitorTeamName;
    String visitorTeamLogo;
    String timeStart;
    String leagueName;
    String date;
    String status;
    String score;
    String idFixtures;
    int localTeamId;
    int visitorTeamId;

    public AllFixturesModel() {
    }

    public AllFixturesModel(String localTeamName, String localTeamLogo, String visitorTeamName, String visitorTeamLogo, String timeStart, String leagueName, String date, String status, String score, String idFixtures, int localTeamId, int visitorTeamId) {
        this.localTeamName = localTeamName;
        this.localTeamLogo = localTeamLogo;
        this.visitorTeamName = visitorTeamName;
        this.visitorTeamLogo = visitorTeamLogo;
        this.timeStart = timeStart;
        this.leagueName = leagueName;
        this.status = status;
        this.score = score;
        this.date = date;
        this.idFixtures = idFixtures;
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

    public String getIdFixtures() {
        return idFixtures;
    }

    public void setIdFixtures(String idFixtures) {
        this.idFixtures = idFixtures;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocalTeamName() {
        return localTeamName;
    }

    public void setLocalTeamName(String localTeamName) {
        this.localTeamName = localTeamName;
    }

    public String getLocalTeamLogo() {
        return localTeamLogo;
    }

    public void setLocalTeamLogo(String localTeamLogo) {
        this.localTeamLogo = localTeamLogo;
    }

    public String getVisitorTeamName() {
        return visitorTeamName;
    }

    public void setVisitorTeamName(String visitorTeamName) {
        this.visitorTeamName = visitorTeamName;
    }

    public String getVisitorTeamLogo() {
        return visitorTeamLogo;
    }

    public void setVisitorTeamLogo(String visitorTeamLogo) {
        this.visitorTeamLogo = visitorTeamLogo;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }
}
