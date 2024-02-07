package ru.rodniki.bikestat.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RouteRealm extends RealmObject {
    private String timeStart;
    private String kkal;
    private String timeTotal;
    private String distanceTotal;
    private String dateStart;
    private String avgBPM;
    private String avgVelocity;
    @PrimaryKey
    private String mapURI;


    public RouteRealm( String timeStart, String kkal, String timeTotal, String dateStart, String avgBPM, String avgVelocity, String mapURI, String distanceTotal) {
        this.timeStart = timeStart;
        this.kkal = kkal;
        this.timeTotal = timeTotal;
        this.dateStart = dateStart;
        this.avgBPM = avgBPM;
        this.avgVelocity = avgVelocity;
        this.mapURI = mapURI;
        this.distanceTotal = distanceTotal;
    }
    public RouteRealm(){

    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getKkal() {
        return kkal;
    }

    public void setKkal(String kkal) {
        this.kkal = kkal;
    }

    public String getTimeTotal() {
        return timeTotal;
    }

    public void setTimeTotal(String timeTotal) {
        this.timeTotal = timeTotal;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getAvgBPM() {
        return avgBPM;
    }

    public void setAvgBPM(String avgBPM) {
        this.avgBPM = avgBPM;
    }

    public String getAvgVelocity() {
        return avgVelocity;
    }

    public void setAvgVelocity(String avgVelocity) {
        this.avgVelocity = avgVelocity;
    }

    public String getMapURI() {
        return mapURI;
    }

    public void setMapURI(String mapURI) {
        this.mapURI = mapURI;
    }
    public String getDistanceTotal() {
        return distanceTotal;
    }

    public void setDistanceTotal(String distanceTotal) {
        this.distanceTotal = distanceTotal;
    }

}
