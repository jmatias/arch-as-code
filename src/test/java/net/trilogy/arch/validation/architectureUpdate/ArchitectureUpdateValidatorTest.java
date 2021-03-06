package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.CapabilitiesContainer;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.Epic;
import net.trilogy.arch.domain.architectureUpdate.FeatureStory;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Jira;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

public class ArchitectureUpdateValidatorTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private ArchitectureUpdate invalidAu;

    @Before
    public void setUp() {
        invalidAu = ArchitectureUpdate.builderPreFilledWithBlanks()
                .decisions(Map.of(
                        new Decision.Id("Missing-TDD-Decision-1"), new Decision("Decision-1-text", null),
                        new Decision.Id("Missing-TDD-Decision-2"), new Decision("Decision-2-text", List.of()),
                        new Decision.Id("Bad-TDD-Decision"), new Decision("Decision-2-text", List.of(new Tdd.Id("BAD-TDD-ID"))),
                        new Decision.Id("Valid-Decision"), new Decision("Decision-3-text", List.of(new Tdd.Id("Valid-TDD-with-decision-and-story")))
                ))
                .functionalRequirements(
                        Map.of(
                                new FunctionalRequirement.Id("Bad-TDD-Functional-Requirement"),
                                new FunctionalRequirement("Text", "Source", List.of(new Tdd.Id("BAD-TDD-ID"))),

                                new FunctionalRequirement.Id("Valid-Functional-Requirement"),
                                new FunctionalRequirement("Text", "Source", List.of()),

                                new FunctionalRequirement.Id("Valid-Functional-Requirement-2"),
                                new FunctionalRequirement("Text", "Source", List.of(new Tdd.Id("Valid-TDD-with-requirement-and-story")))
                        )
                )
                .TDDs(
                        Map.of(
                                new Tdd.ComponentReference("Component-0"), List.of(
                                        new Tdd(new Tdd.Id("Valid-TDD-with-requirement-and-story"), "text")
                                ),
                                new Tdd.ComponentReference("Component-1"), List.of(
                                        new Tdd(new Tdd.Id("TDD-unused-and-without-story"), "text")
                                ),
                                new Tdd.ComponentReference("Component-2"), List.of(
                                        new Tdd(new Tdd.Id("Valid-TDD-with-decision-and-story"), "text"),
                                        new Tdd(new Tdd.Id("TDD-unused-with-story"), "text")
                                )
                        )
                )
                .capabilityContainer(
                        new CapabilitiesContainer(
                                Epic.blank(),
                                List.of(
                                        new FeatureStory("Feat Title", Jira.blank(), List.of(
                                                new Tdd.Id("Valid-TDD-with-requirement-and-story")
                                        ), List.of()),
                                        new FeatureStory("Feat Title", Jira.blank(), List.of(
                                                new Tdd.Id("Valid-TDD-with-decision-and-story"),
                                                new Tdd.Id("TDD-unused-with-story")
                                        ), List.of())
                                )
                        )
                )
                .build();
    }

    @Test
    public void blankAuShouldBeValid() {
        var au = ArchitectureUpdate.blank();

        var result = ArchitectureUpdateValidator.validate(au);

        collector.checkThat(result.isValid(), is(true));
        collector.checkThat(result.isValid(ValidationStage.CAPABILITY), is(true));
        collector.checkThat(result.isValid(ValidationStage.TDD), is(true));
    }

    @Test
    public void shouldFindAllErrors() {
        ArchitectureUpdate au = invalidAu;

        var actualErrors = ArchitectureUpdateValidator.validate(au).getErrors();
        var expectedErrors = List.of(
                ValidationError.forInvalidTddReference(new Decision.Id("Bad-TDD-Decision"), new Tdd.Id("BAD-TDD-ID")),
                ValidationError.forInvalidTddReference(new FunctionalRequirement.Id("Bad-TDD-Functional-Requirement"), new Tdd.Id("BAD-TDD-ID")),

                ValidationError.forMissingTddReference(new Decision.Id("Missing-TDD-Decision-1")),
                ValidationError.forMissingTddReference(new Decision.Id("Missing-TDD-Decision-2")),

                ValidationError.forTddWithoutStory(new Tdd.Id("TDD-unused-and-without-story")),

                ValidationError.forTddWithoutCause(new Tdd.Id("TDD-unused-and-without-story")),
                ValidationError.forTddWithoutCause(new Tdd.Id("TDD-unused-with-story"))
        );

        collector.checkThat(actualErrors.size(), equalTo(expectedErrors.size()));

        expectedErrors.forEach(e ->
                collector.checkThat(actualErrors, hasItem(e)));
    }
}
