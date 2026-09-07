package cn.edu.sdjzu.campussecondhandtradingsystem.service.impl;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Message;
import cn.edu.sdjzu.campussecondhandtradingsystem.mapper.MessageMapper;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.RedisCacheService;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.MessageService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Optional;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private static final String MESSAGE_PRODUCT_KEY = "campus:message:product:";

    private static final Duration MESSAGE_PAGE_TTL = Duration.ofMinutes(5);

    private final RedisCacheService redisCacheService;

    public MessageServiceImpl(RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    @Override
    public Message addMessage(Message message) {
        if (message == null
                || message.getProductId() == null
                || message.getUserId() == null
                || !StringUtils.hasText(message.getContent())
                || message.getContent().length() > 500) {
            return null;
        }

        Long parentId = message.getParentId();
        if (parentId != null && parentId != 0L && getById(parentId) == null) {
            return null;
        }
        if (parentId == null) {
            message.setParentId(0L);
        }

        message.setId(null);
        message.setCreateTime(null);
        boolean saved = save(message);
        if (!saved) {
            return null;
        }

        invalidateMessageCache(message.getProductId());
        return message;
    }

    @Override
    public PageResult<Message> listProductMessages(Long productId, long page, long size) {
        long current = Math.max(page, 1);
        long pageSize = Math.min(Math.max(size, 1), 100);
        String cacheKey = MESSAGE_PRODUCT_KEY + productId + ":" + current + ":" + pageSize;

        Optional<PageResult<Message>> cached = redisCacheService.get(
                cacheKey, new TypeReference<PageResult<Message>>() {
                });
        if (cached.isPresent()) {
            return cached.get();
        }

        Page<Message> messagePage = page(new Page<>(current, pageSize),
                Wrappers.<Message>lambdaQuery()
                        .eq(Message::getProductId, productId)
                        .orderByAsc(Message::getCreateTime));

        PageResult<Message> result = PageResult.of(
                messagePage.getCurrent(),
                messagePage.getSize(),
                messagePage.getTotal(),
                messagePage.getRecords());
        redisCacheService.set(cacheKey, result, MESSAGE_PAGE_TTL);
        return result;
    }

    @Override
    public boolean deleteMessage(Long id) {
        Message message = getById(id);
        boolean removed = removeById(id);
        if (removed && message != null) {
            invalidateMessageCache(message.getProductId());
        }
        return removed;
    }

    private void invalidateMessageCache(Long productId) {
        redisCacheService.deleteByPattern(MESSAGE_PRODUCT_KEY + productId + ":*");
    }
}
