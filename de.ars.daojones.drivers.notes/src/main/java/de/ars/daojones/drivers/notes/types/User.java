package de.ars.daojones.drivers.notes.types;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A common name object that provides a user or a user group. Fields of this
 * type are stored as Names fields within the database. A user consists of
 * <ul>
 * <li>A name. (mandatory, but mutable)</li>
 * <li>Up to 4 organizational units. (optional)</li>
 * <li>An organization name. (optional)</li>
 * <li>A country name. (optional)</li>
 * </ul>
 * If the organization name is identical to a country code, both must be set. If
 * an organizational unit is set, both organization and country code must be
 * set.
 * 
 * @author Ralf Zahn, ARS Computer und Consulting GmbH, 2013
 * @since 2.0
 */
public class User extends Principal {

  private static final long serialVersionUID = 1L;

  private static final String CN = "CN";
  private static final String OU1 = "OU1";
  private static final String OU2 = "OU2";
  private static final String OU3 = "OU3";
  private static final String OU4 = "OU4";
  private static final String O = "O";
  private static final String C = "C";

  /**
   * Creates a name.
   * 
   * @param name
   *          the name
   * @throws NullPointerException
   *           if the name is <tt>null</tt>
   */
  public User( final String name ) {
    super();
    setName( name );
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the name
   * @throws NullPointerException
   *           if the name is <tt>null</tt>
   */
  public void setName( final String name ) {
    User.ensure( name != null );
    setProperty( User.CN, name );
  }

  /**
   * Returns the name.
   * 
   * @return the name
   */
  public String getName() {
    return getProperty( User.CN );
  }

  @Override
  public final PrincipalType getType() {
    return PrincipalType.USER;
  }

  /**
   * Sets the country.
   * 
   * @param country
   *          the country
   * @throws NullPointerException
   *           if the country is set to <tt>null</tt> and the user has an
   *           organizational unit
   */
  public void setCountry( final String country ) {
    User.ensure( null != country || !hasOrganizationalUnit() );
    setProperty( User.C, country );
  }

  /**
   * Returns the country.
   * 
   * @return the country
   */
  public String getCountry() {
    return getProperty( User.C );
  }

  /**
   * Sets the organization.
   * 
   * @param organization
   *          the organization
   * @throws NullPointerException
   *           if the country is set to <tt>null</tt> and the user has an
   *           organizational unit
   */
  public void setOrganization( final String organization ) {
    User.ensure( null != organization || !hasOrganizationalUnit() );
    setProperty( User.O, organization );
  }

  /**
   * Returns the organization.
   * 
   * @return the organization
   */
  public String getOrganization() {
    return getProperty( User.O );
  }

  /**
   * Returns the organizational units. If there isn't any organizational unit,
   * an empty array is returned.
   * 
   * @return the organizational units
   */
  public String[] getOrganizationalUnits() {
    final List<String> result = new LinkedList<String>();
    final String ou1 = getProperty( User.OU1 );
    final String ou2 = getProperty( User.OU2 );
    final String ou3 = getProperty( User.OU3 );
    final String ou4 = getProperty( User.OU4 );
    if ( null != ou1 ) {
      result.add( ou1 );
      if ( null != ou2 ) {
        result.add( ou2 );
        if ( null != ou3 ) {
          result.add( ou3 );
          if ( null != ou4 ) {
            result.add( ou4 );
          }
        }
      }
    }
    return result.toArray( new String[result.size()] );
  }

  /**
   * Sets the organization units. In this case, organization and country must be
   * set too.
   * 
   * @param country
   *          the country
   * @param organization
   *          the organization
   * @param organizationalUnit1
   *          the first organizational unit
   * @param organizationalUnit2
   *          the second organizational unit
   * @param organizationalUnit3
   *          the third organizational unit
   * @param organizationalUnit4
   *          the fourth organizational unit
   * @throws NullPointerException
   *           <ul>
   *           <li>If organization is <tt>null</tt>, but organizationalUnit1 is
   *           not <tt>null</tt>..</li>
   *           <li>If country is <tt>null</tt>, but organizationalUnit1 is not
   *           <tt>null</tt>.</li>
   *           <li>If organizationalUnit1 is <tt>null</tt>, but one of
   *           organizationalUnit2, organizationalUnit3 or organizationalUnit4
   *           is not <tt>null</tt>..</li>
   *           <li>If organizationalUnit2 is <tt>null</tt>, but one of
   *           organizationalUnit3 or organizationalUnit4 is not <tt>null</tt>..
   *           </li>
   *           <li>If organizationalUnit3 is <tt>null</tt>, but
   *           organizationalUnit4 is not <tt>null</tt>..</li>
   *           </ul>
   */
  public void setOrganizationalUnits( final String country, final String organization,
          final String organizationalUnit1, final String organizationalUnit2, final String organizationalUnit3,
          final String organizationalUnit4 ) {
    User.ensure( null == organizationalUnit4 || null != organizationalUnit3 );
    User.ensure( null == organizationalUnit3 || null != organizationalUnit2 );
    User.ensure( null == organizationalUnit2 || null != organizationalUnit1 );
    User.ensure( null == organizationalUnit1 || ( null != organization && null != country ) );
    setProperty( User.OU1, organizationalUnit1 );
    setProperty( User.OU2, organizationalUnit2 );
    setProperty( User.OU3, organizationalUnit3 );
    setProperty( User.OU4, organizationalUnit4 );
    setOrganization( organization );
    setCountry( country );
  }

  /**
   * Sets the organization units. In this case, organization and country must be
   * set too.
   * 
   * @see #setOrganizationalUnits(String, String, String, String, String,
   *      String)
   * @param country
   *          the country
   * @param organization
   *          the organization
   * @param organizationalUnit1
   *          the first organizational unit
   * @param organizationalUnit2
   *          the second organizational unit
   * @param organizationalUnit3
   *          the third organizational unit
   * @throws NullPointerException
   *           <ul>
   *           <li>If organization is <tt>null</tt>, but organizationalUnit1 is
   *           not <tt>null</tt>..</li>
   *           <li>If country is <tt>null</tt>, but organizationalUnit1 is not
   *           <tt>null</tt>.</li>
   *           <li>If organizationalUnit1 is <tt>null</tt>, but one of
   *           organizationalUnit2 or organizationalUnit3 is not <tt>null</tt>..
   *           </li>
   *           <li>If organizationalUnit2 is <tt>null</tt>, but
   *           organizationalUnit3 is not <tt>null</tt>..</li>
   *           </ul>
   */
  public void setOrganizationalUnits( final String country, final String organization,
          final String organizationalUnit1, final String organizationalUnit2, final String organizationalUnit3 ) {
    setOrganizationalUnits( country, organization, organizationalUnit1, organizationalUnit2, organizationalUnit3, null );
  }

  /**
   * Sets the organization units. In this case, organization and country must be
   * set too.
   * 
   * @see #setOrganizationalUnits(String, String, String, String, String,
   *      String)
   * @param country
   *          the country
   * @param organization
   *          the organization
   * @param organizationalUnit1
   *          the first organizational unit
   * @param organizationalUnit2
   *          the second organizational unit
   * @throws NullPointerException
   *           <ul>
   *           <li>If organization is <tt>null</tt>, but organizationalUnit1 is
   *           not <tt>null</tt>..</li>
   *           <li>If country is <tt>null</tt>, but organizationalUnit1 is not
   *           <tt>null</tt>.</li>
   *           <li>If organizationalUnit1 is <tt>null</tt>, but
   *           organizationalUnit2 is not <tt>null</tt>..</li>
   *           </ul>
   */
  public void setOrganizationalUnits( final String country, final String organization,
          final String organizationalUnit1, final String organizationalUnit2 ) {
    setOrganizationalUnits( country, organization, organizationalUnit1, organizationalUnit2, null );
  }

  /**
   * Sets the organization units. In this case, organization and country must be
   * set too.
   * 
   * @see #setOrganizationalUnits(String, String, String, String, String,
   *      String)
   * @param country
   *          the country
   * @param organization
   *          the organization
   * @param organizationalUnit
   *          the first organizational unit
   * @throws NullPointerException
   *           <ul>
   *           <li>If organization is <tt>null</tt>, but organizationalUnit is
   *           not <tt>null</tt>..</li>
   *           <li>If country is <tt>null</tt>, but organizationalUnit is not
   *           <tt>null</tt>.</li>
   *           </ul>
   */
  public void setOrganizationalUnit( final String country, final String organization, final String organizationalUnit ) {
    setOrganizationalUnits( country, organization, organizationalUnit, null );
  }

  /**
   * Removes all organizational units.
   */
  public void removeOrganizationalUnits() {
    setProperty( User.OU1, null );
    setProperty( User.OU2, null );
    setProperty( User.OU3, null );
    setProperty( User.OU4, null );
  }

  /**
   * Checks the organizational units.
   * 
   * @return <tt>true</tt>, if the first organizational unit is not set to
   *         <tt>null</tt>
   */
  public boolean hasOrganizationalUnit() {
    return null != getProperty( User.OU1 );
  }

  /*
   * Shortcut to throw NPEs
   */
  private static void ensure( final boolean condition ) {
    if ( !condition ) {
      throw new NullPointerException();
    }
  }

  @Override
  public final String getValue() {
    return getName();
  }

  @Override
  public boolean isSimple() {
    return null == getProperty( User.C ) && null == getProperty( User.O );
  }

  /**
   * Generates the string representation of the user.
   * 
   * @see #valueOf(String)
   */
  @Override
  public final String toString() {
    return isSimple() ? getName() : super.toString();
  }

  /**
   * Parses the string representation of a user. This can be
   * <ul>
   * <li>a simple name (e.g. <tt>&quot;John Doe&quot;</tt>),</li>
   * <li>a hierarchical name (e.g. <tt>&quot;CN=John Doe/O=ACME&quot;</tt>) or</li>
   * <li>an abbreviated name (e.g. <tt>&quot;John Doe/ACME&quot;</tt>).</li>
   * </ul>
   * 
   * The following abbreviated names are acceptable:
   * <ul>
   * <li>common name/organization</li>
   * <li>common name/organization/country</li>
   * <li>common name/organizational unit/organization/country (up to four
   * organizational units)</li>
   * </ul>
   * 
   * @param value
   *          the string representation
   * @return the user
   * @see #toString()
   */
  public static User valueOf( final String value ) {
    if ( null == value || value.length() == 0 ) {
      return null;
    } else {
      final String[] parts = value.split( "\\/" );
      final User user = new User( "" );
      if ( !Pattern.matches( "[A-Z0-9]+\\=.*[\\/[A-Z0-9]+\\=.*]*", value ) ) {
        user.setName( parts[0] );
        if ( parts.length < 3 ) {
          if ( parts.length > 1 ) {
            user.setOrganization( parts[1] );
          }
        } else {
          user.setOrganizationalUnits( // 
                  parts[parts.length - 1], // country
                  parts[parts.length - 2], // organization
                  parts.length > 3 ? parts[1] : null, // ou1 
                  parts.length > 4 ? parts[2] : null, // ou2
                  parts.length > 5 ? parts[3] : null, // ou3 
                  parts.length > 6 ? parts[4] : null // ou4 
          );
        }
        return user;
      } else {
        // hierarchical name
        for ( final String part : parts ) {
          final String[] pp = part.trim().split( "\\=" );
          user.setProperty( pp[0].trim().toUpperCase(), pp[1].trim() );
        }
      }
      user.setName( user.getName().length() == 0 ? null : user.getName() );
      return user;
    }
  }
}
