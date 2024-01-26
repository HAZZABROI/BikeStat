package ru.rodniki.bikestat;

public class InfoTrailModel {
    String timeStart;
    String timeTotal;
    int idTrail;
    int avgBPM;
    int avgVelocity;
    int totalRange;


    public InfoTrailModel(String timeStart, String timeTotal, int idTrail, int avgBPM, int avgVelocity, int totalRange) {
        this.timeStart = timeStart;
        this.timeTotal = timeTotal;
        this.idTrail = idTrail;
        this.avgBPM = avgBPM;
        this.avgVelocity = avgVelocity;
        this.totalRange = totalRange;
    }


    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeTotal() {
        return timeTotal;
    }

    public int getIdTrail() {
        return idTrail;
    }

    public int getAvgBPM() {
        return avgBPM;
    }

    public int getAvgVelocity() {
        return avgVelocity;
    }

    public int getTotalRange() {
        return totalRange;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeTotal(String timeTotal) {
        this.timeTotal = timeTotal;
    }

    public void setIdTrail(int idTrail) {
        this.idTrail = idTrail;
    }

    public void setAvgBPM(int avgBPM) {
        this.avgBPM = avgBPM;
    }

    public void setAvgVelocity(int avgVelocity) {
        this.avgVelocity = avgVelocity;
    }

    public void setTotalRange(int totalRange) {
        this.totalRange = totalRange;
    }
}
