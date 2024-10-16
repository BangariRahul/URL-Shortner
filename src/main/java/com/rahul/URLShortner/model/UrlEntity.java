package com.rahul.URLShortner.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class UrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    private String orignalUrl;


    private String shortUrl;




    public UrlEntity(String orignalUrl) {
        this.orignalUrl = orignalUrl;
    }
}
