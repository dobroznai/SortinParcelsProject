package idhub.sortinparcels.io.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ReaderFactory {

    private final ExcelReader excelReader;
    private final TXTReader txtReader;

    public DataReader getReader(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Filename is empty");
        }

        String lower = filename.toLowerCase();

        if (lower.endsWith(".xlsx") || lower.endsWith(".xls")) {
            return excelReader;
        }
        if (lower.endsWith(".txt")) {
            return txtReader;
        }

        throw new IllegalArgumentException("Unsupported file format: " + filename);
    }
}