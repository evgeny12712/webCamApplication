package Driving;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.webcamapplication.R;

import MainWindow.MainActivity;

public class AutoStopDialog extends AppCompatDialogFragment {
    private static final String TAG = "AutoStopDialog";
    private static long AUTO_STOP_TIMER_1 = 60000;
    private static final long TIMER_INTERVAL = 1000;
    private TextView timerTv;
    private CountDownTimer countDownTimer;
    private AutoStopDialogListener autoStopDialogListener;
    private long millisecondsUntilDone;
    public AutoStopDialog(long millisecondsUntilDone) {
        this.millisecondsUntilDone = millisecondsUntilDone;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.auto_stop_dialog, null);
        timerTv = view.findViewById(R.id.timer);
        timerTv.setText("60");
        autoStopDialogListener = (AutoStopDialogListener) getTargetFragment();
        //Setting up the timer to auto stop.

        countDownTimer = new CountDownTimer(millisecondsUntilDone, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTv.setText("" + millisUntilFinished / 1000);
                Log.d(TAG, "onTick: " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                //when timer finish close the activity and move to main
                countDownTimer.cancel();
                countDownTimer = null;
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }.start();

        builder.setView(view)
                .setTitle("Recording will stop in : ")
                .setNegativeButton("Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                        autoStopDialogListener.updateAlreadyStarted(false);
                    }
                });
        return builder.create();
    }

    public interface AutoStopDialogListener {
        void updateAlreadyStarted(boolean isAlreadyStarted);
    }

    public void stopAutoStopDialogCounter() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
