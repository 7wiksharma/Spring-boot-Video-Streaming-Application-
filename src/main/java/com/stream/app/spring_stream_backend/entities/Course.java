package com.stream.app.spring_stream_backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;

@Entity
@Table(name= "yt_courses")
public class Course {
    @Id
    private String Id;
    private String title;
//    @OneToMany(mappedBy = "course")
//     private ArrayList<Video> list = new ArrayList<>();

}
