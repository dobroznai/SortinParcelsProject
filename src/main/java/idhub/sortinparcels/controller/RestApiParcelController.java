package idhub.sortinparcels.controller;

import idhub.sortinparcels.repository.ParcelRepository;
import idhub.sortinparcels.service.ExcelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Parcel", description = "Operation on parcel")
@Slf4j
@RequestMapping("/api/parcels")
@RestController

public class RestApiParcelController {

    private final ParcelRepository parcelRepository;
    private final ExcelService excelService;


    public RestApiParcelController(ParcelRepository parcelRepository, ExcelService excelService) {
        this.parcelRepository = parcelRepository;
        this.excelService = excelService;
    }

    @PostMapping("/upload")
    public String uploadParcels(@RequestParam("file") MultipartFile file) {
        excelService.importParcels(file);
        return "Successfully uploaded " + parcelRepository.count() + " parcels";
    }
}
