/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.ResourcePolicyRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.service.ResourcePolicyService;
import org.dspace.content.DSpaceObject;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Converter to translate ResourcePolicy into human readable value
 * configuration.
 *
 * @author Luigi Andrea Pascarelli (luigiandrea.pascarelli at 4science.it)
 */
@Component
public class ResourcePolicyConverter implements DSpaceConverter<ResourcePolicy, ResourcePolicyRest> {

    private static final Logger log = LoggerFactory.getLogger(ResourcePolicyConverter.class);

    @Autowired
    ResourcePolicyService resourcePolicyService;

    // Must be loaded @Lazy, as ConverterService autowires all DSpaceConverter components
    @Lazy
    @Autowired
    ConverterService converterService;

    @Override
    public ResourcePolicyRest convert(ResourcePolicy obj, Projection projection) {

        ResourcePolicyRest model = new ResourcePolicyRest();
        model.setProjection(projection);

        model.setId(obj.getID());

        model.setName(obj.getRpName());
        model.setDescription(obj.getRpDescription());
        model.setPolicyType(obj.getRpType());

        model.setAction(resourcePolicyService.getActionText(obj));

        model.setStartDate(obj.getStartDate());
        model.setEndDate(obj.getEndDate());

        if (obj.getGroup() != null) {
            model.setGroup(converterService.toRest(obj.getGroup(), projection));
        }

        if (obj.getEPerson() != null) {
            model.setEperson(converterService.toRest(obj.getEPerson(), projection));
        }
        if (obj.getdSpaceObject() != null) {
            // The dSpaceObject association is FetchType.LAZY and typed as the abstract DSpaceObject.
            // Hibernate therefore returns a proxy whose getClass() is a DSpaceObject proxy subclass,
            // not the concrete entity class (e.g. Community). ConverterService.requireConverter would
            // then fail with IllegalArgumentException ("No converter found...") -> HTTP 400.
            // Unproxy to obtain the concrete entity so the correct converter is resolved.
            // If the underlying data is invalid/orphaned the unproxy may fail; in that case skip
            // the resource conversion (the field is @JsonIgnore and not serialized in the response).
            try {
                DSpaceObject dso = (DSpaceObject) Hibernate.unproxy(obj.getdSpaceObject());
                model.setResource(converterService.toRest(dso, projection));
            } catch (Exception e) {
                log.warn("Unable to convert ResourcePolicy {} dSpaceObject to REST: {}",
                         obj.getID(), e.getMessage());
            }
        }
        return model;
    }

    @Override
    public Class<ResourcePolicy> getModelClass() {
        return ResourcePolicy.class;
    }

}
