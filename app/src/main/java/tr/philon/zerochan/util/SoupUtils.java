package tr.philon.zerochan.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import tr.philon.zerochan.data.model.GalleryItem;
import tr.philon.zerochan.data.model.TagItem;

public class SoupUtils {

    public static boolean hasNextPage(String page) {
        List<GalleryItem> list = new ArrayList<>();
        Document doc = Jsoup.parse(page);
        Elements elements = doc.select("div#wrapper div#body div#content p.pagination a");

        boolean hasNextPage = false;
        for (Element item : elements) {
            String buttonText = item.attr("rel");

            if (buttonText.contains("next")) hasNextPage = true;
        }

        return hasNextPage;
    }

    public static List<GalleryItem> exportGalleryItems(String page) {
        List<GalleryItem> list = new ArrayList<>();
        Document doc = Jsoup.parse(page);
        Elements elements = doc.select("div#wrapper div#body div#content ul#thumbs2 li");

        for (Element item : elements) {
            String image = item.select("a img").attr("src");
            String pageLink = item.select("a").attr("href");

            list.add(new GalleryItem(image, pageLink));
        }

        return list;
    }
}