package tr.philon.zerochan.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import tr.philon.zerochan.data.model.GalleryItem;

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

    public static List<String> getImageDetails(String page) {
        List<String> list = new ArrayList<>();
        Document doc = Jsoup.parse(page);

        Elements imageDetails = doc.select("div#wrapper div#body div#content div#large p");
        Elements tags = doc.select("div#wrapper div#body div#menu ul#tags li");

        String uploader = doc.select("div#wrapper div#body div#content p a").first().text();
        String all = imageDetails.text();
        String first = imageDetails.first().text();
        String third = imageDetails.select("span").text();
        String second = all.replace(first, "").replace(third, "").replace(" ", "");

        list.add("uploaded by " + uploader);
        list.add(second + ", " + third);

        for (Element item : tags) {
            String tagName = item.select("a").text();

            //if tagName is not fully visible, decode it from url
            if (tagName.substring(tagName.length() - 3, tagName.length()).equals("...")) {
                tagName = item.select("a").attr("href");
                tagName = tagName.substring(1, tagName.length());

                try {
                    tagName = URLDecoder.decode(tagName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    tagName = item.select("a").attr("href");
                }
            }

            list.add(tagName);
        }

        return list;
    }
}