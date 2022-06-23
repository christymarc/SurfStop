package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.surfstop.R;

import java.util.List;

import models.BeachGroup;

public class SpinnerAdapter extends ArrayAdapter<BeachGroup> {

    LayoutInflater layoutInflater;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<BeachGroup> favorite_beaches) {
        super(context, resource, favorite_beaches);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = layoutInflater.inflate(R.layout.activity_custom_spinner, null, true);
        BeachGroup beachGroup = getItem(position);
        TextView tvBeachName = (TextView) rowView.findViewById(R.id.tvBeachName);
        tvBeachName.setText(beachGroup.getKeyGroupName());
        return rowView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_custom_spinner, parent, false);
        }

        BeachGroup beachGroup = getItem(position);
        TextView tvBeachName = convertView.findViewById(R.id.tvBeachName);
        tvBeachName.setText(beachGroup.getKeyGroupName());
        return convertView;
    }
}
