package kr.ac.cbnu.tux.controller;

import kr.ac.cbnu.tux.domain.CmComment;
import kr.ac.cbnu.tux.domain.Community;
import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.dto.CommunityDTO;
import kr.ac.cbnu.tux.dto.CommunityListDTO;
import kr.ac.cbnu.tux.enums.CommunityPostType;
import kr.ac.cbnu.tux.service.CommunityService;
import kr.ac.cbnu.tux.service.UserService;
import kr.ac.cbnu.tux.utility.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class CommunityController {

    private final CommunityService communityService;
    private final UserService userService;

    @Autowired
    public CommunityController(CommunityService communityService, UserService userService) {
        this.communityService = communityService;
        this.userService = userService;
    }

    @PostMapping("/api/community")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(@RequestParam("type") String type, @RequestBody Community post) {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
            communityService.create(post, user, convertType(type));
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

    @PutMapping("/api/community/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void update(@PathVariable("id") Long id, @RequestBody Community post) {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
            communityService.update(id, post, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/api/community/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(@PathVariable("id") Long id) {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
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
    public void addComment(@PathVariable("id") Long id, @RequestBody CmComment comment) {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
            communityService.addComment(id, comment, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/api/community/{id}/comment/{commentId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId) {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
            communityService.deleteComment(commentId, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
