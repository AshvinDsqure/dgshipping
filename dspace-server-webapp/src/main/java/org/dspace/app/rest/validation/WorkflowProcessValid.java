package org.dspace.app.rest.validation;

import org.dspace.app.rest.validation.impl.ValidWorkFlowProcessCheck;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ TYPE_USE })
@Documented
@Constraint(validatedBy = {ValidWorkFlowProcessCheck.class })
public @interface  WorkflowProcessValid {
    String message() default "some Data is missing in request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
