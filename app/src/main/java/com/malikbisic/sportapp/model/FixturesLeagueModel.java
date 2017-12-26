package com.malikbisic.sportapp.model;

import java.util.Date;

/**
 * Created by korisnik on 25/12/2017.
 */

public class FixturesLeagueModel {

    String localTeamName;
    String localTeamLogo;
    String visitorTeamName;
    String visitorTeamLogo;
    String timeStart;
    String leagueName;
    Date date;
    String status;
    String score;
    String idFixtures;
    int localTeamId;
    int visitorTeamId;

    public FixturesLeagueModel() {
    }

    public FixturesLeagueModel(String localTeamName, String localTeamLogo, String visitorTeamName, String visitorTeamLogo, String timeStart,  Date date, String leagueName, String status, String score, String idFixtures, int localTeamId, int visitorTeamId) {
        this.localTeamName = localTeamName;
        this.localTeamLogo = localTeamLogo;
        this.visitorTeamName = visitorTeamName;
        this.visitorTeamLogo = visitorTeamLogo;
        this.timeStart = timeStart;
        this.leagueName = leagueName;
        this.date = date;
        this.status = status;
        this.score = score;
        this.idFixtures = idFixtures;
        this.localTeamId = localTeamId;
        this.visitorTeamId = visitorTeamId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getIdFixtures() {
        return idFixtures;
    }

    public void setIdFixtures(String idFixtures) {
        this.idFixtures = idFixtures;
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
}
