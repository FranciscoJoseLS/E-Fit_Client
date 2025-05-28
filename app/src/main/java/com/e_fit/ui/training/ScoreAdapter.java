package com.e_fit.ui.training;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.e_fit.R;
import com.e_fit.enities.Score;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class ScoreAdapter extends ArrayAdapter<Score> {

    private Context mContext;
    private List<Score> mScores;
    public ScoreAdapter(Context context, List<Score> scores) {
        super(context, 0, scores);
        this.mContext = context;
        this.mScores = scores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Score currentScore = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_score, parent, false);
            holder = new ViewHolder();
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            holder.tvLoad = convertView.findViewById(R.id.tvLoad);
            holder.tvComments = convertView.findViewById(R.id.tvComments);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (currentScore != null) {
            //Pongo la fcha parseada
            String rawDate = currentScore.getRealizationDate().toString();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    LocalDate date = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    holder.tvDate.setText(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())));
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                    holder.tvDate.setText(rawDate);
                }
            } else
                holder.tvDate.setText(rawDate);

            if (currentScore.getComments() != null && !currentScore.getComments().trim().isEmpty()) {
                holder.tvComments.setText("Comentarios: " + currentScore.getComments());
                holder.tvComments.setVisibility(View.VISIBLE);
            } else
                holder.tvComments.setVisibility(View.GONE);

            //Split the load
            String load = currentScore.getLoadValue();
            load = load.replace("[","");
            load = load.replace("]","");
            load = load.replace("*","Kgs *");
            load = load.replace("/","Reps \n");
            holder.tvLoad.setText(load+"Reps");
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvDate;
        TextView tvLoad;
        TextView tvComments;
    }

    public void updateScores(List<Score> newScores) {
        mScores.clear();
        mScores.addAll(newScores);
        notifyDataSetChanged();
    }
}
