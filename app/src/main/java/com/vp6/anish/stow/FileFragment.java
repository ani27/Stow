package com.vp6.anish.stow;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ListView;
import android.widget.Switch;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private ArrayList<String> imageUrl = new ArrayList<>();

    private MyCustomAdapter adapter;
    Context mcontext;
    private ArrayList<String> filename = new ArrayList<>();
    private ArrayList<Integer> filetype = new ArrayList<>();
    SparseBooleanArray mSparse = new SparseBooleanArray();

    public FileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FileFragment newInstance(String param1, String param2) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mcontext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_file, container, false);


        // walkdir(Environment.getExternalStorageDirectory());
        filelist();
        adapter = new MyCustomAdapter(getActivity(), R.layout.file_info, filename, filetype, mSparse, imageUrl);


        ListView listView = (ListView) v.findViewById(R.id.listView1);
        // Assign adapter to ListView

        listView.setAdapter(adapter);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onPause() {

        mSparse = adapter.arrayreturn();
        super.onPause();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }




    public ArrayList<String> getitems() {
        if (adapter.getCheckedItems() != null)
            return adapter.getCheckedItems();
        else {
            ArrayList<String> temp = new ArrayList<>();

            return temp;
        }
    }


    public void filelist() {
        ContentResolver cr = mcontext.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");

// every column, although that is huge waste, you probably need
// BaseColumns.DATA (the path) only.
         String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.MIME_TYPE};
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;
// exclude media files, they would be here also.

        Cursor allFiles = cr.query(uri, projection, null, null, sortOrder +" DESC");

        for (int i = 0; i < allFiles.getCount(); i++) {

            allFiles.moveToPosition(i);
            int data = allFiles.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int name = allFiles.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
           int type = allFiles.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE);

            int mediatype = allFiles.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
           // int nonmedia = allFiles.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);




                if (allFiles.getString(data).endsWith("pdf")) {
                    filetype.add(R.drawable.pdf);
                    imageUrl.add(allFiles.getString(data));
                    filename.add(allFiles.getString(name));

                } else if (allFiles.getString(data).endsWith("doc") || allFiles.getString(data).endsWith("docx")) {
                    filetype.add(R.drawable.doc);
                    imageUrl.add(allFiles.getString(data));
                    filename.add(allFiles.getString(name));

                } else if (allFiles.getString(data).endsWith("txt")) {
                    filetype.add(R.drawable.txt);
                    imageUrl.add(allFiles.getString(data));
                    filename.add(allFiles.getString(name));

                } else if (allFiles.getString(data).endsWith("ppt")) {
                    filetype.add(R.drawable.ppt);
                    imageUrl.add(allFiles.getString(data));
                    filename.add(allFiles.getString(name));

                } else if (allFiles.getString(data).endsWith("xls")) {
                    filetype.add(R.drawable.xls);
                    imageUrl.add(allFiles.getString(data));
                    filename.add(allFiles.getString(name));

                }
                else if (allFiles.getString(data).endsWith("zip")) {
                    filetype.add(R.drawable.zip);
                    imageUrl.add(allFiles.getString(data));
                    filename.add(allFiles.getString(name));

                }


                //Log.i("name",allFiles.getString(name));



//    Log.i("type", i+"");
//    //  System.out.println("=====> Array path => "+imageUrl.get(i));
//}
//        }

        }
    }
}
