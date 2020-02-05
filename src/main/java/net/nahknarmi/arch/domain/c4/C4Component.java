package net.nahknarmi.arch.domain.c4;

<<<<<<< Updated upstream
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;
=======
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
>>>>>>> Stashed changes

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4Component extends BaseEntity implements Entity {
    @NonNull
    protected String technology;

    @Builder
    public C4Component(String name, @NonNull C4Path path, @NonNull String description, @NonNull List<C4Tag> tags, @NonNull List<C4Relationship> relationships, @NonNull String technology) {
        super(name, path, description, tags, relationships);
        this.technology = technology;
    }

    @JsonIgnore
    public String getName() {
        if (name != null) {
            return name;
        } else {
            return path.getComponentName().orElseThrow(() -> new IllegalStateException("Workspace Id not found!!"));
        }
    }
}
