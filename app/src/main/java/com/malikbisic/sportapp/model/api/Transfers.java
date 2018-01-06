package com.malikbisic.sportapp.model.api;

import com.googlecode.mp4parser.srt.SrtParser;

/**
 * Created by Nane on 9.11.2017.
 */

public class Transfers {
    int playerId;
    int fromTeamId;
    int toTeamId;
    int amount;
    String date;

    String fromTeamName;
    String toTeamName;
    String fromTeamLogo;
    String toTeamLogo;

    public Transfers(int playerId, int fromTeamId, int toTeamId, int amount, String date, String fromTeamName, String toTeamName, String fromTeamLogo, String toTeamLogo) {
        this.playerId = playerId;
        this.fromTeamId = fromTeamId;
        this.toTeamId = toTeamId;
        this.amount = amount;
        this.date = date;
        this.fromTeamName = fromTeamName;
        this.toTeamName = toTeamName;
        this.fromTeamLogo = fromTeamLogo;
        this.toTeamLogo  = toTeamLogo;

    }

    public String getFromTeamName() {
        return fromTeamName;
    }

    public void setFromTeamName(String fromTeamName) {
        this.fromTeamName = fromTeamName;
    }

    public String getToTeamName() {
        return toTeamName;
    }

    public void setToTeamName(String toTeamName) {
        this.toTeamName = toTeamName;
    }

    public String getFromTeamLogo() {
        return fromTeamLogo;
    }

    public void setFromTeamLogo(String fromTeamLogo) {
        this.fromTeamLogo = fromTeamLogo;
    }

    public String getToTeamLogo() {
        return toTeamLogo;
    }

    public void setToTeamLogo(String toTeamLogo) {
        this.toTeamLogo = toTeamLogo;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getFromTeamId() {
        return fromTeamId;
    }

    public void setFromTeamId(int fromTeamId) {
        this.fromTeamId = fromTeamId;
    }

    public int getToTeamId() {
        return toTeamId;
    }

    public void setToTeamId(int toTeamId) {
        this.toTeamId = toTeamId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
