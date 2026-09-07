package cn.edu.sdjzu.campussecondhandtradingsystem.controller;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.common.Result;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Message;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.MessageService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private static final Long CURRENT_USER_ID = 666L;

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Result<Message> add(@RequestBody Message message) {
        Message saved = messageService.addMessage(message);
        return saved == null ? Result.fail("invalid message") : Result.success(saved);
    }

    @PostMapping("/product/{productId}")
    public Result<Message> addCurrentUserMessage(
            @PathVariable Long productId,
            @RequestBody Message message) {
        if (message == null) {
            return Result.fail("invalid message");
        }

        message.setProductId(productId);
        message.setUserId(CURRENT_USER_ID);
        Message saved = messageService.addMessage(message);
        return saved == null ? Result.fail("invalid message") : Result.success(saved);
    }

    @GetMapping("/product/{productId}")
    public Result<PageResult<Message>> listByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return Result.success(messageService.listProductMessages(productId, page, size));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(messageService.deleteMessage(id));
    }
}
