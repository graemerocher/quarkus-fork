package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.AnnotationAttribute;
import cdi.lite.extension.model.AnnotationInfo;
import cdi.lite.extension.model.configs.FieldConfig;
import cdi.lite.extension.model.declarations.ClassInfo;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.function.Predicate;
import org.jboss.jandex.DotName;

class FieldConfigImpl extends FieldInfoImpl implements FieldConfig<Object> {
    private final FieldAnnotationTransformations transformations;

    FieldConfigImpl(org.jboss.jandex.IndexView jandexIndex, org.jboss.jandex.FieldInfo jandexDeclaration,
            FieldAnnotationTransformations transformations) {
        super(jandexIndex, jandexDeclaration);
        this.transformations = transformations;
    }

    private FieldAnnotationTransformations.Key key() {
        return new FieldAnnotationTransformations.Key(jandexDeclaration.declaringClass().name(),
                jandexDeclaration.name());
    }

    @Override
    public void addAnnotation(Class<? extends Annotation> clazz, AnnotationAttribute... attributes) {
        transformations.add(key(), ctx -> {
            org.jboss.jandex.AnnotationValue[] jandexAnnotationAttributes = Arrays.stream(attributes)
                    .map(it -> ((AnnotationAttributeImpl) it).jandexAnnotationAttribute)
                    .toArray(org.jboss.jandex.AnnotationValue[]::new);
            ctx.transform().add(clazz, jandexAnnotationAttributes).done();
        });
    }

    @Override
    public void addAnnotation(ClassInfo<?> clazz, AnnotationAttribute... attributes) {
        transformations.add(key(), ctx -> {
            DotName jandexName = ((ClassInfoImpl) clazz).jandexDeclaration.name();
            org.jboss.jandex.AnnotationValue[] jandexAnnotationAttributes = Arrays.stream(attributes)
                    .map(it -> ((AnnotationAttributeImpl) it).jandexAnnotationAttribute)
                    .toArray(org.jboss.jandex.AnnotationValue[]::new);
            ctx.transform().add(jandexName, jandexAnnotationAttributes).done();
        });
    }

    @Override
    public void addAnnotation(AnnotationInfo annotation) {
        transformations.add(key(), ctx -> {
            ctx.transform().add(((AnnotationInfoImpl) annotation).jandexAnnotation).done();
        });
    }

    @Override
    public void removeAnnotation(Predicate<AnnotationInfo> predicate) {
        transformations.add(key(), ctx -> {
            ctx.transform().remove(new Predicate<org.jboss.jandex.AnnotationInstance>() {
                @Override
                public boolean test(org.jboss.jandex.AnnotationInstance annotationInstance) {
                    return predicate.test(new AnnotationInfoImpl(jandexIndex, annotationInstance));
                }
            }).done();
        });
    }

    @Override
    public void removeAllAnnotations() {
        transformations.add(key(), ctx -> {
            ctx.transform().removeAll().done();
        });
    }
}
