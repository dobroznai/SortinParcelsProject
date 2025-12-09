package idhub.sortinparcels.io.reader;

import idhub.sortinparcels.dto.ParcelReaderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TXTReader implements DataReader {

    @Override
    public List<ParcelReaderDto> read(MultipartFile file) {
        List<ParcelReaderDto> inputList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) {
                    log.warn("Skipping empty line {}", lineNumber);
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (parts.length < 3) {
                    log.warn("Skipping invalid line {}: {}", lineNumber, line);
                    continue;
                }

                inputList.add(new ParcelReaderDto(parts[0], parts[1], parts[2]));
            }

            log.info("Parsed {} parcels from TXT file", inputList.size());

        } catch (Exception exception) {
            log.error("Error reading TXT file {}", file.getOriginalFilename(), exception);
            throw new RuntimeException("Error reading TXT file", exception);
        }

        return inputList;
    }
}