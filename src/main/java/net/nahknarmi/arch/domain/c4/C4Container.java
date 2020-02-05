package net.nahknarmi.arch.domain.c4;

import lombok.*;

import java.util.List;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4Container extends BaseEntity implements Entity {
    @NonNull
    protected String technology;

    @Builder
    public C4Container(String name, @NonNull C4Path path, @NonNull String description, @NonNull List<C4Tag> tags, @NonNull List<C4Relationship> relationships, @NonNull String technology) {
        super(name, path, description, tags, relationships);
        this.technology = technology;
    }

<<<<<<< Updated upstream
=======
    C4Container(String name, C4Path path, String technology, String description, List<C4Tag> tags, List<C4Relationship> relationships) {
        super(name, path, technology, description, tags, relationships);
    }

>>>>>>> Stashed changes
    public String getName() {
        if (name != null) {
            return name;
        } else {
            return path.getContainerName().orElseThrow(() -> new IllegalStateException("Container name not found in path " + path));
        }
    }
}
