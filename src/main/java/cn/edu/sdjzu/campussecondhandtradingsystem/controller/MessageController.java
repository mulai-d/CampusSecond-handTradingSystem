package cn.edu.sdjzu.campussecondhandtradingsystem.controller;

import cn.edu.sdjzu.campussecondhandtradingsystem.common.PageResult;
import cn.edu.sdjzu.campussecondhandtradingsystem.common.Result;
import cn.edu.sdjzu.campussecondhandtradingsystem.config.UserContext;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.Message;
import cn.edu.sdjzu.campussecondhandtradingsystem.entity.User;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.MessageService;
import cn.edu.sdjzu.campussecondhandtradingsystem.service.UserService;
import cn.edu.sdjzu.campussecondhandtradingsystem.vo.MessageItemVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
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
        message.setUserId(UserContext.getUserId());
        Message saved = messageService.addMessage(message);
        return saved == null ? Result.fail("invalid message") : Result.success(saved);
    }

    @GetMapping("/product/{productId}")
    public Result<PageResult<MessageItemVO>> listByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        PageResult<Message> messagePage = messageService.listProductMessages(productId, page, size);
        List<Message> messages = messagePage.getRecords();

        Set<Long> parentIds = messages.stream()
                .map(Message::getParentId)
                .filter(parentId -> parentId != null && parentId != 0L)
                .collect(Collectors.toSet());
        Map<Long, Message> parentMessages = parentIds.isEmpty()
                ? Collections.emptyMap()
                : messageService.listByIds(parentIds).stream()
                        .collect(Collectors.toMap(Message::getId, Function.identity()));

        Set<Long> userIds = new HashSet<>();
        messages.stream()
                .map(Message::getUserId)
                .filter(userId -> userId != null)
                .forEach(userIds::add);
        parentMessages.values().stream()
                .map(Message::getUserId)
                .filter(userId -> userId != null)
                .forEach(userIds::add);

        Map<Long, String> usernames = userIds.isEmpty()
                ? Collections.emptyMap()
                : userService.listByIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, User::getUsername));

        List<MessageItemVO> records = messages.stream()
                .map(message -> toMessageItem(message, parentMessages, usernames))
                .collect(Collectors.toList());
        return Result.success(PageResult.of(
                messagePage.getPage(),
                messagePage.getSize(),
                messagePage.getTotal(),
                records));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(messageService.deleteMessage(id));
    }

    private MessageItemVO toMessageItem(
            Message message,
            Map<Long, Message> parentMessages,
            Map<Long, String> usernames) {
        MessageItemVO item = new MessageItemVO();
        item.setId(message.getId());
        item.setProductId(message.getProductId());
        item.setUserId(message.getUserId());
        item.setUsername(usernames.get(message.getUserId()));
        item.setContent(message.getContent());
        item.setParentId(message.getParentId());
        item.setCreateTime(message.getCreateTime());

        Message parentMessage = parentMessages.get(message.getParentId());
        if (parentMessage != null) {
            item.setParentUsername(usernames.get(parentMessage.getUserId()));
        }
        return item;
    }
}
