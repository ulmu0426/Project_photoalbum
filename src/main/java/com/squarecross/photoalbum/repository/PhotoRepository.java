package com.squarecross.photoalbum.repository;

import com.squarecross.photoalbum.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    int countByAlbum_AlbumId(Long albumId);

    List<Photo> findTop4ByAlbum_AlbumIdOrderByUploadedAtDesc(Long albumId);

    Optional<Photo> findByFileNameAndAlbum_AlbumId(String photoName, Long albumId);

    //이름순 정렬
    List<Photo> findByFileNameContainingOrderByFileNameAsc(String keyword);
    List<Photo> findByFileNameContainingOrderByFileNameDesc(String keyword);

    //업로드 날짜 순 정렬
    List<Photo> findByFileNameContainingOrderByUploadedAtAsc(String keyword);
    List<Photo> findByFileNameContainingOrderByUploadedAtDesc(String keyword);
}
