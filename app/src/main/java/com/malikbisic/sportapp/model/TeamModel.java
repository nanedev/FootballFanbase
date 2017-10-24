package com.malikbisic.sportapp.model;

/**
 * Created by Nane on 24.10.2017.
 */

public class TeamModel {

private int playerId;
private int positionId;
private int numberId;
private int countryId;
private String commonName;
private String fullName;
private String firstName;
private String lastName;
private String nationality;
private String birthDate;
private String birthPlace;
private String height;
private String weight;
private String playerImage;
private String positionName;


    public TeamModel() {
    }

    public TeamModel(int playerId, int positionId, int numberId, int countryId, String commonName, String fullName, String firstName,String lastName, String nationality, String birthDate, String birthPlace, String height, String weight, String playerImage, String positionName) {
        this.playerId = playerId;
        this.positionId = positionId;
        this.numberId = numberId;
        this.countryId = countryId;
        this.commonName = commonName;
        this.fullName = fullName;
        this.lastName = lastName;
        this.nationality = nationality;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.height = height;
        this.weight = weight;
        this.playerImage = playerImage;
        this.positionName = positionName;
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getNumberId() {
        return numberId;
    }

    public void setNumberId(int numberId) {
        this.numberId = numberId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPlayerImage() {
        return playerImage;
    }

    public void setPlayerImage(String playerImage) {
        this.playerImage = playerImage;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
}
