package hft.wiinf.de.horario.controller;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.view.ParticipantsListFragment;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<String> data;
    private static LayoutInflater inflater;

    public LazyAdapter(Activity a, ArrayList<String> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        view = inflater.inflate(R.layout.list_row, null);

        TextView participant = (TextView) view.findViewById(R.id.participantName); // participant
        ImageView presenceIndicator = (ImageView) view.findViewById(R.id.presenceIndicator);
        String participantRow;
        participantRow = data.get(position);

        // Setting all values in listview
        if (participantRow.charAt(0) == 'Y') {
            participant.setText(participantRow.substring(2));
            presenceIndicator.setImageResource(R.drawable.ic_fiber_manual_record_green_24dp);
        } else {
            participant.setText(participantRow.substring(2));
            presenceIndicator.setImageResource(R.drawable.ic_fiber_manual_record_red_24dp);
        }

        return view;
    }
}