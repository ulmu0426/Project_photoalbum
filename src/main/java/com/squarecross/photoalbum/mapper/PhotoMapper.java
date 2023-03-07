package com.squarecross.photoalbum.mapper;


import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class PhotoMapper {

    public static PhotoDto convertToDto(Photo photo){
        PhotoDto photoDto = new PhotoDto();
        photoDto.setPhotoId(photo.getPhotoId());
        photoDto.setFileName(photo.getFileName());
        photoDto.setFileSize(photo.getFileSize());
        photoDto.setOriginalUrl(photo.getOriginalUrl());
        photoDto.setThumbUrl(photo.getThumbUrl());
        photoDto.setUploadedAt(photo.getUploadedAt());
        return photoDto;
    }
}
