package hft.wiinf.de.horario.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import hft.wiinf.de.horario.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFeedbackFragment extends Fragment {
    private WebView settings_feedback_webView;

    public SettingsFeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_feedback, container, false);

        // Initial GUI
        settings_feedback_webView = view.findViewById(R.id.settings_feedback_webView);

        // Load the HTML file from the Assets Folder into a WebView.
        settings_feedback_webView.loadUrl("file:///android_asset/settings_feedback");

        return view;
    }

}
