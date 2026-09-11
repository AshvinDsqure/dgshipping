/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bulkupload.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.dspace.authorize.AuthorizeException;
import org.dspace.bulkupload.BulkUploadProcess;
import org.dspace.core.Context;

public interface BulkUploadValidationService {

    BulkUploadProcess validate(Context context, InputStream csvStream, UUID epersonId)
        throws IOException, SQLException, AuthorizeException;

    List<BulkUploadRuleEngine.ColumnValidationResult> validateColumns(InputStream csvStream) throws IOException;

    boolean isValid();

}
