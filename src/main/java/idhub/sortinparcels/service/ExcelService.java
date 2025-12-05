package idhub.sortinparcels.service;

import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.enums.ParcelStatus;
import idhub.sortinparcels.repository.ParcelRepository;
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
public class ExcelService {

    private final ParcelRepository parcelRepository;

    public void importParcels(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Parcel> parcels = new ArrayList<>();

            for (Row row : sheet) {
                // Пропустити перший рядок (заголовки)
                if (row.getRowNum() == 0) continue;

                String trackingNumber = getCellValue(row.getCell(0));
                String gibitNumber = getCellValue(row.getCell(1));
                String tourNumber = getCellValue(row.getCell(2));

                Parcel parcel = new Parcel(trackingNumber, gibitNumber, tourNumber, ParcelStatus.PENDING);
                parcels.add(parcel);
            }

            parcelRepository.saveAll(parcels);
            log.info("✅ Imported {} parcels from Excel", parcels.size());

        } catch (Exception e) {
            log.error("❌ Failed to import Excel file", e);
            throw new RuntimeException("Error reading Excel file", e);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }
}