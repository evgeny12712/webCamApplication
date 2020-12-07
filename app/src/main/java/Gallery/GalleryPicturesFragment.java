package Gallery;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.webcamapplication.R;

import java.io.File;



public class GalleryPicturesFragment extends Fragment {

    public OnListFragmentInteractionListener mListener;
    private RecyclerView.Adapter recyclerViewAdapter;

    public GalleryPicturesFragment() {
        // Required empty public constructor
    }

//    // TODO: Rename and change types and number of parameters
//    public static GalleryPicturesFragment newInstance(String param1, String param2) {
////        GalleryPicturesFragment fragment = new GalleryPicturesFragment();
////        Bundle args = new Bundle();
////        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_gallery_pictures, container, false);
//        Context context = view.getContext();
//        RecyclerView recyclerView = (RecyclerView) view;
//        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
//        recyclerView.setAdapter(new MyItemRecyclerViewAdapter(FilesContent.ITEMS, mListener));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        FilesContent.loadSavedFiles(getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
//        recyclerViewAdapter.notifyDataSetChanged();

    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(PictureItem item);
    }
}