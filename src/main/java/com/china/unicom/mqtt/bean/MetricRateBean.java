package com.china.unicom.mqtt.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricRateBean {

    Long startTime;
    Long endTime;

    Integer totalCount;
    Integer successCount;
    Integer errorCount;
}
