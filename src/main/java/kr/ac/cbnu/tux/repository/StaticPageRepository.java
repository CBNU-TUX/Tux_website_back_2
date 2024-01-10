package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.StaticPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaticPageRepository extends JpaRepository<StaticPage, Long> {

    Optional<StaticPage> findByName(String name);
}
