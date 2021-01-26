package Gallery.SavedFiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.webcamapplication.R;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;
import java.io.IOException;

import Gallery.Gallery.TemporaryFiles.TemporaryVideoDisplayActivity;
import Gallery.GalleryActivity;
import Gallery.Items;
import Gallery.Item;

import static Gallery.Gallery.TemporaryFiles.GalleryTemporaryFilesFragment.*;
import static Gallery.GalleryActivity.*;
import static Gallery.Items.*;


public class SavedVideoDisplayActivity extends AppCompatActivity {
    private static final String TAG = "SavedVideoDisplayActivity";
    //file info
    private String videoPath;
    private File videoFile;
    private Uri uri;
    //buttons
    private ImageButton backBtn;
    private Button btnSave;
    private Button btnDelete;
    private Button btnShare;
    //video player buttons
    private ImageButton exoNext;
    private ImageButton exoBack;
    //title text
    private TextView textViewDate;
    private TextView textViewTime;
    //date && time strings for title text
    private String date;
    private String time;
    //this activity context
    private Context context;
    //the video player
    private SimpleExoPlayer videoPlayer;
    //view of video player
    private PlayerView playerView;
    //the position of the item that currently playing
    private int itemPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_video_display);

        backBtn = (ImageButton) findViewById(R.id.back_btn);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        exoNext = (ImageButton) findViewById(R.id.exo_next);
        exoBack = (ImageButton) findViewById(R.id.exo_prev);
        textViewDate = (TextView) findViewById(R.id.date_recorded_text);
        textViewTime = (TextView) findViewById(R.id.time_recorded_text);
        playerView = (PlayerView) findViewById(R.id.video_player_view);
        itemPosition = getIncomingPosition();
        videoPath = getSavedFiles().get(itemPosition).getFile().getPath();
        videoFile = new File(videoPath);
        uri = Uri.fromFile(videoFile);
        initializePlayer();
        context = this;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Items.deleteFile(getItemByFile(getSavedFiles(), videoFile),
                        context, fileTypes[1]);
            }
        });

        exoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SavedVideoDisplayActivity.class);
                //checking if we are not currently watching the last item
                if(itemPosition < GallerySavedFilesFragment.mediaSources.size()) {
                    itemPosition++;
                }
                intent.putExtra(fileTypes[1], itemPosition);
                context.startActivity(intent);
            }
        });

        exoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SavedVideoDisplayActivity.class);
                //checking if we are not currently watching the first item
                if(itemPosition > 0) {
                    --itemPosition;
                }
                intent.putExtra(fileTypes[1], itemPosition);
                context.startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        videoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initializing the date and time to show them on the title
        if (getItemByFile(getSavedFiles(), videoFile) != null) {
            Toast.makeText(context, "FILE", Toast.LENGTH_SHORT).show();
            date = getItemByFile(getSavedFiles(), videoFile)
                    .getDate()
                    .split(",")[0];
            time = getItemByFile(getSavedFiles(), videoFile)
                    .getDate()
                    .split(",")[1];
            textViewDate.setText(date);
            textViewTime.setText(time);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.setPlayWhenReady(false);
        videoPlayer.release();
    }

    private int getIncomingPosition() {
        if(getIntent().hasExtra(fileTypes[1])) {
            return getIntent().getIntExtra(fileTypes[1], 0);
        }
        return 0;
    }

    private void initializePlayer() {
        //initializing the video player and the player view
        videoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(videoPlayer);
        //making a list of two items - the same items just to make 'next' button enabled
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        concatenatingMediaSource.addMediaSource(GallerySavedFilesFragment.mediaSources.get(itemPosition));
        concatenatingMediaSource.addMediaSource(GallerySavedFilesFragment.mediaSources.get(itemPosition));
        //making the video player repeating the same file to not move automatically to the next one
        videoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        //preparing the video player with the list
        videoPlayer.prepare(concatenatingMediaSource);
    }

    //Make back button on navigation bar go back to the gallery and not to the last file played
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra("fragment", fileTypes[1]);
        context.startActivity(intent);
    }
}