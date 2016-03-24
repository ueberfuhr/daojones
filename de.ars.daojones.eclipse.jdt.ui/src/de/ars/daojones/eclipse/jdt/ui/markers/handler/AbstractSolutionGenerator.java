package de.ars.daojones.eclipse.jdt.ui.markers.handler;

import static de.ars.daojones.eclipse.jdt.ui.LoggerConstants.ERROR;
import static de.ars.daojones.eclipse.jdt.ui.LoggerConstants.getLogger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

import de.ars.daojones.eclipse.jdt.beans.NodeFinder;
import de.ars.daojones.eclipse.jdt.internal.util.StubUtility3;
import de.ars.daojones.eclipse.jdt.markers.solutions.ISolution;
import de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionContext;
import de.ars.daojones.eclipse.jdt.markers.solutions.ISolutionExecutionEnvironment;
import de.ars.daojones.eclipse.jdt.ui.Activator;

/**
 * A class generating solutions for a special problem.
 * @author <a href="mailto:ralf.zahn@ars.de">Ralf Zahn</a>
 *
 */
public abstract class AbstractSolutionGenerator implements IMarkerResolutionGenerator, IQuickFixProcessor {

	/**
	 * Returns the solutions for a special problem.
	 * They should be ordered by priority with the usage of the localization principle.
	 * @param ctx context information
	 * @return the solutions
	 */
	protected abstract ISolution[] getSolutions(ISolutionContext ctx);
//	protected abstract int[] getProblemIDs();
	
