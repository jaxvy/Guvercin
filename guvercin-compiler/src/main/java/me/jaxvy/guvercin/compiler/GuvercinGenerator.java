package me.jaxvy.guvercin.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;

/**
 * Generates the classes that handle LocalBroadcastManager registration and unregistration.
 */
public class GuvercinGenerator {
    private static final ClassName GUVERCIN_BINDER = ClassName.get("me.jaxvy.guvercin", "GuvercinBinder");
    private static final ClassName GUVERCIN_UNBINDER = ClassName.get("me.jaxvy.guvercin", "GuvercinUnbinder");

    private static final ClassName LOCAL_BROADCAST_MANAGER = ClassName.get("android.support.v4.content", "LocalBroadcastManager");
    private static final ClassName BROADCAST_RECEIVER = ClassName.get("android.content", "BroadcastReceiver");
    private static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    private static final ClassName INTENT = ClassName.get("android.content", "Intent");
    private static final ClassName INTENT_FILTER = ClassName.get("android.content", "IntentFilter");

    private Filer filer;
    private Messager messager;

    public GuvercinGenerator(Filer filer, Messager messager) {
        this.filer = filer;
        this.messager = messager;
    }

    public void generateGuvercinClasses(Map<String, ClassBinding> classBindingMap) {
        for (ClassBinding classBinding : classBindingMap.values()) {
            generateClass(classBinding);
        }
    }

    private void generateClass(ClassBinding classBinding) {
        ClassName originalClassName = ClassName.get(classBinding.getPackageName(), classBinding.getClassName());
        TypeVariableName typeVariableName = TypeVariableName.get("T", originalClassName);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classBinding.getClassName() + "_Guvercin")
                .addTypeVariable(typeVariableName)
                .addModifiers(Modifier.PUBLIC);

        String generatedParent = classBinding.getGeneratedParent();
        if (generatedParent == null) {
            classBuilder.addSuperinterface(ParameterizedTypeName.get(GUVERCIN_BINDER, ClassName.get("", "T")));
        } else {
            String packageName = ClassBinding.getPackageName(generatedParent);
            String className = ClassBinding.getClassName(generatedParent);
            ClassName parentClassName = ClassName.get(packageName, className + "_Guvercin");
            classBuilder.superclass(ParameterizedTypeName.get(parentClassName, ClassName.get("", "T")));
        }

        TypeSpec classSpec = classBuilder.addMethod(bind(classBinding))
                .addType(innerUnbinderClass(classBinding))
                .build();

        JavaFile javaFile = JavaFile.builder(classBinding.getPackageName(), classSpec)
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MethodSpec bind(ClassBinding classBinding) {
        ClassName originalClassName = ClassName.get(classBinding.getPackageName(), classBinding.getClassName());
        TypeVariableName typeVariableName = TypeVariableName.get("T", originalClassName);
        return MethodSpec.methodBuilder("bind")
                .addAnnotation(Override.class)
                .returns(GUVERCIN_UNBINDER)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(typeVariableName, "target")
                .addStatement("return new InnerUnbinder(target)")
                .build();
    }


    private TypeSpec innerUnbinderClass(ClassBinding classBinding) {
        ClassName originalClassName = ClassName.get(classBinding.getPackageName(), classBinding.getClassName());

        TypeSpec.Builder innerUnbinderBuilder = TypeSpec.classBuilder("InnerUnbinder");

        String generatedParent = classBinding.getGeneratedParent();
        if (generatedParent == null) {
            innerUnbinderBuilder.addSuperinterface(GUVERCIN_UNBINDER);
        } else {
            String packageName = ClassBinding.getPackageName(generatedParent);
            String className = ClassBinding.getClassName(generatedParent);
            ClassName parentClassName = ClassName.get(packageName, className + "_Guvercin.InnerUnbinder");
            innerUnbinderBuilder.superclass(parentClassName);
        }

        innerUnbinderBuilder.addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                .addField(originalClassName, "target", Modifier.PRIVATE)
                .addField(LOCAL_BROADCAST_MANAGER, "localBroadcastManager", Modifier.PRIVATE);

        for (MethodBinding methodBinding : classBinding.getMethods()) {
            innerUnbinderBuilder.addField(BROADCAST_RECEIVER, methodBinding.getMethodVariableName(), Modifier.PRIVATE);
        }

        innerUnbinderBuilder.addMethod(unbinderConstructor(classBinding))
                .addMethod(unBind(classBinding));

        return innerUnbinderBuilder.build();
    }

    private MethodSpec unbinderConstructor(ClassBinding classBinding) {
        ClassName originalClassName = ClassName.get(classBinding.getPackageName(), classBinding.getClassName());
        MethodSpec.Builder innerUnbinderConstructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PROTECTED)
                .addParameter(originalClassName, "target", Modifier.FINAL);
        if (classBinding.getGeneratedParent() != null) {
            innerUnbinderConstructorBuilder.addStatement("super(target)");
        }


        String contextCall;
        if (classBinding.isFragment()) {
            contextCall = "target.getContext()";
        } else {
            contextCall = "target.getBaseContext()";
        }

        innerUnbinderConstructorBuilder.addStatement("this.target = target")
                .addStatement("localBroadcastManager = $T.getInstance(" + contextCall + ")", LOCAL_BROADCAST_MANAGER);


        for (MethodBinding methodBinding : classBinding.getMethods()) {
            innerUnbinderConstructorBuilder.addStatement(broadcastReceiverStatement(methodBinding),
                    BROADCAST_RECEIVER, CONTEXT, INTENT);

            String methodTag = methodBinding.getTag();
            innerUnbinderConstructorBuilder.addStatement("localBroadcastManager.registerReceiver(" +
                    methodBinding.getMethodVariableName() + ", new $T(" + methodTag + "))", INTENT_FILTER);
        }
        return innerUnbinderConstructorBuilder.build();
    }

    private String broadcastReceiverStatement(MethodBinding methodBinding) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(methodBinding.getMethodVariableName() + " = new $T() {\n");
        stringBuilder.append("  @Override\n");
        stringBuilder.append("  public void onReceive($T context, $T intent) {\n");
        stringBuilder.append("    target." + methodBinding.getName() + "(");
        stringBuilder.append(methodBinding.hasIntentParam() ? "intent" : "");
        stringBuilder.append(");\n  }\n}");
        return stringBuilder.toString();
    }

    private MethodSpec unBind(ClassBinding classBinding) {
        MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unbind")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);

        if (classBinding.getGeneratedParent() != null) {
            unbindMethodBuilder.addStatement("super.unbind()");
        }
        String contextCall;
        if (classBinding.isFragment()) {
            contextCall = "target.getContext()";
        } else {
            contextCall = "target.getBaseContext()";
        }
        unbindMethodBuilder.addStatement("localBroadcastManager = $T.getInstance(" + contextCall + ")", LOCAL_BROADCAST_MANAGER);

        for (MethodBinding methodBinding : classBinding.getMethods()) {
            unbindMethodBuilder.addStatement("localBroadcastManager.unregisterReceiver(" + methodBinding.getMethodVariableName() + ")");
            unbindMethodBuilder.addStatement(methodBinding.getMethodVariableName() + " = null");
        }

        unbindMethodBuilder.addStatement("target = null");
        return unbindMethodBuilder.build();
    }
}
