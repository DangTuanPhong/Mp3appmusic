package com.example.admin.mp3appmusic;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.RenderProcessGoneDetail;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tvTittle, tvStart,tvEnd;
    SeekBar seekBar;
    ImageButton btnNext, btnPlay,btnStop,btnPre;
    Button btnDownload;
    private DownloadManager downloadManager;
    ArrayList<Song> arraySong;
    int positon=0;
    static MediaPlayer mediaPlayer = new MediaPlayer();
    private String folderMusic;
    private ListView listView;

    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Uri music_uri = Uri.parse("https://drive.google.com/open?id=1OAaCXCthjcuYBJ53UuP5K0eDQxb26EhL");

        btnDownload= findViewById(R.id.btn_down);
        tvStart= findViewById(R.id.tv_start);
        tvEnd=  findViewById(R.id.tv_end);
        seekBar= findViewById(R.id.seekBar);
        btnNext=  findViewById(R.id.btn_next);
        btnPlay= findViewById(R.id.btn_play);
        btnStop=  findViewById(R.id.btn_stop);
        btnPre=  findViewById(R.id.btn_prv);
        tvTittle=  findViewById(R.id.tv_tittle);
        listView= findViewById(R.id.listView);
        AddSong();
        KhoitaoMediaplayer();
        listView.getVisibility();
       // tạo file
       folderMusic = "/sdcard/mp3song/";
       File folder = new File(folderMusic);
       folder.mkdirs();


       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
           }
       });

       btnNext.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               positon++;
               if (positon > arraySong.size() - 1) {
                   positon = 0;
               }
               if (mediaPlayer.isPlaying()) {
                   mediaPlayer.stop();
               }
               KhoitaoMediaplayer();
               mediaPlayer.start();
               btnPlay.setImageResource(R.drawable.pause_1);
               SetTimeTotal();
               UpdateTimeSong();

           }
       });

       btnPre.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               positon--;
               if (positon < 0) {
                   positon = arraySong.size() - 1;
               }
               if (mediaPlayer.isPlaying()) {
                   mediaPlayer.stop();
               }
               KhoitaoMediaplayer();
               mediaPlayer.start();
               btnPlay.setImageResource(R.drawable.pause_1);
               SetTimeTotal();
               UpdateTimeSong();

           }
       });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.play_1);
                } else {
                        mediaPlayer.start();
                        btnPlay.setImageResource(R.drawable.pause_1);

                        SetTimeTotal();
                        UpdateTimeSong();

                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                btnPlay.setImageResource(R.drawable.play_1);
                KhoitaoMediaplayer();
            }
        });

        // phần download
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               DownloadData(music_uri,view);
            }
        });

    }


    private void UpdateTimeSong(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            SimpleDateFormat Dinhdangphut = new SimpleDateFormat("mm:ss");
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            // khi hết bài sẽ chuyển sang bài tiếp
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    positon++;
                    if (positon > arraySong.size() - 1) {
                        positon = 0;
                    }
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    KhoitaoMediaplayer();
                    mediaPlayer.start();
                    SetTimeTotal();
                    UpdateTimeSong();
                }
            });


            tvStart.setText(Dinhdangphut.format(mediaPlayer.getCurrentPosition()));
            handler.postDelayed(this, 500);
            }
        },100);
    }

    private void AddSong() {
        arraySong= new ArrayList<>();
        arraySong.add(new Song("bài hát",R.raw.bai_hat));
        arraySong.add(new Song("chia tay",R.raw.chia_tay));
        arraySong.add(new Song("phía sau một cô gái",R.raw.phia_sau_mot_co_gai));
        arraySong.add(new Song("tremor",R.raw.tremor));
    }

    private void KhoitaoMediaplayer(){
        mediaPlayer= MediaPlayer.create(MainActivity.this, arraySong.get(positon).getFile());
        tvTittle.setText(arraySong.get(positon).getTittle());
    }

    private void SetTimeTotal(){
        SimpleDateFormat dinhdangphut = new SimpleDateFormat("mm:ss");  // chuyển tổng thời gian bài hát sang dạng phút: giây
        tvEnd.setText(dinhdangphut.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration()); // set tổng thời gian thanh seekbar = duration bài hát.
    }

    private long DownloadData (Uri uri, View v) {

        final long downloadReference;

        // Create request for android download manager
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        final DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Download mp3");

        //Setting description of request
        request.setDescription("test tải nhạc");

                request.setDestinationInExternalPublicDir(folderMusic,  "lastfile.mp3");
                downloadReference = downloadManager.enqueue(request);
                return downloadReference;
                //file:///storage/emulated/0/sdcard/mp3song/lastfile.mp3
        }


    //Retrieve a list of Music files currently listed in the Media store DB via URI.

    private void GetListAudio() {

        //Some audio may be explicitly marked as not being music
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

         cursor = this.managedQuery(
                 MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, //  content://media/external/audio/media
//                 Uri.parse("/sdcard/MOBState"),
                projection,
                selection,
                null,
                null);

        List<String> songs = new ArrayList<String>();
        while (cursor.moveToNext()) {
            songs.add(
                    cursor.getString(0) + "||" +
                    cursor.getString(1) + "||" +
                    cursor.getString(2) + "||" +
                    cursor.getString(3) + "||" +
                    cursor.getString(4) + "||" +
                    cursor.getString(5));
        }
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,songs);
        listView.setAdapter(adapter);

    }


}