	/**
	 * @see org.eclipse.ui.IMarkerResolutionGenerator#getResolutions(org.eclipse.core.resources.IMarker)
	 */
	@Override
	public IMarkerResolution[] getResolutions(final IMarker marker) {
		final IResource res = marker.getResource();
		try {
			final ICompilationUnit compilationUnit = StubUtility3.findCompilationUnit(res);
			if(null == compilationUnit)	throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "The marker does not select a node from a java compilation unit!"));
			final ASTNode astRoot = StubUtility3.getRoot(compilationUnit, null);
			final ISolutionContext ctx = new ISolutionContext() {
				@Override
				public CompilationUnit getASTRoot() {
					return (CompilationUnit)astRoot;
				}
				@Override
				public ICompilationUnit getCompilationUnit() {
					return compilationUnit;
				}
				@Override
				public IJavaProject getJavaProject() {
					return compilationUnit.getJavaProject();
				}
				public ASTNode getSelectedNode() {
					final int charStart = marker.getAttribute(IMarker.CHAR_START, -1);
					final int charEnd = marker.getAttribute(IMarker.CHAR_END, -1);
					if(charStart >= 0 && charEnd >= 0) {
						return NodeFinder.find(getASTRoot(), charStart, charEnd);
					}
					return null;
				}
			};
			final ISolution[] solutions = getSolutions(ctx);
			if(null == solutions) return null;
			final IMarkerResolution[] markerResolutions = new IMarkerResolution[solutions.length];
			for(int i=0; i<solutions.length; i++) {
				final ISolution solution = solutions[i];
				markerResolutions[i] = new IMarkerResolution() {
					@Override
					public String getLabel() {
						return solution.getTitle();
					}
					@Override
					public void run(final IMarker marker) {
						try {
							solution.solve(new ISolutionExecutionEnvironment() {
								@Override
								public CompilationUnit getASTRoot() {
									return ctx.getASTRoot();
								}
								@Override
								public ICompilationUnit getCompilationUnit() {
									return ctx.getCompilationUnit();
								}
								@Override
								public IJavaProject getJavaProject() {
									return ctx.getJavaProject();
								}
								@Override
								public ASTNode getSelectedNode() {
									return ctx.getSelectedNode();
								}
								@Override
								public TextEdit rewrite(ASTRewrite rewrite) throws JavaModelException, IllegalArgumentException {
									return rewrite.rewriteAST();
								}
								@Override
								public UndoEdit apply(TextEdit edit, IProgressMonitor monitor) throws CoreException {
									return getCompilationUnit().applyTextEdit(edit, monitor);
								}
							});
							compilationUnit.save(null, true);
						} catch (CoreException e) {
							getLogger().log(ERROR, "Unable to fix the problem (\"" + solution.getTitle() + "\")!", e);
						}
					}
				};
			}
			return markerResolutions;
		} catch (CoreException e) {
			getLogger().log(ERROR, "Unable to create solutions!", e);
			return new IMarkerResolution[0];
		}
	}

	/**
	 * @see org.eclipse.jdt.ui.text.java.IQuickFixProcessor#getCorrections(org.eclipse.jdt.ui.text.java.IInvocationContext, org.eclipse.jdt.ui.text.java.IProblemLocation[])
	 */
	@Override
	public IJavaCompletionProposal[] getCorrections(final IInvocationContext context, IProblemLocation[] locations) throws CoreException {
		final ICompilationUnit compilationUnit = context.getCompilationUnit();
		final ISolutionContext ctx = new ISolutionContext() {
			@Override
			public CompilationUnit getASTRoot() {
				return context.getASTRoot();
			}
			@Override
			public ICompilationUnit getCompilationUnit() {
				return compilationUnit;
			}
			@Override
			public IJavaProject getJavaProject() {
				return getCompilationUnit().getJavaProject();
			}
			public ASTNode getSelectedNode() {
				return context.getCoveredNode();
			}
		};
		final ISolution[] solutions = getSolutions(ctx);
		if(null == solutions) return null;
		final IJavaCompletionProposal[] proposals = new IJavaCompletionProposal[solutions.length];
		for(int i=0; i<solutions.length; i++) {
			final ISolution solution = solutions[i];
			final int relevance = 100-i;
			proposals[i] = new IJavaCompletionProposal() {
				@Override
				public int getRelevance() {
					return relevance;
				}
				@Override
				public void apply(final IDocument document) {
					try {
						solution.solve(new ISolutionExecutionEnvironment() {
							@Override
							public CompilationUnit getASTRoot() {
								return ctx.getASTRoot();
							}
							@Override
							public ICompilationUnit getCompilationUnit() {
								return ctx.getCompilationUnit();
							}
							@Override
							public IJavaProject getJavaProject() {
								return ctx.getJavaProject();
							}
							@Override
							public ASTNode getSelectedNode() {
								return ctx.getSelectedNode();
							}
							@Override
							public TextEdit rewrite(ASTRewrite rewrite) throws JavaModelException, IllegalArgumentException {
								return rewrite.rewriteAST(document, getJavaProject().getOptions(true));
							}
							@Override
							public UndoEdit apply(TextEdit edit, IProgressMonitor monitor) throws CoreException {
								try {
									return edit.apply(document);
								} catch (MalformedTreeException e) {
									throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to fix the problem (\"" + solution.getTitle() + "\")!", e));
								} catch (BadLocationException e) {
									throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to fix the problem (\"" + solution.getTitle() + "\")!", e));
								}
							}
						});
						compilationUnit.getBuffer().setContents(document.get());
					} catch (CoreException e) {
						getLogger().log(ERROR, "Unable to fix the problem (\"" + solution.getTitle() + "\")!", e);
					}
				}
				@Override
				public String getAdditionalProposalInfo() {
					return solution.getDescription();
				}
				@Override
				public IContextInformation getContextInformation() {
					return null;
				}
				@Override
				public String getDisplayString() {
					return solution.getTitle();
				}
				@Override
				public Image getImage() {
					return null;
				}
				@Override
				public Point getSelection(IDocument document) {
					return null;
				}
			};
		}
		return proposals;
	}

	/**
	 * @see org.eclipse.jdt.ui.text.java.IQuickFixProcessor#hasCorrections(org.eclipse.jdt.core.ICompilationUnit, int)
	 */
	@Override
	public boolean hasCorrections(ICompilationUnit unit, int problemId) {
		return true;
	}

}
