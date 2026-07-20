package org.dspace.app.rest.validation.impl;

import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.app.rest.validation.WorkflowProcessValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidatorFactory;
import java.util.regex.Pattern;

public class ValidWorkFlowProcessCheck  implements ConstraintValidator<WorkflowProcessValid, WorkFlowProcessRest> {
    private ValidatorFactory validatorFactory;
    @Override
    public boolean isValid(WorkFlowProcessRest workFlowProcessRest, ConstraintValidatorContext constraintValidatorContext) {
        if(workFlowProcessRest.getSubject().trim().length() ==0){
            System.out.println("workFlowProcessRest.getSubject().trim().length()::"
            +workFlowProcessRest.getSubject().trim().length());
            constraintValidatorContext.buildConstraintViolationWithTemplate("subject not found")
                    .addConstraintViolation();
            return  false;
        }
        Pattern emailpattern = Pattern.compile("^(.+)@(.+)$");
        if(workFlowProcessRest.getWorkflowProcessSenderDiaryRest() == null  || !emailpattern.matcher(workFlowProcessRest.getWorkflowProcessSenderDiaryRest().getEmail()).matches()){
            constraintValidatorContext.buildConstraintViolationWithTemplate("email not found in sender list")
                    .addConstraintViolation();
            return  false;
        }
        return  true;
    }
}
