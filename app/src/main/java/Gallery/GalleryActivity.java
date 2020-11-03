package Gallery;

import android.content.Intent;
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
    GridView gridView;
    Button btnSavedVideos;
    Button btnPictures;
    Button btnHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(this));

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
                Intent intent = new Intent(getApplicationContext(), GallerySavedVideos.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        btnPictures = (Button) findViewById(R.id.btnPictures);
        btnPictures.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(GalleryActivity.this, "Pictures", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), GalleryPictures.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(GalleryActivity.this, "id" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });
    }
}
