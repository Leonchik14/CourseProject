package com.app.cardsapplication.utils;

import com.app.cardsapplication.models.Card;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Objects;

public class Tools {

    public static ObjectId GetCardId(Card card) {
        MongoDatabase database = MongoUtil.getClient().getDatabase("cardbox");
        MongoCollection<Document> collection = database.getCollection("cards");


        Bson frontCondition = card.getFrontFile() != null
                ? Filters.eq("frontFile", card.getFrontFile())
                : Filters.eq("frontText", card.getFrontText());

        Bson backCondition = card.getBackFile() != null
                ? Filters.eq("backFile", card.getBackFile())
                : Filters.eq("backText", card.getBackText());

        Document foundCard = collection.find(Filters.and(frontCondition, backCondition)).first();

        return (ObjectId) Objects.requireNonNull(foundCard).get("_id");
    }
}
