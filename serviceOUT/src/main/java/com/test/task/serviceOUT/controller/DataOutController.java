package com.test.task.serviceOUT.controller;

import com.test.task.serviceOUT.dto.FrameDTO;
import com.test.task.serviceOUT.dto.FrameResultDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

@RestController
@RequestMapping("/api/analyze")
@Log4j2
public class DataOutController {
    @PostMapping("/frame")
    public FrameResultDTO analyzeFrame(@RequestBody FrameDTO frameDTO) {
        log.info("Frame received");
        try {
            byte[] imageBytes = Base64.getDecoder().decode(frameDTO.getFrameData());
            ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new RuntimeException("Unable to decode frame");
            }
            int width = image.getWidth();
            int height = image.getHeight();
            int frameSize = imageBytes.length;
            FrameResultDTO result = new FrameResultDTO();
            result.setTimestamp(frameDTO.getTimestamp());
            result.setFrameSize(frameSize);
            result.setWidth(width);
            result.setHeight(height);
            result.setNote("Frame analyzed");
            return result;
        } catch (Exception e) {
            log.error("Frame processing error:", e);
            throw new RuntimeException("Frame processing error: " + e.getMessage());
        }
    }
}
