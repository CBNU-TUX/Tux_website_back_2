package kr.ac.cbnu.tux.service;

import jakarta.transaction.Transactional;
import kr.ac.cbnu.tux.domain.StaticPage;
import kr.ac.cbnu.tux.repository.StaticPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaticPageService {

    private final StaticPageRepository staticPageRepository;

    @Autowired
    public StaticPageService(StaticPageRepository staticPageRepository) {
        this.staticPageRepository = staticPageRepository;
    }

    public void create(StaticPage page) {
        staticPageRepository.save(page);
    }

    @Transactional
    public void update(String name, StaticPage updated) {
        StaticPage page = staticPageRepository.findByName(name).orElseThrow();
        page.setBody(updated.getBody());
    }

    public StaticPage read(String name) {
        return staticPageRepository.findByName(name).orElseThrow();
    }

}
