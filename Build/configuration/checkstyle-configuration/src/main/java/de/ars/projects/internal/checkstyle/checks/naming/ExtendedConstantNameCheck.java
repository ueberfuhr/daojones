package de.ars.projects.internal.checkstyle.checks.naming;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.ScopeUtils;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.naming.AbstractAccessControlNameCheck;

public class ExtendedConstantNameCheck extends AbstractAccessControlNameCheck {

  /*
   * Default values
   */
  private static final Set<String> defaultImmutableTypes = new HashSet<String>();
  static {
    ExtendedConstantNameCheck.defaultImmutableTypes.add(String.class.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Boolean.class.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Byte.class.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Short.class.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Integer.class.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Long.class.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Float.class.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Double.class.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Character.class.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Boolean.TYPE.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Byte.TYPE.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Short.TYPE.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Integer.TYPE.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Long.TYPE.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Float.TYPE.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Double.TYPE.getName());
    ExtendedConstantNameCheck.defaultImmutableTypes.add(Character.TYPE.getName());
  }
  private static final boolean defaultImmutableValue = true;

  /*
   * Instance variables
   */
  private final Set<String> immutableTypes = new HashSet<String>(
          ExtendedConstantNameCheck.defaultImmutableTypes);
  private boolean arrayImmutable = false;
  private boolean excludeMutableTypes = true;

  // set of the imports
  private final Map<String, String> qualifiedTypeBySimpleName = new HashMap<String, String>();
  private final Set<String> importedPackages = new HashSet<String>();
  private String currentPackage;

  /**
   * Creates a new <code>ConstantNameCheck</code> instance.
   */
  public ExtendedConstantNameCheck() {
    super("^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$");
  }

  /*
   * Properties
   */
  public boolean isArrayImmutable() {
    return arrayImmutable;
  }

  public void setArrayImmutable(final boolean arrayImmutable) {
    this.arrayImmutable = arrayImmutable;
  }

  public boolean isExcludeMutableTypes() {
    return excludeMutableTypes;
  }

  public void setExcludeMutableTypes(final boolean excludeMutableTypes) {
    this.excludeMutableTypes = excludeMutableTypes;
  }

  // for checkstyle
  public void setImmutableTypes(final String[] immutableTypes) {
    setImmutableTypesInternal(new HashSet<String>(Arrays.asList(immutableTypes)));
  }

  private void setImmutableTypesInternal(final Set<String> immutableTypes) {
    this.immutableTypes.clear();
    this.immutableTypes.addAll(ExtendedConstantNameCheck.defaultImmutableTypes);
    this.immutableTypes.addAll(immutableTypes);
  }

  /*
   * Collect imports
   */

  @Override
  public void beginTree(final DetailAST aRootAST) {
    qualifiedTypeBySimpleName.clear();
    importedPackages.clear();
    currentPackage = null;
  }

  @Override
  public void finishTree(final DetailAST aRootAST) {
    qualifiedTypeBySimpleName.clear();
    importedPackages.clear();
    currentPackage = null;
  }

  @Override
  public int[] getDefaultTokens() {
    return new int[] { //
    TokenTypes.IMPORT, // imports
        TokenTypes.PACKAGE_DEF, // current package to resolve types without import
        TokenTypes.VARIABLE_DEF, //
    };
  }

  private void processImport(final DetailAST aAST) {
    final FullIdent name = FullIdent.createFullIdentBelow(aAST);
    if ((name != null) && !name.getText().endsWith(".*")) {
      final String qualifiedName = name.getText();
      if (qualifiedName.endsWith(".*")) {
        importedPackages.add(qualifiedName.substring(0, qualifiedName.length() - 2));
      } else {
        qualifiedTypeBySimpleName.put(qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1),
                qualifiedName);

      }
    }
  }

  private void processPackageDef(final DetailAST aAST) {
    currentPackage = FullIdent.createFullIdent(aAST.getFirstChild().getNextSibling()).getText();
  }

  @Override
  public void visitToken(final DetailAST aAST) {
    switch (aAST.getType()) {
    case TokenTypes.IMPORT:
      processImport(aAST);
      break;
    case TokenTypes.PACKAGE_DEF:
      processPackageDef(aAST);
      break;
    case TokenTypes.VARIABLE_DEF:
      super.visitToken(aAST);
      break;
    }
  }

  /*
   * Immutables
   */

  private Collection<String> findQualifiedNames(final String typeName) {
    final Collection<String> result = new HashSet<String>();
    // type could be from same package, default package, from imported package or is directly imported
    // a '.' inside the type does not indicate a fully-qualified name (inner classes!)
    result.add(typeName); // type is in default package or is already a full-qualified name
    if (null != currentPackage) { // type is in the same package
      result.add(currentPackage.concat(".").concat(typeName));
    }
    // Inner classes!
    final int idx = typeName.indexOf('.');
    final String key = typeName.substring(0, idx < 0 ? typeName.length() : idx);
    final String qualifiedName = qualifiedTypeBySimpleName.get(key);
    if (null != qualifiedName) {
      result.add(qualifiedName + (idx < 0 ? "" : typeName.substring(idx + 1)));
    }
    result.add("java.lang.".concat(typeName));
    return result;
  }

  // Check immutable
  private boolean isImmutableIdent(final DetailAST node) {
    if (null != node) {
      final String typeName = FullIdent.createFullIdent(node).getText();
      final Collection<String> qualifiedNames = findQualifiedNames(typeName);
      qualifiedNames.retainAll(immutableTypes);
      return !qualifiedNames.isEmpty();
    } else {
      return ExtendedConstantNameCheck.defaultImmutableValue;
    }
  }

  private boolean isImmutableType(final DetailAST node) {
    if (null != node) {
      // node can contain IDENT or ARRAY_DECLARATOR
      if (node.getFirstChild().getType() == TokenTypes.ARRAY_DECLARATOR) {
        // is array
        return isArrayImmutable() && isImmutableType(node.getFirstChild());
      } else {
        return isImmutableIdent(node.findFirstToken(TokenTypes.IDENT));
      }
    }
    return ExtendedConstantNameCheck.defaultImmutableValue;
  }

  @Override
  protected final boolean mustCheckName(final DetailAST aAST) {
    boolean retVal = false;

    final DetailAST modifiersAST = aAST.findFirstToken(TokenTypes.MODIFIERS);
    final boolean isStatic = (modifiersAST != null)
            && modifiersAST.branchContains(TokenTypes.LITERAL_STATIC);
    final boolean isFinal = (modifiersAST != null) && modifiersAST.branchContains(TokenTypes.FINAL);
    // is immutable
    final boolean isImmutable = !isExcludeMutableTypes()
            || isImmutableType(aAST.findFirstToken(TokenTypes.TYPE));

    if ((isStatic && isFinal && isImmutable && shouldCheckInScope(modifiersAST))
            || ScopeUtils.inInterfaceOrAnnotationBlock(aAST)) {
      // Handle the serialVersionUID and serialPersistentFields  constants
      // which are used for Serialization. Cannot enforce rules on it. :-)
      final DetailAST nameAST = aAST.findFirstToken(TokenTypes.IDENT);
      if ((nameAST != null) && !("serialVersionUID".equals(nameAST.getText()))
              && !("serialPersistentFields".equals(nameAST.getText()))) {
        retVal = true;
      }
    }

    return retVal;
  }
}
