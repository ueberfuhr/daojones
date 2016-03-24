package de.ars.daojones.eclipse.jdt.beans;

import org.eclipse.jdt.core.dom.*;

/**
 * A visitor having one {@link #visitNode(ASTNode)} method for general
 * operations.
 * 
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 */
class GenericVisitor extends ASTVisitor {

	public GenericVisitor() {
		super();
	}

	/**
	 * @param visitJavadocTags
	 *            <code>true</code> if doc comment tags are to be visited by
	 *            default, and <code>false</code> otherwise
	 * @see Javadoc#tags()
	 * @see #visit(Javadoc)
	 * @since 3.0
	 */
	public GenericVisitor(boolean visitJavadocTags) {
		super(visitJavadocTags);
	}

	// ---- Hooks for subclasses
	// -------------------------------------------------

	protected boolean visitNode(ASTNode node) {
		return true;
	}

	protected void endVisitNode(ASTNode node) {
		// do nothing
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ArrayAccess node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ArrayCreation node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ArrayType node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(AssertStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(Assignment node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(Block node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(BreakStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(CastExpression node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(CatchClause node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(CompilationUnit node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ContinueStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(EmptyStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(FieldAccess node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(IfStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(InfixExpression node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(Initializer node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(Javadoc node) {
		if (super.visit(node))
			return visitNode(node);
		else
			return false;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(NullLiteral node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(NumberLiteral node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(PostfixExpression node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(PrefixExpression node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(PrimitiveType node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(QualifiedName node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ReturnStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(SimpleName node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(SimpleType node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(StringLiteral node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(SwitchCase node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(SwitchStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ThisExpression node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(ThrowStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(TryStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(TypeLiteral node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		return visitNode(node);
	}

	@Override
	public boolean visit(WhileStatement node) {
		return visitNode(node);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * AnnotationTypeDeclaration)
	 */
	public boolean visit(AnnotationTypeDeclaration node) {
		return visitNode(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * AnnotationTypeMemberDeclaration)
	 */
	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		return visitNode(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * BlockComment)
	 */
	@Override
	public boolean visit(BlockComment node) {
		return visitNode(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * EnhancedForStatement)
	 */
	@Override
	public boolean visit(EnhancedForStatement node) {
		return visitNode(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * EnumConstantDeclaration)
	 */
	@Override
	public boolean visit(EnumConstantDeclaration node) {
		return visitNode(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * EnumDeclaration)
	 */
	@Override
	public boolean visit(EnumDeclaration node) {
		return visitNode(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * LineComment)
	 */
	@Override
	public boolean visit(LineComment node) {
		return visitNode(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * MarkerAnnotation)
	 */
	@Override
	public boolean visit(MarkerAnnotation node) {
		return visitNode(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MemberRef
	 * )
	 */
	@Override
	public boolean visit(MemberRef node) {
		return visitNode(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * MemberValuePair)
	 */
	@Override
	public boolean visit(MemberValuePair node) {
		return visitNode(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodRef
	 * )
	 */
	@Override
	public boolean visit(MethodRef node) {
		return visitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodRefParameter)
	 */
	@Override
	public boolean visit(MethodRefParameter node) {
		return visitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Modifier)
	 */
	@Override
	public boolean visit(Modifier node) {
		return visitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NormalAnnotation)
	 */
	@Override
	public boolean visit(NormalAnnotation node) {
		return visitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ParameterizedType)
	 */
	@Override
	public boolean visit(ParameterizedType node) {
		return visitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.QualifiedType)
	 */
	@Override
	public boolean visit(QualifiedType node) {
		return visitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SingleMemberAnnotation)
	 */
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		return visitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TagElement)
	 */
	@Override
	public boolean visit(TagElement node) {
		return visitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TextElement)
	 */
	@Override
	public boolean visit(TextElement node) {
		return visitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeParameter)
	 */
	@Override
	public boolean visit(TypeParameter node) {
		return visitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.WildcardType)
	 */
	@Override
	public boolean visit(WildcardType node) {
		return visitNode(node);
	}

	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ArrayAccess node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ArrayCreation node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ArrayType node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(AssertStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(Assignment node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(Block node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(BooleanLiteral node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(BreakStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(CastExpression node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(CatchClause node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(CharacterLiteral node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ClassInstanceCreation node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ConditionalExpression node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ConstructorInvocation node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ContinueStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(DoStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(EmptyStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ExpressionStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(FieldAccess node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(FieldDeclaration node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ForStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(IfStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ImportDeclaration node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(InfixExpression node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(InstanceofExpression node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(Initializer node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(Javadoc node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(LabeledStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(MethodInvocation node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(NullLiteral node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(NumberLiteral node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(PackageDeclaration node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ParenthesizedExpression node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(PostfixExpression node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(PrefixExpression node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(PrimitiveType node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(QualifiedName node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ReturnStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(SimpleName node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(SimpleType node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(StringLiteral node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(SuperConstructorInvocation node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(SuperFieldAccess node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(SuperMethodInvocation node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(SwitchCase node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(SwitchStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(SynchronizedStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ThisExpression node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(ThrowStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(TryStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(TypeDeclarationStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(TypeLiteral node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(SingleVariableDeclaration node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(VariableDeclarationExpression node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(VariableDeclarationStatement node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(VariableDeclarationFragment node) {
		endVisitNode(node);
	}

	@Override
	public void endVisit(WhileStatement node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.AnnotationTypeDeclaration)
	 */
	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration)
	 */
	@Override
	public void endVisit(AnnotationTypeMemberDeclaration node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.BlockComment)
	 */
	@Override
	public void endVisit(BlockComment node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.EnhancedForStatement)
	 */
	@Override
	public void endVisit(EnhancedForStatement node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.EnumConstantDeclaration)
	 */
	@Override
	public void endVisit(EnumConstantDeclaration node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.EnumDeclaration)
	 */
	@Override
	public void endVisit(EnumDeclaration node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.LineComment)
	 */
	@Override
	public void endVisit(LineComment node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.MarkerAnnotation)
	 */
	@Override
	public void endVisit(MarkerAnnotation node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.MemberRef)
	 */
	@Override
	public void endVisit(MemberRef node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.MemberValuePair)
	 */
	@Override
	public void endVisit(MemberValuePair node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.MethodRef)
	 */
	@Override
	public void endVisit(MethodRef node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.MethodRefParameter)
	 */
	@Override
	public void endVisit(MethodRefParameter node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.Modifier)
	 */
	@Override
	public void endVisit(Modifier node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.NormalAnnotation)
	 */
	@Override
	public void endVisit(NormalAnnotation node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.ParameterizedType)
	 */
	@Override
	public void endVisit(ParameterizedType node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.QualifiedType)
	 */
	@Override
	public void endVisit(QualifiedType node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.SingleMemberAnnotation)
	 */
	@Override
	public void endVisit(SingleMemberAnnotation node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.TagElement)
	 */
	@Override
	public void endVisit(TagElement node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.TextElement)
	 */
	@Override
	public void endVisit(TextElement node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.TypeParameter)
	 */
	@Override
	public void endVisit(TypeParameter node) {
		endVisitNode(node);
	}

	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#endVisit(org.eclipse.jdt.core.dom.WildcardType)
	 */
	@Override
	public void endVisit(WildcardType node) {
		endVisitNode(node);
	}

}
