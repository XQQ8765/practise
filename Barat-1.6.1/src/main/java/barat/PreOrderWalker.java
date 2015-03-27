package barat;

import barat.reflect.*;
import barat.reflect.Class;

import barat.collections.*;

/** 
 * This class, which wraps a non-descending visitor, walks the abstract
 * syntax tree and calls the wrapped visitor using a pre-order traversal.
 *
 * @author jesper@selskabet.org
 */

public class PreOrderWalker extends DescendingVisitor {

  Visitor real;

  public PreOrderWalker(Visitor realVisitor) {
    real = realVisitor;
  }
  
  protected void acceptIfPresent (Node o) {
    if (o != null) o.accept (this);
  }
  // visiting methods below this line

  public void visitLiteral(Literal o)
  {
    o.accept(real);
  }
  public void visitCaseBranch(CaseBranch o)
  {
    o.accept(real);
    o.getConstantExpression().accept(this);
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitInterface(Interface o)
  {
    o.accept(real);
    for(FieldIterator i=o.getFields().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(AbstractMethodIterator i=o.getAbstractMethods().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(InterfaceIterator i=o.getNestedInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getNestedClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitClass(Class o)
  {
    o.accept(real);
    for(BlockIterator i=o.getStaticInitializers().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ConstructorIterator i=o.getConstructors().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(FieldIterator i=o.getFields().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(AbstractMethodIterator i=o.getAbstractMethods().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ConcreteMethodIterator i=o.getConcreteMethods().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(InterfaceIterator i=o.getNestedInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getNestedClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitPackage(barat.reflect.Package o)
  {
    o.accept(real);
    for(InterfaceIterator i=o.getInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitCompilationUnit(CompilationUnit o)
  {
    o.accept(real);
    for(InterfaceIterator i=o.getInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitAbstractMethod(AbstractMethod o)
  {
    o.accept(real);
    for(ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitParameter(Parameter o)
  {
    o.accept(real);
  }
  public void visitBlock(Block o)
  {
    o.accept(real);
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitConstructor(Constructor o)
  {
    o.accept(real);
    for(ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getConstructorCall());
    o.getBody().accept(this);
  }
  public void visitConcreteMethod(ConcreteMethod o)
  {
    o.accept(real);
    for(ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.getBody().accept(this);
  }
  public void visitForInitDeclaration(ForInitDeclaration o)
  {
    o.accept(real);
    for(VariableDeclarationIterator i=o.getDeclarations().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitAssert(Assert o)
  {
    o.accept(real);
    o.getConditionExpression().accept(this);
    acceptIfPresent(o.getFailureExpression());
  }
  public void visitVariableDeclaration(VariableDeclaration o)
  {
    o.accept(real);
    o.getVariable().accept(this);
  }
  public void visitLocalVariable(LocalVariable o)
  {
    o.accept(real);
    acceptIfPresent (o.getInitializer());
  }
  public void visitField(Field o)
  {
    o.accept(real);
    acceptIfPresent (o.getInitializer());
  }
  public void visitThis(This o)
  {
    o.accept(real);
  }
  public void visitCatch(Catch o)
  {
    o.accept(real);
    o.getParameter().accept(this);
    o.getBlock().accept(this);
  }
  public void visitFinally(Finally o)
  {
    o.accept(real);
    o.getBlock().accept(this);
  }
  public void visitSynchronized(Synchronized o)
  {
    o.accept(real);
    o.getExpression().accept(this);
    o.getBlock().accept(this);
  }
  public void visitConstructorCall(ConstructorCall o)
  {
    o.accept(real);
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitObjectAllocation(ObjectAllocation o)
  {
    o.accept(real);
    acceptIfPresent (o.getEnclosingInstance());
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitTry(Try o)
  {
    o.accept(real);
    o.getBlock().accept(this);
    for(CatchIterator i=o.getCatchClauses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getFinallyClause());
  }
  public void visitArrayAccess(ArrayAccess o)
  {
    o.accept(real);
    o.getArray().accept(this);
    o.getIndex().accept(this);
  }
  public void visitArrayAllocation(ArrayAllocation o)
  {
    o.accept(real);
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getInitializer());
  }
  public void visitArrayInitializer(ArrayInitializer o)
  {
    o.accept(real);
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitAssignment(Assignment o)
  {
    o.accept(real);
    o.getLvalue().accept(this);
    o.getOperand().accept(this);
  }
  public void visitBinaryOperation(BinaryOperation o)
  {
    o.accept(real);
    o.getLeftOperand().accept(this);
    o.getRightOperand().accept(this);
  }
  public void visitCast(Cast o)
  {
    o.accept(real);
    o.getOperand().accept(this);
  }
  public void visitConditional(Conditional o)
  {
    o.accept(real);
    o.getCondition().accept(this);
    o.getIfTrue().accept(this);
    o.getIfFalse().accept(this);
  }
  public void visitContinue(Continue o)
  {
    o.accept(real);
    /*** o.getTarget().accept(this); ***/
  }
  public void visitDefaultBranch(DefaultBranch o)
  {
    o.accept(real);
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitDo(Do o)
  {
    o.accept(real);
    o.getBody().accept(this);
    o.getExpression().accept(this);
  }
  public void visitEmptyStatement(EmptyStatement o)
  {
    o.accept(real);
  }
  public void visitExpressionStatement(ExpressionStatement o)
  {
    o.accept(real);
    o.getExpression().accept(this);
  }
  public void visitFor(For o)
  {
    o.accept(real);
    acceptIfPresent (o.getForInit());
    acceptIfPresent (o.getExpression());
    for(AExpressionIterator i=o.getUpdateExpressions().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getBody());
  }
  public void visitForInitExpression(ForInitExpression o)
  {
    o.accept(real);
    for(AExpressionIterator i=o.getExpressions().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitIf(If o)
  {
    o.accept(real);
    o.getExpression().accept(this);
    o.getThenBranch().accept(this);
    acceptIfPresent (o.getElseBranch());
  }
  public void visitInstanceFieldAccess(InstanceFieldAccess o)
  {
    o.accept(real);
    o.getInstance().accept(this);
  }
  public void visitInstanceMethodCall(InstanceMethodCall o)
  {
    o.accept(real);
    o.getInstance().accept(this);
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitArrayLengthAccess(ArrayLengthAccess o)
  {
    o.accept(real);
    o.getOperand().accept(this);
  }
  public void visitParenExpression(ParenExpression o)
  {
    o.accept(real);
    o.getOperand().accept(this);
  }
  public void visitReturn(Return o)
  {
    o.accept(real);
    acceptIfPresent (o.getExpression());
  }
  public void visitStaticFieldAccess(StaticFieldAccess o)
  {
    o.accept(real);
  }
  public void visitInstanceof(Instanceof o)
  {
    o.accept(real);
    o.getOperand().accept(this);
  }
  public void visitStaticMethodCall(StaticMethodCall o)
  {
    o.accept(real);
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitBreak(Break o)
  {
    o.accept(real);
    /*** o.getTarget().accept(this); ***/
  }
  public void visitSwitch(Switch o)
  {
    o.accept(real);
    o.getExpression().accept(this);
    for(ASwitchBranchIterator i=o.getBranches().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitUnaryOperation(UnaryOperation o)
  {
    o.accept(real);
    o.getOperand().accept(this);
  }
  public void visitThrow(Throw o)
  {
    o.accept(real);
    o.getExpression().accept(this);
  }
  public void visitVariableAccess(VariableAccess o)
  {
    o.accept(real);
  }
  public void visitWhile(While o)
  {
    o.accept(real);
    o.getExpression().accept(this);
    o.getBody().accept(this);
  }
  public void visitAnonymousAllocation(AnonymousAllocation o)
  {
    o.accept(real);
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.getAnonymousClass().accept(this);
  }
  public void visitUserTypeDeclaration(UserTypeDeclaration o)
  {
    o.accept(real);
    o.getUserType().accept(this);
  }
  public void visitClassExpression(ClassExpression o)
  {
    o.accept(real);
  }
}
