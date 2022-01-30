package com.cloud.proxy.weatherapigateway.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(TableName.API_ROUTE)
public class ApiRoute {

    @Id // Indicating that this field is primary key in our database table
    private UUID id;

    private String path;
    private String method;
    private String uri;
}
