package xyz.winston.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@UtilityClass
public final class Impls {

    Map<Class<?>, Class<?>> IMPLEMENTATIONS = new HashMap<>();

    private Class<?> getImplementation(Class<?> cls) {
        Class<?> impl = IMPLEMENTATIONS.get(cls);

        if (impl == null) {
            impl = readImplementation(cls);

            if (impl == null) {
                throw new IllegalStateException("Cannot load impl for " + cls.getName());
            }

            IMPLEMENTATIONS.put(cls, impl);
        }

        return impl;
    }

    private Class<?> readImplementation(Class<?> cls) {
        try (InputStream is = cls.getClassLoader().getResourceAsStream("META-INF/impl/" + cls.getName())) {
            if (is == null) return null;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                return br.lines()
                        .map(line -> {
                            int separator = line.indexOf(':');
                            String implName = line.substring(0, separator);
                            String priority = line.substring(separator + 1);

                            return new ImplModel(implName, ImplPriority.valueOf(priority));
                        })
                        .min(Comparator.naturalOrder())
                        .flatMap(model -> {
                            try {
                                return Optional.of(Class.forName(model.getImplName()));
                            } catch (Exception e) {
                                return Optional.empty();
                            }
                        })
                        .orElse(null);
            }
        } catch (IOException e) {
            return null;
        }
    }

    public String debug() {
        return IMPLEMENTATIONS.toString();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> cls) {
        try {
            Class<?> impl = getImplementation(cls);

            try {
                return (T) impl.getDeclaredField("INSTANCE").get(null);
            } catch (NoSuchFieldException e) {
                return (T) impl.getConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
