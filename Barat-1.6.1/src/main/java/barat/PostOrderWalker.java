package barat;

import barat.reflect.*;
import barat.reflect.Class;
import barat.collections.*;

/** 
 * This class, which wraps a non-descending visitor, walks the abstract
 * syntax tree and calls the wrapped visitor using a post-order traversal.
 *
 * @author jesper@selskabet.org
 */
public class PostOrderWalker extends DescendingVisitor {

  Visitor real;

  public PostOrderWalker(Visitor realVisitor) {
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
    o.getConstantExpression().accept(this);
    for(AStatementIterator i=o.getStatements().iterator(); 
i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitInterface(Interface o)
  {
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
    o.accept(real);
  }
  public void visitClass(Class o)
  {
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
    o.accept(real);
  }
  public void visitPackage(barat.reflect.Package o)
  {
    for(InterfaceIterator i=o.getInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitCompilationUnit(CompilationUnit o)
  {
    for(InterfaceIterator i=o.getInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitAbstractMethod(AbstractMethod o)
  {
    for(ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitParameter(Parameter o)
  {
    o.accept(real);
  }
  public void visitBlock(Block o)
  {
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitConstructor(Constructor o)
  {
    for(ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getConstructorCall());
    o.getBody().accept(this);
    o.accept(real);
  }
  public void visitConcreteMethod(ConcreteMethod o)
  {
    for(ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.getBody().accept(this);
    o.accept(real);
  }
  public void visitForInitDeclaration(ForInitDeclaration o)
  {
    for(VariableDeclarationIterator i=o.getDeclarations().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitAssert(Assert o)
  {
    o.getConditionExpression().accept(this);
    acceptIfPresent(o.getFailureExpression());
    o.accept(real);
  }
  public void visitVariableDeclaration(VariableDeclaration o)
  {
    o.getVariable().accept(this);
    o.accept(real);
  }
  public void visitLocalVariable(LocalVariable o)
  {
    acceptIfPresent (o.getInitializer());
    o.accept(real);
  }
  public void visitField(Field o)
  {
    acceptIfPresent (o.getInitializer());
    o.accept(real);
  }
  public void visitThis(This o)
  {
    o.accept(real);
  }
  public void visitCatch(Catch o)
  {
    o.getParameter().accept(this);
    o.getBlock().accept(this);
    o.accept(real);
  }
  public void visitFinally(Finally o)
  {
    o.getBlock().accept(this);
    o.accept(real);
  }
  public void visitSynchronized(Synchronized o)
  {
    o.getExpression().accept(this);
    o.getBlock().accept(this);
    o.accept(real);
  }
  public void visitConstructorCall(ConstructorCall o)
  {
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitObjectAllocation(ObjectAllocation o)
  {
    acceptIfPresent (o.getEnclosingInstance());
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitTry(Try o)
  {
    o.getBlock().accept(this);
    for(CatchIterator i=o.getCatchClauses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getFinallyClause());
    o.accept(real);
  }
  public void visitArrayAccess(ArrayAccess o)
  {
    o.getArray().accept(this);
    o.getIndex().accept(this);
    o.accept(real);
  }
  public void visitArrayAllocation(ArrayAllocation o)
  {
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getInitializer());
    o.accept(real);
  }
  public void visitArrayInitializer(ArrayInitializer o)
  {
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitAssignment(Assignment o)
  {
    o.getLvalue().accept(this);
    o.getOperand().accept(this);
    o.accept(real);
  }
  public void visitBinaryOperation(BinaryOperation o)
  {
    o.getLeftOperand().accept(this);
    o.getRightOperand().accept(this);
    o.accept(real);
  }
  public void visitCast(Cast o)
  {
    o.getOperand().accept(this);
    o.accept(real);
  }
  public void visitConditional(Conditional o)
  {
    o.getCondition().accept(this);
    o.getIfTrue().accept(this);
    o.getIfFalse().accept(this);
    o.accept(real);
  }
  public void visitContinue(Continue o)
  {
    /*** o.getTarget().accept(this); ***/
    o.accept(real);
  }
  public void visitDefaultBranch(DefaultBranch o)
  {
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitDo(Do o)
  {
    o.getBody().accept(this);
    o.getExpression().accept(this);
    o.accept(real);
  }
  public void visitEmptyStatement(EmptyStatement o)
  {
    o.accept(real);
  }
  public void visitExpressionStatement(ExpressionStatement o)
  {
    o.getExpression().accept(this);
    o.accept(real);
  }
  public void visitFor(For o)
  {
    acceptIfPresent (o.getForInit());
    acceptIfPresent (o.getExpression());
    for(AExpressionIterator i=o.getUpdateExpressions().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getBody());
    o.accept(real);
  }
  public void visitForInitExpression(ForInitExpression o)
  {
    for(AExpressionIterator i=o.getExpressions().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitIf(If o)
  {
    o.getExpression().accept(this);
    o.getThenBranch().accept(this);
    acceptIfPresent (o.getElseBranch());
    o.accept(real);
  }
  public void visitInstanceFieldAccess(InstanceFieldAccess o)
  {
    o.getInstance().accept(this);
    o.accept(real);
  }
  public void visitInstanceMethodCall(InstanceMethodCall o)
  {
    o.getInstance().accept(this);
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitArrayLengthAccess(ArrayLengthAccess o)
  {
    o.getOperand().accept(this);
    o.accept(real);
  }
  public void visitParenExpression(ParenExpression o)
  {
    o.getOperand().accept(this);
    o.accept(real);
  }
  public void visitReturn(Return o)
  {
    acceptIfPresent (o.getExpression());
    o.accept(real);
  }
  public void visitStaticFieldAccess(StaticFieldAccess o)
  {
    o.accept(real);
  }
  public void visitInstanceof(Instanceof o)
  {
    o.getOperand().accept(this);
    o.accept(real);
  }
  public void visitStaticMethodCall(StaticMethodCall o)
  {
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitBreak(Break o)
  {
    /*** o.getTarget().accept(this); ***/
    o.accept(real);
  }
  public void visitSwitch(Switch o)
  {
    o.getExpression().accept(this);
    for(ASwitchBranchIterator i=o.getBranches().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.accept(real);
  }
  public void visitUnaryOperation(UnaryOperation o)
  {
    o.getOperand().accept(this);
    o.accept(real);
  }
  public void visitThrow(Throw o)
  {
    o.getExpression().accept(this);
    o.accept(real);
  }
  public void visitVariableAccess(VariableAccess o)
  {
    o.accept(real);
  }
  public void visitWhile(While o)
  {
    o.getExpression().accept(this);
    o.getBody().accept(this);
    o.accept(real);
  }
  public void visitAnonymousAllocation(AnonymousAllocation o)
  {
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.getAnonymousClass().accept(this);
    o.accept(real);
  }
  public void visitUserTypeDeclaration(UserTypeDeclaration o)
  {
    o.getUserType().accept(this);
    o.accept(real);
  }
  public void visitClassExpression(ClassExpression o)
  {
    o.accept(real);
  }
}
