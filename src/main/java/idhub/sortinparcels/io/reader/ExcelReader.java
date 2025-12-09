package idhub.sortinparcels.io.reader;

import idhub.sortinparcels.dto.ParcelReaderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelReader implements DataReader {

    /**
     * Reads the given Excel file and converts each row into ParcelExcelDto.
     * Skips the first row (header).
     *
     * @param file uploaded Excel file (.xlsx/.xls)
     * @return list of parsed DTOs
     */
    @Override
    public List<ParcelReaderDto> read(MultipartFile file) {
        List<ParcelReaderDto> inputList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // Skip the header row
                if (row.getRowNum() == 0) continue;

                String trackingNumber = getCellValue(row.getCell(0)).trim();
                // Skip empty rows
                if (trackingNumber.isBlank()) {
                    log.warn("Skipping row {} because tracking number is blank", row.getRowNum());
                    continue;
                }
                String zoneCode = getCellValue(row.getCell(1)).trim();

                if (zoneCode.isBlank()) {
                    log.warn("Skipping row {} because zoneCode is blank", row.getRowNum());
                    continue;
                }
                String routeNumber = getCellValue(row.getCell(2)).trim();
                if (routeNumber.isBlank()) {
                    log.warn("Skipping row {} because routeNumber is blank", row.getRowNum());
                    continue;
                }


                inputList.add(new ParcelReaderDto(
                        trackingNumber,
                        zoneCode,
                        routeNumber));
            }

            log.info("Parsed {} parcels from Excel", inputList.size());
        } catch (Exception exception) {
            log.error("Failed to parse Excel file", exception);
            throw new RuntimeException("Error reading Excel file", exception);
        }

        return inputList;
    }

    /**
     * Utility to convert cell value into String
     */
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue()).trim();
            default -> "";
        };
    }
}