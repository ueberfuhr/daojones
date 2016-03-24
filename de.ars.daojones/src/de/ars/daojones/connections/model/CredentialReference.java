package de.ars.daojones.connections.model;

/**
 * A credential containing a reference to another credential.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public class CredentialReference extends Credential implements ICredentialReference {

	private static final long serialVersionUID = 3962935762960696328L;

	private final String referenceId;

	/**
	 * Creates an instance.
	 * @param referenceId the referenceId.
	 */
	public CredentialReference(String referenceId) {
		super();
		this.referenceId = referenceId;
	}

	/**
	 * Creates an instance.
	 * @param credential the credential that should be referenced.
	 */
	public CredentialReference(ICredential credential) {
		this(credential.getId());
	}

	/**
	 * @see de.ars.daojones.connections.model.ICredentialReference#getReferenceId()
	 */
	public String getReferenceId() {
		return referenceId;
	}

}
