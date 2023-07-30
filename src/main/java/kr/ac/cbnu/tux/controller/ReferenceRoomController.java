package kr.ac.cbnu.tux.controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.ac.cbnu.tux.domain.Attachment;
import kr.ac.cbnu.tux.domain.Community;
import kr.ac.cbnu.tux.domain.ReferenceRoom;
import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.dto.CommunityListDTO;
import kr.ac.cbnu.tux.dto.ReferenceRoomDTO;
import kr.ac.cbnu.tux.dto.ReferenceRoomListDTO;
import kr.ac.cbnu.tux.enums.CommunityPostType;
import kr.ac.cbnu.tux.enums.ReferenceRoomPostType;
import kr.ac.cbnu.tux.service.AttachmentService;
import kr.ac.cbnu.tux.service.ReferenceRoomService;
import kr.ac.cbnu.tux.service.UserService;
import kr.ac.cbnu.tux.utility.FileHandler;
import kr.ac.cbnu.tux.utility.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final UserService userService;
    private final AttachmentService attachmentService;

    @Autowired
    public ReferenceRoomController(ReferenceRoomService referenceRoomService, UserService userService, AttachmentService attachmentService) {
        this.referenceRoomService = referenceRoomService;
        this.userService = userService;
        this.attachmentService = attachmentService;
    }


    /* 파일 업로드 및 글쓰기 */
    @PostMapping(path = "/api/referenceroom/file")
    @ResponseBody
    public Long fileUploadBeforeCreation(
            @RequestParam("type") String type, @RequestParam("file") MultipartFile multipartFile) {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
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
    public void addFile(@PathVariable("id") Long id, @RequestParam("file") MultipartFile multipartFile) {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
            ReferenceRoom data = referenceRoomService.getData(id).orElseThrow();

            if (user.getId().equals(data.getUser().getId())) {
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
    public void updateAfterTemporalCreate(@PathVariable("id") Long id, @RequestBody ReferenceRoom data) {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
            referenceRoomService.updateAfterTemporalCreate(id, data, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @PostMapping("/api/referenceroom")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void createWithoutFileUpload(@RequestParam("type") String type, @RequestBody ReferenceRoom data) {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
            referenceRoomService.createWithoutFileUpload(convertType(type), data, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    
    /* 글 삭제 */
    @DeleteMapping("/api/referenceroom/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(@PathVariable("id") Long id) {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
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


    /* 첨부파일 다운로드 */
    @GetMapping(value = "/api/referenceroom/{id}/file/{filename}", produces = MediaType.ALL_VALUE)
    @ResponseBody
    public FileSystemResource getFile(
            @PathVariable("id") String id, @PathVariable("filename") String filename, HttpServletResponse response
    ) throws Exception {
        String path = System.getProperty("user.dir") +
                String.format("/src/main/resources/static/file/referenceroom/%s/%s", id, URLDecoder.decode(filename, StandardCharsets.UTF_8));

        if (new File(path).exists()) {
            response.setContentType(Files.probeContentType(Path.of(path)));
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=" + new String(filename.getBytes("UTF-8"), "ISO-8859-1"
            ));
            return new FileSystemResource(path);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }
    
    private ReferenceRoomPostType convertType(String type) {
        if (type.equals("study"))  return ReferenceRoomPostType.STUDY;
        if (type.equals("exam")) return ReferenceRoomPostType.EXAM;
        if (type.equals("gallery")) return ReferenceRoomPostType.GALLERY;
        else return ReferenceRoomPostType.STUDY;
    }

}
