package Gallery.Pictures;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webcamapplication.R;

import Gallery.GalleryActivity;
import Gallery.SavedFiles.VideoFiles;

public class SavedImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private MediaController mediaController;
    private String videoPath;
    private Uri uri;
    private ImageButton backBtn;
    private Button btnSave;
    private Button btnDelete;
    private Button btnShare;
    private TextView textViewDate;
    private TextView textViewTime;
    private String date;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        imageView = (ImageView) findViewById(R.id.image_view);
        mediaController = new MediaController(this);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        textViewDate = (TextView) findViewById(R.id.date_recorded_text);
        textViewTime = (TextView) findViewById(R.id.time_recorded_text);


        videoPath = getIncomingVideoPath();
        uri = Uri.parse(videoPath);
        imageView.setVideoURI(uri);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
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
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoFiles.deleteVideoFile(VideoFiles.findItemByUri(VideoFiles.getSavedFiles(), uri),
                        getApplicationContext(), "saved");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        date = VideoFiles.getDateFromFile(VideoFiles.findItemByUri(VideoFiles.getTemporaryFiles(), uri).getFile()).split(",")[0];
        time = VideoFiles.getDateFromFile(VideoFiles.findItemByUri(VideoFiles.getTemporaryFiles(), uri).getFile()).split(",")[1];

        textViewDate.setText(date);
        textViewTime.setText(time);


    }

    private String getIncomingImage() {
        if(getIntent().hasExtra("video")) {
            return getIntent().getStringExtra("video");
        }
        return null;
    }

}