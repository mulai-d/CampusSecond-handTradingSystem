package cn.edu.sdjzu.campussecondhandtradingsystem.service;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MessageService extends IService<Message> {

    Message addMessage(Message message);

    PageResult<Message> listProductMessages(Long productId, long page, long size);

    boolean deleteMessage(Long id);
}
