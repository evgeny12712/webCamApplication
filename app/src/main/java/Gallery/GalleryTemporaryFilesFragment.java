package Gallery;

import android.content.Context;
import android.graphics.Camera;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.webcamapplication.R;

import java.io.File;
import java.util.ArrayList;

import Driving.CameraRecordingFragment;
import Gallery.FilesContent;
import Gallery.Gallery.TemporaryFiles.MyItemRecyclerViewAdapter;
import Gallery.GalleryPicturesFragment;
import Gallery.PictureItem;

public class GalleryTemporaryFilesFragment extends Fragment  {

    private static final String TAG = "RecyclerViewTemporaryFragment";
    private static final int DATASET_COUNT = 60;

    protected RecyclerView mRecyclerView;
    protected MyItemRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<File> mDataset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_temporary_files, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyItemRecyclerViewAdapter(getActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES));
        mRecyclerView.setAdapter(mAdapter);
        Toast.makeText(this.getContext() , "" + mAdapter.getmDataset().size(), Toast.LENGTH_SHORT).show();
        return view;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ImageView imageView);
    }
}