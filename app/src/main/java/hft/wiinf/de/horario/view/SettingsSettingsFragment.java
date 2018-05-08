package hft.wiinf.de.horario.view;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hft.wiinf.de.horario.R;
import hft.wiinf.de.horario.controller.PersonController;
import hft.wiinf.de.horario.model.Person;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsSettingsFragment extends Fragment {
    private static final String TAG = "SettingFragmentActivity";
    EditText editTextUsername;
    Person person;

    public SettingsSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_settings, container, false);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextUsername = (EditText) view.findViewById(R.id.settings_settings_editText_username);

        //If username is already saved -> pull it from db an set Text equal to it
        if (person != null) {
            editTextUsername.setText(person.getName());
        }

        if (person == null) {
            try {
                person = PersonController.getPersonWhoIam();
            } catch (NullPointerException e) {
                Log.d(TAG, "SettingsActivity:" + e.getMessage());
            }
        }
        if (person != null) {
            editTextUsername.setText(person.getName());
        }

        //Make EditText-Field editable
        editTextUsername.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextUsername.setFocusable(true);
                editTextUsername.setFocusableInTouchMode(true);
                return false;
            }
        });

        //Everything that needs to happen after Username was written in the EditText-Field
        editTextUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String inputText = v.getText().toString();

                //RegEx: no whitespace at the beginning
                Pattern pattern_username = Pattern.compile("^([\\S]).*");
                Matcher matcher_username = pattern_username.matcher(inputText);

                if (actionId == EditorInfo.IME_ACTION_DONE && matcher_username.matches()) {
                    if (person != null) {
                        person.setName(inputText);
                        PersonController.addPersonMe(person);
                    } else {
                        //ToDo: get correct phoneNumber
                        person = new Person(true, "007", inputText);
                        PersonController.addPersonMe(person);
                    }
                    Toast toast = Toast.makeText(view.getContext(), R.string.thanksForUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    editTextUsername.setFocusable(false);
                    editTextUsername.setFocusableInTouchMode(false);
                } else {
                    Toast toast = Toast.makeText(view.getContext(), R.string.noValidUsername, Toast.LENGTH_SHORT);
                    toast.show();
                    if (person != null) {
                        editTextUsername.setText(person.getName());
                    }
                    return true;
                }
                return false;
            }
        });

    }
}