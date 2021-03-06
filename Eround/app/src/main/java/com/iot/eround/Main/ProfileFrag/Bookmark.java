package com.iot.eround.Main.ProfileFrag;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iot.eround.Adapter.ProfileContentAdapter;
import com.iot.eround.Adapter.ProfileReplyAdapter;
import com.iot.eround.MainActivity;
import com.iot.eround.R;
import com.iot.eround.Util.ApiService;
import com.iot.eround.Util.ImageRoader;
import com.iot.eround.VO.Board;
import com.iot.eround.VO.Feeling;
import com.iot.eround.VO.Heart;
import com.iot.eround.VO.Reply;
import com.iot.eround.VO.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Bookmark extends Fragment {

    public Bookmark(){}

    MainActivity mainActivity;
    ProfileContentAdapter adapter;
    ArrayList<Integer> boardNumList = new ArrayList<>();
    ArrayList<Board> boardList = new ArrayList<>();
    RecyclerView profileContentRecycler;

    Random random = new Random();
    ImageRoader imageRoader = new ImageRoader();
    int[] defaultRandomNumberArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main_profile_content, container, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        mainActivity = (MainActivity) getActivity();

        profileContentRecycler = view.findViewById(R.id.activity_main_profile_content_id_recycler);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);

        profileContentRecycler.setLayoutManager(layoutManager);

        Thread thread = new Thread(() -> {

            Call<List<Heart>> heartFindall = apiService.heartFindall();
            heartFindall.enqueue(new Callback<List<Heart>>() {
                @Override
                public void onResponse(Call<List<Heart>> call, Response<List<Heart>> response) {

                    List<Heart> responseList = response.body();

                    Log.i("test1 test", "1");
                    Log.i("test1 responseList", String.valueOf(responseList.size()));

                    for(int i = 0; i < responseList.size(); i++) {

                        Log.i("test1 test", "2");
                        Log.i("test1  i", String.valueOf(i));

                        boardNumList.add(responseList.get(i).getBoard().getBoardNum());

                        Log.i("test1 test", "3");
                        Log.i("test1 boardNumList", boardNumList.toString());

                    }

                    Call<List<Board>> boardFindall = apiService.boardFindall();
                    boardFindall.enqueue(new Callback<List<Board>>() {
                        @Override
                        public void onResponse(Call<List<Board>> call, Response<List<Board>> response) {

                            for(int i1 = 0; i1 < response.body().size(); i1++) {

                                for(int i2 = 0; i2 < boardNumList.size(); i2++){
                                    if(response.body().get(i1).getBoardNum() == boardNumList.get(i2)){
                                        boardList.add(response.body().get(i1));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Board>> call, Throwable t) {

                            Log.i("test1 onFailure", t.toString());

                        }
                    });

                }

                @Override
                public void onFailure(Call<List<Heart>> call, Throwable t) {

                }
            });


            adapter = new ProfileContentAdapter();

            adapter.setItems(boardList);

            profileContentRecycler.setAdapter(adapter);

        });

        thread.start();

        try{

            thread.join();

        }catch (Exception e){

            Log.i("test1 Join Exception", e.toString());

        }

        adapter.setOnItemClickListener((v, position) -> {

            switch (v.getId()){
                case R.id.list_profile_content_group_id_delete:

                    break;
                case R.id.list_profile_content_group_id_body:

                    ArrayList<Board> reBoard = adapter.getBoardList();
                    String url;

                    if(reBoard.get(position).getAttachFile().size() == 0) {

                        url = ApiService.URL + "/image/" + (random.nextInt(defaultRandomNumberArray.length) + 1) + ".jpg";

                    } else {

                        url = ApiService.URL + reBoard.get(position).getAttachFile().get(1).getFilePath();

                    }

                    ImageView backgroundImage = mainActivity.findViewById(R.id.activity_main_content_id_background_image);
                    backgroundImage.setImageBitmap(imageRoader.getBitmapImg(url));

                    TextView text = mainActivity.findViewById(R.id.activity_main_content_id_body_text);
                    text.setText(reBoard.get(position).getBoardContent());

                    TextView location = mainActivity.findViewById(R.id.activity_main_content_id_body_location);
                    location.setText(reBoard.get(position).getBoardRegion().toString());

                    TextView time = mainActivity.findViewById(R.id.activity_main_content_id_body_time);
                    time.setText(reBoard.get(position).getBoardCreateDate());

                    if (Translate(reBoard.get(position)).getFeeling().getFeelingNum() == 0) {
                        TextView emotion = mainActivity.findViewById(R.id.activity_main_content_id_body_emotion);
                        emotion.setVisibility(View.INVISIBLE);
                    } else {
                        TextView emotion = mainActivity.findViewById(R.id.activity_main_content_id_body_emotion);
                        emotion.setText(reBoard.get(position).getFeeling().getFeelingEmoticon());
                    }

                    TextView favorite = mainActivity.findViewById(R.id.activity_main_content_id_body_favorite);
                    favorite.setText(String.valueOf(reBoard.get(position).getHeart().size()));

                    TextView comment = mainActivity.findViewById(R.id.activity_main_content_id_body_comment);
                    comment.setText(String.valueOf(reBoard.get(position).getReply().size()));

                    Button contentHeaderMenu1 = mainActivity.findViewById(R.id.activity_main_id_content_header_menu);
                    Button contentHeaderClose1 = mainActivity.findViewById(R.id.activity_main_id_content_header_close);
                    Button contentHeaderMore1 = mainActivity.findViewById(R.id.activity_main_id_content_header_more);
                    TextView contentHeaderTitle1 = mainActivity.findViewById(R.id.activity_main_id_content_header_title);
                    LinearLayout contentHeader1 = mainActivity.findViewById(R.id.activity_main_id_content_header);
                    LinearLayout hashHeader1 = mainActivity.findViewById(R.id.activity_main_id_hash_header);
                    LinearLayout writeHeader1 = mainActivity.findViewById(R.id.activity_main_id_write_header);
                    LinearLayout alarmHeader1 = mainActivity.findViewById(R.id.activity_main_id_alarm_header);
                    LinearLayout writeFooter1 = mainActivity.findViewById(R.id.activity_main_id_write_footer);
                    LinearLayout contentFooter1 = mainActivity.findViewById(R.id.activity_main_id_content_footer);

                    mainActivity.changeFragment();

                    contentFooter1.setBackgroundColor(Color.argb(0,0,0,0));
                    contentFooter1.setVisibility(View.VISIBLE);

                    contentHeader1.setVisibility(View.VISIBLE);
                    hashHeader1.setVisibility(View.GONE);
                    writeHeader1.setVisibility(View.GONE);
                    alarmHeader1.setVisibility(View.GONE);
                    writeFooter1.setVisibility(View.GONE);

                    contentHeaderMenu1.setVisibility(View.VISIBLE);
                    contentHeaderClose1.setVisibility(View.INVISIBLE);
                    contentHeaderMore1.setVisibility(View.INVISIBLE);
                    contentHeaderTitle1.setText("AROUND");

                    break;
            }

        }) ;

        return view;

    }

    public Board Translate(Board board) {

        Optional<Board> optionalBoard = Optional.of(board);

        Feeling feeling = new Feeling(0, "", "", null, null, null);

        board.setFeeling(optionalBoard.map(Board::getFeeling).orElse(feeling));

        return board;
    }

}

