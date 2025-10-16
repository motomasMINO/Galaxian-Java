package com.galaxian.demo;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final List<Score> scores = new CopyOnWriteArrayList<>();

    @PostMapping
    public Score submitScore(@RequestBody Score score){
        scores.add(score);
        return score;
    }

    @GetMapping
    public List<Score> getScores(){
        // スコア順に降順ソートして返す
        scores.sort((a,b) -> b.getScore() - a.getScore());
        return scores;
    }
}
