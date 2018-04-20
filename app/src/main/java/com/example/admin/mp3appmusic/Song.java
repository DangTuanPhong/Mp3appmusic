package com.example.admin.mp3appmusic;

/**
 * Created by Admin on 12/9/2017.
 */

public class Song {
    private String Tittle;
    private int File;

    public Song(String tittle, int file) {
        Tittle = tittle;
        File = file;
    }

    public String getTittle() {
        return Tittle;
    }

    public void setTittle(String tittle) {
        Tittle = tittle;
    }

    public int getFile() {
        return File;
    }

    public void setFile(int file) {
        File = file;
    }
}
