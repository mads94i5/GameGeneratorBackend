package com.example.gamegenerator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class GameRating {

    /**
     * These must be a string because the @DecimalMax and @DecimalMin annotations only accepts strings,
     * and the value must be a constant, so we cannot use a double and cast it to a string.
     */
    private static final String MAX_SCORE_STRING = "5.0";
    private static final String MIN_SCORE_STRING = "1.0";

    /**
     * But we can allow code to access the value as a double by parsing the string,
     * and exposing it using the public visibility modifier.
     * Wierd? Yes. But it works. :) 
     * 
     * Suggestions for improvement are welcome.
     */
    public static double MAX_SCORE = Double.parseDouble(MAX_SCORE_STRING);


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A numeric value between MIN_SCORE and MAX_SCORE
     */
    @DecimalMin(value = MIN_SCORE_STRING, inclusive = true, message = "Value must be greater than or equal to 1")
    @DecimalMax(value = MAX_SCORE_STRING, inclusive = true, message = "Value must be less than or equal to 5")
    private double score;

    /**
     * The game idea that this rating is for
     */
    @ManyToOne
    private GameIdea gameIdea;

    public GameRating(double score, GameIdea gameIdea) {
        this.score = score;
        this.gameIdea = gameIdea;
    }
}
