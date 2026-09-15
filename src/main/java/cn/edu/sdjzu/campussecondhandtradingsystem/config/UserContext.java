package cn.edu.sdjzu.campussecondhandtradingsystem.config;

public class UserContext {

    private static final ThreadLocal<Long> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long userId) {
        HOLDER.set(userId);
    }

    public static Long getUserId() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
