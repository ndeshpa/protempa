package org.protempa.ksb.protege;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.Project;

/**
 * Access to Protege knowledge bases in local files or at URIs.
 * 
 * @author Andrew Post
 * 
 */
final class LocalConnectionManager extends ConnectionManager {

    /**
     * Creates a connection manager for specified knowledge base. For accessing
     * knowledge bases on Protege servers, see {@link RemoteConnectionManager}.
     *
     * @param filePathOrURI
     *            a file path or URI to the Protege project. This is used as
     *            the knowledge base name. Cannot be <code>null</code>.
     * @see #getProjectIdentifier()
     */
    LocalConnectionManager(String filePathOrURI) {
        super(filePathOrURI);
    }

    /**
     * Opens the project specified by the file path or URI given in
     * the constructor.
     *
     * @return a Protege {@link Project}.
     * @see ConnectionManager#initProject()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Project initProject() {
            Collection errors = new ArrayList();
        String projectFilePathOrURI = getProjectIdentifier();
        Util.logger().fine("Trying to load Protege project "
            + projectFilePathOrURI);
        Project project = new Project(projectFilePathOrURI, errors);
        if (errors.size() == 0) {
            Util.logger().fine("Protege project "
                + projectFilePathOrURI + " is opened.");
            return project;
        } else {
            throw new IllegalStateException(
                    "Error(s) loading knowledge base "
                    + projectFilePathOrURI + ": " + errors);
        }
    }

}
