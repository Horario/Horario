package hft.wiinf.de.horario.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import hft.wiinf.de.horario.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCopyrightFragment extends Fragment {
    private TextView settings_copyright_privacyPolicy;

    public SettingsCopyrightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_copyright, container, false);

        // Initial GUI
        settings_copyright_privacyPolicy = view.findViewById(R.id.settings_copyright_textView_privacyPolicy);

        // Show always Scrollbar on Description TextView
        settings_copyright_privacyPolicy.setMovementMethod(new ScrollingMovementMethod());

        // Get the TXT File from res/raw Folder an put it into the TextView
        InputStream inputStream = getResources().openRawResource(R.raw.privacy_policy_eng);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        settings_copyright_privacyPolicy.setText(byteArrayOutputStream.toString());


        return view;
    }

}
