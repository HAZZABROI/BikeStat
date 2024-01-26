package ru.rodniki.bikestat.models;

public class InfoTrailModel {
    String timeStart;
    String timeTotal;
    String avgBPM;
    String avgVelocity;
    String totalRange;
    String dateStart;
    String id;
    String kkal;


    public InfoTrailModel(String timeStart, String timeTotal, String avgBPM, String avgVelocity, String totalRange, String dateStart, String id, String kkal) {
        this.timeStart = timeStart;
        this.timeTotal = timeTotal;
        this.avgBPM = avgBPM;
        this.avgVelocity = avgVelocity;
        this.totalRange = totalRange;
        this.dateStart = dateStart;
        this.id = id;
        this.kkal = kkal;
    }


    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeTotal() {
        return timeTotal;
    }


    public String getAvgBPM() {
        return avgBPM;
    }

    public String getAvgVelocity() {
        return avgVelocity;
    }

    public String getTotalRange() {
        return totalRange;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeTotal(String timeTotal) {
        this.timeTotal = timeTotal;
    }


    public void setAvgBPM(String avgBPM) {
        this.avgBPM = avgBPM;
    }

    public void setAvgVelocity(String avgVelocity) {
        this.avgVelocity = avgVelocity;
    }

    public void setTotalRange(String totalRange) {
        this.totalRange = totalRange;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKkal() {
        return kkal;
    }

    public void setKkal(String kkal) {
        this.kkal = kkal;
    }
}
