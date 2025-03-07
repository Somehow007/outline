package com.ujs.outline.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.outline.domain.State;
import com.ujs.outline.mapper.StateMapper;
import com.ujs.outline.service.StateService;
import org.springframework.stereotype.Service;

@Service
public class StateServiceImpl extends ServiceImpl<StateMapper, State> implements StateService {
}
