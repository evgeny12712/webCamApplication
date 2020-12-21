package Gallery;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import MainWindow.MainActivity;
import com.example.webcamapplication.R;

public class GalleryActivity extends AppCompatActivity implements GalleryTemporaryFilesFragment.OnListFragmentInteractionListener,
        GalleryPicturesFragment.OnListFragmentInteractionListener,
        GallerySavedFilesFragment.OnListFragmentInteractionListener {
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
        pictures = new GallerySavedFilesFragment();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        res = getResources();
        setSupportActionBar(toolbar);

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
        int grey = res.getColor(R.color.grey);
        int darkGrey = res.getColor(R.color.darkGrey);
        switch (buttonName) {
            case "videos":
                btnVideos.setBackgroundColor(darkGrey);
                btnSavedVideos.setBackgroundColor(grey);
                btnPictures.setBackgroundColor(grey);
                break;
            case "savedVideos":
                btnVideos.setBackgroundColor(grey);
                btnSavedVideos.setBackgroundColor(darkGrey);
                btnPictures.setBackgroundColor(grey);
                break;
            case "pictures":
                btnVideos.setBackgroundColor(grey);
                btnSavedVideos.setBackgroundColor(grey);
                btnPictures.setBackgroundColor(darkGrey);
                break;
        }
    }


    @Override
    public void onListFragmentInteraction(PictureItem item) {

    }

    @Override
    public void onListFragmentInteraction(ImageView imageView) {

    }
}
