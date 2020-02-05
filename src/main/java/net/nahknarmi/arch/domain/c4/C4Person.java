package net.nahknarmi.arch.domain.c4;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4Person extends BaseEntity implements Entity, Locatable {

<<<<<<< Updated upstream
    private C4Location location = C4Location.UNSPECIFIED;

    @Builder
    public C4Person(String name, C4Path path, String description, List<C4Tag> tags, List<C4Relationship> relationships, C4Location location) {
        super(name, path, description, tags, relationships);
        this.location = location;
    }

    @JsonIgnore
=======
    public C4Person(String name, C4Path path, String technology, String description, List<C4Tag> tags, List<C4Relationship> relationships, C4Location location) {
        super(name, path, technology, description, tags, relationships);
        this.location = location;
    }

    private C4Location location = C4Location.UNSPECIFIED;

>>>>>>> Stashed changes
    public String getName() {
        if (name != null) {
            return name;
        } else {
            return path.getPersonName();
        }
    }
}
