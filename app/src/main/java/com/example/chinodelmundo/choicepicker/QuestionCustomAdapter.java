package com.example.chinodelmundo.choicepicker;

/**
 * Created by Chino.DelMundo on 8/3/2017.
 */

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class QuestionCustomAdapter extends ArrayAdapter<QuestionDataModel> implements View.OnClickListener{
    private ArrayList<QuestionDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageView imgDelete, imgStart;
    }

    public QuestionCustomAdapter(ArrayList<QuestionDataModel> data, Context context) {
        super(context, R.layout.questions_row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        QuestionDataModel dataModel = (QuestionDataModel)object;

        switch (v.getId())
        {
            case R.id.ivDelete:
                if(mContext instanceof MainActivity){
                    ((MainActivity)mContext).deleteQuestion(dataModel.getId(), dataModel.getText());
                }
                break;
            case R.id.ivStart:
                if(mContext instanceof MainActivity){
                    ((MainActivity)mContext).chooseItem(dataModel.getId());
                }
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        QuestionDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.questions_row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.tvQuestion);
            viewHolder.imgDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
            viewHolder.imgStart = (ImageView) convertView.findViewById(R.id.ivStart);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(dataModel.getText());
        viewHolder.imgDelete.setOnClickListener(this);
        viewHolder.imgDelete.setTag(position);
        viewHolder.imgStart.setOnClickListener(this);
        viewHolder.imgStart.setTag(position);

        // Return the completed view to render on screen
        return convertView;
    }
}
