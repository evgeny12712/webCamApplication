package Gallery;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import MainWindow.MainActivity;
import com.example.webcamapplication.R;

public class GalleryActivity extends AppCompatActivity {
    private GridView gridView;
    private Button btnSavedVideos;
    private Button btnPictures;
    private Button btnHome;
    private Button btnVideos;
    private GalleryTemporaryFilesFragment temporaryFiles;
    private GalleryTemporaryFilesFragment savedFiles;
    private GalleryPicturesFragment pictures;
    private Resources res;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        res = getResources();

        temporaryFiles = new GalleryTemporaryFilesFragment();
        savedFiles = new GalleryTemporaryFilesFragment();
        pictures = new GalleryPicturesFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.gridFLayoutFragment, temporaryFiles)
                .commit();


        btnHome = (Button)findViewById(R.id.btnHome);
        btnVideos = (Button)findViewById(R.id.btnVideos);
        btnSavedVideos = (Button) findViewById(R.id.btnSavedVideos);
        btnPictures = (Button) findViewById(R.id.btnPictures);


        btnHome = (Button)findViewById(R.id.btnHome);
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
                        .replace(R.id.gridFLayoutFragment, temporaryFiles)
                        .commit();
                setButtonPressed("videos");
            }
        });

        btnSavedVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GalleryActivity.this, "savedVideos", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gridFLayoutFragment, savedFiles)
                        .commit();
                setButtonPressed("savedVideos");
            }
        });

        btnPictures.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(GalleryActivity.this, "Pictures", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gridFLayoutFragment, pictures)
                        .commit();
                setButtonPressed("pictures");

            }
        });



//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(GalleryActivity.this, "id" + position, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
//                intent.putExtra("id", position);
//                startActivity(intent);
//            }
//        });
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
}
