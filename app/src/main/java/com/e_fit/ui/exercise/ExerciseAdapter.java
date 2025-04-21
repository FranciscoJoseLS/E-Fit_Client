package com.e_fit.ui.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.e_fit.R;
import com.e_fit.enities.Exercise;

import java.io.InputStream;
import java.util.ArrayList;
import android.graphics.drawable.Drawable;

public class ExerciseAdapter extends ArrayAdapter<Exercise> {
    private ArrayList<Exercise> exercises;

    public ExerciseAdapter(Context c, ArrayList<Exercise> exercises){
        super(c, R.layout.exercise_item_view,exercises);
        this.exercises=exercises;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int posicion, View convertView, @NonNull ViewGroup parent){
        LayoutInflater mostrado= LayoutInflater.from(getContext());
        @SuppressLint("ViewHolder")
        View view=mostrado.inflate(R.layout.exercise_item_view, parent, false);
        //Muestro los datos
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        tvName.setText(exercises.get(posicion).getName());
        TextView tvMuscularGroup = (TextView) view.findViewById(R.id.tvMuscularGroup);
        tvMuscularGroup.setText(exercises.get(posicion).getMuscularGroup().toString());
        //Imágenes
        ImageView imageView = (ImageView) view.findViewById(R.id.ivExercise);
        String imageName = exercises.get(posicion).getName()+".png";
        Drawable drawable = getDrawableFromAssets(view.getContext(), imageName);
        imageView.setImageDrawable(drawable);

        //Incorporo click en la vista
        Button btn = (Button) view.findViewById(R.id.btnVer);
        btn.setOnClickListener(it->{
            Intent i = new Intent(getContext(), ExerciseView.class);
            i.putExtra("exercise",exercises.get(posicion));
            getContext().startActivity(i);
        });

        return view;
    }

    public Drawable getDrawableFromAssets(Context context, String imageName) {
        try {
            InputStream ims = context.getAssets().open(imageName);
            Drawable drawable = Drawable.createFromStream(ims, null);
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateData(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return exercises.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
