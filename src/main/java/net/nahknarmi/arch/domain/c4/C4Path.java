package net.nahknarmi.arch.domain.c4;

import lombok.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4Path {
    private static final int COMPONENT_GROUP_NUMBER = 4;
    private static final int CONTAINER_GROUP_NUMBER = 3;
    private static final int SYSTEM_OR_PERSON_GROUP_NUMBER = 2;
    private static final String ENTITY_PREFIX = "c4://";
    private static final String PERSON_PREFIX = "@";

    private static final String regex = "(c4:\\/\\/|\\@)([\\w\\s\\-]+)\\/?([\\w\\s\\-]+)?\\/?([\\w\\s\\-]+)?";
    private static final Pattern pattern = Pattern.compile(regex);

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Matcher matcher;

    @NonNull
    private String path;

    public C4Path(String path) {
        this.path = path;
        this.matcher = matcher();
    }

    private Matcher matcher() {
        if (this.matcher == null) {
            this.matcher = pattern.matcher(this.path);
            boolean found = matcher.find();
            checkArgument(found, "Path does not match expected pattern.");
        }
        return this.matcher;
    }

    public String getName() {
        return Arrays.stream(path.split("(/|//|\\@)"))
                .reduce((first, second) -> second)
                .orElse(null);
    }

    public C4Type getType() {
        if (this.getPersonName() != null) {
            return C4Type.person;
        }

        if (this.getComponentName().isPresent()) {
            return C4Type.component;
        }

        if (this.getContainerName().isPresent()) {
            return C4Type.container;
        }

        if (this.getSystemName() != null) {
            return C4Type.system;
        }

        return null;
    }

    public String getPersonName() {
        if (this.path.startsWith(PERSON_PREFIX)) {
            return matcher().group(SYSTEM_OR_PERSON_GROUP_NUMBER);
        }

        return null;
    }

    public String getSystemName() {
        if (this.path.startsWith(ENTITY_PREFIX)) {
            return matcher().group(SYSTEM_OR_PERSON_GROUP_NUMBER);
        }

        return null;
    }

    public Optional<String> getContainerName() {
        if (this.path.startsWith(ENTITY_PREFIX)) {
            return ofNullable(matcher().group(CONTAINER_GROUP_NUMBER));
        }

        return empty();
    }

    public Optional<String> getComponentName() {
        if (this.path.startsWith(ENTITY_PREFIX)) {
            return ofNullable(matcher().group(COMPONENT_GROUP_NUMBER));
        }

        return empty();
    }
}