package com.jxufe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/*
 * 商品介绍
 * @author 逍遥
 * @create 2024/11/25 下午11:38
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_blog")
@ApiModel( value = "博客")
public class Blog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 商户id
     */
    @ApiModelProperty(value = "商户id")
    private Long shopId;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;
    /**
     * 用户图标
     */
    @ApiModelProperty(value = "用户图标")
    @TableField(exist = false)
    private String icon;
    /**
     * 用户姓名
     */
    @ApiModelProperty(value = "用户姓名")
    @TableField(exist = false)
    private String name;
    /**
     * 是否点赞过了
     */
    @ApiModelProperty(value = "是否点赞过了")
    @TableField(exist = false)
    private Boolean isLike;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 探店的照片，最多9张，多张以","隔开
     */
    @ApiModelProperty(value = "探店的照片")
    private String images;

    /**
     * 探店的文字描述
     */
    @ApiModelProperty(value = "探店的文字描述")
    private String content;

    /**
     * 点赞数量
     */
    @ApiModelProperty(value = "点赞数量")
    private Integer liked;

    /**
     * 评论数量
     */
    @ApiModelProperty(value = "评论数量")
    private Integer comments;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;


}
