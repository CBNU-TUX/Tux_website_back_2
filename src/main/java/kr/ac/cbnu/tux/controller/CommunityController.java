package kr.ac.cbnu.tux.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import kr.ac.cbnu.tux.domain.*;
import kr.ac.cbnu.tux.dto.CommunityDTO;
import kr.ac.cbnu.tux.dto.CommunityListDTO;
import kr.ac.cbnu.tux.enums.CommunityPostType;
import kr.ac.cbnu.tux.service.AttachmentService;
import kr.ac.cbnu.tux.service.CommunityService;
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

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Controller
public class CommunityController {

    private final CommunityService communityService;
    private final AttachmentService attachmentService;

    @Autowired
    public CommunityController(CommunityService communityService, AttachmentService attachmentService) {
        this.communityService = communityService;
        this.attachmentService = attachmentService;
    }

    /* 파일 업로드 및 글쓰기 */

    @PostMapping("/api/community")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createWithoutFileUpload(@RequestParam("type") String type, @RequestBody Community post,
                                        @AuthenticationPrincipal User user) {
        try {
            communityService.createWithoutFileUpload(convertType(type), post, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(path = "/api/community/file")
    @ResponseBody
    public Long fileUploadBeforeCreation(
            @RequestParam("type") String type, @RequestParam("file") MultipartFile multipartFile,
            @AuthenticationPrincipal User user) {
        try {
            Community post = communityService.temporalCreate(convertType(type), user);
            Attachment file = attachmentService.create(multipartFile, post);
            communityService.addAttachment(file, post);
            FileHandler.saveAttactment("community", post.getId().toString(), multipartFile);
            return post.getId();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(path = "/api/community/{id}/file")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void addFile(
            @PathVariable("id") Long id, @RequestParam("file") MultipartFile multipartFile,
            @AuthenticationPrincipal User user) {
        try {
            Community post = communityService.getData(id).orElseThrow();

            if (user.getId().equals(post.getUser().getId())) {
                Attachment file = attachmentService.create(multipartFile, post);
                communityService.addAttachment(file, post);
                FileHandler.saveAttactment("community", post.getId().toString(), multipartFile);
            } else {
                throw new Exception("User not matched");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/api/community/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void updateAfterTemporalCreate(@PathVariable("id") Long id, @RequestBody Community post,
                                          @AuthenticationPrincipal User user) {
        try {
            communityService.updateAfterTemporalCreate(id, post, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @PutMapping("/api/community/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void update(@PathVariable("id") Long id,  @RequestParam("type") String type, @RequestBody Community updated,
                       @AuthenticationPrincipal User user) {
        try {
            communityService.update(id, convertType(type), updated, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/api/community/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(@PathVariable("id") Long id, @AuthenticationPrincipal User user) {
        try {
            communityService.delete(id, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/community/{id}")
    @ResponseBody
    public CommunityDTO read(@PathVariable("id") Long id) {
        try {
            Community post = communityService.read(id).orElseThrow();
            return CommunityDTO.build(post);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }


    /* 게시판 리스트 조회 */

    @GetMapping("/api/community/list")
    @ResponseBody
    public Page<CommunityListDTO> list(@RequestParam(name = "query", defaultValue = "") String query, Pageable pageable) {
        try {
            Page<Community> found;
                if (StringUtils.hasText(query)) {
                    found = communityService.searchList(query, pageable);
                } else {
                    found = communityService.list(pageable);
            }

            return new PageImpl<>(
                found.getContent().stream().map(post -> CommunityListDTO.build(post)).toList(),
                pageable,
                found.getTotalElements()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/community/list/category")
    @ResponseBody
    public Page<CommunityListDTO> listByCategory(
            @RequestParam(name = "query", defaultValue = "") String query, @RequestParam String type, Pageable pageable) {
        try {
            Page<Community> found;
            if (StringUtils.hasText(query)) {
                found = communityService.searchListByCategory(query, pageable, convertType(type));
            } else {
                found = communityService.listByCategory(pageable, convertType(type));
            }
            return new PageImpl<>(
                    found.getContent().stream().map(post -> CommunityListDTO.build(post)).toList(),
                    pageable,
                    found.getTotalElements()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /* 댓글 */
    @PostMapping("/api/community/{id}/comment")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void addComment(@PathVariable("id") Long id, @RequestBody CmComment comment,
                           @AuthenticationPrincipal User user) {
        try {
            communityService.addComment(id, comment, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/api/community/{id}/comment/{commentId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId,
                              @AuthenticationPrincipal User user) {
        try {
            communityService.deleteComment(commentId, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /* 첨부파일 다운로드 */
    @GetMapping(value = "/api/community/{id}/file/{filename}")
    public ResponseEntity<FileSystemResource> getFile(
            @PathVariable("id") String id, @PathVariable("filename") String filename) throws Exception {
        String path = System.getProperty("user.dir") +
                String.format("/src/main/resources/static/file/community/%s/%s", id, URLDecoder.decode(filename, StandardCharsets.UTF_8));

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
    @DeleteMapping(value = "/api/community/{id}/file/{filename}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteFile(@PathVariable("id") Long id, @PathVariable("filename") String filename,
                           @AuthenticationPrincipal User user) {
        try {
            Community post = communityService.getData(id).orElseThrow();

            if (user.getId().equals(post.getUser().getId())) {
                Attachment file = attachmentService.getFile(URLDecoder.decode(filename, StandardCharsets.UTF_8), post).orElseThrow();
                attachmentService.delete(file, post);
            } else {
                throw new Exception("User not matched");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private CommunityPostType convertType(String type) {
        if (type.equals("notice"))  return CommunityPostType.NOTICE;
        if (type.equals("teamrecruitment")) return CommunityPostType.TEAMRECRUITMENT;
        if (type.equals("contest")) return CommunityPostType.CONTEST;
        if (type.equals("job")) return CommunityPostType.JOB;
        else return CommunityPostType.FREE;
    }
}
