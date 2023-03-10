package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    //기본 CRUD기능은 구현되어있어서 제공되는 메서드를 호출하면 됨.

    //이름순 정렬
    List<Album> findByAlbumNameContainingOrderByAlbumNameAsc(String keyword);
    List<Album> findByAlbumNameContainingOrderByAlbumNameDesc(String keyword);

    List<Album> findByAlbumNameContainingOrderByCreatedAtAsc(String keyword);  //생성날짜 순 정렬
    List<Album> findByAlbumNameContainingOrderByCreatedAtDesc(String keyword);  //생성날짜 순 정렬
}
