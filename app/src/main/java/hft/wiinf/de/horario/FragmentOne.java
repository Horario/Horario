package hft.wiinf.de.horario;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class FragmentOne extends Fragment {
    private static final String TAG = "Tab1Fragment";
    private Button btnTEST1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        btnTEST1 = view.findViewById(R.id.btnTEST1);
        btnTEST1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Button1 wurde gedrückt", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
