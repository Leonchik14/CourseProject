package com.app.cardsapplication.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

@Data
public class Collection {
    public String name;
    public List<Card> cards;

    @BsonCreator
    public Collection(@BsonProperty("name") String name, @BsonProperty("cards") List<Card> cards) {
        this.name = name;
        this.cards = cards;
    }

}
