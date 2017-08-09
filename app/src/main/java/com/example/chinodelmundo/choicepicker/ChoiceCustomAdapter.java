package com.example.chinodelmundo.choicepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chino.DelMundo on 8/3/2017.
 */

public class ChoiceCustomAdapter extends ArrayAdapter<ChoiceDataModel> implements View.OnClickListener{
    private ArrayList<ChoiceDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageView imgDelete;
    }

    public ChoiceCustomAdapter(ArrayList<ChoiceDataModel> data, Context context) {
        super(context, R.layout.choices_row_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        ChoiceDataModel dataModel = (ChoiceDataModel)object;

        switch (v.getId())
        {
            case R.id.ivDelete:
                if(mContext instanceof QuestionDetailsActivity){
                    ((QuestionDetailsActivity)mContext).deleteChoice(dataModel.getId(), dataModel.getText());
                }
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChoiceDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.choices_row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.tvChoice);
            viewHolder.imgDelete = (ImageView) convertView.findViewById(R.id.ivDelete);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(dataModel.getText());
        viewHolder.imgDelete.setOnClickListener(this);
        viewHolder.imgDelete.setTag(position);

        // Return the completed view to render on screen
        return convertView;
    }
}