package com.example.graduationprojectprocessmanagement.controller;

import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/")
@RequiredArgsConstructor
public class FileController {

    @Value("${my.upload}")
    private String uploadDirectory;


    @PostMapping(value = "upload/{pname}")
    public Mono<ResultVO> upload(@PathVariable String pname, Mono<FilePart> file) {
        return file.flatMap(filePart -> {
                    Path p = Path.of(uploadDirectory).resolve(pname);
                    return Mono.fromCallable(() -> Files.createDirectories(p))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(path ->
                                    filePart.transferTo(path.resolve(filePart.filename())));
                })
                .thenReturn(ResultVO.success(Map.of()));
    }

    @GetMapping("download")
    public ResponseEntity<Flux<DataBuffer>> download() {
        Path path = Path.of("D:/a.mp4");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", path.getFileName().toString());
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(DataBufferUtils.read(path, new DefaultDataBufferFactory(), 1024 * 1000));
    }
}
