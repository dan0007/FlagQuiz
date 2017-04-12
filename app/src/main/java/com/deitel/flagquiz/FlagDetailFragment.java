package com.deitel.flagquiz;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;





public class FlagDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceStat) {
        View view =
                inflater.inflate(R.layout.fragment_flag_detail, container, false);
        Bundle args = this.getArguments();
        TextView flagDetailTextView = (TextView) view.findViewById(R.id.flagDetailTextView);
        TextView flagDetailAnswerTextView = (TextView) view.findViewById(R.id.flagDetailAnswerTextView);
        if (args.getBoolean("Correct"))
        {
            flagDetailAnswerTextView.setText(args.getString("Country") + "!");
            flagDetailAnswerTextView.setTextColor(
                    getResources().getColor(R.color.correct_answer,
                            getContext().getTheme()));
        } else {
            flagDetailAnswerTextView.setText(String.format("The correct answer is %s!", args.getString("Country")));
            flagDetailAnswerTextView.setTextColor(getResources().getColor(
                    R.color.incorrect_answer, getContext().getTheme()));
        }
        flagDetailTextView.setText(String.format((String) flagDetailTextView.getText(), args.getString("Region").replaceAll("_", " "), args.getString("Country")));

       return view;
    }


}
