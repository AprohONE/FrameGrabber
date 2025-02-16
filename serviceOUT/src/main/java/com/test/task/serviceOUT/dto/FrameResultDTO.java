package com.test.task.serviceOUT.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameResultDTO {
    private String timestamp;
    private int frameSize;
    private int width;
    private int height;
    private String note;
}
