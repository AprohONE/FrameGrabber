package com.test.task.serviceIN.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameDTO {
    private String timestamp;
    private String frameData;
}