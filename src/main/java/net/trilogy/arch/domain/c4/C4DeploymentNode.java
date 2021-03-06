package net.trilogy.arch.domain.c4;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4DeploymentNode extends BaseEntity implements Entity {
    private String technology;
    private String environment;
    private Integer instances;
    private List<C4DeploymentNode> children = new ArrayList<>();
    private List<C4ContainerInstance> containerInstances = new ArrayList<>();

    @Builder
    C4DeploymentNode(String id,
                     String alias,
                     String name,
                     C4Path path,
                     String description,
                     @Singular Set<C4Tag> tags,
                     @Singular List<C4Relationship> relationships,
                     String technology,
                     String environment,
                     Integer instances,
                     List<C4DeploymentNode> children,
                     List<C4ContainerInstance> containerInstances) {
        super(id, alias, path, name, description, tags, relationships);
        this.technology = technology;
        this.environment = environment;
        this.instances = instances;
        this.children = children;
        this.containerInstances = containerInstances;
    }

    public void addChild(C4DeploymentNode node) {
        this.children.add(node);
    }

    public void addContainerInstance(C4ContainerInstance containerInstance) {
        this.containerInstances.add(containerInstance);
    }

    public C4Type getType() {
        return C4Type.deploymentNode;
    }
}
