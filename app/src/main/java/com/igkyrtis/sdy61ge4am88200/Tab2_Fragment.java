package com.igkyrtis.sdy61ge4am88200;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class Tab2_Fragment extends Fragment {

    private static final String TAG = "Tab1Fragment";

    private Button mSignInButton;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_fragment, container, false);

        mSignInButton = (Button) rootView.findViewById(R.id.button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String displayToast = new String("Congratulations! Thank you for registering!");
                Toast.makeText(getContext(), displayToast, Toast.LENGTH_SHORT).show();

            }
        });


        return rootView;
    }


}
