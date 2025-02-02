package guru.qa.niffler.helpers.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.CategoryExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({
        CategoryExtension.class
})
public @interface User {

    String username();

    Category[] categories() default {};

    CreatingSpend[] spendings() default {};
}
