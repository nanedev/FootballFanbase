package com.malikbisic.sportapp.fragment.api;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malikbisic.sportapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerStatsFragment extends Fragment {
    TextView total_matches_textview;
    TextView from_bench_textview;
    TextView from_beggining_textview;
    TextView goals_textview;
    TextView assists_textview;
    TextView minutes_textview;
    TextView yellow_textview;
    TextView red_textview;
    TextView injured_textview;
    Intent intent;


    public PlayerStatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_stats, container, false);

        total_matches_textview = (TextView) view.findViewById(R.id.matches_played);
        from_bench_textview = (TextView) view.findViewById(R.id.from_bench);
        from_beggining_textview = (TextView) view.findViewById(R.id.started_match);
        goals_textview = (TextView) view.findViewById(R.id.goals_stats);
        assists_textview = (TextView) view.findViewById(R.id.assists_stats);
        minutes_textview = (TextView) view.findViewById(R.id.minutes);
        yellow_textview = (TextView) view.findViewById(R.id.yellow_cards);
        red_textview = (TextView) view.findViewById(R.id.red_cards);
        injured_textview = (TextView) view.findViewById(R.id.isinjured);

        intent = getActivity().getIntent();
        String totalMathes = intent.getStringExtra("appearances");
        String fromBench = intent.getStringExtra("substituteIn");
        String fromStart = intent.getStringExtra("lineups");
        String goals = intent.getStringExtra("goals");
        String assists = intent.getStringExtra("assists");
        String minutes = intent.getStringExtra("minutes");
        String yellowCards = intent.getStringExtra("yellowCards");
        String redCards = intent.getStringExtra("redCards");
        String injured = intent.getStringExtra("playerInjured");
        boolean isClickedLineUp = intent.getBooleanExtra("openMatchInfo", false);
        String player_id = intent.getStringExtra("playerID");

        if (!isClickedLineUp) {
            total_matches_textview.setText(totalMathes);
            from_bench_textview.setText(fromBench);
            from_beggining_textview.setText(fromStart);
            goals_textview.setText(goals);
            assists_textview.setText(assists);
            minutes_textview.setText(minutes);
            yellow_textview.setText(yellowCards);
            red_textview.setText(redCards);
            if (injured.equals("true")) {
                injured_textview.setText("Player is injured");
            } else {
                injured_textview.setText("Player is not injured");
            }

        } else {

        }
        return view;
    }

}
