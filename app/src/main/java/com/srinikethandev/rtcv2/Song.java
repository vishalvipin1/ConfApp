package com.srinikethandev.rtcv2;

public class Song {

    private  String songname,songurl;

    public Song(String songname, String songurl) {
        this.songname = songname;
        this.songurl = songurl;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getSongurl() {
        return songurl;
    }

    public void setSongurl(String songurl) {
        this.songurl = songurl;
    }
}
