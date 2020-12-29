package Gallery;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import Gallery.Gallery.TemporaryFiles.GalleryTemporaryFilesFragment;
import Gallery.Pictures.GalleryPicturesFragment;
import Gallery.SavedFiles.GallerySavedFilesFragment;
import Gallery.SavedFiles.Items;
import MainWindow.MainActivity;
import com.example.webcamapplication.R;

public class GalleryActivity extends AppCompatActivity {
    private Button btnSavedVideos;
    private Button btnPictures;
    private Button btnHome;
    private Button btnVideos;
    private Resources res;
    private Toolbar toolbar;
    private Fragment temporaryFiles;
    private Fragment savedFiles;
    private Fragment pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        btnHome = (Button)findViewById(R.id.btnHome);
        btnVideos = (Button)findViewById(R.id.btnVideos);
        btnSavedVideos = (Button) findViewById(R.id.btnSavedVideos);
        btnPictures = (Button) findViewById(R.id.btnPictures);

        temporaryFiles = new GalleryTemporaryFilesFragment();
        savedFiles = new GallerySavedFilesFragment();
        pictures = new GalleryPicturesFragment();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        res = getResources();
        setSupportActionBar(toolbar);

        Items.loadFiles(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "temporary");
        Items.loadFiles(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "saved");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, temporaryFiles)
                .commit();

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
            setButtonPressed("videos");

            }
        });

        btnSavedVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, savedFiles)
                        .commit();
                setButtonPressed("savedVideos");
            }
        });

        btnPictures.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, pictures)
                        .commit();
                setButtonPressed("pictures");
            }
        });

    }


    public void setButtonPressed(String buttonName) {
        switch (buttonName) {
            case "videos":
                btnVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_bold_button_border));
                btnSavedVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                btnPictures.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                break;
            case "savedVideos":
                btnVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                btnSavedVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_bold_button_border));
                btnPictures.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                break;
            case "pictures":
                btnVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                btnSavedVideos.setBackground(this.getResources().getDrawable(R.drawable.gallery_button_border));
                btnPictures.setBackground(this.getResources().getDrawable(R.drawable.gallery_bold_button_border));
                break;
        }
    }
}
