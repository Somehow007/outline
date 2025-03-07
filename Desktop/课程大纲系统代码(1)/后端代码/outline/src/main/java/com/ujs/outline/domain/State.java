package com.ujs.outline.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 状态表pojo类
 * @author wjy
 */
@Data
@TableName("state")
public class State {
    @TableId("state_id")
    private String stateId;//状态ID
    private String stateName;//状态
}
