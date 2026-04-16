package com.galaxian.demo;

// スコアを表すクラス
public class Score {
    private String player;
    private int score;

    public Score() {}

    public Score(String player, int score){
        this.player = player;
        this.score = score;
    }

    public String getPlayer() { return player; } // プレイヤー名を取得
    public void setPlayer(String player) { this.player = player; } // プレイヤー名を設定

    public int getScore() { return score; } // スコアを取得
    public void setScore(int score) { this.score = score; }
}
