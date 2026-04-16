package com.galaxian.demo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// スコア管理のためのコントローラー
@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final List<Score> scores = new CopyOnWriteArrayList<>();

    // スコアの送信
    @PostMapping
    public Score submitScore(@RequestBody Score score){
        scores.add(score);
        return score;
    }

    // スコアの取得
    @GetMapping
    public List<Score> getScores(){
        // スコア順に降順ソートして返す
        scores.sort((a,b) -> b.getScore() - a.getScore());
        return scores;
    }
}
