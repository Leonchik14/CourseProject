package com.app.cardsapplication.utils;
import com.app.cardsapplication.models.Card;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongoUtil {
    private static MongoClient mongoClient;
    @Getter
    private static MongoDatabase database;




    static {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                        .automatic(true)
                        .register("com.app.cardsapplication.models")
                        .register(Card.class.getName())
                        /*.register(Card.Content.class.getName())*/
                        .build())
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017/"))
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("cardbox");
    }

    public static MongoClient getClient() {
        return mongoClient;
    }
}
