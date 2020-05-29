package top.mihile.cableMonitorMedia.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Mihile azuretodd@foxmail.com
 * @date 0122020/4/12
 */
@Data
@Builder
public class Result implements Serializable {
    private static final long serialVersionUID = -7882620080992146156L;
    private Integer code;
    private String msg;
    private Map<String,String> data;
}
