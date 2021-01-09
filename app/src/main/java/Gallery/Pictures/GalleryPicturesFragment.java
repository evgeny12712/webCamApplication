package Gallery.Pictures;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Gallery.GalleryActivity;
import Gallery.Items;

public class GalleryPicturesFragment extends Fragment  {

    private static final String TAG = "RecyclerViewTemporaryFragment";

    protected RecyclerView mRecyclerView;
    protected MyPicturesRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_pictures, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyPicturesRecyclerViewAdapter(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getContext());
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

}