package com.sonrisa.swarm.model.constraint.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.model.constraint.AfterDate;

/**
 * Validator for dates which returns false if date is not null and it is before
 * the validation date of the annotation 
 * 
 * @author Barnabas
 *
 */
public class AfterDateValidator implements ConstraintValidator<AfterDate, Date> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterDateValidator.class);

    /**
     * Date used for comparison
     */
    private Date compareTo;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(AfterDate annotation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(annotation.dateFormat());
        try {
            this.compareTo = dateFormat.parse(annotation.value());
        } catch (ParseException e) {
            LOGGER.warn("Failed to initialize date validator, using zero instead of value: {}", annotation.value(), e);
            this.compareTo = new Date(0L);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Date argument, ConstraintValidatorContext context) {
        // Null dates are valid
        if (argument == null) {
            return true;
        }

        return argument.after(compareTo);
    }
}
