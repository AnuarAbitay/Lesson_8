package tests;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Selenide;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FileTest {
    ClassLoader classLoader = getClass().getClassLoader();

    @Test
    void selenideDownloadTest() throws Exception {
        open("https://github.com/junit-team/junit5/blob/main/README.md");
        File downloadedFile = $("#raw-url").download();
        try (InputStream is = new FileInputStream(downloadedFile)){
            assertThat(new String(is.readAllBytes(), StandardCharsets.UTF_8)).contains("JUnit");
        }
    }

    @Test
    void uploadSelenideTest() {
        Selenide.open("https://demoqa.com/upload-download");
        $("input[type='file']").uploadFromClasspath("files/1.txt");
        $("#uploadedFilePath").shouldHave(text("1.txt"));
    }

    @Test
    void pdfParseTest() throws Exception{
        try (InputStream is = classLoader.getResourceAsStream("pdf/junit-user-guide-5.8.2.pdf")){
            assert is != null;
            PDF pdf = new PDF(is);
            assertThat(pdf.author).contains("Marc Philipp");
            assertThat(pdf.numberOfPages).isEqualTo(166);
        }
    }

    @Test
    void xlsParseTest() throws Exception{
        open("http://romashka2008.ru/price");
        File xlsDownload = $(".site-main__inner a[href*='prajs_ot']").download();
        XLS xls = new XLS(xlsDownload);
        assertThat(xls.excel
                .getSheetAt(0)
                .getRow(11)
                .getCell(1)
                .getStringCellValue())
                .contains("Сахалинская");
    }

    @Test
    void csvParseTest() throws Exception{
        try (InputStream is = classLoader.getResourceAsStream("csv/addresses.csv");
             CSVReader reader = new CSVReader(new InputStreamReader(is))){
            List<String[]> content = reader.readAll();
            assertThat(content.get(0)).contains("John",
                                                "Doe",
                                                "120 jefferson st.",
                                                "Riverside",
                                                " NJ",
                                                " 08075");
        }
    }

    @Test
    void zipParseTest() throws Exception{
        ZipFile zf = new ZipFile("zip/Test.zip");
        try (InputStream is = classLoader.getResourceAsStream("zip/Test.zip");
             ZipInputStream zis = new ZipInputStream(is)){
            ZipEntry entry = zis.getNextEntry();
            while (entry != null){
                if (entry.getName().contains(".pdf")){
                    try (InputStream stream = zf.getInputStream(entry)){
                        assert stream != null;
                        PDF pdf = new PDF(stream);
                        assertThat(pdf.author).contains("Marc Philipp");
                        assertThat(pdf.numberOfPages).isEqualTo(166);
                    }
                }
                if (entry.getName().contains(".xls")){
                    try (InputStream stream = zf.getInputStream(entry)){
                        assert stream != null;
                        XLS xls = new XLS(stream);
                        assertThat(xls.excel
                                .getSheetAt(0)
                                .getRow(11)
                                .getCell(1)
                                .getStringCellValue())
                                .contains("Сахалинская");
                    }

                }
            }

        }

    }


}
