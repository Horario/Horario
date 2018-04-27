package hft.wiinf.de.horario.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import hft.wiinf.de.horario.R;

//TODO Kommentieren und Java Doc Info Schreiben
public class NewEventActivity extends Fragment {
    private static final String TAG = "NewEventFragmentActivity";
    private Button btnTEST1;
    FloatingActionButton fabOpenClose, fabGoToScanner, fabCreateEvent;
    Animation ActionButtonOpen, ActionButtonClose, ActionButtonRotateRight, ActionButtonRotateLeft;
    boolean isActionButtonOpen = false;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_new_event, container, false);
        btnTEST1 = view.findViewById(R.id.btnTEST1);
        btnTEST1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Button1 wurde gedrückt", Toast.LENGTH_SHORT).show();
            }
        });

        fabOpenClose = (FloatingActionButton) view.findViewById(R.id.floatingActionButtonOpenClose);
        fabGoToScanner = (FloatingActionButton) view.findViewById(R.id.floatingActionButtonGoToScanner);
        fabCreateEvent = (FloatingActionButton) view.findViewById(R.id.floatingActionButtonCreateEvent);

        ActionButtonOpen = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonopen);
        ActionButtonClose = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonclose);
        ActionButtonRotateRight = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonrotateright);
        ActionButtonRotateLeft = AnimationUtils.loadAnimation(getContext(), R.anim.actionbuttonrotateleft);

        fabOpenClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActionButtonOpen) {
                    fabGoToScanner.startAnimation(ActionButtonClose);
                    fabCreateEvent.startAnimation(ActionButtonClose);
                    fabOpenClose.startAnimation(ActionButtonRotateLeft);
                    fabGoToScanner.setClickable(false);
                    fabCreateEvent.setClickable(false);
                    isActionButtonOpen = false;

                } else {
                    fabGoToScanner.startAnimation(ActionButtonOpen);
                    fabCreateEvent.startAnimation(ActionButtonOpen);
                    fabOpenClose.startAnimation(ActionButtonRotateRight);
                    fabGoToScanner.setClickable(true);
                    fabCreateEvent.setClickable(true);
                    isActionButtonOpen = true;

                }


            }
        });

        return view;
    }
}
