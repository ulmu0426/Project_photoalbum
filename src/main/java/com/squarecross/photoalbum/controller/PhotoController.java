package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/albums/{albumId}/photos")
public class PhotoController {
    @Autowired
    private PhotoService photoService;

    @RequestMapping(value = "/{photoId}", method = RequestMethod.GET)
    public ResponseEntity<PhotoDto> getPhotoInfo(@PathVariable("photoId") final Long photoId){
        PhotoDto photoDto = photoService.getPhoto(photoId);
        return new ResponseEntity<>(photoDto, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<List<PhotoDto>> uploadPhotos(@PathVariable("albumId") final Long albumId,
                                                       @RequestParam("photos") final MultipartFile[] files) throws IOException {
        List<PhotoDto> photos = new ArrayList<>();
        for(MultipartFile file : files){
            PhotoDto photoDto = photoService.savePhoto(albumId, file);
            photos.add(photoDto);
        }
        return new ResponseEntity<>(photos, HttpStatus.OK);
    }
}
