package com.vp6.anish.stow;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 29-07-2016.
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.MyViewHolder> {

    private List<String> images;
    private List<String> filesize;
    private Context mContext;
    private List<String> filetype;
    private List<Boolean>fileuploaded;
  private ArrayList<String> filecreated;
    private ArrayList<String> fileid;
    DownloadActivity downloadActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public ProgressBar progressBar;
        public TextView textView;
        public ImageButton imageButton;
        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            progressBar =(ProgressBar)view.findViewById(R.id.progresscircle);
            textView = (TextView)view.findViewById(R.id.filename);
            imageButton = (ImageButton)view.findViewById(R.id.arrowbutton);
        }
    }


    public FileListAdapter(Context context, List<String> filename, List<String> filetype, List<Boolean> fileuploaded, ArrayList<String> files, DownloadActivity downloadActivity, ArrayList<String>fileid, ArrayList<String> filecreated ) {
        this.mContext = context;
        this.filetype = filetype;
        this.images = filename;
        this.fileuploaded = fileuploaded;
        this.filesize = files;
       this.filecreated = filecreated;
        this.downloadActivity = downloadActivity;
        this.fileid = fileid;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //        notifyAll();
        Log.i("Tag2",position+"");

        final int temp = position;
        int location = 0;

        if (!filetype.get(position).equals("image")) {

            switch (filetype.get(position)) {
                case "pdf":

                    location = R.drawable.pdf;
                    break;
                case "ppt":
                    location = R.drawable.ppt;
                    break;
                case "music":
                    location = R.drawable.musiz;
                    break;
                case "text":
                    location = R.drawable.txt;
                    break;
                case "zip":
                    location = R.drawable.zip;
                    break;
                case "xls":
                    location = R.drawable.xls;
                    break;
                default:
                    location = R.drawable.uploadfile;
                    break;


            }
        }
            else{
                location = R.drawable.uploadfile;
            }


            if (filetype.get(position).equals("image")) {

                Log.i("inside", "if");
                Glide.with(mContext).load(location)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(holder.thumbnail);
                //Log.i("url", fileaddress.get(position));



            }
            else {
                Log.i("inside","else");

                Glide.with(mContext).load(location)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(holder.thumbnail);
                holder.thumbnail.setScaleType(ImageView.ScaleType.CENTER);

            }
            final String name = images.get(position);
            holder.textView.setText(name);

            if (fileuploaded.get(position) == true) {
                holder.progressBar.setVisibility(View.GONE);

            } else {
                holder.progressBar.setVisibility(View.VISIBLE);
            }
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                DownloadActivity downloadActivity= new DownloadActivity();
                downloadActivity.checkclick(position,holder.imageButton,1, fileuploaded.get(position) , name,fileid.get(position));
            }
        });


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

    public void insert(int position, String data, String type, String size, String date) {

        if(images.size() <= 0)
        {
            downloadActivity.firstfile();
        }
        images.add(position, data);
        filetype.add(position,type);
        filesize.add(position,size);
        filecreated.add(position,date);
        Log.i("insertes", "groupadapter");


        fileuploaded.add(position,false);
        notifyItemInserted(position);


    }
    public void renamed(int position, String newname){
        images.set(position, newname);
        notifyItemChanged(position);
    }
    public void deletefile(int position) {
        images.remove(position);
        filetype.remove(position);
        filesize.remove(position);
        fileuploaded.remove(position);
        filecreated.remove(position);
        fileid.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,images.size());

        Log.i("Tag",position+"");


        if(images.size() <=0)

        {
            downloadActivity.lastfile();
        }

    }
    public void uploadcomplete(int position, String id) {
        fileuploaded.add(position,true);
        fileid.add(position,id);
        notifyItemChanged(position, fileuploaded);
    }

    ///////////////////////check internet connection

    public boolean hasInternetAccess(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e("TAG", "Error checking internet connection", e);
            }
        } else {
            Log.d("TAG", "No network available!");
        }
        return false;

    }


    //to check connection

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
