/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.security;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.UUID;

import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.EPersonService;
import org.dspace.services.RequestService;
import org.dspace.services.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Administrators are always allowed to perform any action on any DSpace object. This plugin will check if
 * the authenticated EPerson is an administrator of the provided target DSpace Object. If that is the case,
 * the authenticated EPerson is allowed to perform the requested action.
 */
@Component
public class AdminRestPermissionEvaluatorPlugin extends RestObjectPermissionEvaluatorPlugin {

    private static final Logger log = LoggerFactory.getLogger(RestObjectPermissionEvaluatorPlugin.class);

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private EPersonService ePersonService;

    @Override
    public boolean hasDSpacePermission(Authentication authentication, Serializable targetId, String targetType,
                                       DSpaceRestPermission permission) {

        //We do not check the "permission" object here because administrators are allowed to do everything

        // If Spring Security Authentication has ADMIN authority, grant permission immediately.
        // This works even when the DSpace Context doesn't have the user set, because the
        // authorities were populated by StatelessAuthenticationFilter during authentication.
        if (authentication != null && authentication.getAuthorities() != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ADMIN".equals(authority.getAuthority())) {
                    return true;
                }
            }
        }

        Request request = requestService.getCurrentRequest();
        Context context = ContextUtil.obtainContext(request.getHttpServletRequest());
        EPerson ePerson = null;

        try {
            ePerson = context.getCurrentUser();
            if (ePerson == null) {
                String currentUserId = requestService.getCurrentUserId();
                if (currentUserId != null && !"null".equals(currentUserId)) {
                    try {
                        ePerson = ePersonService.find(context, UUID.fromString(currentUserId));
                        if (ePerson != null) {
                            context.setCurrentUser(ePerson);
                        }
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid EPerson ID in request service: {}", currentUserId);
                    }
                }
            }
            if (ePerson != null) {

                //Check if user is a repository admin
                if (authorizeService.isAdmin(context, ePerson)) {
                    return true;
                }

                //We don't check the DSO Admin level here as this is action specific.
                //For example see org.dspace.authorize.AuthorizeConfiguration.canCollectionAdminPerformItemDeletion

            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
