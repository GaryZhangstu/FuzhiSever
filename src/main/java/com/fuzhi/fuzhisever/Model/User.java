package com.fuzhi.fuzhisever.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class User {

    @Id
    private String id;

    private String name;

    private String pwd;

    private String phoneNumber;

    private String email;

    private Integer age;

    private String avatar;

    private Integer gender;

    @DBRef
    private List<SkinAnalysis> skinAnalysisList;
}
