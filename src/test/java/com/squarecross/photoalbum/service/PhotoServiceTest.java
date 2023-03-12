package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PhotoServiceTest {
    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    PhotoService photoService;

    @Test
    void getPhoto() {
        Photo photo = new Photo();
        photo.setFileName("테스트");
        Photo savedPhoto = photoRepository.save(photo);

        PhotoDto resPhoto = photoService.getPhoto(savedPhoto.getPhotoId());
        assertEquals("테스트", resPhoto.getFileName());
    }

    @Test
    void getPhotoList() throws InterruptedException {
        Photo photo1 = new Photo();
        Photo photo2 = new Photo();
        photo1.setFileName("aaa");
        photo2.setFileName("aab");

        photoRepository.save(photo1);
        TimeUnit.MILLISECONDS.sleep(1);
        photoRepository.save(photo2);

        //이름순 정렬 테스트
        List<Photo> resDataSort = photoRepository.findByFileNameContainingOrderByFileNameDesc("aa");
        assertEquals("aab", resDataSort.get(0).getFileName());
        assertEquals("aaa", resDataSort.get(1).getFileName());
        assertEquals(2,resDataSort.size());

        //최신순 정렬 테스트
        List<Photo> resNameSort = photoRepository.findByFileNameContainingOrderByUploadedAtDesc("aa");
        assertEquals("aab", resNameSort.get(0).getFileName());
        assertEquals("aaa", resNameSort.get(1).getFileName());
        assertEquals(2,resNameSort.size());
    }
}