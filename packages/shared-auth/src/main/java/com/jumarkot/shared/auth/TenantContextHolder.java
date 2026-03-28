package com.jumarkot.shared.auth;

/**
 * Thread-local store for the current request's TenantContext.
 * Always call {@link #clear()} in a finally block or filter cleanup
 * to prevent context leakage between requests in thread-pool environments.
 */
public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> HOLDER = new ThreadLocal<>();

    private TenantContextHolder() {}

    public static void set(TenantContext context) {
        HOLDER.set(context);
    }

    /**
     * @throws IllegalStateException if called outside an authenticated request
     */
    public static TenantContext require() {
        TenantContext ctx = HOLDER.get();
        if (ctx == null) {
            throw new IllegalStateException(
                    "TenantContext is not set. Ensure this call is inside an authenticated request context.");
        }
        return ctx;
    }

    public static TenantContext get() {
        return HOLDER.get();
    }

    public static boolean isPresent() {
        return HOLDER.get() != null;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
