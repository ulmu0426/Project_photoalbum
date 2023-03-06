package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.Constants;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest     //스프링 컨테이너 내에 있는 빈을 DI로 가져와 사용할 수 있도록 @Autowired로 모든 빈 사용 가능
@Transactional
class AlbumServiceTest {

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    AlbumService albumService;

    @Autowired
    PhotoRepository photoRepository;

    @Test
    void getAlbum() {
        Album album = new Album();
        album.setAlbumName("테스트");
        Album savedAlbum = albumRepository.save(album);

        AlbumDto resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
        assertEquals("테스트", resAlbum.getAlbumName());
    }

    @Test
    void testPhotoCount() {
        Album album = new Album();
        album.setAlbumName("테스트앨범");
        Album savedAlbum = albumRepository.save(album);

        Photo photo1 = new Photo();
        photo1.setFileName("사진1");
        photo1.setAlbum(savedAlbum);
        photoRepository.save(photo1);

        Photo photo2 = new Photo();
        photo2.setFileName("사진2");
        photo2.setAlbum(savedAlbum);
        photoRepository.save(photo2);

        Photo photo3 = new Photo();
        photo3.setFileName("사진3");
        photo3.setAlbum(savedAlbum);
        photoRepository.save(photo3);

        Photo photo4 = new Photo();
        photo4.setFileName("사진4");
        photo4.setAlbum(savedAlbum);
        photoRepository.save(photo4);


        AlbumDto resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());
        assertEquals(4, resAlbum.getCount());
    }

    @Test
    void createAlbum() throws IOException {
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("테스트");
        AlbumDto resDto = albumService.createAlbum(albumDto);

        assertEquals("테스트", resDto.getAlbumName());
        assertNotNull(resDto.getAlbumId());
        assertNotNull(resDto.getCreatedAt());

        Files.createDirectories(Paths.get(Constants.PATH_PREFIX + "/photos/original/" + resDto.getAlbumId()));
        Files.createDirectories(Paths.get(Constants.PATH_PREFIX + "/photos/thumb/" + resDto.getAlbumId()));

        Files.delete(Paths.get(Constants.PATH_PREFIX + "/photos/original/" + resDto.getAlbumId()));
        Files.delete(Paths.get(Constants.PATH_PREFIX + "/photos/thumb/" + resDto.getAlbumId()));
    }

    @Test
    void testAlbumRepository() throws InterruptedException {
        Album album1 = new Album();
        Album album2 = new Album();
        album1.setAlbumName("aaa");
        album2.setAlbumName("aab");

        albumRepository.save(album1);
        TimeUnit.MILLISECONDS.sleep(1);
        albumRepository.save(album2);

        //최신순 정렬 테스트
        List<Album> resDateSort = albumRepository.findByAlbumNameContainingOrderByCreatedAtDesc("aa");
        assertEquals("aab", resDateSort.get(0).getAlbumName());
        assertEquals("aaa", resDateSort.get(1).getAlbumName());
        assertEquals(2, resDateSort.size());

        //이름순 정렬 테스트
        List<Album> resNameSort = albumRepository.findByAlbumNameContainingOrderByAlbumNameAsc("aa");
        assertEquals("aaa", resNameSort.get(0).getAlbumName());
        assertEquals("aab", resNameSort.get(1).getAlbumName());
        assertEquals(2, resNameSort.size());

    }

    @Test
    void testChangeAlbumName() throws IOException {
        //앨범 생성
        AlbumDto albumDto = new AlbumDto();
        albumDto.setAlbumName("변경전");
        AlbumDto res = albumService.createAlbum(albumDto);

        Long albumId = res.getAlbumId(); // 생성된 앨범 아이디 추출
        AlbumDto updateDto = new AlbumDto();
        updateDto.setAlbumName("변경후"); // 업데이트용 Dto 생성
        albumService.changeName(albumId, updateDto);

        AlbumDto updatedDto = albumService.getAlbum(albumId);

        //앨범명 변경되었는지 확인
        assertEquals("변경후", updatedDto.getAlbumName());
    }

    @Test
    void testDeleteAlbum() throws IOException {
        //앨범 생성
        Album album = new Album();
        album.setAlbumName("테스트앨범");
        Album savedAlbum = albumRepository.save(album);

        Photo photo1 = new Photo();
        photo1.setFileName("사진1");
        photo1.setAlbum(savedAlbum);
        photoRepository.save(photo1);

        Photo photo2 = new Photo();
        photo2.setFileName("사진2");
        photo2.setAlbum(savedAlbum);
        photoRepository.save(photo2);

        Photo photo3 = new Photo();
        photo3.setFileName("사진3");
        photo3.setAlbum(savedAlbum);
        photoRepository.save(photo3);

        Photo photo4 = new Photo();
        photo4.setFileName("사진4");
        photo4.setAlbum(savedAlbum);
        photoRepository.save(photo4);

        AlbumDto resAlbum = albumService.getAlbum(savedAlbum.getAlbumId());

        Long albumId = resAlbum.getAlbumId(); // 생성된 앨범 아이디 추출
        albumService.deleteAlbum(albumId);

        Optional<Album> res = this.albumRepository.findById(albumId);
        //앨범명 변경되었는지 확인
        assertTrue(res.isEmpty());
    }
}