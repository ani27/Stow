package com.vp6.anish.stow;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 11-07-2016.
 */
public class GroupUploadProgressAdapter extends RecyclerView.Adapter<GroupUploadProgressAdapter.MyViewHolder> {

    private List<String> images;
    private Context mContext;
    private List<String> filetype;
    private List<Boolean>fileuploaded;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
       public ProgressBar progressBar;
        public TextView textView;
        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            progressBar =(ProgressBar)view.findViewById(R.id.progresscircle);
            textView = (TextView)view.findViewById(R.id.filename);
        }
    }


    public GroupUploadProgressAdapter(Context context, List<String> filename, List<String> filetype, List<Boolean> fileuploaded) {
        this.mContext = context;
        this.filetype = filetype;
        this.images = filename;
        this.fileuploaded = fileuploaded;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        final int temp =position;

 Log.i("im","in bindview");
        Glide.with(mContext).load(R.drawable.image_for_empty_url)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);
        String address = images.get(position);
        holder.textView.setText(address);

        if(fileuploaded.get(position)==true)
        {
            holder.progressBar.setVisibility(View.GONE);

        }
        else
        {
            holder.progressBar.setVisibility(View.VISIBLE);
        }

        }
    public String getfiletype(int pos)
    {
        return filetype.get(pos);
    }
    public String getfilename(int pos)
    {
        return images.get(pos);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void insert(int position, String data) {
        images.add(position, data);
        Log.i("insertes", "groupadapter");

        fileuploaded.add(position,false);
        notifyItemInserted(position);


    }

    public void insertfolder(int position, String data) {
        images.add(position, data);
        Log.i("insertes", "groupadapter");

        filetype.add(position,"folder");
        fileuploaded.add(position,false);
        notifyItemInserted(position);


    }
    public void uploadcomplete(int position) {
        fileuploaded.add(position,true);
        notifyItemChanged(position,fileuploaded);
    }

//public void showprogress(RecyclerView recyclerView, int progress, int position)
//{
////RecyclerView.ViewHolder
//// v =recyclerView.findViewHolderForAdapterPosition(position);
//    View v = recyclerView.getChildAt(position);
//    int chilposition= recyclerView.getChildAdapterPosition(v);
//
//   ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progresscircle);
//
//    progressBar.setVisibility(View.VISIBLE);
//
//    if(progress == 100)
//    {
//        progressBar.setVisibility(View.GONE);
//
//    }
//}




}
