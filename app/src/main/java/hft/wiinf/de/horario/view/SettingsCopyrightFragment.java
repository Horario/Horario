package hft.wiinf.de.horario.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hft.wiinf.de.horario.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCopyrightFragment extends Fragment {


    public SettingsCopyrightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_copyright, container, false);


        return view;
    }

}
