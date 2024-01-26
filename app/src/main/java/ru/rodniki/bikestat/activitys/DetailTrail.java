package ru.rodniki.bikestat.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ru.rodniki.bikestat.R;

public class DetailTrail extends AppCompatActivity {

    String totalTime, totalDistance, avgBPM, kkal, avgVelocity, startDate;
    TextView startDateT, avgVelocityT, kkalT, avgBPMT, totalDistanceT,
            totalTimeT, textMapT, textGraphT;

    CardView cardMap;

    ConstraintLayout layoutBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trail);
        Intent intent = getIntent();

        startDateT = findViewById(R.id.dateStart);
        avgVelocityT = findViewById(R.id.textAvgVelocity);
        kkalT = findViewById(R.id.totalKkal);
        avgBPMT = findViewById(R.id.textBPM);
        totalDistanceT = findViewById(R.id.totalDistance);
        totalTimeT = findViewById(R.id.totalTime);

        textMapT = findViewById(R.id.textMap);
        textGraphT = findViewById(R.id.textGraph);

        cardMap = findViewById(R.id.cardMap);

        layoutBack = findViewById(R.id.layoutBack);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailTrail.this, MainActivity.class);
                startActivity(intent);
            }
        });

        getIntentExtras(intent);
        setTextInfo();
        toggleCards();

    }

    private void toggleCards(){
        textMapT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardMap.setVisibility(View.VISIBLE);
            }
        });
        textGraphT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardMap.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setTextInfo(){
        startDateT.setText(startDate);
        avgVelocityT.setText(avgVelocity);
        kkalT.setText(kkal);
        avgBPMT.setText(avgBPM);
        totalDistanceT.setText(totalDistance);
        totalTimeT.setText(totalTime);
    }
    private void getIntentExtras(Intent intent){
        totalTime = intent.getStringExtra("totalTime");
        totalDistance = intent.getStringExtra("totalDistance");
        avgBPM = intent.getStringExtra("avgBPM");
        kkal = intent.getStringExtra("kkal");
        avgVelocity = intent.getStringExtra("avgVelocity");
        startDate = intent.getStringExtra("startDate");
    }

}