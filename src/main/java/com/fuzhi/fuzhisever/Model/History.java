package com.fuzhi.fuzhisever.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
@CompoundIndexes({
        @CompoundIndex(name = "id_unique", def = "{'id': 1}", unique = true),
        @CompoundIndex(name = "userId_index", def = "{'userId': 1}", unique = false)
})
public class History {

    @Id
    private String id;

    private String userId;

    private Date timeStamp;

    private Object score;

    private String imageKey;
}
