package com.squarecross.photoalbum.dto;

import java.util.List;

public class PhotoDtoMove {
    private Long fromAlbumId;

    private Long toAlbumId;

    private List<Long> photosIds;

    public Long getFromAlbumId() {
        return fromAlbumId;
    }

    public void setFromAlbumId(Long fromAlbumId) {
        this.fromAlbumId = fromAlbumId;
    }

    public Long getToAlbumId() {
        return toAlbumId;
    }

    public void setToAlbumId(Long toAlbumId) {
        this.toAlbumId = toAlbumId;
    }

    public List<Long> getPhotosIds() {
        return photosIds;
    }

    public void setPhotosIds(List<Long> photosIds) {
        this.photosIds = photosIds;
    }
}
