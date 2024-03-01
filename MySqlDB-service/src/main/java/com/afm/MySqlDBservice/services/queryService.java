package com.afm.MySqlDBservice.services;



import com.afm.MySqlDBservice.model.Video;
import com.afm.MySqlDBservice.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class queryService {

    private final VideoRepository repository;

    @Autowired
    public queryService(VideoRepository repository) {
        this.repository = repository;
    }

    public List<Video> findAll() {
        return repository.findAll();
    }

    public Optional<Video> findById(Long id) {
        return repository.findById(id);
    }
    public void addVideo(String name,String owner,String url){
        repository.add(name,owner,url);
    }
}

