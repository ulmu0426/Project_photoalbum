package com.squarecross.photoalbum.dto;

import java.util.Date;
import java.util.List;

public class AlbumDto {
    //앨범정보를 DB에서 조회후 데이터를 API Response로 보내기 위해 필요한 정보를 매핑해서 전달하는 역할을 함.

    //AlbumId, AlbumName, CreatedAt, Count 정보를 담음

    Long albumId;
    String albumName;
    Date createdAt;
    int count;

    private List<String> thumbUrls;

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getThumbUrls() {
        return thumbUrls;
    }

    public void setThumbUrls(List<String> thumbUrls) {
        this.thumbUrls = thumbUrls;
    }
}
