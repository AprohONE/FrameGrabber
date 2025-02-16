package com.test.task.serviceIN.controller;

import com.test.task.serviceIN.service.VideoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Log4j2
@Controller
@RequestMapping("/api/stream")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }
//    @GetMapping("/stream")
//    @ResponseBody
//    public SseEmitter stream(@RequestParam("streamURL") String videoUrl) {
//        SseEmitter emitter = new SseEmitter(0L);
//        videoService.processLiveStream(videoUrl, emitter);
//        return emitter;
//    }
    @PostMapping("/start")
    @ResponseBody
    public String start(@RequestParam("streamUrl") String videoUrl) {
        videoService.processLiveStream(videoUrl);
        return "Stream processing started.";
    }
    @PostMapping("/stop")
    @ResponseBody
    public String stop() {
        videoService.stopProcessing();
        return "Stream processing stopped.";
    }
}