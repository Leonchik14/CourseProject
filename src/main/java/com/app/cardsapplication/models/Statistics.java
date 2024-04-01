package com.app.cardsapplication.models;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;

@AllArgsConstructor
@NoArgsConstructor
public class Statistics {
    @BsonProperty("collectionName")
    public String collectionName;

    @BsonProperty("progress")
    public double progress;

}
