package com.vp6.anish.stow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anish on 29-07-2016.
 */
public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.MyViewHolder> {


    private Context mcontext;

    ArrayList<String> foldername;
    ArrayList<String> foldercreated;
    ArrayList<String> folderid;
    ArrayList<String> foldersize;

    DownloadActivity downloadActivity;
    private int id;

    public FolderListAdapter(Context context, int textViewResourceId, ArrayList<String> foldername, ArrayList<String> foldercreated, DownloadActivity downloadActivity, ArrayList<String>folderid, ArrayList<String>foldersize) {
        //super(context,textViewResourceId,foldername);

        this.foldername = new ArrayList<>();
        this.foldername = foldername;
        this.mcontext = context;
        this.id = textViewResourceId;


        this.folderid = folderid;
        this.foldersize = foldersize;

        this.foldercreated = foldercreated;
        this.downloadActivity = downloadActivity;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        //public ProgressBar progressBar;
        public TextView textView;
        public TextView folder_id;
        public ImageButton imageButton;
        public RelativeLayout folderlayout;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.filelistimage);
            folderlayout = (RelativeLayout)view.findViewById(R.id.folderlayout);
            //progressBar =(ProgressBar)view.findViewById(R.id.progresscircle);
            folder_id = (TextView)view.findViewById(R.id.folderid);
            textView = (TextView) view.findViewById(R.id.textView);
            imageButton = (ImageButton) view.findViewById(R.id.imageView2);
        }
    }
//    private class ViewHolder {
//        TextView foldername;
//        ImageView folderimage;
//        ImageButton menudetails;
//    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.download_file_list, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.thumbnail.setImageResource(R.drawable.folder);
        holder.textView.setText(" " + foldername.get(position) + " ");
        holder.folderlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadActivity.openfolder(folderid.get(position), foldername.get(position) );
            }
        });
//        holder.folder_id.setText(folderid.get(position));
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadActivity.checkclick(position, holder.imageButton, 0, true, foldername.get(position), folderid.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return foldername.size();
    }


    public void renamed(int position, String newname) {
        foldername.set(position, newname);

        notifyItemChanged(position);

    }

    public void insert( String data, String size, String date) {

        int position = foldername.size();
        if(foldername.size() <= 0)
        {
            downloadActivity.firstfolder();
        }
        foldername.add(position,data);
        //foldertype.add(position+1,type);
        foldersize.add(position,size);
        foldercreated.add(position,date);

        Log.i("insertes", "groupadapter");


        //fileuploaded.add(position,false);
        notifyItemInserted(position);


    }
    public void upload_complete( String id) {
        //fileuploaded.add(position,true);
        folderid.add(id);
       // notifyItemChanged(position, fileuploaded);
    }
    public void deletefolder(int position) {
        foldername.remove(position);
        folderid.remove(position);
        foldercreated.remove(position);
        foldersize.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,foldername.size());
        if (foldername.size() <= 0){
            downloadActivity.lastfolder();
        }

    }

}
