package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideFilesTest {

    @Test
    void parseTxtFileTest() throws Exception {
        String result;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("test.txt")) {
            result = new String(is.readAllBytes(), "UTF-8");
        }
        assertThat(result).contains("Достоевский");
    }

    @Test
    void parsePdfTest() throws Exception {
        open("https://all-the-books.ru/books/dostoevskiy-fedor-idiot/");
        File download = $(byText("Скачать Идиот в PDF")).download();
        PDF parsed = new PDF(download);
        assertThat(parsed.author).contains("Федор Михайлович Достоевский");
        assertThat(parsed.text).contains("Идиот");
    }

    @Test
    void parseExcelTest() throws Exception {
//        Selenide.open("https://okeanbusin.ru/shop/zakaz-cherez-excel-fayly/");
//        File download = $(byText("Чешские бусины, разные, фасовка 10 гр.")).download();
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("zakaz.xlsx")) {
            XLS parsed = new XLS(stream);
            assertThat(parsed.excel.getSheetAt(0).getRow(7).getCell(0).getStringCellValue())
                    .isEqualTo("Изображение");
        }
    }

    @Test
    void parseDocTest() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("test.docx")) {
            XWPFDocument docxFile = new XWPFDocument(is);
            List<XWPFParagraph> paragraphs = docxFile.getParagraphs();
            assertThat(paragraphs.get(0).getParagraphText().contains("Достоевский"));
        }
    }

    @Test
    void zipFileTest() throws Exception {
        ZipFile zipFile = new ZipFile("./src/test/resources/response.zip");

        if (zipFile.isEncrypted()) {// Если установлен пароль
            zipFile.setPassword("123456qQ");
        }
        zipFile.extractAll("./src/test/resources/extract");

        List fileHeaderList = zipFile.getFileHeaders();
        for (int i = 0; i < fileHeaderList.size(); i++) {
            FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
            assertThat(fileHeader.getFileName()).contains("response");
        }
    }
}