package me.jaxvy.guvercin.compiler;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;

import me.jaxvy.guvercin.Guvercin;

import static java.util.Collections.singleton;
import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.Diagnostic.Kind.NOTE;

@AutoService(Processor.class)
public class GuvercinProcessor extends AbstractProcessor {

    private static Random random = new Random(System.currentTimeMillis());
    private static final int RANDOM_NUMBER_RANGE = 1024 * 1024;
    private GuvercinGenerator guvercinGenerator;
    private Messager messager;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        guvercinGenerator = new GuvercinGenerator(processingEnv.getFiler(), messager);
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, ClassBinding> classBindingMap = getMethodBindings(roundEnv);
        determineGeneratedParents(classBindingMap);
        guvercinGenerator.generateGuvercinClasses(classBindingMap);
        return true;
    }

    private Map<String, ClassBinding> getMethodBindings(RoundEnvironment roundEnv) {
        Map<String, ClassBinding> classBindingMap = new HashMap<>();
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Guvercin.class)) {
            if (annotatedElement.getKind() == ElementKind.METHOD) {
                String methodTag = getAnnotationTag(annotatedElement);
                String methodName = getAnnotatedMethodName(annotatedElement);
                boolean hasIntentParam = hasIntentParamAsInput(annotatedElement);
                String qualifiedName = getClassQualifiedName(annotatedElement);

                ClassBinding classBinding = classBindingMap.get(qualifiedName);
                if (classBinding == null) {
                    classBinding = new ClassBinding();
                    classBinding.setQualifiedName(qualifiedName);
                    classBinding.setClassHierarchy(getClassHierarchy(annotatedElement));
                    classBinding.setFragment(isFragment(annotatedElement));
                }
                String methodVariableName = "br_" + methodName + "_" + random.nextInt(RANDOM_NUMBER_RANGE);
                classBinding.addMethod(new MethodBinding(methodName, methodTag, hasIntentParam, methodVariableName));
                classBindingMap.put(qualifiedName, classBinding);
            }
        }
        return classBindingMap;
    }

    private void determineGeneratedParents(Map<String, ClassBinding> classBindingMap) {
        for (ClassBinding classBinding : classBindingMap.values()) {
            for (String parentQualifiedName : classBinding.getClassHierarchy()) {
                if (classBindingMap.containsKey(parentQualifiedName)) {
                    classBinding.setGeneratedParent(parentQualifiedName);
                    break;
                }
            }
        }
    }

    private String getAnnotationTag(Element element) {
        String annotationTag = null;
        if (element.getAnnotationMirrors().size() == 1) {
            AnnotationMirror annotationMirror = element.getAnnotationMirrors().get(0);
            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
            if (elementValues != null) {
                for (ExecutableElement executableElement : elementValues.keySet()) {
                    annotationTag = elementValues.get(executableElement).toString();
                    break;
                }
            }
        }
        return annotationTag;
    }

    private String getAnnotatedMethodName(Element element) {
        ExecutableElement executableElement = (ExecutableElement) element;
        return executableElement.getSimpleName().toString();
    }

    private boolean hasIntentParamAsInput(Element element) {
        ExecutableElement executableElement = (ExecutableElement) element;
        boolean hasIntentParam = false;
        List<? extends VariableElement> parameters = executableElement.getParameters();
        if (parameters.size() > 0) {
            VariableElement variableElement = parameters.get(0);
            hasIntentParam = variableElement.asType().toString().equals("android.content.Intent");
        }
        return hasIntentParam;
    }

    private String getClassQualifiedName(Element element) {
        TypeElement declaringClass = (TypeElement) element.getEnclosingElement();
        return declaringClass.getQualifiedName().toString();
    }

    /**
     * Used to determine the generatedParent (which is the first parent in a classes parent hierarchy
     * which uses the @Guvercin annotation.
     *
     * @param element
     * @return
     */
    private List<String> getClassHierarchy(Element element) {
        List<String> classHierarchy = new ArrayList<>();
        TypeElement parentClass = (TypeElement) element.getEnclosingElement();
        do {
            parentClass = (TypeElement) typeUtils.asElement(parentClass.getSuperclass());
            String parentQualifiedName = parentClass.toString();
            if (parentQualifiedName.startsWith("android")) {
                break;
            }
            classHierarchy.add(parentClass.toString());
        } while (true);
        return classHierarchy;
    }

    /**
     * @param element
     * @return true is Fragment, false if Activity
     */
    private boolean isFragment(Element element) {
        boolean isFragment = false;
        TypeElement parentClass = (TypeElement) element.getEnclosingElement();
        do {
            parentClass = (TypeElement) typeUtils.asElement(parentClass.getSuperclass());
            String parentQualifiedName = parentClass.toString();
            if (parentQualifiedName.startsWith("android")) {
                isFragment = parentQualifiedName.contains("Fragment");
                messager.printMessage(NOTE, parentQualifiedName);
                break;
            }
        } while (true);
        return isFragment;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return singleton(Guvercin.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }
}
