package com.sbisht.netflux.movie.util;

import java.util.Objects;

public final class LimitResolver {

    public static final int DEFAULT_LIMIT = 20;
    public static final int MAX_LIMIT = 100;

    private LimitResolver() {
    }

    public static int resolve(Integer limit) {
        if (Objects.isNull(limit) || limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
