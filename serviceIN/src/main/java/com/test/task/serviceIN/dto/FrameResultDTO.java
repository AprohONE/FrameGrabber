package com.test.task.serviceIN.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameResultDTO {
    private String timestamp;
    private float frameSize;
    private float width;
    private float height;
    private String note;
}