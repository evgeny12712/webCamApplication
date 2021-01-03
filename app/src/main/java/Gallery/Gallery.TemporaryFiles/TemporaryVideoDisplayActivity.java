package Gallery.Gallery.TemporaryFiles;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;

import Gallery.GalleryActivity;
import Gallery.Items;
import Gallery.Item;

import static Gallery.GalleryActivity.*;

public class TemporaryVideoDisplayActivity extends AppCompatActivity {
    private VideoView videoView;
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
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporary_video_display);
        videoView = (VideoView) findViewById(R.id.video_view);
        mediaController = new MediaController(this);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        textViewDate = (TextView) findViewById(R.id.date_recorded_text);
        textViewTime = (TextView) findViewById(R.id.time_recorded_text);
        context = this;

        videoPath = getIncomingVideoPath();
        uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra("fragment", fileTypes[0]);
                startActivity(intent);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Video share");
                intent.putExtra(Intent.EXTRA_TEXT," WebCamApplication video");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(intent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File saveDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                Item item = Items.findItemByUri(Items.getTemporaryFiles(), uri);
                try {
                    Items.saveFile(item, saveDir, context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Items.deleteFile(Items.findItemByUri(Items.getTemporaryFiles(), uri),
                        context, fileTypes[0]);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        date = Items.getDateFromFile(Items.findItemByUri(Items.getTemporaryFiles(), uri).getFile()).split(",")[0];
        time = Items.getDateFromFile(Items.findItemByUri(Items.getTemporaryFiles(), uri).getFile()).split(",")[1];

        textViewDate.setText(date);
        textViewTime.setText(time);
        videoView.start();
    }

    private String getIncomingVideoPath() {
        if(getIntent().hasExtra(fileTypes[0])) {
            return getIntent().getStringExtra(fileTypes[0]);
        }
        return null;
    }
}
