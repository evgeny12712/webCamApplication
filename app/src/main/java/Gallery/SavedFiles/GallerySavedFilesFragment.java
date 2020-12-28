package Gallery.SavedFiles;

import android.content.Context;
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
import java.util.ArrayList;

import Gallery.Gallery.TemporaryFiles.MyTemporaryFilesRecyclerViewAdapter;

public class GallerySavedFilesFragment extends Fragment  {

    private static final String TAG = "RecyclerViewSavedFilesFragment";

    protected RecyclerView mRecyclerView;
    protected MySavedFilesRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_saved_files, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MySavedFilesRecyclerViewAdapter(getContext(), getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}