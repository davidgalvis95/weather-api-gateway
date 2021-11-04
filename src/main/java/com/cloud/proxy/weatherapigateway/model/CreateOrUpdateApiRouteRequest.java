package com.cloud.proxy.weatherapigateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrUpdateApiRouteRequest {

    @NotBlank
    private String path;

    private String method;

    @NotBlank
    private String uri;
}