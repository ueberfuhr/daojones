package de.ars.daojones.eclipse.jdt.markers.solutions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEdit;

/**
 * A class holding utility methods for Solutions.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
public abstract class SolutionUtilities {

	/**
	 * Tries to add an import declaration and returns true, if the class is imported.
	 * @param env
	 * @param rewrite
	 * @param qualifiedName
	 * @return false if the import could not be done because there is another import with the same simple name.
	 * @throws JavaModelException
	 * @throws IllegalArgumentException
	 * @throws CoreException
	 */
	public static boolean tryToImport(final ISolutionExecutionEnvironment env, ASTRewrite rewrite, String qualifiedName) throws JavaModelException, IllegalArgumentException, CoreException {
		if(qualifiedName.indexOf(".")<0) return true;
		final CompilationUnit unit = env.getASTRoot();
		final Set<String> imports = new HashSet<String>();
		final Set<String> importsSimpleNames = new HashSet<String>();
		final List<ImportDeclaration> decs = new LinkedList<ImportDeclaration>(); 
		final PackageDeclaration[] packageDeclarations = new PackageDeclaration[1];
		unit.accept(new ASTVisitor() {
			@Override
			public boolean visit(PackageDeclaration node) {
				packageDeclarations[0] = node;
				return super.visit(node);
			}

			@Override
			public boolean visit(ImportDeclaration node) {
				Name name = node.getName();
				imports.add(name.getFullyQualifiedName());
				if(name.isQualifiedName()) {
					name = ((QualifiedName)name).getName();
				}
				importsSimpleNames.add(name.getFullyQualifiedName());
				decs.add(node);
				return false;
			}
			
		});
		final String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
		final String className = qualifiedName.substring(qualifiedName.lastIndexOf(".")+1);
		if(!imports.contains(qualifiedName) && !imports.contains(packageName + ".*")) {
			if(importsSimpleNames.contains(className)) return false;
			final ImportDeclaration newDec = unit.getAST().newImportDeclaration();
			newDec.setName(unit.getAST().newName(qualifiedName.split("\\.")));
			//unit.imports().add(newDec);
			if(null == rewrite) {
				rewrite = ASTRewrite.create(unit.getAST());
				rewrite.getListRewrite(unit, CompilationUnit.IMPORTS_PROPERTY).insertLast(newDec, null);
				final TextEdit edits = env.rewrite(rewrite);
				env.apply(edits, null);
			} else {
				rewrite.getListRewrite(unit, CompilationUnit.IMPORTS_PROPERTY).insertLast(newDec, null);
			}
		}
		return true;
	}
	
	/**
	 * Tries to import the name to the compilation unit and (if successful) returns the
	 * simple name that can be used in the compilation unit.
	 * @param env
	 * @param rewrite
	 * @param name
	 * @return the simple name that can be used in the compilation unit or the qualified name, if the import was not successful
	 * @throws JavaModelException
	 * @throws IllegalArgumentException
	 * @throws CoreException
	 */
	public static Name tryToImport(final ISolutionExecutionEnvironment env, final ASTRewrite rewrite, final Name name) throws JavaModelException, IllegalArgumentException, CoreException {
		if(name.isQualifiedName()) {
			if(tryToImport(env, rewrite, name.getFullyQualifiedName())) {
				return env.getASTRoot().getAST().newSimpleName(((QualifiedName)name).getName().getFullyQualifiedName());
			} else {
				return name;
			}
		} else {
			return name;
		}
	}
	
	/**
	 * Creates a qualified type.
	 * @param ast
	 * @param qualifiedName
	 * @return the qualified type
	 */
	public 	static Type newQualifiedType(AST ast, String qualifiedName) {
		if(null == qualifiedName) return null;
		final int lastDot = qualifiedName.lastIndexOf('.');
		if(lastDot>=0) {
			return ast.newQualifiedType(newQualifiedType(ast, qualifiedName.substring(0, lastDot-1)), ast.newSimpleName(qualifiedName.substring(lastDot)));
		} else {
			return ast.newSimpleType(ast.newSimpleName(qualifiedName));
		}
	}

	
}
