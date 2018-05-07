package hft.wiinf.de.horario.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hft.wiinf.de.horario.R;

public class OverviewAdapter extends ArrayAdapter<OverviewListItems>{

    Context context;
    int resource;

    public OverviewAdapter(@NonNull Context context, int resource, @NonNull List<OverviewListItems> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String date = getItem(position).getDate();
        ArrayList<String> list = getItem(position).toStringList();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView tvDate = convertView.findViewById(R.id.adapterTvDate);
        ListView lvItem = convertView.findViewById(R.id.adapterLvItems);

        tvDate.setText(date);
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, list);
        lvItem.setAdapter(adapter);

        return convertView;
    }
}
