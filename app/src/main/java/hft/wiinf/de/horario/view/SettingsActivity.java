package hft.wiinf.de.horario.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import hft.wiinf.de.horario.R;


public class SettingsActivity extends Fragment {

    private static final String TAG = "SettingFragmentActivity";
    Button button_settings, button_support, button_copyright, button_feedback;
    RelativeLayout rLayout_main, rLayout_settings, rLayout_support, rLayout_copyright, rLayout_feedback;

    public SettingsActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_settings, container, false);
        return view;
    }

    //Method will be called directly after View is created
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //Initialize all Gui-Elements
        button_settings = (Button) view.findViewById(R.id.settings_button_settings);
        button_support = (Button) view.findViewById(R.id.settings_button_support);
        button_copyright = (Button) view.findViewById(R.id.settings_button_copyright);
        button_feedback = (Button) view.findViewById(R.id.settings_button_feedback);

        rLayout_main = (RelativeLayout) view.findViewById(R.id.settings_relativeLayout_main);
        rLayout_copyright = (RelativeLayout) view.findViewById(R.id.settings_relativeLayout_copyright);
        rLayout_feedback = (RelativeLayout) view.findViewById(R.id.settings_relativeLayout_feedback);
        rLayout_settings = (RelativeLayout) view.findViewById(R.id.settings_relativeLayout_settings);
        rLayout_support = (RelativeLayout) view.findViewById(R.id.settings_relativeLayout_support);

        //Everything that needs to happen after click on "Settings" button
        //set Visibility of mainLayout to Gone and settingsLayout to Visible
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLayout_main.setVisibility(View.GONE);
                rLayout_settings.setVisibility(View.VISIBLE);
            }
        });

        //Everything that needs to happen after click on "Feedback" button
        //set Visibility of mainLayout to Gone and FeedbackLayout to Visible
        button_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLayout_main.setVisibility(View.GONE);
                rLayout_feedback.setVisibility(View.VISIBLE);
            }
        });

        //Everything that needs to happen after click on "Copyright" button
        //set Visibility of mainLayout to Gone and copyrightLayout to Visible
        button_copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLayout_main.setVisibility(View.GONE);
                rLayout_copyright.setVisibility(View.VISIBLE);
            }
        });

        //Everything that needs to happen after click on "Support" button
        //set Visibility of mainLayout to Gone and supportLayout to Visible
        button_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLayout_main.setVisibility(View.GONE);
                rLayout_support.setVisibility(View.VISIBLE);
            }
        });
    }
}
