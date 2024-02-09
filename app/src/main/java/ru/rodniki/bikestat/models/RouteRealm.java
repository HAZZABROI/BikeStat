package ru.rodniki.bikestat.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RouteRealm extends RealmObject {
    private String timeStart;
    private String kkal;
    private String timeTotal;
    private String distanceTotal;
    private String dateStart;
    private String BPM;
    private String avgVelocity;
    private String diff;
    private String diffPre;
    private String distanceTotalMetr;
    private Boolean isSchedule;
    @PrimaryKey
    private String mapURI;


    public RouteRealm( String timeStart, String kkal, String timeTotal, String dateStart, String BPM, String avgVelocity, String mapURI, String distanceTotal, String diff, String diffPre, String distanceTotalMetr, Boolean isSchedule) {
        this.timeStart = timeStart;
        this.kkal = kkal;
        this.timeTotal = timeTotal;
        this.dateStart = dateStart;
        this.BPM = BPM;
        this.avgVelocity = avgVelocity;
        this.mapURI = mapURI;
        this.distanceTotal = distanceTotal;
        this.diff = diff;
        this.diffPre = diffPre;
        this.distanceTotalMetr = distanceTotalMetr;
        this.isSchedule= isSchedule;
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

    public String getBPM() {
        return BPM;
    }

    public void setBPM(String BPM) {
        this.BPM = BPM;
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

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public String getDiffPre() {
        return diffPre;
    }

    public void setDiffPre(String diffPre) {
        this.diffPre = diffPre;
    }

    public String getDistanceTotalMetr() {
        return distanceTotalMetr;
    }

    public void setDistanceTotalMetr(String distanceTotalMetr) {
        this.distanceTotalMetr = distanceTotalMetr;
    }

    public Boolean getSchedule() {
        return isSchedule;
    }

    public void setSchedule(Boolean schedule) {
        isSchedule = schedule;
    }
}
