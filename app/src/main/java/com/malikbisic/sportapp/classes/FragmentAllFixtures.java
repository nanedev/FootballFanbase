//package com.malikbisic.sportapp.activity;


/**
 * A simple {@link Fragment} subclass.
 */
/*public class FragmentAllFixtures extends Fragment {


    private String URL_BASE = "https://soccer.sportmonks.com/api/v2.0/fixtures/date/";
    private String URL_APIKEY = "?api_token=wwA7eL6lditWNSwjy47zs9mYHJNM6iqfHc3TbnMNWonD0qSVZJpxWALiwh2s";
    private String URL_INCLUDES = "&include=localTeam,visitorTeam,league.country";
    public static final int OPEN_NEW_DATE = 12345;
    private String dateFixtures;
    ArrayList<AllFixturesModel> listFixtures = new ArrayList();
    RecyclerView fixturesRec;
    AllFixturesAdapter adapter;
    TextView dontPlay;

    Calendar c;
    SimpleDateFormat df;
    String formattedDate;

    String homeTeamName;
    String awayTeamName;
    String homeTeamLogo;
    String awayTeamLogo;
    String leagueName;
    String mstartTime;
    String datum;
    String idFixtures;
    String countryName;
    int localTeamId;
    int visitorTeamId;

    Button prevBtn;
    Button nextBtn;
    TextView dateLabel;
    String statusS;
    String ftScore;

    String prevLeagueName = "";
    String currentLeagueName = "";

    Toolbar fixturesToolbar;
    TextView titleToolbar;
    List<LeagueNameAllFixtureModel> leagueNameAllFixtureModels;

    ArrayList<String> leagueNameArray = new ArrayList<>();
    ArrayList<String> countryNameArray = new ArrayList<>();
    public Map<String, Object> stringArrayListHashtable;

    public FragmentAllFixtures() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_all_fixtures, container, false);
        setHasOptionsMenu(true);

        leagueNameAllFixtureModels = new ArrayList<>();
        fixturesRec = (RecyclerView) view.findViewById(R.id.allFixtures_recView);
        titleToolbar = (TextView) view.findViewById(R.id.toolbar_title);
        fixturesRec.setLayoutManager(new LinearLayoutManager(getActivity()));
        fixturesToolbar = (Toolbar) view.findViewById(R.id.toolbarAllFixtures);
        dontPlay = (TextView) view.findViewById(R.id.emptyMathes);
        ((AppCompatActivity) getActivity()).setSupportActionBar(fixturesToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        c = Calendar.getInstance();

        System.out.println("Current time => " + c.getTime());

        df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c.getTime());
        listFixtures.clear();
        fixtures(formattedDate);


        String mytime = formattedDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(mytime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy");
        String finalDate = timeFormat.format(myDate);
        titleToolbar.setText(finalDate);


        return view;
    }

    public void fixtures(String formattedDate) {
        int date = 2017 - 12 - 10;
        String fullURL = URL_BASE + "2017-12-10" + URL_APIKEY + URL_INCLUDES;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {


                    JSONArray dataArray = response.getJSONArray("data");
                    if (dataArray.length() == 0) {
                        fixturesRec.setVisibility(View.GONE);
                        dontPlay.setVisibility(View.VISIBLE);
                    } else {
                        fixturesRec.setVisibility(View.VISIBLE);
                        dontPlay.setVisibility(View.GONE);

                        for (int d = 0; d < dataArray.length(); d++) {
                            JSONObject objectMain = dataArray.getJSONObject(d);

                            JSONObject leagueMain = objectMain.getJSONObject("league");
                            JSONObject dataLeague = leagueMain.getJSONObject("data");
                            JSONObject countryNameOb = dataLeague.getJSONObject("country");
                            JSONObject contryNameData = countryNameOb.getJSONObject("data");

                            String leagueNameClub = dataLeague.getString("name");
                            countryName = contryNameData.getString("name");
                            if (!leagueNameArray.contains(leagueNameClub)) {
                                leagueNameArray.add(leagueNameClub);
                            }
                        }
                        Log.i("lige", leagueNameArray.toString());
                        for (int y = 0; y < leagueNameArray.size(); y++) {

                            for (int i = 0; i < dataArray.length(); i++) {


                                JSONObject objectMain = dataArray.getJSONObject(i);
                                idFixtures = objectMain.getString("id");

                                JSONObject locTeam = objectMain.getJSONObject("localTeam");
                                JSONObject visTeam = objectMain.getJSONObject("visitorTeam");
                                JSONObject localTeamObj = locTeam.getJSONObject("data");
                                JSONObject visitorTeamObj = visTeam.getJSONObject("data");

                                homeTeamName = localTeamObj.getString("name");
                                homeTeamLogo = localTeamObj.getString("logo_path");
                                localTeamId = localTeamObj.getInt("id");
                                awayTeamName = visitorTeamObj.getString("name");
                                awayTeamLogo = visitorTeamObj.getString("logo_path");
                                visitorTeamId = visitorTeamObj.getInt("id");

                                JSONObject timeMain = objectMain.getJSONObject("time");
                                JSONObject starting_at = timeMain.getJSONObject("starting_at");

                                mstartTime = starting_at.getString("time");
                                datum = starting_at.getString("date");
                                statusS = timeMain.getString("status");


                                JSONObject scores = objectMain.getJSONObject("scores");
                                String localScore = scores.getString("localteam_score");
                                String visitScore = scores.getString("visitorteam_score");

                                ftScore = localScore + " - " + visitScore;

                                JSONObject leagueMain = objectMain.getJSONObject("league");
                                JSONObject dataLeague = leagueMain.getJSONObject("data");
                                JSONObject countryNameOb = dataLeague.getJSONObject("country");
                                JSONObject contryNameData = countryNameOb.getJSONObject("data");

                                leagueName = dataLeague.getString("name");
                                countryName = contryNameData.getString("name");


                                listFixtures.add(new AllFixturesModel(homeTeamName, homeTeamLogo, awayTeamName, awayTeamLogo, mstartTime, datum, statusS, ftScore, idFixtures, localTeamId, visitorTeamId));
                                prevLeagueName = currentLeagueName;


                                leagueNameAllFixtureModels.add(new LeagueNameAllFixtureModel(leagueName, listFixtures, countryName));
                            }
                            adapter = new AllFixturesAdapter(leagueNameAllFixtureModels, getActivity());
                            fixturesRec.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            Set<LeagueNameAllFixtureModel> foo = new HashSet<LeagueNameAllFixtureModel>(leagueNameAllFixtureModels);
                            leagueNameAllFixtureModels.clear();
                            leagueNameAllFixtureModels.addAll(foo);


                        }


                    }
                } catch (JSONException e) {
                    Log.e("ERROR json", e.getLocalizedMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR Volley", error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(getActivity()).add(request);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.all_fixtures_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.openCalendar) {
            startActivityForResult(new Intent(getActivity(), DateActivity.class), OPEN_NEW_DATE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_NEW_DATE && resultCode == Activity.RESULT_OK) {

            listFixtures.clear();
            currentLeagueName = "";
            prevLeagueName = "";

            String selectedDate = data.getStringExtra("newDate");
            fixtures(selectedDate);

            String mytime = selectedDate;
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault());
            Date myDate = null;
            try {
                myDate = dateFormat.parse(mytime);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String finalDate = timeFormat.format(myDate);
            titleToolbar.setText(finalDate);

        }
    }
}
*/