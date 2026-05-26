package com.stargraph.comfyui.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueResponse {

    @SerializedName("queue_running")
    private List<List<Object>> queueRunning;

    @SerializedName("queue_pending")
    private List<List<Object>> queuePending;
}
