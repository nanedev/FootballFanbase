/**
 * Created by korisnik on 23/10/2017.
 */
/*
public class AllFixturesViewHolder extends ChildViewHolder {

    TextView leagueName;
    TextView timeStart;
    TextView localTeamNameTXT;
    TextView visitorTeamNameTXT;
    ImageView localTeamLogo;
    ImageView visitorTeamLogo;
    TextView league;
    RelativeLayout leagueNameLayout;


    public AllFixturesViewHolder(View itemView) {
        super(itemView);

        timeStart = (TextView) itemView.findViewById(R.id.timeStartMatch);
        localTeamNameTXT = (TextView) itemView.findViewById(R.id.localTeamName);
        visitorTeamNameTXT = (TextView) itemView.findViewById(R.id.visitorTeamName);
        localTeamLogo = (ImageView) itemView.findViewById(R.id.localTeamLogo);
        visitorTeamLogo = (ImageView) itemView.findViewById(R.id.visitorTeamLogo);


    }

    public void updateUI(AllFixturesModel model) {
        String status = model.getStatus();

        localTeamNameTXT.setText(model.getLocalTeamName());
        visitorTeamNameTXT.setText(model.getVisitorTeamName());
        Picasso.with(localTeamLogo.getContext()).load(model.getLocalTeamLogo()).into(localTeamLogo);
        Picasso.with(visitorTeamLogo.getContext()).load(model.getVisitorTeamLogo()).into(visitorTeamLogo);



        if (status.equals("FT") || status.equals("HT")) {
            timeStart.setText(model.getScore());
        } else if (status.equals("NS")) {
            timeStart.setText(model.getTimeStart().substring(0, 5));
        }

    }

}*/
