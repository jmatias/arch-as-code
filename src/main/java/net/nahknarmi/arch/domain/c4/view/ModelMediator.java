package net.nahknarmi.arch.domain.c4.view;

import com.structurizr.model.*;
import net.nahknarmi.arch.domain.c4.C4Path;

public class ModelMediator {
    private final Model model;

    public ModelMediator(Model model) {
        this.model = model;
    }

    public Person person(C4Path path) {
        String id = path.getPath();
        return (Person) model.getElement(id);
    }

    public SoftwareSystem softwareSystem(C4Path path) {
        String id = path.systemPath().getPath();
        return (SoftwareSystem) model.getElement(id);
    }

    public Container container(C4Path path) {
        String id = path.containerPath().getPath();
        return (Container) model.getElement(id);
    }

    public Component component(C4Path path) {
        String id = path.componentPath().getPath();
        return (Component) model.getElement(id);
    }

}
