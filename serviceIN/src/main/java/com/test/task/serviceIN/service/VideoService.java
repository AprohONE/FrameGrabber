package com.test.task.serviceIN.service;

import com.test.task.serviceIN.dto.FrameDTO;
import com.test.task.serviceIN.dto.FrameResultDTO;
import lombok.extern.log4j.Log4j2;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@Log4j2
public class VideoService {
    private final Java2DFrameConverter frameConverter = new Java2DFrameConverter();
    private final WebClient webClient;
    private volatile boolean running = false;
    private final SimpMessagingTemplate messagingTemplate;
    private volatile Thread processingThread = null;
    public VideoService(SimpMessagingTemplate messagingTemplate, WebClient.Builder webClientBuilder) {
        this.messagingTemplate = messagingTemplate;
        String frameAnalyticsServiceUrl = "http://serviceout:8082/api/analyze/frame";
        this.webClient = webClientBuilder.baseUrl(frameAnalyticsServiceUrl).build();
    }
    public void processLiveStream(String streamUrl) {
        running = true;
        processingThread = new Thread(() -> {
            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(streamUrl)) {
                BufferedWriter writer = new BufferedWriter(new FileWriter("framesLog_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy_HH-mm-ss")) + ".txt", true));
                log.info("Stream processing started: {}", streamUrl);
                grabber.setFormat("rtsp");
                grabber.setOption("rtsp_transport", "tcp");
                FFmpegLogCallback.set();
                grabber.start();
                int frameNum = 0;
                while (running) {
                    Frame frame = grabber.grabImage();
                    if (frame == null) continue;
                    BufferedImage bufferedImage = frameConverter.convert(frame);
                    if (bufferedImage == null) continue;
                    frameNum++;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", stream);
                    stream.flush();
                    String encodedImage = Base64.getEncoder().encodeToString(stream.toByteArray());
                    stream.close();
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                    FrameDTO frameDTO = new FrameDTO(timestamp, encodedImage);
                    String logEntry = String.format("Frame: %d, Time: %s, Frame size (bytes): %d Height: %d, Width: %d",
                            frameNum, timestamp, stream.size(), bufferedImage.getWidth(), bufferedImage.getHeight());
                    writer.write(logEntry);
                    writer.newLine();
                    writer.flush();
                    FrameResultDTO analysisResult = webClient.post()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(frameDTO)
                            .retrieve()
                            .bodyToMono(FrameResultDTO.class)
                            .block();
                    messagingTemplate.convertAndSend("/topic/frame", analysisResult);
                }

                grabber.stop();
            } catch (Exception e) {
                log.error("Error stream processing:", e);
                messagingTemplate.convertAndSend("/topic/frame", "Error: " + e.getMessage());
            }
        });
        processingThread.start();
    }
    public void stopProcessing() {
        running = false;
        if (processingThread != null) {
            processingThread.interrupt();
        }
        log.info("Stream processing stopped.");
    }
}