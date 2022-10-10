package xyz.winston.impl.compiletime;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import xyz.winston.impl.Impl;
import xyz.winston.impl.ImplModel;
import xyz.winston.impl.ImplPriority;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SupportedAnnotationTypes("xyz.winston.impl.Impl")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public final class ImplProcessor extends AbstractProcessor {

    Map<String, List<ImplModel>> models = new HashMap<>();

    private static String getClassName(final @NonNull TypeElement type) {
        Element enclosedIn = type.getEnclosingElement();

        // in case of innerclass
        if (enclosedIn instanceof TypeElement) {
            return getClassName((TypeElement) enclosedIn) + '$' + type.getSimpleName();
        }

        return type.getQualifiedName().toString();
    }

    @Override
    public boolean process(final @NonNull Set<? extends TypeElement> annotations,
                           final @NonNull RoundEnvironment roundEnv) {
        for (val element : roundEnv.getElementsAnnotatedWith(Impl.class)) {
            val type = (TypeElement) element;
            val name = getClassName(type);

            val annotation = element.getAnnotation(Impl.class);

            val priority = annotation.priority();

            String apiName;

            try {
                apiName = annotation.value().getName();
            } catch (MirroredTypeException e) {
                apiName = getClassName(processingEnv.getElementUtils().getTypeElement(e.getTypeMirror().toString()));
            }

            models.computeIfAbsent(apiName, x -> new ArrayList<>()).add(new ImplModel(name, priority));
        }

        if (roundEnv.processingOver()) {
            for (val entry : models.entrySet()) {
                val apiName = entry.getKey();
                val modelList = entry.getValue();

                try {
                    val object = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,
                            "", "META-INF/impl/" + apiName);

                    try (val writer = object.openWriter()) {
                        for (val model : modelList) {
                            writer.append(model.getImplName());
                            writer.append(':');
                            writer.append(model.getPriority().name());
                            writer.append('\n');
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}
