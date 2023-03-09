package com.squarecross.photoalbum.controller;

import com.squarecross.photoalbum.dto.PhotoDto;
import com.squarecross.photoalbum.service.PhotoService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadPhotos(@RequestParam("photoIds") Long[] photoIds, HttpServletResponse response) {
        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;
        try {
            if(photoIds.length == 1){
                File file = photoService.getImageFile(photoIds[0]);
                OutputStream outputStream = response.getOutputStream();
                IOUtils.copy(new FileInputStream(file), outputStream);
                outputStream.close();
            }else {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/zip");
                response.addHeader("Content-Disposition", "attachment; filename=\"사진.zip\"");

                try {
                    zipOut = new ZipOutputStream(response.getOutputStream());
                    byte[] buffer = new byte[2048];
                    FileInputStream in = null;

                    for(Long photoId : photoIds){
                        File file = photoService.getImageFile(photoId);
                        in = new FileInputStream(file);
                        zipOut.putNextEntry(new ZipEntry(file.getName()));

                        int len;
                        while((len = in.read(buffer)) > 0){
                            zipOut.write(buffer, 0, len);
                        }
                        zipOut.closeEntry();
                        in.close();
                    }
                    zipOut.close();
                }catch (IOException e){
                    try { if(fis != null)fis.close(); } catch (IOException e1) {e1.getMessage();/*ignore*/}
                    try { if(zipOut != null)zipOut.closeEntry();} catch (IOException e2) {e2.getMessage();/*ignore*/}
                    try { if(zipOut != null)zipOut.close();} catch (IOException e3) {e3.getMessage();/*ignore*/}
                    try { if(fos != null)fos.close(); } catch (IOException e4) {e4.getMessage();/*ignore*/}
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("error");
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
