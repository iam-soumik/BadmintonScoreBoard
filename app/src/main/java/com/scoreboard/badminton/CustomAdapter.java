package com.scoreboard.badminton;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.scoreboard.badminton.Model.Match;

import java.util.ArrayList;

/**
 * Created by HOME on 1/8/2019.
 */

public class CustomAdapter extends ArrayAdapter<Match> {

    private ArrayList<Match> matchList;
    Context mContext;

    public CustomAdapter(ArrayList<Match> data, Context context) {
        super(context, R.layout.list_item, data);
        this.matchList = data;
        this.mContext=context;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView textView_match_name;
        TextView textView_winning_team_name;
        TextView textView_team_one;
        TextView textView_team_two;
        TextView textView_points;
        TextView textView_duration;
        LinearLayout list_item_layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Match match = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.textView_match_name          = (TextView) convertView.findViewById(R.id.textView_match_name);
            viewHolder.textView_winning_team_name   = (TextView) convertView.findViewById(R.id.textView_winning_team_name);
            viewHolder.textView_team_one = (TextView) convertView.findViewById(R.id.textView_team_one);
            viewHolder.textView_team_two = (TextView) convertView.findViewById(R.id.textView_team_two);
            viewHolder.textView_points   = (TextView) convertView.findViewById(R.id.textView_points);
            viewHolder.textView_duration = (TextView) convertView.findViewById(R.id.textView_duration);
            viewHolder.list_item_layout  = (LinearLayout) convertView.findViewById(R.id.list_item_layout);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        if(position % 2 == 0){
            viewHolder.list_item_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.roundTextViewBackgroundColorTwo));
        }
        else{
            viewHolder.list_item_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.roundTextViewBackgroundColorOne));
        }
        viewHolder.textView_match_name.setText(match.getMatchID());
        viewHolder.textView_winning_team_name.setText(match.getWinnerTeam());
        //viewHolder.textView_team_one.setText(match.getTeamList().get(0).getLeftPlayer() + " / " + (match.getTeamList().get(0).getRightPlayer()));
        //viewHolder.textView_team_two.setText(match.getTeamList().get(1).getLeftPlayer() + " / " + (match.getTeamList().get(1).getRightPlayer()));
        viewHolder.textView_team_one.setText(match.getTeam1());
        viewHolder.textView_team_two.setText(match.getTeam2());

        String points = match.getAllPoints();
        /*for (int i = 0; i < match.getPoints().length; i++) {
            for (int j = 0; j < match.getPoints()[i].length; j++) {
                points = points + match.getPoints()[i][j];
                points = points + " - ";
            }
            points = points + " , ";
        }*/
        viewHolder.textView_points.setText(points);
        viewHolder.textView_duration.setText("Duration : " + Util.gettimeDuration(match.getMatchDuration()));
        // Return the completed view to render on screen
        return convertView;
    }

}
