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
        final Uri music_uri = Uri.parse("https://zmp3-mp3-s1-te-vnno-zn-5.zadn.vn/de0f10f2c8b621e878a7/8028330437800394190?authen=exp=1522295016~acl=/de0f10f2c8b621e878a7/*~hmac=ae915fe3e14042db4376470a5f0e3d5f&filename=Dung-Ai-Nhac-Ve-Anh-Ay-Tra-My-Idol.mp3");

        btnDownload= (Button) findViewById(R.id.btn_down);
         tvStart= (TextView) findViewById(R.id.tv_start);
         tvEnd= (TextView) findViewById(R.id.tv_end);
         seekBar= (SeekBar) findViewById(R.id.seekBar);
         btnNext= (ImageButton) findViewById(R.id.btn_next);
         btnPlay= (ImageButton) findViewById(R.id.btn_play);
       btnStop= (ImageButton) findViewById(R.id.btn_stop);
        btnPre= (ImageButton) findViewById(R.id.btn_prv);
        tvTittle= (TextView) findViewById(R.id.tv_tittle);
        listView= (ListView) findViewById(R.id.listView);
       // Addsongfromsdcard();
        AddSong();




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

                KhoitaoMediaplayer();
                mediaPlayer.start();
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

       Listnhac();
        GetListAudio();
    }

//    private void playLocalAudio_UsingDescriptor(String audioPath) {
//        AssetFileDescriptor fileDesc = getResources().openRawResourceFd(MainActivity.this, );
//        String mp3path  = Environment.getExternalStoragePublicDirectory(folderMusic).getPath()+"/" + "lastfile.mp3";
//        if (fileDesc != null) {
//            mediaPlayer = new MediaPlayer();
//            try {
//                mediaPlayer.setDataSource(fileDesc.getFileDescriptor(), fileDesc
//                        .getStartOffset(), fileDesc.getLength());
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
        /*mediaPlayer.stop();
        String mp3path  = Environment.getExternalStoragePublicDirectory(folderMusic).getPath()+"/" + "lastfile.mp3";
        mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(mp3path));*/



    }

   /* private void Addsongfromsdcard(){

        try {
            AssetFileDescriptor afd = getAssets().openFd(folderMusic);
        mediaPlayer.setDataSource(afd.getFileDescriptor());
        mediaPlayer.prepare();
        mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private void SetTimeTotal(){
        SimpleDateFormat dinhdangphut = new SimpleDateFormat("mm:ss");  // chuyển tổng thời gian bài hát sang dạng phút: giây
        tvEnd.setText(dinhdangphut.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration()); // set tổng thời gian thanh seekbar = duration bài hát.
    }



//    private void playLocalAudio_UsingDescriptor() throws Exception {
//        AssetFileDescriptor fileDesc = getResources().openRawResourceFd(Integer.parseInt(folderMusic));
//        if (fileDesc != null) {
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setDataSource(fileDesc.getFileDescriptor(), fileDesc
//                    .getStartOffset(), fileDesc.getLength());
//
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        }
//        }
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


//    public void getPublicAlbumStorageDir() {
//        File file;
//        FileOutputStream fos;
//
//        try {
//            file = new File(Environment.getExternalStorageDirectory(),"mp3Album");
//            fos = new FileOutputStream(file);
//            Log.d("MainActivity", Environment.getExternalStorageDirectory().getAbsolutePath());
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri uri= Uri.parse(folderMusic);
        //Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor Songcursor= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Songcursor = contentResolver.query(uri,null,null,null,null,null);
        }
        if (Songcursor!=null){
            Songcursor.moveToFirst();
            int songtitle=Songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            do {
                String song2 = Songcursor.getString(songtitle);
                arrayList.add(song2);
            }
            while (Songcursor.moveToNext());

        }

    }
    public void Listnhac(){
        arrayList =new ArrayList<>();
        getMusic();
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
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




