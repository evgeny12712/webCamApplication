package Gallery.Gallery.TemporaryFiles;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.webcamapplication.R;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.ArrayList;
import java.util.List;

import Gallery.General.GalleryActivity;
import Gallery.General.Item;

import static Gallery.General.Items.*;

public class GalleryTemporaryFilesFragment extends Fragment  {

    private static final String TAG = "RecyclerViewTemporary";

    protected RecyclerView mRecyclerView;
    protected MyTemporaryFilesRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected Activity parentActivity;
    protected static List<MediaSource> mediaSources;
    protected static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (GalleryActivity) getActivity();
        context = this.getContext();
        mediaSources = new ArrayList<MediaSource>();
        createMediaSources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_temporary_files, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            mLayoutManager = new GridLayoutManager(getActivity(), 4);
        }
        else {
            mLayoutManager = new GridLayoutManager(getActivity(), 3);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyTemporaryFilesRecyclerViewAdapter(getContext(), parentActivity);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.getItemCount();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void createMediaSources() {
        for(Item item : getTemporaryFiles()) {
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, "sample");
            mediaSources.add(new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(item.getUri()));
        }
    }


}