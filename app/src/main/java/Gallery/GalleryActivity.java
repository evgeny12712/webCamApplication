package Gallery;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import Gallery.Gallery.TemporaryFiles.GalleryTemporaryFilesFragment;
import Gallery.Pictures.GalleryPicturesFragment;
import Gallery.SavedFiles.GallerySavedFilesFragment;
import MainWindow.MainActivity;
import com.example.webcamapplication.R;

import static Gallery.Items.*;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity";
    private Button btnSavedVideos;
    private Button btnPictures;
    private Button btnHome;
    private Button btnVideos;
    private Resources res;
    private Toolbar toolbar;
    private Toolbar selectionToolBar;
    private Fragment temporaryFiles;
    private Fragment savedFiles;
    private Fragment images;
    private String chosenFragment;
    public static String[] fileTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toast.makeText(this, "onCreate" , Toast.LENGTH_SHORT).show();

        btnHome = (Button)findViewById(R.id.btnHome);
        btnVideos = (Button)findViewById(R.id.btnVideos);
        btnSavedVideos = (Button) findViewById(R.id.btnSavedVideos);
        btnPictures = (Button) findViewById(R.id.btnPictures);


        temporaryFiles = new GalleryTemporaryFilesFragment();
        savedFiles = new GallerySavedFilesFragment();
        images = new GalleryPicturesFragment();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        selectionToolBar = (Toolbar) findViewById(R.id.toolbar_temporary_selection);
        fileTypes = getResources().getStringArray(R.array.fileTypes);
        setSupportActionBar(toolbar);

        loadFiles(getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileTypes[0], getApplicationContext());
        loadFiles(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileTypes[1], getApplicationContext());
        loadFiles(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileTypes[2], getApplicationContext());

        if(!getIncomingIntent()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, temporaryFiles)
                    .commit();
        }

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(intent);
            }
        });

        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, temporaryFiles)
                        .commit();
            setButtonPressed(fileTypes[0]);
            }
        });

        btnSavedVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, savedFiles)
                        .commit();
                setButtonPressed(fileTypes[1]);
            }
        });

        btnPictures.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, images)
                        .commit();
                setButtonPressed(fileTypes[2]);
            }
        });

    }


    public void setButtonPressed(String buttonName) {
        switch (buttonName) {
            case "temporary videos":
                btnVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_bold_button_border));
                btnSavedVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                btnPictures.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                break;
            case "saved videos":
                btnVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                btnSavedVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_bold_button_border));
                btnPictures.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                break;
            case "images":
                btnVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                btnSavedVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                btnPictures.setBackground(this.getResources().getDrawable(R.drawable.gallery_bold_button_border));
                break;
        }
    }

    private boolean getIncomingIntent() {
        if(getIntent().hasExtra("fragment")) {
            chosenFragment = getIntent().getStringExtra("fragment");
                setButtonPressed(chosenFragment);
                switch (chosenFragment) {
                    case "temporary videos":
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout, temporaryFiles)
                                .commit();
                        return true;
                    case "saved videos":
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout, savedFiles)
                                .commit();
                        return true;
                    case "images":
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout, images)
                                .commit();
                        return true;
                }
            }
        return false;
    }

}
