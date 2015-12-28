package org.faudroids.werewolf.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.faudroids.werewolf.R;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_game_setup)
public class GameSetupActivity extends AbstractActivity {

    @InjectView(R.id.cnf_autoassign_btn) private Button mAutoAssignButton;
    @InjectView(R.id.cnf_tobeassigned_label) private TextView mToBeAssignedTextView;

    @InjectView(R.id.cnf_player_count_picker) private NumberPicker mPlayerCountPicker;

    @InjectView(R.id.cnf_werewolf_count_picker) private NumberPicker mWerewolfCountPicker;
    @InjectView(R.id.cnf_villager_count_picker) private NumberPicker mVillagerCountPicker;
    @InjectView(R.id.cnf_seer_count_picker) private NumberPicker mSeerCountPicker;
    @InjectView(R.id.cnf_doctor_count_picker) private NumberPicker mDoctorCountPicker;
    @InjectView(R.id.cnf_hunter_count_picker) private NumberPicker mHunterCountPicker;
    @InjectView(R.id.cnf_witch_count_picker) private NumberPicker mWitchCountPicker;
    @InjectView(R.id.cnf_priest_count_picker) private NumberPicker mPriestCountPicker;
    @InjectView(R.id.cnf_amor_count_picker) private NumberPicker mAmorCountPicker;

    @InjectView(R.id.cnf_start_btn) private Button mStartButton;

    private final int DEFAULT_PLAYER_COUNT = 10;

    private List<NumberPicker> allPickers;
    private List<NumberPicker> specialRolePickers;
    private int mToBeAssignedCount = DEFAULT_PLAYER_COUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allPickers = new ArrayList<>();
        allPickers.add(mWerewolfCountPicker);
        allPickers.add(mVillagerCountPicker);
        allPickers.add(mSeerCountPicker);
        allPickers.add(mDoctorCountPicker);
        allPickers.add(mHunterCountPicker);
        allPickers.add(mWitchCountPicker);
        allPickers.add(mPriestCountPicker);
        allPickers.add(mAmorCountPicker);

        specialRolePickers = new ArrayList<>();
        specialRolePickers.add(mSeerCountPicker);
        specialRolePickers.add(mDoctorCountPicker);
        specialRolePickers.add(mHunterCountPicker);
        specialRolePickers.add(mWitchCountPicker);
        specialRolePickers.add(mPriestCountPicker);
        specialRolePickers.add(mAmorCountPicker);


        for(NumberPicker np : allPickers){
            np.setMinValue(0);
            np.setWrapSelectorWheel(false);
        }

        mAutoAssignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int playerCount = mPlayerCountPicker.getValue();
                int villagersCount = playerCount / 2 + playerCount % 2;
                int werewolfCount = playerCount / 2;

                for (NumberPicker np : specialRolePickers) {
                    np.setMinValue(0);
                    if (villagersCount > 2) {
                        villagersCount--;
                        np.setMaxValue(1);
                        np.setValue(1);
                    } else {
                        np.setMaxValue(0);
                        np.setValue(0);
                    }
                    np.setWrapSelectorWheel(false);
                }

                mWerewolfCountPicker.setMinValue(0);
                mWerewolfCountPicker.setMaxValue(werewolfCount);
                mWerewolfCountPicker.setValue(werewolfCount);
                mWerewolfCountPicker.setWrapSelectorWheel(false);

                mVillagerCountPicker.setMinValue(0);
                mVillagerCountPicker.setMaxValue(villagersCount);
                mVillagerCountPicker.setValue(villagersCount);
                mVillagerCountPicker.setWrapSelectorWheel(false);


                mToBeAssignedCount = 0;
                mToBeAssignedTextView.setText("" + mToBeAssignedCount);
            }


        });

        // default player count
        mToBeAssignedTextView.setText("" + DEFAULT_PLAYER_COUNT);
        mPlayerCountPicker.setMinValue(3);
        mPlayerCountPicker.setMaxValue(100);
        mPlayerCountPicker.setValue(DEFAULT_PLAYER_COUNT);
        mPlayerCountPicker.setWrapSelectorWheel(false);

        NumberPicker.OnValueChangeListener listener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int playerCount = mPlayerCountPicker.getValue();

                int assignedPlayerCount = 0;
                for(NumberPicker np : allPickers){
                    assignedPlayerCount += np.getValue();
                }

                mToBeAssignedCount = playerCount - assignedPlayerCount;
                mToBeAssignedTextView.setText("" + mToBeAssignedCount);

                for(NumberPicker np : allPickers){
                    np.setMinValue(0);
                    np.setMaxValue(np.getValue() + mToBeAssignedCount);
                    np.setWrapSelectorWheel(false);
                }
            }
        };

        mPlayerCountPicker.setOnValueChangedListener(listener);

        for(NumberPicker np : allPickers){
            np.setOnValueChangedListener(listener);
        }

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mToBeAssignedCount > 0){
                    new AlertDialog.Builder(GameSetupActivity.this)
                            .setMessage("There are " + mToBeAssignedCount + " players left to be assiged. Proceed anyway?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //hExampleActivity.this.finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    Toast.makeText(GameSetupActivity.this, "Game Starts Now!", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}