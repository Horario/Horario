package hft.wiinf.de.horario.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;

public class SettingsActivity extends Fragment {

    private static final String TAG = "SettingFragmentActivity";
    Button button_settings, button_support, button_copyright, button_feedback;
    RelativeLayout rLayout_settings_helper, rLayout_settings_button;
    Person person;

    public SettingsActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    //Method will be called directly after View is created
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        try {
            person = PersonController.getPersonWhoIam();
        } catch (NullPointerException e) {
            Log.d(TAG, "SettingsActivity:" + e.getMessage());
        }

        //Initialize all Gui-Elements
        button_settings = (Button) view.findViewById(R.id.settings_button_settings);
        button_support = (Button) view.findViewById(R.id.settings_button_support);
        button_copyright = (Button) view.findViewById(R.id.settings_button_copyright);
        button_feedback = (Button) view.findViewById(R.id.settings_button_feedback);

        rLayout_settings_helper = view.findViewById(R.id.settings_relativeLayout_helper);
        rLayout_settings_button = view.findViewById(R.id.settings_relativeLayout_button);

        //onCLickListener for every button in settings fragment
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                //settings_relativeLayout_helper: in this Layout all other layouts will be uploaded
                fr.replace(R.id.settings_relativeLayout_helper, new SettingsSettingsFragment());
                fr.addToBackStack(null);
                fr.commit();
                //set settings-buttons invisible and new Layout_helper visible
                rLayout_settings_button.setVisibility(View.GONE);
                rLayout_settings_helper.setVisibility(View.VISIBLE);
            }
        });

        button_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.settings_relativeLayout_helper, new SettingsSupportFragment());
                fr.addToBackStack(null);
                fr.commit();
                rLayout_settings_button.setVisibility(View.GONE);
                rLayout_settings_helper.setVisibility(View.VISIBLE);
            }
        });

        button_copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.settings_relativeLayout_helper, new SettingsCopyrightFragment());
                fr.addToBackStack(null);
                fr.commit();
                rLayout_settings_button.setVisibility(View.GONE);
                rLayout_settings_helper.setVisibility(View.VISIBLE);
            }
        });

        button_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.settings_relativeLayout_helper, new SettingsFeedbackFragment());
                fr.addToBackStack(null);
                fr.commit();
                rLayout_settings_button.setVisibility(View.GONE);
                rLayout_settings_helper.setVisibility(View.VISIBLE);
            }
        });
    }
}
