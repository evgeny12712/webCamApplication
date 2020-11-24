package Gallery;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webcamapplication.MainActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        res = getResources();

        temporaryFiles = new GalleryTemporaryFilesFragment();
        savedFiles = new GalleryTemporaryFilesFragment();
        pictures = new GalleryPicturesFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.gridFLayoutFragment, temporaryFiles)
                .commit();

        btnVideos = (Button)findViewById(R.id.btnVideos);

//        gridView = (GridView) findViewById(R.id.grid_view);
//        gridView.setAdapter(new ImageAdapter(this));

        btnHome = (Button)findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(intent);
            }
        });


        btnSavedVideos = (Button) findViewById(R.id.btnSavedVideos);
        btnSavedVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GalleryActivity.this, "savedVides", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gridFLayoutFragment, savedFiles)
                        .commit();
                setButtonPressed("savedVideos");
                overridePendingTransition(0, 0);
            }
        });

        btnPictures = (Button) findViewById(R.id.btnPictures);
        btnPictures.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(GalleryActivity.this, "Pictures", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.gridFLayoutFragment, pictures)
                        .commit();
                setButtonPressed("pictures");
                overridePendingTransition(0, 0);

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
            case "home":
                btnHome.setBackgroundColor(darkGrey);
                btnVideos.setBackgroundColor(grey);
                btnSavedVideos.setBackgroundColor(grey);
                btnPictures.setBackgroundColor(grey);
                break;
            case "videos":
                btnHome.setBackgroundColor(grey);
                btnVideos.setBackgroundColor(darkGrey);
                btnSavedVideos.setBackgroundColor(grey);
                btnPictures.setBackgroundColor(grey);
                break;
            case "savedVideos":
                btnHome.setBackgroundColor(darkGrey);
                btnVideos.setBackgroundColor(grey);
                btnSavedVideos.setBackgroundColor(darkGrey);
                btnPictures.setBackgroundColor(grey);
                break;
            case "pictures":
                btnHome.setBackgroundColor(darkGrey);
                btnVideos.setBackgroundColor(grey);
                btnSavedVideos.setBackgroundColor(grey);
                btnPictures.setBackgroundColor(darkGrey);
                break;
        }
    }
}
