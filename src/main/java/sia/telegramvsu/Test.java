package sia.telegramvsu;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sia.telegramvsu.service.DownloadExcel;
import sia.telegramvsu.service.ExcelParser;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Test.class);



        var download = (DownloadExcel) ctx.getBean("");

        download.downloadSchedules();
    }
}
