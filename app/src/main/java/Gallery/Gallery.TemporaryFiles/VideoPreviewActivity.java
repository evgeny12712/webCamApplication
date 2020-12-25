package Gallery.Gallery.TemporaryFiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webcamapplication.R;

import java.io.File;

import Gallery.GalleryActivity;
import Gallery.SavedFiles.VideoFiles;

public class VideoPreviewActivity extends AppCompatActivity {

    VideoView videoView;
    MediaController mediaController;
    String videoPath;
    Uri uri;
    ImageButton backBtn;
    Button btnSave;
    Button btnDelete;
    Button btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        videoView = (VideoView) findViewById(R.id.video_view);
        mediaController = new MediaController(this);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnDelete = (Button) findViewById(R.id.btn_delete);

        videoPath = getIncomingVideoPath();
        uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoFiles.saveFile(VideoFiles.findItemByUri(VideoFiles.getTemporaryFiles(), uri), getApplicationContext());
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoFiles.deleteFromTemporary(VideoFiles.findItemByUri(VideoFiles.getTemporaryFiles(), uri));
                File file = new File(uri.getPath());
                file.delete();
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    private String getIncomingVideoPath() {
        if(getIntent().hasExtra("video")) {
            return getIntent().getStringExtra("video");
        }
        return null;
    }
}
