package im.prize.api.application;

import java.io.IOException;

public interface PdfService {
    void read(String fileName) throws IOException;
}
