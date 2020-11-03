package Gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webcamapplication.MainActivity;
import com.example.webcamapplication.R;
// import com.example.webcamapplication.R;

public class GalleryPictures extends AppCompatActivity {
    Button btnVideos;
    Button btnSavedVideos;
    Button btnHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_pictures);

        Toast.makeText(this, "PICS", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(GalleryPictures.this, "otherScreens/Gallery", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        btnSavedVideos = (Button) findViewById(R.id.btnSavedVideos);
        btnSavedVideos.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(GalleryPictures.this, "Saved Videos", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), GallerySavedVideos.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

    }
}

