package me.jaxvy.guvercin.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores necessary information to generate a class. Each class has a packageName, className and a list of
 * methodBindings
 */
public class ClassBinding {
    private String qualifiedName; //i.e. me.jaxvy.guvercin.GuvercinProcessor
    private List<String> classHierarchy;
    private boolean isFragment; // if false, it's an Activity

    /**
     * If current class extends another class using the @Guvercin annotation, then the generatedParent
     * is first such parent, otherwise its null (meaning the generated code will extend
     * GuvercinBinder and GuvercinUnbinder).
     */
    private String generatedParent;
    private List<MethodBinding> methodBindings;

    public String getPackageName() {
        return getPackageName(qualifiedName);
    }


    public String getClassName() {
        return getClassName(qualifiedName);
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public List<MethodBinding> getMethods() {
        return methodBindings;
    }

    public void addMethod(MethodBinding methodBinding) {
        if (methodBindings == null) {
            methodBindings = new ArrayList<>();
        }
        methodBindings.add(methodBinding);
    }

    public List<String> getClassHierarchy() {
        return classHierarchy;
    }

    public void setClassHierarchy(List<String> classHierarchy) {
        this.classHierarchy = classHierarchy;
    }

    public String getGeneratedParent() {
        return generatedParent;
    }

    public void setGeneratedParent(String generatedParent) {
        this.generatedParent = generatedParent;
    }

    public static String getPackageName(String classQualifiedName) {
        return classQualifiedName.substring(0, classQualifiedName.lastIndexOf("."));
    }

    public static String getClassName(String classQualifiedName) {
        return classQualifiedName.substring(classQualifiedName.lastIndexOf(".") + 1, classQualifiedName.length());
    }

    public boolean isFragment() {
        return isFragment;
    }

    public void setFragment(boolean fragment) {
        isFragment = fragment;
    }
}
