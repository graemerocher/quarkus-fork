package io.quarkus.arc.processor.cdi.lite.ext;

import cdi.lite.extension.model.declarations.ClassInfo;
import cdi.lite.extension.model.types.ClassType;
import org.jboss.jandex.DotName;

class ClassTypeImpl extends TypeImpl<org.jboss.jandex.ClassType> implements ClassType {
    ClassTypeImpl(org.jboss.jandex.IndexView jandexIndex, AllAnnotationOverlays annotationOverlays,
            org.jboss.jandex.ClassType jandexType) {
        super(jandexIndex, annotationOverlays, jandexType);
    }

    @Override
    public ClassInfo declaration() {
        DotName name = jandexType.name();
        return new ClassInfoImpl(jandexIndex, annotationOverlays, jandexIndex.getClassByName(name));
    }
}
