package com.jxufe.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty( value = "主键")
    private Long id;

    @ApiModelProperty( value = "昵称")
    private String nickName;

    @ApiModelProperty( value = "图片地址")
    private String icon;

}
