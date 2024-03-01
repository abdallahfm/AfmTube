package com.afm.MySqlDBservice.repository;

import com.afm.MySqlDBservice.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    //@Query(value = " INSERT into  metadata.video (name, owner,url) VALUES (?1,?2,?3)")
    @Modifying
    @Transactional
    @Query(value = "INSERT into afmtube.video (name,owner,url) values(:#{#name}, :#{#owner}, :#{#url})",nativeQuery = true)

   public abstract void add(String name, String owner, String url);


}


