package com.school.comp3717.moviecollection;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.MaterialSection;
import it.neokree.materialnavigationdrawer.MaterialSectionListener;


public class MainActivity extends MaterialNavigationDrawer{
    MaterialSection section1, section2, recorder, night, last;

    @Override
    @SuppressWarnings("unchecked") //this.newSection causes warnings with MaterialNavigationDrawer types, but needed for example.
    public void init(Bundle savedInstanceState) {
        // create sections

        section1 = this.newSection("Section 1",new FragmentIndex());

        section2 = this.newSection("Section 2",new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection section) {
                Toast.makeText(MainActivity.this, "Section 2 Clicked", Toast.LENGTH_SHORT).show();

                // deselect section when is clicked
                section.unSelect();
            }
        });
        // recorder section with icon and 10 notifications
        recorder = this.newSection("Recorder",this.getResources().getDrawable(R.drawable.ic_launcher),new FragmentIndex()).setNotifications(10);
        // night section with icon, section color and notifications
        night = this.newSection("Night Section", this.getResources().getDrawable(R.drawable.ic_launcher), new FragmentIndex())
                .setSectionColor(Color.parseColor("#2196f3"),Color.parseColor("#1565c0")).setNotifications(150);
        // night section with section color
        last = this.newSection("Last Section", new FragmentButton()).setSectionColor(Color.parseColor("#ff9800"),Color.parseColor("#ef6c00"));

        // add your sections to the drawer
        this.addSection(section1);
        this.addSection(section2);
        this.addSubheader("Subheader");
        this.addSection(recorder);
        this.addSection(night);
        this.addDivisor();
        this.addSection(last);

        /*
        This keeps the drawer from being open when launched
        material usually will launch the app with the drawer open the first couple
        of times to let users know it uses the material-style drawer.
         */
        this.disableLearningPattern();
    }
}
