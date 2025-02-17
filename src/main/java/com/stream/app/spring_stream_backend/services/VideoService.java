package com.stream.app.spring_stream_backend.services;

import com.stream.app.spring_stream_backend.entities.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    //save video
    Video save(Video video , MultipartFile file);


    // get by id
    Video getById(String videoId);
    // get by title
    Video getByTitle(String title);
    // get all
    List<Video> getAll();

    String processVideo(String videoId);
}
