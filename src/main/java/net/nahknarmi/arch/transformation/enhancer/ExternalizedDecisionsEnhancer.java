package net.nahknarmi.arch.transformation.enhancer;

import com.structurizr.Workspace;
import com.structurizr.documentation.Format;
import net.nahknarmi.arch.domain.ArchitectureDataStructure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.stream.Stream;

import static com.structurizr.documentation.DecisionStatus.Proposed;
import static com.structurizr.documentation.Format.Markdown;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

public class ExternalizedDecisionsEnhancer implements WorkspaceEnhancer {
    private final File documentationRoot;

    public ExternalizedDecisionsEnhancer(File documentationRoot) {
        this.documentationRoot = documentationRoot;
    }

    @Override
    public void enhance(Workspace workspace, ArchitectureDataStructure dataStructure) {
        File[] filesInDirectory = decisionsPath(dataStructure).listFiles();

        Stream.of(ofNullable(filesInDirectory)
                .orElse(new File[0]))
                .sorted()
                .forEach(file -> {
                    Format format = Markdown;
                    String sectionDefinition = "##";

                    try {
                        String content = new String(Files.readAllBytes(file.toPath()), UTF_8);

                        //TODO: need to find better way of inferring/storing the last modified date & Decision
                        workspace.getDocumentation()
                                .addDecision(file.getName(), new Date(file.lastModified()), file.getName(), Proposed, Markdown, content);
                    } catch (IOException e) {
                        throw new IllegalStateException(String.format("Failed to read contents of file %s", file), e);
                    }
                });
    }

    private File decisionsPath(ArchitectureDataStructure dataStructure) {
        String path = String.format("%s/%s/decisions/", documentationRoot.getAbsolutePath(), dataStructure.getName().toLowerCase());
        return new File(path);
    }

}
