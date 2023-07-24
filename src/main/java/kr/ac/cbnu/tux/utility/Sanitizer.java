package kr.ac.cbnu.tux.utility;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

@Component
public class Sanitizer {

    private final PolicyFactory policy = Sanitizers.BLOCKS
            .and(Sanitizers.LINKS)
            .and(Sanitizers.FORMATTING)
            .and(Sanitizers.IMAGES)
            .and(Sanitizers.STYLES)
            .and(Sanitizers.TABLES);

    public String sanitize(String html) {
        return policy.sanitize(html);
    }
}
