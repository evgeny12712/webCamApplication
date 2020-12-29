package Gallery.Gallery.TemporaryFiles;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.webcamapplication.R;

import Gallery.SavedFiles.Items;

public class GalleryTemporaryFilesFragment extends Fragment  {

    private static final String TAG = "RecyclerViewTemporaryFragment";

    protected RecyclerView mRecyclerView;
    protected MyTemporaryFilesRecyclerViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

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
        mAdapter = new MyTemporaryFilesRecyclerViewAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        Toast.makeText(this.getContext() , "" + Items.getTemporaryFiles().size(), Toast.LENGTH_SHORT).show();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}