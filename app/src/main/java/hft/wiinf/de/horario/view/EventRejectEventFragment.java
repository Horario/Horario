package hft.wiinf.de.horario.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import hft.wiinf.de.horario.R;

public class EventRejectEventFragment extends Fragment{

    public EventRejectEventFragment() {

    }

    private static final String TAG = "EventRejectEvent";
    EditText reason_for_rejection;
    TextView reject_event_header, reject_event_description;
    Spinner spinner_reason;
    Button button_reject_event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_reject_event, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize GUI-Elements
        reason_for_rejection = (EditText) view.findViewById(R.id.reject_event_editText_note);
        reject_event_description = (TextView) view.findViewById(R.id.reject_event_textView_description);
        reject_event_header = (TextView) view.findViewById(R.id.reject_event_textView_header);
        spinner_reason = (Spinner) view.findViewById(R.id.reject_event_spinner_reason);
        button_reject_event = (Button) view.findViewById(R.id.reject_event_button_reject);

        ArrayAdapter reasonAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.reason_for_rejection, android.R.layout.simple_spinner_item);
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_reason.setAdapter(reasonAdapter);
    }
}
