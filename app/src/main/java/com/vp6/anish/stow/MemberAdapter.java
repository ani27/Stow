package com.vp6.anish.stow;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anish on 03-07-2016.
 */
public class MemberAdapter extends ArrayAdapter<String> {

    private ArrayList<String> membername;
    private Context mcontext;
    private int id;

    ArrayList<String> email;
     public MemberAdapter(Context context,int textViewResourceId, ArrayList<String> membername,
                         ArrayList<String> memberemail) {
        super(context,textViewResourceId,membername);

       this.membername = new ArrayList<>();
        this.membername = membername;
        this.mcontext= context;
        this.id=textViewResourceId;
        this.email = new ArrayList<>();
        this.email = memberemail;
        }



    private class ViewHolder {
        TextView code;
        ImageView type;
        TextView codemail;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //ViewHolder holder = null;
        ViewHolder holder = new ViewHolder();
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.members_info, null);


            holder.code = (TextView) convertView.findViewById(R.id.code);
            holder.codemail = (TextView) convertView.findViewById(R.id.email_member);
            holder.type = (ImageView)convertView.findViewById(R.id.imageView1);
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Country country = countryList.get(position);
        holder.code.setText(  membername.get(position) );
         holder.codemail.setText(email.get(position));
      //  holder.name.setChecked(mSparseBooleanArray.get(position));
       // holder.name.setTag(position);
        //holder.name.setOnCheckedChangeListener(mCheckedChangeListener);
        holder.type.setImageResource(R.drawable.user);

        final ViewHolder finalHolder = holder;
        return convertView;

    }
}
