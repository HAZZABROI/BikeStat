package ru.rodniki.bikestat.models;

public class InfoTrailModel {
    String timeStart;
    String timeTotal;
    int avgBPM;
    int avgVelocity;
    String totalRange;
    String dateStart;
    String id;


    public InfoTrailModel(String timeStart, String timeTotal, int avgBPM, int avgVelocity, String totalRange, String dateStart, String id) {
        this.timeStart = timeStart;
        this.timeTotal = timeTotal;
        this.avgBPM = avgBPM;
        this.avgVelocity = avgVelocity;
        this.totalRange = totalRange;
        this.dateStart = dateStart;
        this.id = id;
    }


    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeTotal() {
        return timeTotal;
    }


    public int getAvgBPM() {
        return avgBPM;
    }

    public int getAvgVelocity() {
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


    public void setAvgBPM(int avgBPM) {
        this.avgBPM = avgBPM;
    }

    public void setAvgVelocity(int avgVelocity) {
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
}
