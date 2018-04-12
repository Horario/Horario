package hft.wiinf.de.horario.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import hft.wiinf.de.horario.R;


public class SettingActivity extends Fragment {

    private static final String TAG = "SettingFragmentActivity";
    Button button_settings, button_support, button_copyright, button_feedback;
    RelativeLayout rLayout_main, rLayout_settings, rLayout_support, rLayout_copyright, rLayout_feedback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_settings, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Initialize all Gui-Elements
        button_settings = (Button)view.findViewById(R.id.settings_button_settings);
        button_support = (Button)view.findViewById(R.id.settings_button_support);
        button_copyright = (Button)view.findViewById(R.id.settings_button_copyright);
        button_feedback = (Button)view.findViewById(R.id.settings_button_feedback);

        rLayout_main = (RelativeLayout)view.findViewById(R.id.settings_relativeLayout_main);
        rLayout_copyright = (RelativeLayout)view.findViewById(R.id.settings_relativeLayout_copyright);
        rLayout_feedback = (RelativeLayout)view.findViewById(R.id.settings_relativeLayout_feedback);
        rLayout_settings = (RelativeLayout)view.findViewById(R.id.settings_relativeLayout_settings);
        rLayout_support = (RelativeLayout)view.findViewById(R.id.settings_relativeLayout_support);

        //Everything that needs to happen after click on "Settings" button
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLayout_main.setVisibility(View.GONE);
                rLayout_settings.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rLayout_main.setVisibility(View.VISIBLE);
        rLayout_settings.setVisibility(View.GONE);
    }
}
