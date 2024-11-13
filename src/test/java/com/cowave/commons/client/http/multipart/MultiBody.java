package com.cowave.commons.client.http.multipart;

import lombok.Data;

/**
 *
 * @author shanhuiming
 *
 */
@Data
public class MultiBody {

    private Integer id;

    private String name;

    private String fileName;

    private String fileContent;
}
