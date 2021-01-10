package Gallery.SavedFiles;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.webcamapplication.R;

import java.io.File;

import Gallery.GalleryActivity;
import Gallery.Items;

import static Gallery.GalleryActivity.fileTypes;
import static Gallery.Items.*;
import static Gallery.Items.findItemByUri;
import static Gallery.Items.getTemporaryFiles;

public class SavedVideoDisplayActivity extends AppCompatActivity {
    private static final String TAG = "SavedVideoDisplayActivity";
    private VideoView videoView;
    private MediaController mediaController;
    private String videoPath;
    private Uri uri;
    private File videoFile;
    private ImageButton backBtn;
    private Button btnDelete;
    private Button btnShare;
    private TextView textViewDate;
    private TextView textViewTime;
    private String date;
    private String time;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_video_display);
        videoView = (VideoView) findViewById(R.id.video_view);
        mediaController = new MediaController(this);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        textViewDate = (TextView) findViewById(R.id.date_recorded_text);
        textViewTime = (TextView) findViewById(R.id.time_recorded_text);
        context = this;

        videoPath = getIncomingPath();
        videoFile = new File(videoPath);
        uri = Uri.fromFile(videoFile);
        videoView.setVideoURI(uri);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra("fragment", fileTypes[1]);
                startActivity(intent);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", videoFile);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Video share");
                intent.putExtra(Intent.EXTRA_TEXT," WebCamApplication video");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Items.deleteFile(findItemByUri(getSavedFiles(), uri),
                        context , fileTypes[1]);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (findItemByUri(getSavedFiles(), uri) != null) {
            date = findItemByUri(getSavedFiles(), uri)
                    .getDate()
                    .split(",")[0];
            time = findItemByUri(getSavedFiles(), uri)
                    .getDate()
                    .split(",")[1];
            textViewDate.setText(date);
            textViewTime.setText(time);
        }
        videoView.start();
    }

    private String getIncomingPath() {
        if(getIntent().hasExtra(fileTypes[1])) {
            return getIntent().getStringExtra(fileTypes[1]);
        }
        return null;
    }

}