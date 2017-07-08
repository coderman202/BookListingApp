package com.example.android.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Reggie on 08/07/2017. A custom adapter class for the drop down menu allowing the
 * user to select how many results they want
 */

class NumResultsAdapter extends ArrayAdapter<Integer> {

    static class ViewHolder{
        @BindView(R.id.spinner_item_view) TextView spinnerItem;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
    /**
     * Instantiates a new {@link NumResultsAdapter}.
     *
     * @param context     the context
     * @param optionsList the list of options
     */
    NumResultsAdapter(Context context, List<Integer> optionsList){
        super(context, 0, optionsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        ViewHolder holder;

        View spinnerItemView = convertView;

        if(spinnerItemView == null){
            spinnerItemView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
            holder = new ViewHolder(spinnerItemView);
            spinnerItemView.setTag(holder);
        }
        else{
            holder = (ViewHolder) spinnerItemView.getTag();
        }

        final int currentOption;
        if(getItem(position) == null){
            currentOption = 10;
        }
        else{
            currentOption = getItem(position);
        }

        holder.spinnerItem.setText(String.valueOf(currentOption));

        return spinnerItemView;
    }
}
