package hft.wiinf.de.horario.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import hft.wiinf.de.horario.R;

//TODO Kommentieren und Java Doc Info Schreiben
public class CalendarActivity extends Fragment {
    private static final String TAG = "CalendarFragmentActivity";
    private Button btnTEST2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_calendar, container, false);
        btnTEST2 = view.findViewById(R.id.btnTEST2);
        btnTEST2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Button2 wurde gedrückt", Toast.LENGTH_SHORT).show();
            }
        });





        return view;
    }
}
