package dev.pkgh.sdk.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This shit is completely pasted from LiteCloud source.
 * IDK who is the original author, maybe @WhileIn, but
 * anyway, credit to him <3
 *
 * @author LiteCloud / Whilein
 */
@SuppressWarnings("unused")
@Deprecated
public final class MetafactoryBuilder<T> {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    @NotNull
    private final Class<T> lambda;

    @NotNull
    private final Method method;

    @Nullable
    private Target target;

    @NotNull
    public T buildLambda() {
        return buildLambda(null);
    }

    @NotNull
    @SneakyThrows
    public T buildLambda(final @Nullable Object object) {
        if (target == null)
            throw new IllegalStateException("You have to specify target using target() method");

        val declaringType = target.getType();

        val name = declaringType.getPackageName().replace('.', '/')
                + "/Gen$" + COUNTER.incrementAndGet();

        val type = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        type.visit(Opcodes.V11, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, name, null,
                "java/lang/Object", new String[]{Type.getInternalName(lambda)});

        if (object != null) {
            type.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "instance",
                    Type.getDescriptor(object.getClass()), null, null).visitEnd();
        }

        { // constructor
            val descriptor = "(" + (object == null ? "" : Type.getDescriptor(object.getClass())) + ")V";

            val mv = type.visitMethod(Opcodes.ACC_PUBLIC, "<init>", descriptor, null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

            if (object != null) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                mv.visitFieldInsn(Opcodes.PUTFIELD, name, "instance",
                        Type.getDescriptor(object.getClass()));
            }

            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        target.generateAccessor(name, type, object, lambda, method);

        type.visitEnd();

        val result = type.toByteArray();

        val resultType = MethodHandles.privateLookupIn(declaringType, MethodHandles.lookup())
                .defineClass(result).asSubclass(lambda);

        if (object != null) {
            return resultType.getDeclaredConstructor(object.getClass())
                    .newInstance(object);
        } else {
            return resultType.getDeclaredConstructor()
                    .newInstance();
        }
    }

    @NotNull
    public MetafactoryBuilder<T> target(@NotNull Constructor<?> constructor) {
        target = new ConstructorTarget(constructor);
        return this;
    }

    @NotNull
    public MetafactoryBuilder<T> target(@NotNull Method method) {
        target = new MethodTarget(method);
        return this;
    }

    public MetafactoryBuilder(@NotNull Class<T> lambda) {
        if (!lambda.isInterface()) {
            throw new IllegalArgumentException("Lambda must be an interface");
        }

        this.lambda = lambda;
        this.method = getLambdaMethod(lambda);
    }

    @NotNull
    private static Method getLambdaMethod(@NotNull Class<?> cls) {
        Method[] lambdaMethods = cls.getMethods();
        Method found = null;

        for (Method method : lambdaMethods) {
            if (Modifier.isAbstract(method.getModifiers())) {
                if (found != null)
                    throw new IllegalStateException("Too many methods in " + cls.getSimpleName());
                found = method;
            }
        }

        if (found == null)
            throw new IllegalStateException("Cannot find any methods in " + cls.getSimpleName());

        return found;
    }

    private static abstract class Target {

        public abstract void generateAccessor(String name, ClassWriter type, Object object,
                                              Class<?> lambda, Method lambdaMethod);

        public abstract Class<?> getType();

        protected void visitReturn(final MethodVisitor mv, final Class<?> fromType, final Class<?> toType) {
            if (toType != void.class) {
                if (!fromType.isAssignableFrom(toType)) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(toType));
                }

                mv.visitInsn(Type.getType(toType).getOpcode(Opcodes.IRETURN));
            } else {
                mv.visitInsn(Opcodes.RETURN);
            }
        }


        protected void copyParameters(final int offset, final Class<?>[] from, final Class<?>[] to,
                                      final MethodVisitor mv) {
            int stack = 1 + offset;

            for (int i = 0; i < to.length; i++) {
                val toType = to[i];
                val fromType = from[i + offset];

                mv.visitVarInsn(Opcodes.ALOAD, stack);

                if (!toType.isAssignableFrom(fromType)) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(toType));
                }

                stack += Type.getType(toType).getSize();
            }
        }
    }

    @RequiredArgsConstructor
    private static final class MethodTarget extends Target {

        @NotNull
        private final Method method;

        @Override
        public Class<?> getType() {
            return method.getDeclaringClass();
        }

        @Override
        public void generateAccessor(final String name, final ClassWriter type, final Object object,
                                     final Class<?> lambda, final Method lambdaMethod) {
            val exceptions = Arrays.stream(lambdaMethod.getExceptionTypes())
                    .map(Type::getInternalName).toArray(String[]::new);

            val mv = type.visitMethod(Opcodes.ACC_PUBLIC, lambdaMethod.getName(),
                    Type.getMethodDescriptor(lambdaMethod), null, exceptions);

            mv.visitCode();

            val parameters = lambdaMethod.getParameterTypes();

            final Class<?> objectType;
            int counter = 0;

            val isObjectRequired = !Modifier.isStatic(method.getModifiers());

            if (isObjectRequired) {
                if (object == null) {
                    mv.visitVarInsn(Opcodes.ALOAD, counter + 1);
                    objectType = parameters[0];
                    counter++;
                } else {
                    objectType = object.getClass();

                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitFieldInsn(Opcodes.GETFIELD, name, "instance",
                            Type.getDescriptor(objectType));
                }

                val requiredObjectType = method.getDeclaringClass();

                if (!requiredObjectType.isAssignableFrom(objectType)) {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(requiredObjectType));
                }
            }

            copyParameters(counter, parameters, method.getParameterTypes(), mv);

            mv.visitMethodInsn(isObjectRequired ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKESTATIC,
                    Type.getInternalName(method.getDeclaringClass()), method.getName(),
                    Type.getMethodDescriptor(method), false);

            visitReturn(mv, method.getReturnType(), lambdaMethod.getReturnType());
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }


    }

    @RequiredArgsConstructor
    private static final class ConstructorTarget extends Target {

        @NotNull
        private final Constructor<?> constructor;

        @Override
        public Class<?> getType() {
            return constructor.getDeclaringClass();
        }

        @Override
        public void generateAccessor(final String name, final ClassWriter type, final Object object,
                                     final Class<?> lambda, final Method lambdaMethod) {
            val exceptions = Arrays.stream(lambdaMethod.getExceptionTypes())
                    .map(Type::getInternalName).toArray(String[]::new);

            val mv = type.visitMethod(Opcodes.ACC_PUBLIC, lambdaMethod.getName(),
                    Type.getMethodDescriptor(lambdaMethod), null, exceptions);

            mv.visitCode();

            val parameters = lambdaMethod.getParameterTypes();

            mv.visitTypeInsn(Opcodes.NEW, Type.getInternalName(constructor.getDeclaringClass()));
            mv.visitInsn(Opcodes.DUP);

            copyParameters(0, parameters, constructor.getParameterTypes(), mv);

            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(constructor.getDeclaringClass()),
                    "<init>", Type.getConstructorDescriptor(constructor), false);

            visitReturn(mv, constructor.getDeclaringClass(), lambdaMethod.getReturnType());

            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

    }

}