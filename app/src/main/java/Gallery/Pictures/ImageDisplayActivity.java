package Gallery.Pictures;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.webcamapplication.R;

import java.io.File;

import Gallery.GalleryActivity;
import Gallery.Item;
import Gallery.Items;
import static Gallery.GalleryActivity.fileTypes;
import static Gallery.Items.getImages;

public class ImageDisplayActivity extends AppCompatActivity {
    private static final String TAG = "imageDisplayActivity";
    private ImageView imageView;
    private String imagePath;
    private File imageFile;
    private ImageButton backBtn;
    private Button btnDelete;
    private Button btnShare;
    private Button btnRotate;
    private TextView textViewDate;
    private TextView textViewTime;
    private String date;
    private String time;
    private Uri imageUri;
    private Context context;
    private Float x1,x2;
    final int[] rotate = {90, 180, 270, 360};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        imageView = (ImageView) findViewById(R.id.image_view);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnRotate = (Button) findViewById(R.id.btn_rotate);
        textViewDate = (TextView) findViewById(R.id.date_recorded_text);
        textViewTime = (TextView) findViewById(R.id.time_recorded_text);
        context = this;

        imagePath = getIncomingImage();
        imageFile = new File(imagePath);
        imageUri = Uri.fromFile(imageFile);
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        imageView.setImageBitmap(bitmap);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra("fragment", fileTypes[2]);
                startActivity(intent);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", imageFile);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "image sharing"));
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Items.deleteFile(Items.findItemByUri(Items.getImages(), imageUri, context),
                        context, fileTypes[2]);
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            int i = 0;

            @Override
            public void onClick(View v) {
                imageView.setRotation(rotate[i++]);
                if(rotate.length == i) {
                    i = 0;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Items.getItemByFile(Items.getImages(), imageFile) != null) {
            Item item = Items.getItemByFile(Items.getImages(), imageFile);
            date = item.getDate().split(",")[0];
            time = item.getDate().split(",")[1];
            textViewDate.setText(date);
            textViewTime.setText(time);
        }
        if(imageView.getRotation() == 0 || imageView.getRotation() == 180)
            imageView.setRotation(90);
    }

    private String getIncomingImage() {
        if(getIntent().hasExtra(fileTypes[2])) {
            return getIntent().getStringExtra(fileTypes[2]);
        }
        return null;
    }

    //Make back button on navigation bar go back to the gallery and not to the last file played
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra("fragment", fileTypes[2]);
        context.startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = Items.getImages().indexOf(Items.getItemByFile(Items.getImages(), imageFile));
        switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    {
                        x1 = event.getX();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        x2 = event.getX();
                        if(x1 > x2) {
                            if(index < Items.getImages().size()-1) {
                                Intent intent = new Intent(context, ImageDisplayActivity.class);
                                intent.putExtra(fileTypes[2], getImages().get(index+1).getFile().getPath());
                                context.startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(context, ImageDisplayActivity.class);
                                intent.putExtra(fileTypes[2], getImages().get(index).getFile().getPath());
                                context.startActivity(intent);
                            }
                            return true;
                        }
                        if(x1 < x2) {
                            if(index > 0) {
                                Intent intent = new Intent(context, ImageDisplayActivity.class);
                                intent.putExtra(fileTypes[2], getImages().get(index-1).getFile().getPath());
                                context.startActivity(intent);
                            }
                            return true;
                        }
                        break;
                    }
                }
        return false;
    }
}
