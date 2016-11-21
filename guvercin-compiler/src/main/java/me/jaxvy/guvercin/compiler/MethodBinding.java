package me.jaxvy.guvercin.compiler;

/**
 * Stores information about @Guvercin annotated methodBindings.
 */
public class MethodBinding {
    private String name;
    private String tag;
    private boolean hasIntentParam;
    private String methodVariableName; // Unique name for broadcast receiver variable

    public MethodBinding(String name, String tag, boolean hasIntentParam, String methodVariableName) {
        this.name = name;
        this.tag = tag;
        this.hasIntentParam = hasIntentParam;
        this.methodVariableName = methodVariableName;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public boolean hasIntentParam() {
        return hasIntentParam;
    }

    public String getMethodVariableName() {
        return methodVariableName;
    }
}
