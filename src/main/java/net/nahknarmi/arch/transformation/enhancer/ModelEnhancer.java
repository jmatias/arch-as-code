package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.model.*;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;
import net.nahknarmi.arch.domain.c4.*;
import net.nahknarmi.arch.domain.c4.view.ModelMediator;
import net.nahknarmi.arch.generator.PathIdGenerator;
import net.nahknarmi.arch.transformation.LocationTransformer;

import static java.util.Optional.ofNullable;
import static net.nahknarmi.arch.domain.c4.C4Model.NONE;

public class ModelEnhancer implements WorkspaceEnhancer {

    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        Model workspaceModel = workspace.getModel();
        C4Model dataStructureModel = dataStructure.getModel();

        workspaceModel.setIdGenerator(new PathIdGenerator(dataStructureModel));

        addPeople(workspaceModel, dataStructureModel);
        addSystems(workspaceModel, dataStructureModel);
        addContainers(workspaceModel, dataStructureModel);
        addComponents(workspaceModel, dataStructureModel);
        addRelationships(workspaceModel, dataStructureModel);
    }

    private void addPeople(Model model, C4Model dataStructureModel) {
        ofNullable(dataStructureModel)
                .orElse(NONE)
                .getPeople()
                .forEach(p -> {
                    Location location = LocationTransformer.c4LocationToLocation(p.getLocation());
                    Person person = model.addPerson(location, p.name(), p.getDescription());
                    person.addTags(getTags(p));
                });
    }

    private void addSystems(Model model, C4Model dataStructureModel) {
        ofNullable(dataStructureModel)
                .orElse(NONE)
                .getSystems()
                .forEach(s -> addSystem(model, s));
    }

    private void addSystem(Model model, C4SoftwareSystem s) {
        Location location = LocationTransformer.c4LocationToLocation(s.getLocation());
        SoftwareSystem softwareSystem = model.addSoftwareSystem(location, s.name(), s.getDescription());
        softwareSystem.addTags(getTags(s));
    }

    private void addContainers(Model workspaceModel, C4Model dataStructureModel) {
        dataStructureModel.getContainers().forEach(c -> addContainer(workspaceModel, c));
    }

    private void addContainer(Model model, C4Container c) {
        SoftwareSystem softwareSystem = new ModelMediator(model).softwareSystem(c.getPath().systemPath());
        Container container = softwareSystem.addContainer(c.name(), c.getDescription(), c.getTechnology());
        container.addTags(getTags(c));
    }

    private void addComponents(Model workspaceModel, C4Model dataStructureModel) {
        dataStructureModel.getComponents().forEach(c -> addComponent(workspaceModel, c));
    }

    private void addComponent(Model model, C4Component c) {
        Container container = new ModelMediator(model).container(c.getPath().containerPath());
        Component component = container.addComponent(c.name(), c.getDescription(), c.getTechnology());

        component.addTags(getTags(c));
    }

    private String[] getTags(HasTag t) {
        return t.getTags().stream().map(C4Tag::getTag).toArray(String[]::new);
    }

    private void addRelationships(Model workspaceModel, C4Model dataStructureModel) {
        addPeopleRelationships(workspaceModel, dataStructureModel);
        addSystemRelationships(workspaceModel, dataStructureModel);
        addContainerRelationships(workspaceModel, dataStructureModel);
        addComponentRelationships(workspaceModel, dataStructureModel);
    }

    private void addPeopleRelationships(Model workspaceModel, C4Model dataStructureModel) {
        ModelMediator modelMediator = new ModelMediator(workspaceModel);

        dataStructureModel.getPeople().forEach(p -> {
            Person person = modelMediator.person(p.getPath());

            p.getRelationships()
                    .forEach(r -> {
                        String description = r.getDescription();
                        String technology = r.getTechnology();
                        C4Type typeDestination = r.getWith().type();

                        if (r.getAction() == C4Action.USES) {
                            SoftwareSystem systemDestination = modelMediator.softwareSystem(r.getWith());

                            switch (typeDestination) {
                                case system: {
                                    person.uses(systemDestination, description, technology);
                                    break;
                                }
                                case container: {
                                    Container container = modelMediator.container(r.getWith());
                                    person.uses(container, description, technology);
                                    break;
                                }
                                case component: {
                                    Component component = modelMediator.component(r.getWith());
                                    person.uses(component, description, technology);
                                    break;
                                }
                                default:
                                    throw new IllegalStateException("Unsupported type " + typeDestination);
                            }
                        }
                        if (r.getAction() == C4Action.INTERACTS_WITH) {
                            if (typeDestination != C4Type.person) {
                                throw new IllegalStateException("Action INTERACTS_WITH supported only with type person, not: " + typeDestination);
                            } else {
                                Person personDestination = modelMediator.person(r.getWith());
                                person.interactsWith(personDestination, description, technology);
                            }
                        }
                    });
        });
    }

    private void addSystemRelationships(Model workspaceModel, C4Model dataStructureModel) {
        ModelMediator modelMediator = new ModelMediator(workspaceModel);
        dataStructureModel.getSystems().forEach(s -> {
            SoftwareSystem softwareSystem = modelMediator.softwareSystem(s.getPath());

            s.getRelationships()
                    .forEach(r -> {
                        String description = r.getDescription();
                        String technology = r.getTechnology();
                        C4Type typeDestination = r.getWith().type();

                        if (r.getAction() == C4Action.USES) {
                            SoftwareSystem systemDestination = modelMediator.softwareSystem(r.getWith());

                            switch (typeDestination) {
                                case system: {
                                    softwareSystem.uses(systemDestination, description, technology);
                                    break;
                                }
                                case container: {
                                    Container containerDestination = modelMediator.container(r.getWith());
                                    softwareSystem.uses(containerDestination, description, technology);
                                    break;
                                }
                                case component: {
                                    Component component = modelMediator.component(r.getWith());
                                    softwareSystem.uses(component, description, technology);
                                    break;
                                }
                                default:
                                    throw new IllegalStateException("Unsupported type " + typeDestination);
                            }
                        }
                        if (r.getAction() == C4Action.DELIVERS) {
                            if (typeDestination != C4Type.person) {
                                throw new IllegalStateException("Action DELIVERS supported only with type person, not: " + typeDestination);
                            } else {
                                Person personDestination = modelMediator.person(r.getWith());
                                softwareSystem.delivers(personDestination, description, technology);
                            }
                        }
                    });
        });
    }

    private void addContainerRelationships(Model workspaceModel, C4Model dataStructureModel) {
        ModelMediator modelMediator = new ModelMediator(workspaceModel);

        dataStructureModel.getContainers().forEach(c -> {
            Container container = modelMediator.container(c.getPath());

            c.getRelationships()
                    .forEach(r -> {
                        String description = r.getDescription();
                        String technology = r.getTechnology();
                        C4Type typeDestination = r.getWith().type();

                        if (r.getAction() == C4Action.USES) {
                            SoftwareSystem systemDestination = modelMediator.softwareSystem(r.getWith());

                            switch (typeDestination) {
                                case system: {
                                    container.uses(systemDestination, description, technology);
                                    break;
                                }
                                case container: {
                                    Container containerDestination = modelMediator.container(r.getWith());
                                    container.uses(containerDestination, description, technology);
                                    break;
                                }
                                case component: {
                                    Component componentDestination = modelMediator.component(r.getWith());

                                    if (componentDestination == null) {
                                        System.err.println("Hanging reference - " + r.getWith());
                                    } else {
                                        container.uses(componentDestination, description, technology);
                                    }

                                    break;
                                }
                                default:
                                    throw new IllegalStateException("Unsupported type " + typeDestination);
                            }
                        }
                        if (r.getAction() == C4Action.DELIVERS) {
                            if (typeDestination != C4Type.person) {
                                throw new IllegalStateException("Action DELIVERS supported only with type person, not: " + typeDestination);
                            } else {
                                Person person = modelMediator.person(r.getWith());
                                container.delivers(person, description, technology);
                            }
                        }
                    });
        });
    }

    private void addComponentRelationships(Model workspaceModel, C4Model dataStructureModel) {
        ModelMediator modelMediator = new ModelMediator(workspaceModel);
        dataStructureModel.getComponents().forEach(comp -> {
            Component component = modelMediator.component(comp.getPath());

            comp.getRelationships()
                    .forEach(r -> {
                        String description = r.getDescription();
                        String technology = r.getTechnology();
                        C4Type typeDestination = r.getWith().type();

                        if (r.getAction() == C4Action.USES) {
                            SoftwareSystem systemDestination = modelMediator.softwareSystem(r.getWith());

                            switch (typeDestination) {
                                case system: {
                                    component.uses(systemDestination, description, technology);
                                    break;
                                }
                                case container: {
                                    Container containerDestination = modelMediator.container(r.getWith());
                                    component.uses(containerDestination, description, technology);
                                    break;
                                }
                                case component: {
                                    Component componentDestination = modelMediator.component(r.getWith());

                                    if (componentDestination == null) {
                                        System.err.println("Hanging reference " + r.getWith());
                                    } else {
                                        component.uses(componentDestination, description, technology);
                                    }

                                    break;
                                }
                                default:
                                    throw new IllegalStateException("Unsupported type " + typeDestination);
                            }
                        }
                        if (r.getAction() == C4Action.DELIVERS) {
                            if (typeDestination != C4Type.person) {
                                throw new IllegalStateException("Action DELIVERS supported only with type person, not: " + typeDestination);
                            } else {
                                Person person = modelMediator.person(r.getWith());
                                component.delivers(person, description, technology);
                            }
                        }
                    });
        });
    }
}
