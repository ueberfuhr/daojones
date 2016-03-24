/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ars.daojones.eclipse.jdt.internal.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

/*
 * This class is copied from org.eclipse.jdt.ui, Package org.eclipse.jdt.internal.corext.codemanipulation.
 */
/**
 * Utilities for code generation based on ast rewrite.
 * 
 * 
 * @since 0.9.0
 */
public final class StubUtility {
	
	public static IMethodBinding[] getUnimplementedMethods(ITypeBinding typeBinding) {
		return getUnimplementedMethods(typeBinding, false);
	}

	private static IMethodBinding findMethodBinding(IMethodBinding method, List allMethods) {
		for (int i= 0; i < allMethods.size(); i++) {
			IMethodBinding curr= (IMethodBinding) allMethods.get(i);
			if (Bindings.isSubsignature(method, curr)) {
				return curr;
			}
		}
		return null;
	}

	private static void findUnimplementedInterfaceMethods(ITypeBinding typeBinding, HashSet visited, ArrayList allMethods, IPackageBinding currPack, ArrayList toImplement) {
		if (visited.add(typeBinding)) {
			IMethodBinding[] typeMethods= typeBinding.getDeclaredMethods();
			for (int i= 0; i < typeMethods.length; i++) {
				IMethodBinding curr= typeMethods[i];
				IMethodBinding impl= findMethodBinding(curr, allMethods);
				if (impl == null || !Bindings.isVisibleInHierarchy(impl, currPack)) {
					if (impl != null)
						allMethods.remove(impl);
					toImplement.add(curr);
					allMethods.add(curr);
				}
			}
			ITypeBinding[] superInterfaces= typeBinding.getInterfaces();
			for (int i= 0; i < superInterfaces.length; i++)
				findUnimplementedInterfaceMethods(superInterfaces[i], visited, allMethods, currPack, toImplement);
		}
	}

	public static IMethodBinding[] getUnimplementedMethods(ITypeBinding typeBinding, boolean implementAbstractsOfInput) {
		ArrayList allMethods= new ArrayList();
		ArrayList toImplement= new ArrayList();

		IMethodBinding[] typeMethods= typeBinding.getDeclaredMethods();
		for (int i= 0; i < typeMethods.length; i++) {
			IMethodBinding curr= typeMethods[i];
			int modifiers= curr.getModifiers();
			if (!curr.isConstructor() && !Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers)) {
				allMethods.add(curr);
			}
		}

		ITypeBinding superClass= typeBinding.getSuperclass();
		while (superClass != null) {
			typeMethods= superClass.getDeclaredMethods();
			for (int i= 0; i < typeMethods.length; i++) {
				IMethodBinding curr= typeMethods[i];
				int modifiers= curr.getModifiers();
				if (!curr.isConstructor() && !Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers)) {
					if (findMethodBinding(curr, allMethods) == null) {
						allMethods.add(curr);
					}
				}
			}
			superClass= superClass.getSuperclass();
		}

		for (int i= 0; i < allMethods.size(); i++) {
			IMethodBinding curr= (IMethodBinding) allMethods.get(i);
			int modifiers= curr.getModifiers();
			if ((Modifier.isAbstract(modifiers) || curr.getDeclaringClass().isInterface()) && (implementAbstractsOfInput || typeBinding != curr.getDeclaringClass())) {
				// implement all abstract methods
				toImplement.add(curr);
			}
		}

		HashSet visited= new HashSet();
		ITypeBinding curr= typeBinding;
		while (curr != null) {
			ITypeBinding[] superInterfaces= curr.getInterfaces();
			for (int i= 0; i < superInterfaces.length; i++) {
				findUnimplementedInterfaceMethods(superInterfaces[i], visited, allMethods, typeBinding.getPackage(), toImplement);
			}
			curr= curr.getSuperclass();
		}

		return (IMethodBinding[]) toImplement.toArray(new IMethodBinding[toImplement.size()]);
	}


	/**
	 * Evaluates the insertion position of a new node.
	 * 
	 * @param listRewrite The list rewriter to which the new node will be added
	 * @param sibling The Java element before which the new element should be added.
	 * @return the AST node of the list to insert before or null to insert as last.
	 * @throws JavaModelException thrown if accessing the Java element failed
	 */
	public static ASTNode getNodeToInsertBefore(ListRewrite listRewrite, IJavaElement sibling) throws JavaModelException {
		if (sibling instanceof IMember) {
			ISourceRange sourceRange= ((IMember) sibling).getSourceRange();
			if (sourceRange == null) {
				return null;
			}
			int insertPos= sourceRange.getOffset();

			List members= listRewrite.getOriginalList();
			for (int i= 0; i < members.size(); i++) {
				ASTNode curr= (ASTNode) members.get(i);
				if (curr.getStartPosition() >= insertPos) {
					return curr;
				}
			}
		}
		return null;
	}
	
	/**
	 * Creates a new stub utility.
	 */
	private StubUtility() {
		// Not for instantiation
	}
}
