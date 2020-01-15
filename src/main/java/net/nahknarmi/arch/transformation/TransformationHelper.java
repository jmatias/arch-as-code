package net.nahknarmi.arch.transformation;

import com.structurizr.Workspace;
import com.structurizr.model.Element;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class TransformationHelper {

    private TransformationHelper() {
    }

    public static Element getElementWithName(Workspace workspace, String name) {
        checkNotNull(workspace);
        checkNotNull(name);

        return workspace.getModel()
                .getElements()
                .stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unable to find element with name %s.", name)));
    }
}
