package Gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webcamapplication.R;

public class VideoPreviewActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        videoView = (VideoView) findViewById(R.id.video_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getIncomingIntent();
        videoView.start();
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("video_uri")) {
            String uri = getIntent().getStringExtra("video");
            videoView.setVideoURI(Uri.parse(uri));
        }
    }
}
