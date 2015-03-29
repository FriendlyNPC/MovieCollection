package com.school.comp3717.moviecollection;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class About extends Fragment {

    private static final String TEAM_EMAIL = "team.kgy@gmail.com";

    public About() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        Button feedbackButton = (Button) rootView.findViewById(R.id.feedbackButton);
        Button reportButton   = (Button) rootView.findViewById(R.id.reportButton);
        Button shareButton    = (Button) rootView.findViewById(R.id.shareButton);

        setEmailButton(feedbackButton,
                       TEAM_EMAIL,
                       getResources().getString(R.string.feedback_subject),
                       "");
        setEmailButton(reportButton,
                       TEAM_EMAIL,
                       getResources().getString(R.string.report_subject),
                       "");
        setEmailButton(shareButton,
                       "",
                       getResources().getString(R.string.share_subject),
                       getResources().getString(R.string.share_body));

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.about_header);
    }

    public void setEmailButton(Button button,
                               final String mailTo,
                               final String subject,
                               final String body) {
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode(mailTo) +
                        "?subject=" + Uri.encode(subject) +
                        "&body=" + Uri.encode(body);
                Uri uri = Uri.parse(uriText);

                send.setData(uri);
                startActivity(Intent.createChooser(send, "Send mail..."));
            }
        });
    }

}
