package com.kyloren.tabahiit;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class SettingsFragment extends Fragment {


    private Button btBack, btCancel;
    private EditText etWork, etReady, etRest, etFullRest, etLaps, etSets;
    private String ready, work, rest, fullRest, finalLaps, finalSets;
    View v;

    public SettingsFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        btBack = v.findViewById(R.id.btBack);
        btCancel = v.findViewById(R.id.btCancel);
        etWork = v.findViewById(R.id.etWork);
        etRest = v.findViewById(R.id.etRest);
        etReady = v.findViewById(R.id.etReady);
        etFullRest = v.findViewById(R.id.etFullRest);
        etLaps = v.findViewById(R.id.etLaps);
        etSets = v.findViewById(R.id.etSets);


        ready = String.valueOf(sharedPref.getInt("ready", 10));
        work = String.valueOf(sharedPref.getInt("work", 30));
        rest = String.valueOf(sharedPref.getInt("rest", 20));
        fullRest = String.valueOf(sharedPref.getInt("ready", 20));
        finalLaps = String.valueOf(sharedPref.getInt("ready", 2));
        finalSets = String.valueOf(sharedPref.getInt("ready", 3));

        etReady.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "60")});
        etWork.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "600")});
        etRest.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "600")});
        etFullRest.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "600")});
        etLaps.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "99")});
        etSets.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "99")});


        etReady.setText(ready);
        etWork.setText(work);
        etRest.setText(rest);
        etFullRest.setText(fullRest);
        etSets.setText(finalSets);
        etLaps.setText(finalLaps);


        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etReady.getText().toString().equals("") || etWork.getText().toString().equals("") || etRest.getText().toString().equals("") ||
                        etFullRest.getText().toString().equals("") || etLaps.getText().toString().equals("") ||
                        etSets.getText().toString().equals("")){

                    Snackbar.make(view, R.string.complete_all, Snackbar.LENGTH_SHORT)
                            .show();

                }else {

                    ready = etReady.getText().toString();
                    work = etWork.getText().toString();
                    rest = etRest.getText().toString();
                    fullRest = etFullRest.getText().toString();
                    finalLaps = etLaps.getText().toString();
                    finalSets = etSets.getText().toString();

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("ready", Integer.parseInt(ready));
                    editor.putInt("work", Integer.parseInt(work));
                    editor.putInt("rest", Integer.parseInt(rest));
                    editor.putInt("finalSets", Integer.parseInt(finalSets));
                    editor.putInt("finalLaps", Integer.parseInt(finalLaps));
                    editor.putInt("fullRest", Integer.parseInt(fullRest));
                    editor.commit();


                    ((MainActivity)getActivity()).refresh();

                    FragmentManager fm = getFragmentManager();
                    fm.popBackStack();
                }

            }
        });


        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });




    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
