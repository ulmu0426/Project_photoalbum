package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.domain.Album;
import org.springframework.stereotype.Service;
import com.squarecross.photoalbum.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class AlbumService {
    @Autowired
    private AlbumRepository albumRepository;

    public Album getAlbum(Long albumId){
        Optional<Album> res = albumRepository.findById(albumId);
        //값이 있는경우에만 반환
        if(res.isPresent()){
            return res.get();
        } else {
            throw new EntityNotFoundException(String.format("앨범 아이디 %d로 조회되지 않았습니다.", albumId));
        }
    }
}
