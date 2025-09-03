package sia.telegramvsu.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


@Slf4j
@Service
public class DownloadExcel {

    @Value("${path.excel}")
    private String pathExcel;
    @Value("${path.website}")
    private String[] siteUrls;



    private void downloadFile(String fileUrl, String outputFileName) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            connection.setInstanceFollowRedirects(true);

            try (InputStream in = connection.getInputStream();
                 ReadableByteChannel rbc = Channels.newChannel(in);
                 FileOutputStream fos = new FileOutputStream(outputFileName)) {

                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }

        } catch (IOException e) {
            log.error("Error downloading schedules");
        }
    }

    public void downloadSchedules() {
        String filePattern = ".xlsx";
        try {
            int i = 1;
            for (String siteUrl : siteUrls) {
                Document doc = Jsoup.connect(siteUrl).get();
                Elements links = doc.select("a[href]");

                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.contains(filePattern)) {
                        // Скачать файл по найденной ссылке
                        downloadFile("https://vsu.by" + href, pathExcel + i + ".xlsx");
                        log.info("Excel file downloaded successful");
                        i++;
                    }
                }
                log.error("ExcelFile in website not found");
            }
        } catch (IOException e) {
            log.error("Excel file no downloaded");
        }
    }
}
