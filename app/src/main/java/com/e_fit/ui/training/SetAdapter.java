    package com.e_fit.ui.training;

    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.text.TextUtils;
    import android.text.TextWatcher;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.CheckBox;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.CompoundButton;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.e_fit.R;
    import com.e_fit.enities.SetType;
    import com.e_fit.enities.SetTypeParcelable;

    import java.util.List;

    public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder> {

        private Context mContext;
        private List<SetTypeParcelable> setsList;
        private OnSetCheckedListener setCheckedListener;
        private int parentExercisePosition;

        public SetAdapter(Context context, List<SetTypeParcelable> setsList, OnSetCheckedListener listener, int exercisePosition) {
            this.mContext = context;
            this.setsList = setsList;
            this.setCheckedListener = listener;
            this.parentExercisePosition = exercisePosition;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNset;
            EditText tvKg;
            EditText tvRep;
            CheckBox cbDone;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNset = itemView.findViewById(R.id.tvNset);
                tvKg = itemView.findViewById(R.id.tvKg);
                tvRep = itemView.findViewById(R.id.tvRep);
                cbDone = itemView.findViewById(R.id.cbDone);

                //DESCOMENTAR SOLO PARA PRUEBAS
                /*tvKg.setText("50");
                tvRep.setText("10");
                cbDone.setChecked(true);*/
            }
        }

        @Override
        public void onBindViewHolder(@NonNull SetAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            SetTypeParcelable setType = setsList.get(position);
            holder.tvNset.setText(setType.getSetType() == SetType.STANDARD ? String.valueOf(position + 1) : setType.getSetType().name().substring(0,1));
            holder.tvNset.setOnClickListener(i->{
                Toast.makeText(mContext,setType.toString(),Toast.LENGTH_SHORT).show();
            });

            if (holder.tvKg.getTag() instanceof TextWatcher)
                holder.tvKg.removeTextChangedListener((TextWatcher) holder.tvKg.getTag());

            if (holder.tvRep.getTag() instanceof TextWatcher)
                holder.tvRep.removeTextChangedListener((TextWatcher) holder.tvRep.getTag());


            TextWatcher commonTextWatcher = new TextWatcherAdapter() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String currentKgText = holder.tvKg.getText().toString();
                    String currentRepText = holder.tvRep.getText().toString();
                    boolean currentFieldsArePopulated = !TextUtils.isEmpty(currentKgText) && !TextUtils.isEmpty(currentRepText);

                    holder.cbDone.setEnabled(currentFieldsArePopulated);

                    if (!currentFieldsArePopulated && holder.cbDone.isChecked()) {
                        holder.cbDone.setChecked(false);
                        if (setCheckedListener != null)
                            setCheckedListener.onSetChecked(parentExercisePosition, position, false);

                    }
                }
            };

            holder.tvKg.addTextChangedListener(commonTextWatcher);
            holder.tvKg.setTag(commonTextWatcher);

            holder.tvRep.addTextChangedListener(commonTextWatcher);
            holder.tvRep.setTag(commonTextWatcher);


            String kgText = holder.tvKg.getText().toString();
            String repText = holder.tvRep.getText().toString();
            boolean fieldsArePopulated = !TextUtils.isEmpty(kgText) && !TextUtils.isEmpty(repText);
            holder.cbDone.setEnabled(fieldsArePopulated);

            holder.cbDone.setOnCheckedChangeListener(null);
            holder.cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (setCheckedListener != null) {
                        int currentSetPosition = holder.getAdapterPosition();
                        if (currentSetPosition != RecyclerView.NO_POSITION)
                            setCheckedListener.onSetChecked(parentExercisePosition, currentSetPosition, isChecked);
                    }
                }
            });
        }

        @NonNull
        @Override
        public SetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_set_training, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return setsList.size();
        }

        public static abstract class TextWatcherAdapter implements TextWatcher {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(android.text.Editable s) { }
        }
    }