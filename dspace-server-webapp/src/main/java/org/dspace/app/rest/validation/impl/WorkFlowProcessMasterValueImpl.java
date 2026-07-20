package org.dspace.app.rest.validation.impl;

import org.dspace.app.rest.model.WorkFlowProcessMasterValueRest;
import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.app.rest.validation.WorkflowProcessMasterValueValid;
import org.dspace.app.rest.validation.WorkflowProcessValid;
import org.dspace.content.WorkFlowProcessMasterValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidatorFactory;
import java.util.regex.Pattern;

public class WorkFlowProcessMasterValueImpl implements ConstraintValidator<WorkflowProcessMasterValueValid, WorkFlowProcessMasterValueRest> {
    private ValidatorFactory validatorFactory;
    @Override
    public boolean isValid(WorkFlowProcessMasterValueRest workFlowProcessMasterValueRest, ConstraintValidatorContext constraintValidatorContext) {
        if(workFlowProcessMasterValueRest.getUuid() == null || workFlowProcessMasterValueRest.getUuid().trim().length() ==0){
            System.out.println("i am validating the sevice ...");
            constraintValidatorContext.buildConstraintViolationWithTemplate("Master value not present")
                    .addConstraintViolation();
            return  false;
        }
        return  true;
    }
}
