package com.squarecross.photoalbum.domain;

import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;

@Entity
@Table(name = "photo", schema = "photo_album", uniqueConstraints = {@UniqueConstraint(columnNames = "photo_id")})
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //가장 최근 id에 +1해서 다음 아이디 생성
    @Column(name = "photo_id", unique = true, nullable = false)
    private Long photoId;

    @Column(name = "file_name", unique = false, nullable = true)
    private String fileName;

    @Column(name = "thumb_url", unique = false, nullable = true)
    private String thumbUrl;

    @Column(name = "original_url", unique = false, nullable = true)
    private String originalUrl;

    @Column(name = "file_size", unique = false, nullable = true)
    private int fileSize;

    @Column(name = "uploaded_at", unique = false, nullable = true)
    @CreationTimestamp  //신규 사진파일 DB INSERT시 자동으로 현재 시간을 입력
    private Date uploadedAt;

    //Foreign Key
    @ManyToOne(fetch = FetchType.LAZY)  //앨범 정보가 필요할때만 불러옴.
    @JoinColumn(name = "album_id")      //앨범의 PK 참조
    private Album album;

    public Photo(){};

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
