package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.Constants;
import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.domain.Photo;
import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.mapper.PhotoMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.apache.tika.Tika;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private AlbumRepository albumRepository;

    private final String original_path = Constants.PATH_PREFIX + "/photos/original";
    private final String thumb_path = Constants.PATH_PREFIX + "/photos/thumb";

    public PhotoDto getPhoto(Long photoId) {
        Optional<Photo> res = photoRepository.findById(photoId);
        //값이 있는경우에만 반환
        if (res.isPresent()) {
            PhotoDto photoDto = PhotoMapper.convertToDto(res.get());
            return photoDto;
        } else {
            throw new EntityNotFoundException(String.format("사진 아이디 %d로 조회되지 않았습니다.", photoId));
        }
    }

    public PhotoDto savePhoto(Long albumId, MultipartFile file) throws IOException {
        Optional<Album> res = albumRepository.findById(albumId);
        if(res.isEmpty()){
            throw new EntityNotFoundException("앨범이 존재하지 않습니다.");
        }

        String fileName = file.getOriginalFilename();
        String filePath = albumId + "/" + fileName;
        //파일 확장자 및 img 체크
        if(!checkFile(file, Paths.get(original_path + "/" + filePath))){
            throw new IOException("파일 확장자가 Image 형식이 아닙니다.");
        }


        int fileSize = (int)file.getSize(); //int가 나타낼수 있는 최대 파일 크기 = 대략 2GB - 사진파일은 그렇게 큰 파일이 아니므로 int형 선언
        fileName = getNextFileName(fileName, albumId);
        saveFile(file, albumId, fileName);

        Photo photo = new Photo();
        photo.setOriginalUrl("/photos/original/" + albumId + "/" + fileName);
        photo.setThumbUrl("/photos/thumb/" + albumId + "/" + fileName);
        photo.setFileName(fileName);
        photo.setFileSize(fileSize);
        photo.setAlbum(res.get());
        Photo createdPhoto = photoRepository.save(photo);

        return PhotoMapper.convertToDto(createdPhoto);
    }

    private String getNextFileName(String fileName, Long albumId){
        String fileNameNoExt = StringUtils.stripFilenameExtension(fileName);
        String ext = StringUtils.getFilenameExtension(fileName);

        Optional<Photo> res = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);

        int count = 2;
        while(res.isPresent()){
            fileName = String.format("%s (%d).%s", fileNameNoExt, count, ext);
            res = photoRepository.findByFileNameAndAlbum_AlbumId(fileName, albumId);
            count++;
        }
        return fileName;
    }

    private void saveFile(MultipartFile file, Long albumId, String fileName) throws IOException {
        try {
            String filePath = albumId + "/" + fileName;
            Files.copy(file.getInputStream(), Paths.get(original_path + "/" + filePath));

            BufferedImage thumbImg = Scalr.resize(ImageIO.read(file.getInputStream()), Constants.THUMB_SIZE, Constants.THUMB_SIZE);
            File thumbFile = new File(thumb_path + "/" + filePath);
            String ext = StringUtils.getFilenameExtension(fileName);
            if (ext == null) {
                throw new IllegalArgumentException("No Extention");
            }
            ImageIO.write(thumbImg, ext, thumbFile);
        } catch (Exception e){
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    private boolean checkFile(MultipartFile file, Path filePath) throws IOException {
        if(!file.isEmpty()){
            List<String> notValidTypeList = Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/gif", "image/bmp", "image/x-windows-bmp");
            Tika tika = new Tika();
            String mimeType = tika.detect(filePath);

            return notValidTypeList.stream().anyMatch(notValidType -> notValidType.equals(mimeType));
        }else {
            return false;
        }
    }

    public File getImageFile(Long photoId){
        Optional<Photo> res = photoRepository.findById(photoId);
        if(res.isEmpty()){
            throw new EntityNotFoundException(String.format("사진의 ID %d를 찾을 수 없습니다.", photoId));
        }
        return new File(Constants.PATH_PREFIX + res.get().getOriginalUrl());
    }

    public List<PhotoDto> getPhotoList(String keyword, String sort, String orderBy) {
        List<Photo> photos;
        if(Objects.equals(sort, "byName")){
            if (Objects.equals(orderBy, "desc")) {
                photos = photoRepository.findByFileNameContainingOrderByFileNameDesc(keyword);
            } else {
                photos = photoRepository.findByFileNameContainingOrderByFileNameAsc(keyword);
            }
        }else if(Objects.equals(sort, "byDate")){
            if (Objects.equals(orderBy, "desc")) {
                photos = photoRepository.findByFileNameContainingOrderByUploadedAtDesc(keyword);
            } else {
                photos = photoRepository.findByFileNameContainingOrderByUploadedAtAsc(keyword);
            }
        }else {
            throw new IllegalArgumentException("알 수 없는 정렬 기준입니다.");
        }
        List<PhotoDto> photoDtoList = PhotoMapper.convertToDtoList(photos);

        return photoDtoList;
    }

    public List<PhotoDto> deletePhoto(List<Long> photosId) {
        for (Long photoId : photosId){
            Optional<Photo> photo = this.photoRepository.findById(photoId);
            if (photo.isEmpty()){
                throw new NoSuchElementException(String.format("Album ID '%d'가 존재하지 않습니다", photo));
            }

            String originPath = Constants.PATH_PREFIX + photo.get().getOriginalUrl();
            String thumbPath = Constants.PATH_PREFIX + photo.get().getThumbUrl();

            File originD = new File(originPath);
            File thumbD = new File(thumbPath);

            this.photoRepository.delete(photo.get());
            originD.delete();
            thumbD.delete();
        }

        List<PhotoDto> photoDtoList = PhotoMapper.convertToDtoList(photoRepository.findAll());
        return photoDtoList;
    }
}
