package kr.ac.cbnu.tux.utility;

import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

@Component
public class Sanitizer {

    private final PolicyFactory policy = new HtmlPolicyBuilder()
            .allowCommonBlockElements()
            .allowCommonInlineFormattingElements()
            .allowStyling()
            .allowStandardUrlProtocols().allowElements("a")

            .allowElements(
                    "table", "tr", "td", "th",
                    "colgroup", "caption", "col",
                    "thead", "tbody", "tfoot")
            .allowAttributes("summary").onElements("table")
            .allowAttributes("align", "valign")
            .onElements("table", "tr", "td", "th",
                    "colgroup", "col",
                    "thead", "tbody", "tfoot")
            .allowTextIn("table")  // WIDGY

            .allowUrlProtocols("http", "https").allowElements("img")
            .allowAttributes("alt", "src").onElements("img")
            .allowAttributes("border", "height", "width").matching(INTEGER)
            .onElements("img")

            .allowAttributes("alt", "align", "title", "img").onElements("img")
            .allowAttributes("href", "target", "class").onElements("a")
            .allowAttributes("class", "id", "style").onElements("div", "li", "p", "span")
            .allowAttributes("id").onElements("ul")
            .allowAttributes("rel", "href", "media").onElements("link")
            .toFactory();


    private static final AttributePolicy INTEGER = new AttributePolicy() {
        public String apply(
                String elementName, String attributeName, String value) {
            int n = value.length();
            if (n == 0) { return null; }
            for (int i = 0; i < n; ++i) {
                char ch = value.charAt(i);
                if (ch == '.') {
                    if (i == 0) { return null; }
                    return value.substring(0, i);  // truncate to integer.
                } else if (!('0' <= ch && ch <= '9')) {
                    return null;
                }
            }
            return value;
        }
    };

    public String sanitize(String html) {
        return policy.sanitize(html);
    }
}
