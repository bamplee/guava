package im.prize.api.application;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PdfServiceImplTest {
    @Test
    void name() {
        PdfService pdfService = new PdfServiceImpl();
        try {
            pdfService.read("202011.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}