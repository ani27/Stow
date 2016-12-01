package com.vp6.anish.stow;

/**
 * Created by anish on 16-06-2016.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyCustomAdapter extends ArrayAdapter<String> {

    private ArrayList<String> files;
    private Context mcontext;
    ArrayList<Integer> filetype;
    private int id;
    ArrayList<String> address;
    SparseBooleanArray mSparseBooleanArray;
    //private int i;
    public MyCustomAdapter(Context context,int textViewResourceId, ArrayList<String> files,
                           ArrayList<Integer> Filetype, SparseBooleanArray sparseBooleanArray,ArrayList<String>address1) {
        super(context,textViewResourceId,files);

        filetype = new ArrayList<>();
        this.filetype = Filetype;
        this.mcontext= context;
        this.id=textViewResourceId;
        mSparseBooleanArray = new SparseBooleanArray();
        this.files= files;
        address = new ArrayList<>();
        this.address = address1;
        this.mSparseBooleanArray= sparseBooleanArray;
    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<address.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                mTempArry.add(address.get(i));
            }
        }

        return mTempArry;
    }
    private class ViewHolder {
        TextView code;
        ImageView type;
        CheckBox name;
    }
    public SparseBooleanArray arrayreturn(){
        return mSparseBooleanArray;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //ViewHolder holder = null;
         ViewHolder holder = new ViewHolder();
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.file_info, null);


            holder.code = (TextView) convertView.findViewById(R.id.code);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.type = (ImageView)convertView.findViewById(R.id.imageView1);
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Country country = countryList.get(position);
        holder.code.setText(" (" + files.get(position) + ")");
        // holder.name.setText(country.getName());
        holder.name.setChecked(mSparseBooleanArray.get(position));
        holder.name.setTag(position);
        //holder.name.setOnCheckedChangeListener(mCheckedChangeListener);
        holder.type.setImageResource(filetype.get(position));

        final ViewHolder finalHolder = holder;
        holder.code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSparseBooleanArray.put(position,!mSparseBooleanArray.get(position));
                finalHolder.name.setChecked(mSparseBooleanArray.get(position));
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View v){

                mSparseBooleanArray.put(position,!mSparseBooleanArray.get(position));
                finalHolder.name.setChecked(mSparseBooleanArray.get(position));
            }
        });
        holder.type.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                mSparseBooleanArray.put(position,!mSparseBooleanArray.get(position));
                finalHolder.name.setChecked(mSparseBooleanArray.get(position));
            }
        });
        return convertView;

    }

    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            int getPosition = (Integer) buttonView.getTag();
            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
        }
    };



}

