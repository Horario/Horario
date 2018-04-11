package hft.wiinf.de.horario;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class FragmentThree extends Fragment {
    private static final String TAG = "Tab3Fragmen";
    private Button btnTEST3;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        btnTEST3 = (Button)view.findViewById(R.id.btnTEST3);
        btnTEST3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Button3 wurde gedrückt", Toast.LENGTH_SHORT).show();
            }
        });





        return view;
    }
}
