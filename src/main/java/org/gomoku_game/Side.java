package org.gomoku_game;

public enum Side
{
    BLACK (0,"Black"),WHITE (1,"White");
    private int state;
    private String player;

    Side (int state,String player) {this.state = state;this.player = player;}
    public int getState () {return state;}
    public String getPlayer() {return player;}
}