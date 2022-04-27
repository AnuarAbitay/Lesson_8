package tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FileTest {

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

    
}
