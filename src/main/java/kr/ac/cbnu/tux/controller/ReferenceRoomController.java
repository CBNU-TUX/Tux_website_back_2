package kr.ac.cbnu.tux.controller;

import kr.ac.cbnu.tux.domain.*;
import kr.ac.cbnu.tux.dto.ReferenceRoomDTO;
import kr.ac.cbnu.tux.dto.ReferenceRoomListDTO;
import kr.ac.cbnu.tux.enums.ReferenceRoomPostType;
import kr.ac.cbnu.tux.enums.UserRole;
import kr.ac.cbnu.tux.service.AttachmentService;
import kr.ac.cbnu.tux.service.LikeService;
import kr.ac.cbnu.tux.service.ReferenceRoomService;
import kr.ac.cbnu.tux.service.UserService;
import kr.ac.cbnu.tux.utility.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.net.URLDecoder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class ReferenceRoomController {

    private final ReferenceRoomService referenceRoomService;
    private final AttachmentService attachmentService;
    private final LikeService likeService;

    @Autowired
    public ReferenceRoomController(ReferenceRoomService referenceRoomService, AttachmentService attachmentService,
                                   LikeService likeService) {
        this.referenceRoomService = referenceRoomService;
        this.attachmentService = attachmentService;
        this.likeService = likeService;
    }


    /* 파일 업로드 및 글쓰기 */

    @PostMapping("/api/referenceroom")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void createWithoutFileUpload(@RequestParam("type") String type, @RequestBody ReferenceRoom data,
                                        @AuthenticationPrincipal User user) {
        try {
            referenceRoomService.createWithoutFileUpload(convertType(type), data, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(path = "/api/referenceroom/file")
    @ResponseBody
    public Long fileUploadBeforeCreation(
            @RequestParam("type") String type, @RequestParam("file") MultipartFile multipartFile,
            @AuthenticationPrincipal User user) {
        try {
            ReferenceRoom data = referenceRoomService.temporalCreate(convertType(type), user);
            Attachment file = attachmentService.create(multipartFile, data);
            referenceRoomService.addAttachment(file, data);
            FileHandler.saveAttactment("referenceroom", data.getId().toString(), multipartFile);
            return data.getId();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(path = "/api/referenceroom/{id}/file")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void addFile(@PathVariable("id") Long id, @RequestParam("file") MultipartFile multipartFile,
                        @AuthenticationPrincipal User user) {
        try {
            ReferenceRoom data = referenceRoomService.getData(id).orElseThrow();

            if (user.getId().equals(data.getUser().getId()) || user.getRole() == UserRole.ADMIN) {
                Attachment file = attachmentService.create(multipartFile, data);
                referenceRoomService.addAttachment(file, data);
                FileHandler.saveAttactment("referenceroom", data.getId().toString(), multipartFile);
            } else {
                throw new Exception("User not matched");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/api/referenceroom/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void updateAfterTemporalCreate(@PathVariable("id") Long id, @RequestBody ReferenceRoom data,
                                          @AuthenticationPrincipal User user) {
        try {
            referenceRoomService.updateAfterTemporalCreate(id, data, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /* 글 수정 */
    @PutMapping("/api/referenceroom/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void update(@PathVariable("id") Long id, @RequestParam("type") String type, @RequestBody ReferenceRoom updated,
                       @AuthenticationPrincipal User user) {
        try {
            referenceRoomService.update(id, convertType(type), updated, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    /* 글 삭제 */
    @DeleteMapping("/api/referenceroom/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(@PathVariable("id") Long id, @AuthenticationPrincipal User user) {
        try {
            referenceRoomService.delete(id, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /* 글 읽기 */
    @GetMapping("/api/referenceroom/{id}")
    @ResponseBody
    public ReferenceRoomDTO read(@PathVariable("id") Long id) {
        try {
            ReferenceRoom data = referenceRoomService.read(id).orElseThrow();
            return ReferenceRoomDTO.build(data);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }
    
    /* 자료실 리스트 조회 */

    @GetMapping("/api/referenceroom/list")
    @ResponseBody
    public Page<ReferenceRoomListDTO> list(@RequestParam(name = "query", defaultValue = "") String query, Pageable pageable) {
        try {
            Page<ReferenceRoom> found;
            if (StringUtils.hasText(query)) {
                found = referenceRoomService.searchList(query, pageable);
            } else {
                found = referenceRoomService.list(pageable);
            }

            return new PageImpl<>(
                    found.getContent().stream().map(data -> ReferenceRoomListDTO.build(data)).toList(),
                    pageable,
                    found.getTotalElements()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/referenceroom/list/category")
    @ResponseBody
    public Page<ReferenceRoomListDTO> listByCategory(
            @RequestParam(name = "query", defaultValue = "") String query, @RequestParam String type, Pageable pageable) {
        try {
            Page<ReferenceRoom> found;
            if (StringUtils.hasText(query)) {
                found = referenceRoomService.searchListByCategory(query, pageable, convertType(type));
            } else {
                found = referenceRoomService.listByCategory(pageable, convertType(type));
            }
            return new PageImpl<>(
                    found.getContent().stream().map(data -> ReferenceRoomListDTO.build(data)).toList(),
                    pageable,
                    found.getTotalElements()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    
    /* 댓글 */
    @PostMapping("/api/referenceroom/{id}/comment")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void addComment(@PathVariable("id") Long id, @RequestBody RfComment comment,
                           @AuthenticationPrincipal User user) {
        try {
            referenceRoomService.addComment(id, comment, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/api/referenceroom/{id}/comment/{commentId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId,
                              @AuthenticationPrincipal User user) {
        try {
            referenceRoomService.deleteComment(commentId, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /* 첨부파일 다운로드 */
    @GetMapping(value = "/api/referenceroom/{id}/file/{filename}")
    public ResponseEntity<FileSystemResource> getFile(
            @PathVariable("id") String id, @PathVariable("filename") String filename,
            @RequestParam(name = "aid", defaultValue = "-1") Long aid) throws Exception {
        
        // 다운로드 수 늘리기
        if (aid != -1)
            attachmentService.increaseDownloadCountById(aid);

        String path = System.getProperty("user.dir") +
                String.format("/src/main/resources/static/file/referenceroom/%s/%s", id, URLDecoder.decode(filename, StandardCharsets.UTF_8));

        if (new File(path).exists()) {
            FileSystemResource resource = new FileSystemResource(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Files.probeContentType(Path.of(path))))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                            new String(filename.getBytes("UTF-8"), "ISO-8859-1") + "\"")
                    .body(resource);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }

    /* 첨부파일 삭제 */
    @DeleteMapping(value = "/api/referenceroom/{id}/file/{filename}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteFile(@PathVariable("id") Long id, @PathVariable("filename") String filename,
                           @AuthenticationPrincipal User user) {
        try {
            ReferenceRoom data = referenceRoomService.getData(id).orElseThrow();

            if (user.getId().equals(data.getUser().getId()) || user.getRole() == UserRole.ADMIN) {
                Attachment file = attachmentService.getFile(URLDecoder.decode(filename, StandardCharsets.UTF_8), data).orElseThrow();
                attachmentService.delete(file, data);
            } else {
                throw new Exception("User not matched");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /* 추천 비추천 추가 */
    @PostMapping("/api/referenceroom/{id}/likes")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void addLike(@PathVariable("id") Long id, @RequestParam Boolean dislike,
                        @AuthenticationPrincipal User user) {
        try {
            if (user == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            ReferenceRoom data = referenceRoomService.getData(id).orElseThrow();
            likeService.create(data, user, dislike);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }



    private ReferenceRoomPostType convertType(String type) {
        if (type.equals("study"))  return ReferenceRoomPostType.STUDY;
        if (type.equals("exam")) return ReferenceRoomPostType.EXAM;
        if (type.equals("gallery")) return ReferenceRoomPostType.GALLERY;
        else return ReferenceRoomPostType.STUDY;
    }

}
