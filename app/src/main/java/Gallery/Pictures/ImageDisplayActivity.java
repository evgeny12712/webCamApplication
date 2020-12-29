package Gallery.Pictures;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webcamapplication.R;

import java.io.File;

import Gallery.GalleryActivity;
import Gallery.SavedFiles.Items;

public class ImageDisplayActivity extends AppCompatActivity {
    private ImageView imageView;
    private String imagePath;
    private File imageFile;
    private ImageButton backBtn;
    private Button btnDelete;
    private Button btnShare;
    private TextView textViewDate;
    private TextView textViewTime;
    private String date;
    private String time;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        imageView = (ImageView) findViewById(R.id.image_view);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        textViewDate = (TextView) findViewById(R.id.date_recorded_text);
        textViewTime = (TextView) findViewById(R.id.time_recorded_text);


        imagePath = getIncomingImage();
        imageFile = new File(imagePath);
        imageUri = Uri.parse(imageFile.getPath());
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        imageView.setImageBitmap(bitmap);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(intent);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Image share");
                intent.putExtra(Intent.EXTRA_TEXT,"Image");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Items.deleteFile(Items.findItemByUri(Items.getSavedFiles(), imageUri),
                        getApplicationContext(), "saved");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        date = Items.getDateFromFile(Items.findItemByUri(Items.getTemporaryFiles(), imageUri).getFile()).split(",")[0];
//        time = Items.getDateFromFile(Items.findItemByUri(Items.getTemporaryFiles(), imageUri).getFile()).split(",")[1];
//        textViewDate.setText(date);
//        textViewTime.setText(time);
    }

    private String getIncomingImage() {
        if(getIntent().hasExtra("image")) {
            return getIntent().getStringExtra("image");
        }
        return null;
    }

}