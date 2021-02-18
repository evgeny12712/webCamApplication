package Gallery.SavedFiles;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import static android.R.style.Theme_Black_NoTitleBar_Fullscreen;


public class SavedVideoDisplayActivity extends AppCompatActivity {
    private static final String TAG = "SavedVideoDisplayActivity";
    //file info
    private String videoPath;
    private File videoFile;
    private Uri uri;
    //buttons
    private ImageButton backBtn;
    private Button btnDelete;
    private Button btnShare;
    private Button btnRotate;
    //video player buttons
    private ImageButton exoNext;
    private ImageButton exoBack;
    private FrameLayout exoFullScreen;
    private ImageView exoFullScreenImageIcon;
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
    //texture view of the player view
    private TextureView textureView;
    //the position of the item that currently playing
    private int itemPosition;
    //rotation options
    final int[] rotate = {90, 180, 270, 360};

    //full screen dialog
    private Dialog fullScreenDialog;
    private boolean isExoPlayerFullscreen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_video_display);

        backBtn = (ImageButton) findViewById(R.id.back_btn);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnRotate = (Button) findViewById(R.id.btn_rotate);
        exoNext = (ImageButton) findViewById(R.id.exo_next);
        exoBack = (ImageButton) findViewById(R.id.exo_prev);
        exoFullScreenImageIcon = (ImageView) findViewById(R.id.exo_fullscreen_icon);
        exoFullScreen = (FrameLayout) findViewById(R.id.exo_fullscreen_button);
        exoFullScreenImageIcon.setClickable(true);
        textViewDate = (TextView) findViewById(R.id.date_recorded_text);
        textViewTime = (TextView) findViewById(R.id.time_recorded_text);
        playerView = (PlayerView) findViewById(R.id.video_player_view);
        textureView = (TextureView) playerView.getVideoSurfaceView();
        itemPosition = getIncomingPosition();
        videoPath = getSavedFiles().get(itemPosition).getFile().getPath();
        videoFile = new File(videoPath);
        isExoPlayerFullscreen = false;
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

        btnRotate.setOnClickListener(new View.OnClickListener() {
            int i = 0;

            @Override
            public void onClick(View v) {
                textureView.setRotation(rotate[i++]);
                if(rotate.length == i) {
                    i = 0;
                }
            }
        });

        exoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SavedVideoDisplayActivity.class);
                //checking if we are not currently watching the last item
                if(itemPosition+1 < GallerySavedFilesFragment.mediaSources.size()) {
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

        exoFullScreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isExoPlayerFullscreen) {
                    openFullscreenDialog();
                }
                else {
                    closeFullscreenDialog();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //starting the video automatically
        videoPlayer.setPlayWhenReady(true);
        if(getItemByFile(getSavedFiles(), videoFile).getIsLandscape()){
            Toast.makeText(context, "video is landscape", Toast.LENGTH_SHORT).show();
            textureView.setRotation(rotate[2]);
        }

        //setting title for date and time
        if (getItemByFile(getSavedFiles(), videoFile) != null) {
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
    protected void onResume() {
        super.onResume();
        //initializing the date and time to show them on the title
        initFullscreenDialog();
        if(isExoPlayerFullscreen) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
            fullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            exoFullScreenImageIcon.setImageDrawable(ContextCompat.getDrawable(SavedVideoDisplayActivity.this, R.drawable.ic_fullscreen_skrink));
            fullScreenDialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fullScreenDialog != null)
            fullScreenDialog.dismiss();
        videoPlayer.setPlayWhenReady(false);
        videoPlayer.release();

    }

    private int getIncomingPosition() {
        if(getIntent().hasExtra(fileTypes[1])) {
            return getIntent().getIntExtra(fileTypes[1], 0);
        }
        return 0;
    }

    //Make back button on navigation bar go back to the gallery and not to the last file played
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra("fragment", fileTypes[1]);
        context.startActivity(intent);
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

    //FULL SCREEN
    private void initFullscreenDialog() {

        fullScreenDialog = new Dialog(this, Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (isExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        fullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        exoFullScreenImageIcon.setImageDrawable(ContextCompat.getDrawable(SavedVideoDisplayActivity.this, R.drawable.ic_fullscreen_skrink));
        isExoPlayerFullscreen = true;
        fullScreenDialog.show();
    }

    private void closeFullscreenDialog() {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(playerView);
        isExoPlayerFullscreen = false;
        fullScreenDialog.dismiss();
        exoFullScreenImageIcon.setImageDrawable(ContextCompat.getDrawable(SavedVideoDisplayActivity.this, R.drawable.ic_fullscreen_expand));
    }

}