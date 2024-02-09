package ru.rodniki.bikestat;

public class getDiff {

    String diffStr;
    int k;

    public String diff(String bpm, String time, String distance, String avgBPM){
        k = (Integer.parseInt(bpm)*Integer.parseInt(time))/Integer.parseInt(distance);
        if(k < Integer.parseInt(avgBPM)){
            diffStr = "легкий";
        } else if (k > Integer.parseInt(avgBPM)) {
            diffStr = "сложный";
        } else if (k >= Integer.parseInt(avgBPM) + 15 && k <= (Integer.parseInt(avgBPM) - 15)) {
            diffStr = "средний";
        }
        return diffStr;
    }
}
