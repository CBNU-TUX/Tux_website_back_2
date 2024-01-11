package kr.ac.cbnu.tux.service;

import jakarta.transaction.Transactional;
import kr.ac.cbnu.tux.domain.StaticPage;
import kr.ac.cbnu.tux.repository.StaticPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaticPageService {

    private final StaticPageRepository staticPageRepository;

    @Autowired
    public StaticPageService(StaticPageRepository staticPageRepository) {
        this.staticPageRepository = staticPageRepository;
    }

    @Transactional
    public void createAndUpdate(String name, StaticPage updated) {
        Optional<StaticPage> opage = staticPageRepository.findByName(name);

        if (opage.isPresent()) {
            StaticPage page = opage.get();
            page.setBody(updated.getBody());
        }
        else {
            staticPageRepository.save(updated);
        }
    }

    public StaticPage read(String name) {
        return staticPageRepository.findByName(name).orElseThrow();
    }

}
