package spring.api.trijava.chuyendewebjavajob.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spring.api.trijava.chuyendewebjavajob.domain.response.file.ResUpdateFileDTO;
import spring.api.trijava.chuyendewebjavajob.service.FileService;
import spring.api.trijava.chuyendewebjavajob.util.annotation.ApiMessage;
import spring.api.trijava.chuyendewebjavajob.util.error.UploadException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Value("${cdweb.upload-file.base-uri}")
    private String baseUri;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUpdateFileDTO> upload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, UploadException {

        if (file == null || file.isEmpty()) {
            throw new UploadException("File is empty. Please upload a file");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (isValid == false) {
            throw new UploadException("Only allows " + allowedExtensions.toString());
        }
        // Tạo folder
        this.fileService.createDirectory(baseUri + folder);

        // Lưu file vào folder đã tạo
        String uploadedFile = this.fileService.store(file, folder);
        ResUpdateFileDTO res = new ResUpdateFileDTO(uploadedFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }
}
