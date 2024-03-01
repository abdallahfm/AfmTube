package com.afm.MySqlDBservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "video", schema = "afmtube")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;
    @Getter
    @Setter
    private String name;
    @Setter
    @Getter
    private String owner;
    @Setter
    @Getter
    private String url;


}





