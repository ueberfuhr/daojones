package de.ars.projects.internal.checkstyle.checks.coding;

import java.util.Set;

import com.google.common.collect.Sets;
import com.puppycrawl.tools.checkstyle.api.AnnotationUtility;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.coding.AbstractIllegalCheck;

/**
 * Throwing java.lang.Error or java.lang.RuntimeException is almost never
 * acceptable.
 * 
 * @author Oliver Burn
 */
public final class ExtendedIllegalThrowsCheck extends AbstractIllegalCheck {

  /** Default ignored method names. */
  private static final String[] DEFAULT_IGNORED_METHOD_NAMES = { "finalize", };

  /** methods which should be ignored. */
  private final Set<String> mIgnoredMethodNames = Sets.newHashSet();

  private boolean excludedInherited = true;

  /** Creates new instance of the check. */
  public ExtendedIllegalThrowsCheck() {
    super(new String[] { "Error", "RuntimeException", "Throwable", "java.lang.Error",
        "java.lang.RuntimeException", "java.lang.Throwable", });
    setIgnoredMethodNames(ExtendedIllegalThrowsCheck.DEFAULT_IGNORED_METHOD_NAMES);
  }

  public boolean isExcludedInherited() {
    return excludedInherited;
  }

  public void setExcludedInherited(final boolean excludedInherited) {
    this.excludedInherited = excludedInherited;
  }

  private boolean isInherited(final DetailAST methodAST) {
    return AnnotationUtility.containsAnnotation(methodAST, Override.class.getSimpleName())
            || AnnotationUtility.containsAnnotation(methodAST, Override.class.getName());
  }

  @Override
  public int[] getDefaultTokens() {
    return new int[] { TokenTypes.LITERAL_THROWS };
  }

  @Override
  public int[] getRequiredTokens() {
    return getDefaultTokens();
  }

  @Override
  public void visitToken(final DetailAST aDetailAST) {
    DetailAST token = aDetailAST.getFirstChild();
    // Check if the method with the given name should be ignored.
    if (!(shouldIgnoreMethod(aDetailAST.getParent()))) {
      while (token != null) {
        if (token.getType() != TokenTypes.COMMA) {
          final FullIdent ident = FullIdent.createFullIdent(token);
          if (isIllegalClassName(ident.getText())) {
            log(token, "illegal.throw", ident.getText());
          }
        }
        token = token.getNextSibling();
      }
    }
  }

  private boolean shouldIgnoreMethod(final DetailAST methodAST) {
    return shouldIgnoreMethodName(methodAST.findFirstToken(TokenTypes.IDENT).getText())
            || isExcludedInherited() && isInherited(methodAST);
  }

  private boolean shouldIgnoreMethodName(final String aName) {
    return mIgnoredMethodNames.contains(aName);
  }

  /**
   * Set the list of ignore method names.
   * 
   * @param aMethodNames
   *          array of ignored method names
   */
  public void setIgnoredMethodNames(final String[] aMethodNames) {
    mIgnoredMethodNames.clear();
    for (final String element : aMethodNames) {
      mIgnoredMethodNames.add(element);
    }
  }
}