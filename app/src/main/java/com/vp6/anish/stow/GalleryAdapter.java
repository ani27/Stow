package com.vp6.anish.stow;

/**
 * Created by anish on 05-07-2016.
 */
import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import com.vp6.anish.stow.R;

/**
 * Created by Lincoln on 31/03/16.
 */


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<String> images;
    private Context mContext;
    SparseBooleanArray mSparseBooleanArray;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public SquareLayout gallerylayout;
        public CheckBox checkBox;
        public MyViewHolder(View view) {
            super(view);
            gallerylayout= (SquareLayout) view.findViewById(R.id.gallery_layout);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            checkBox =(CheckBox)view.findViewById(R.id.checkBox1);
        }
    }


    public GalleryAdapter(Context context, List<String> imageurl,SparseBooleanArray sparseBooleanArray) {
        mContext = context;
        mSparseBooleanArray = new SparseBooleanArray();
        this.images = imageurl;
        this.mSparseBooleanArray= sparseBooleanArray;
    }


    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<images.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                mTempArry.add(images.get(i));
            }
        }

        return mTempArry;
    }


    public SparseBooleanArray getSparseArray(){
        SparseBooleanArray mTempArry = new SparseBooleanArray();

        for(int i=0;i<images.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                mTempArry.put(i, true);
            }
            else
                mTempArry.put(i,false);
        }
        return  mTempArry;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Log.i("INSIDE BINDVIEW HOLDER " , (position)+"");

        final int temp =position;


        Glide.with(mContext).load(images.get(position))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.thumbnail);

        if(mSparseBooleanArray.get(temp))
        {
            holder.thumbnail.setColorFilter(R.color.yellow);
        }
        else
        {
            holder.thumbnail.setColorFilter(null);
        }

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INSIDE BINDVIEW HOLDER " , (temp)+"");
                mSparseBooleanArray.put(temp, !mSparseBooleanArray.get(temp));
               // holder.checkBox.setChecked(mSparseBooleanArray.get(temp));
                holder.gallerylayout.setSelected(mSparseBooleanArray.get(temp));
                if(mSparseBooleanArray.get(temp))
                {
                    holder.thumbnail.setColorFilter(R.color.yellow);
                }
                else
                {
                    holder.thumbnail.setColorFilter(null);
                }

            }
        });
    }
    @Override
    public int getItemCount() {
        return images.size();
    }


}
