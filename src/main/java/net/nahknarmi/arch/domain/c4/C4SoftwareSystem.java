package net.nahknarmi.arch.domain.c4;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4SoftwareSystem extends BaseEntity implements Entity, Locatable {
    private C4Location location = C4Location.UNSPECIFIED;

<<<<<<< Updated upstream

    @Builder
    public C4SoftwareSystem(String name, @NonNull C4Path path, @NonNull String description, @NonNull List<C4Tag> tags, @NonNull List<C4Relationship> relationships, C4Location location) {
        super(name, path, description, tags, relationships);
        this.location = location;
    }

    @JsonIgnore
=======
    public C4SoftwareSystem(String name, C4Path path, String technology, String description, List<C4Tag> tags, List<C4Relationship> relationships, C4Location location) {
        super(name, path, technology, description, tags, relationships);
        this.location = location;
    }

    private C4Location location = C4Location.UNSPECIFIED;

>>>>>>> Stashed changes
    public String getName() {
        if (name != null) {
            return name;
        } else {
            return path.getSystemName();
        }
    }
}
