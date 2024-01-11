package kr.ac.cbnu.tux.controller;

import kr.ac.cbnu.tux.domain.StaticPage;
import kr.ac.cbnu.tux.service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// 연혁, 연락처 등 정적인 페이지를 저장하기 위한 컨트롤러

// 프론트엔드 코드 수정 없이 내용 수정이 가능하게 DB에 저장하도록 개선함 - 2024년 1월 11일

@Controller
public class StaticPageController {

    private final StaticPageService staticPageService;

    @Autowired
    public StaticPageController(StaticPageService staticPageService) {
        this.staticPageService = staticPageService;
    }

    /* 생성 및 수정 */
    @PostMapping("/api/admin/staticpage/{name}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void createAndUpdate(@PathVariable("name") String name, @RequestBody StaticPage updated) {
        try {
            System.out.println("StaticPageController.createAndUpdate");
            staticPageService.createAndUpdate(name, updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/staticpage/{name}")
    @ResponseBody
    public StaticPage read(@PathVariable("name") String name) {
        try {
            return staticPageService.read(name);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }
}
