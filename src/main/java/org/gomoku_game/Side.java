package org.gomoku_game;

public enum Side
{
    BLACK (0,"Black"),WHITE (1,"White");
    private int code;
    private String str;

    Side (int code, String str) {this.code = code;this.str = str;}
    public int getCode () {return code;}
    public String getStr () {return str;}
}