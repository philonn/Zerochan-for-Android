package tr.philon.zerochan.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import tr.philon.zerochan.data.model.GalleryItem;

public class SoupUtils {

    public static List<GalleryItem> exportItems(String page){
        List<GalleryItem> list = new ArrayList<>();
        Document doc = Jsoup.parse(page);
        Elements elements = doc.select("div#wrapper div#body div#content ul#thumbs2 li");
        for (Element item : elements) {
            String pagelink = item.select("a").attr("href");
            String image = item.select("a img").attr("src");

            list.add(new GalleryItem(pagelink, image));
        }
        return list;
    }
}
