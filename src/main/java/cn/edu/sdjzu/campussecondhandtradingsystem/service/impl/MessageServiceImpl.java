package cn.edu.sdjzu.campussecondhandtradingsystem.service.impl;

import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Message;
import cn.edu.sdjzu.campussecondhandtradingsystem.mapper.MessageMapper;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.MessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
}
