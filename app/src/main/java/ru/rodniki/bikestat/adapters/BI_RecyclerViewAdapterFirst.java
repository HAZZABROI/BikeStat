package ru.rodniki.bikestat.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.rodniki.bikestat.R;
import ru.rodniki.bikestat.models.RouteRealm;
import ru.rodniki.bikestat.activitys.DetailTrailActivity;


public class BI_RecyclerViewAdapterFirst extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<RouteRealm> infoTrailModels;
    private Boolean initialized;


    public BI_RecyclerViewAdapterFirst(Context context, ArrayList<RouteRealm> infoTrailModels, Boolean initialized) {
        this.context = context;
        this.infoTrailModels = infoTrailModels;
        this.initialized = initialized;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new NormalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int pos = position;
        ((NormalViewHolder) holder).timeStart.setText(infoTrailModels.get(pos).getTimeStart());
        ((NormalViewHolder) holder).timeTotal.setText(infoTrailModels.get(pos).getTimeTotal());
        ((NormalViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailTrailActivity.class);
                intent.putExtra("totalTime",infoTrailModels.get(pos).getTimeTotal());
                intent.putExtra("totalDistance",infoTrailModels.get(pos).getDistanceTotal());
                intent.putExtra("avgBPM",infoTrailModels.get(pos).getBPM());
                intent.putExtra("kkal",infoTrailModels.get(pos).getKkal());
                intent.putExtra("avgVelocity",infoTrailModels.get(pos).getAvgVelocity());
                intent.putExtra("startDate",infoTrailModels.get(pos).getDateStart());
                intent.putExtra("mapURI",infoTrailModels.get(pos).getMapURI());
                intent.putExtra("diff",infoTrailModels.get(pos).getDiff());
                intent.putExtra("isInit",initialized);
                initialized = true;
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoTrailModels.size();
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView timeStart, timeTotal;
        CardView cardView;
        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            timeStart = itemView.findViewById(R.id.textTimeStart);
            timeTotal = itemView.findViewById(R.id.textTime);
            cardView = itemView.findViewById(R.id.cardTrail);
        }
    }

}
