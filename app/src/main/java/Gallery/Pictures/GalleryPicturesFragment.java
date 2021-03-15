package Gallery.Pictures;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webcamapplication.R;

import Gallery.General.GalleryActivity;

public class GalleryPicturesFragment extends Fragment  {

    private static final String TAG = "RecyclerViewTemporaryFragment";

    protected RecyclerView mRecyclerView;
    protected MyPicturesRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected Activity parentActivity;
    protected static Context context;
    protected float x1,x2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (GalleryActivity) getActivity();
        context = this.getContext();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_pictures, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            mLayoutManager = new GridLayoutManager(getActivity(), 4);
        }
        else {
            mLayoutManager = new GridLayoutManager(getActivity(), 3);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyPicturesRecyclerViewAdapter(context, parentActivity);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.getItemCount();
        return view;
    }
}