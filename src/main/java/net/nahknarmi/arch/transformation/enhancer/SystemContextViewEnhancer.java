package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.Element;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.transformation.TransformationHelper;

public class SystemContextViewEnhancer implements WorkspaceEnhancer {
    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        ViewSet viewSet = workspace.getViews();

        if (dataStructure.getModel().getViews().getSystemView() != null) {
            dataStructure.getModel().getViews().getSystemView().getSystems().stream().forEach(s -> {
                SoftwareSystem softwareSystem = workspace.getModel().getSoftwareSystemWithName(s.getName());
                SystemContextView context = viewSet.createSystemContextView(softwareSystem, s.getName(), s.getDescription());

                s.getRelationships().stream().forEach(r -> {
                    String description = r.getDescription();
                    Element fromElement = TransformationHelper.getElementWithName(workspace, r.getName());
                    Element toElement = TransformationHelper.getElementWithName(workspace, r.getWith());

                    addRelationshipToContext(context, fromElement, toElement, description);
                });

                context.setAutomaticLayout(true);
            });
        }
    }

    private void addRelationshipToContext(SystemContextView context, Element fromElement, Element toElement, String description) {
        // TODO: Clean up, currently only Person.uses interaction type supported
        if (fromElement instanceof Person) {
            Person fromPerson = (Person) fromElement;
            SoftwareSystem toSystem = (SoftwareSystem) toElement;

            fromPerson.uses(toSystem, description);
            context.add(fromPerson);
            context.add(toSystem);
        }
    }
}