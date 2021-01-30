package Settings;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webcamapplication.R;

public class SettingsActivity extends AppCompatActivity {
    private Button howToStartBtn;
    private Button resetFreqBtn;
    private Button fragmentationSizeBtn;
    private Button notificationsBtn;
    private Button soundBtn;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        howToStartBtn = (Button)findViewById(R.id.howToStartBtn);
        howToStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("howToStart");
            }
        });

        resetFreqBtn = (Button)findViewById(R.id.num_of_files_btn);
        resetFreqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("resetFreq");
            }
        });

        fragmentationSizeBtn = (Button)findViewById(R.id.fragmentationBtn);
        fragmentationSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("fragSize");
            }
        });

        notificationsBtn = (Button)findViewById(R.id.notificationsBtn);
        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("notifications");
            }
        });

        soundBtn = (Button)findViewById(R.id.soundBtn);
        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("sound");
            }
        });



    }

    public void openDialog(String dialogType) {
        switch(dialogType) {
            case "howToStart":
                HowToStartDialog howToStartDialog = new HowToStartDialog();
                howToStartDialog.show(getSupportFragmentManager(), "How to start dialog");
                break;
            case "resetFreq":
                numOfFilesDialog numOfFilesDialog = new numOfFilesDialog();
                numOfFilesDialog.show(getSupportFragmentManager(), "Reset frequency dialog");
                break;
            case "fragSize":
                FragmentationDialog fragmentationDialog = new FragmentationDialog();
                fragmentationDialog.show(getSupportFragmentManager(), "Fragmentation size dialog");
                break;
            case "notifications":
                NotificationsDialog notificationsDialog = new NotificationsDialog();
                notificationsDialog.show(getSupportFragmentManager(), "Notifications Dialog");
                break;
            case "sound":
                SoundDialog soundDialog = new SoundDialog();
                soundDialog.show(getSupportFragmentManager(), "Sound Dialog");
                break;
            default:
                Toast.makeText(this, "SOMETHING WENT WRONG!!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void initSettings(Context context) {

    }
}
