package com.app.cardsapplication.models;


import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.File;
import java.util.UUID;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Setter
    private String frontText;

    @Setter
    private String frontFile;

    @Setter
    private String backText;

    @Setter String backFile;


}

