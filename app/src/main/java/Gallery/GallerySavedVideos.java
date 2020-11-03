package Gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webcamapplication.MainActivity;
import com.example.webcamapplication.R;

public class GallerySavedVideos extends AppCompatActivity {
    Button btnVideos;
    Button btnPictures;
    Button btnHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_saved_videos);

        btnHome = (Button)findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(intent);
            }
        });

        btnVideos = (Button) findViewById(R.id.btnVideos);
        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        btnPictures = (Button) findViewById(R.id.btnPictures);
        btnPictures.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryPictures.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });

    }
}

