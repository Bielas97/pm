package com.jbs.pm;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.AccessTarget.Predicates.constructor;
import static com.tngtech.archunit.core.domain.AccessTarget.Predicates.declaredIn;
import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.originOwner;
import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameContaining;
import static com.tngtech.archunit.lang.conditions.ArchConditions.callCodeUnitWhere;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.is;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.Predefined;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

public class HexagonalArchitectureTest {

  private static final String BASE = "com.jbs.pm";

  private static final String ADAPTER_IN = "adapter.input";

  private static final String ADAPTER_OUT = "adapter.output";

  private final JavaClasses importedClasses =
      new ClassFileImporter()
          .withImportOption(Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages(BASE);

  @Test
  // TODO change once the package structure is clear
  public void ensureHexagonalArchitecture() {
    ArchRule ensureHexagonalArchitecture =
        onionArchitecture()
            .domainModels(BASE + ".domain.model..")
            .domainServices(
                BASE + ".domain.service..",
                BASE + ".domain.repository..",
                // TODO: decide if to move this, since it's accessed everywhere
                BASE + "." + ADAPTER_OUT + ".logging..")
            .applicationServices(BASE + ".domain.usecase..")
            //             spring controllers
            //            .adapter("input.controller", BASE + "." + ADAPTER_IN + ".controller..")
            //             interceptors, filters
            .adapter("input.web", BASE + "." + ADAPTER_IN + ".web..")
            //             outside systems
            .adapter("output.outsideservice1", BASE + "." + ADAPTER_OUT + ".outsideservice1..")
            .adapter("output.outsideservice2", BASE + "." + ADAPTER_OUT + ".outsideservice2..")
            .adapter("output.db", BASE + "." + ADAPTER_OUT + ".db..");

    ensureHexagonalArchitecture.check(importedClasses);
  }

  @Test
  public void ensureNotUsingJavaCoreExceptions() {
    JavaClasses classes = new ClassFileImporter().importPackages("com.jbs.pm");

    ArchCondition<JavaClass> creationOfJavaLangException =
        callCodeUnitWhere(
            target(
                    is(constructor())
                        .and(
                            is(
                                declaredIn(
                                    simpleNameContaining("Exception")
                                        .and(resideInAnyPackage("java.lang.."))))))
                .and(
                    not(
                        originOwner(
                            is(
                                simpleNameContaining("Exception")
                                    .and(resideInAnyPackage("java.lang..")))))));

    ArchCondition<JavaClass> creationOfJavaUtilException =
        callCodeUnitWhere(
            target(
                    is(constructor())
                        .and(
                            is(
                                declaredIn(
                                    simpleNameContaining("Exception")
                                        .and(resideInAnyPackage("java.util.."))))))
                .and(
                    not(
                        originOwner(
                            is(
                                simpleNameContaining("Exception")
                                    .and(resideInAnyPackage("java.util..")))))));

    ArchCondition<JavaClass> creationOfSpringException =
        callCodeUnitWhere(
            target(
                    is(constructor())
                        .and(
                            is(
                                declaredIn(
                                    simpleNameContaining("Exception")
                                        .and(resideInAnyPackage("org.springframework.."))))))
                .and(
                    not(
                        originOwner(
                            is(
                                simpleNameContaining("Exception")
                                    .and(resideInAnyPackage("org.springframework..")))))));

    noClasses()
        .that(
            simpleNameContaining("Service")
                .or(simpleNameContaining("Controller"))
                .or(simpleNameContaining("Repository"))
                .or(simpleNameContaining("Adapter"))
                .or(simpleNameContaining("Handler"))
                .or(resideInAnyPackage("..domain.usecase.."))
                .and(not(simpleNameContaining("Mock")))
                .and(not(simpleNameContaining("Test")))
                .and(not(simpleNameContaining("ControllerExceptionHandler"))))
        .should(
            creationOfJavaLangException
                .or(creationOfJavaUtilException)
                .or(creationOfSpringException))
        .because("we want to use custom exceptions")
        .check(classes);
  }
}
