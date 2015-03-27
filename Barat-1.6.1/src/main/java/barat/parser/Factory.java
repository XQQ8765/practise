/*
 * File: Factory.java
 *
 * $Id: Factory.java,v 1.37 2002/10/28 10:38:19 bokowski Exp $
 *
 * This file is part of Barat.
 * Copyright (c) 1998-2000 Boris Bokowski (bokowski@users.sourceforge.net)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 *   Neither the name of Boris Bokowski nor the names of his contributors
 *   may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BORIS BOKOWSKI
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package barat.parser;

import java.util.Hashtable;
import barat.reflect.*;
import barat.reflect.Class;
import barat.*;
import java.util.*;
//import java.util.*;
import java.io.InputStream;

/**
 * Factory for the Barat Parser.
 */
public class Factory implements java.io.Serializable {

  private static Factory instance = null;

  public static Factory getInstance() {
    if (instance == null) instance = new Factory();
    return instance;
  }
  
  public static void setInstance(Factory i)
  {
    if(instance!=null)
    {
      throw new RuntimeException("factory may be set only once!");
    }
    instance = i;
  }
  
  private static List<AnalysisVisitor> analysisObjects = new ArrayList<AnalysisVisitor>();

  public static void registerAttributeAdder(Visitor v)
  {
    addAnalysis(new AttributeAdderVisitor(v));
  }
  
  public static void addAnalysis(AnalysisVisitor v)
  {
    analysisObjects.add(v);
  }
  
  protected void addAttributes(NodeImpl n)
  {
    for(Iterator<AnalysisVisitor> i = analysisObjects.iterator(); i.hasNext();)
    {
      n.accept(i.next());
    }
  }

  public static Scope currentScope()
  {
  	return BaratParser.currentScope;
  }

	static void preParse(java.io.InputStream in, Scope scope) throws Exception
	{
		if(NameAnalysis.parserInUse) throw new RuntimeException("Parser in use! (a special tag is parsed or what?)");
		NameAnalysis.parserInUse = true;

		if (NameAnalysis.theParser == null)
			NameAnalysis.theParser = new BaratParser (in);
		else
			BaratParser.ReInit (in);

		NameAnalysis.theParser.currentScope = scope;
	}
	
	static InputStream preParse(String source, Scope scope) throws Exception
	{
		java.io.InputStream in = new java.io.StringBufferInputStream(source);
                
                preParse(in, scope);
		
		return in;
	}
	
	static void postParse(InputStream stream) throws Exception
	{
		NameAnalysis.theParser.currentScope = null;

		stream.close();

		NameAnalysis.parserInUse = false;
	}
        
        // parse source contained in "inputStream" (must be compilation unit)
        // "inputStream" will be closed!
        public static CompilationUnitImpl parseCompilationUnit(String filename, InputStream is)
        {
            try
            {
                preParse(is, null);
                
                CompilationUnitImpl cu = NameAnalysis.theParser.CompilationUnit(filename);
                
                postParse(is);
                
                return cu;
            }
            catch(Exception ex)
            {
              ex.printStackTrace();
              return null;
            }
        }
        
        public static CompilationUnitImpl parseCompilationUnit(java.io.File f)
        {
            try
            {
                return parseCompilationUnit(f.getAbsolutePath(), new java.io.FileInputStream(f));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }

	// parse "source" in scope "s" as an expression
	public static AExpression makeExpression(String source, Scope s)
	{
		Attribute<AExpression> expr = null;

		try
		{
			InputStream is = preParse(source, s);

			expr = NameAnalysis.theParser.Expression();

			postParse(is);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return expr.value();
	}

	// parse "source" in scope "s" as a statement
	public static AStatement makeStatement(String source, Scope s)
	{
		AStatement stmnt = null;

		try
		{
			InputStream is = preParse(source, s);

			stmnt = NameAnalysis.theParser.Statement();

			postParse(is);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return stmnt;
	}


  // Book-keeping of known instances.
  // The keys in these Hashtables are the fully
  // qualified names of the reflected objects.

  Hashtable knownPackages   = new Hashtable();
  
  // book-keeping methods

  public PackageImpl getPackage (String name) {

    PackageImpl p = (PackageImpl)knownPackages.get (name);
    if (p == null) {
      p = new PackageImpl (QualifiedName.from(name));
      knownPackages.put (name, p);
      NameAnalysis.addNameAnalysis(p);
    }
    return p;

  }

  // singleton objects for primitive types

  private PrimitiveTypeImpl booleanImpl = null;
  private PrimitiveTypeImpl charImpl    = null;
  private PrimitiveTypeImpl byteImpl    = null;
  private PrimitiveTypeImpl shortImpl   = null;
  private PrimitiveTypeImpl intImpl     = null;
  private PrimitiveTypeImpl longImpl    = null;
  private PrimitiveTypeImpl floatImpl   = null;
  private PrimitiveTypeImpl doubleImpl  = null;
  private NullTypeImpl      nullImpl    = null;
  
  public PrimitiveTypeImpl getBoolean() {
    if (booleanImpl == null) {
      booleanImpl = new PrimitiveTypeImpl();
      booleanImpl.isBoolean(true);
    }
    return booleanImpl;
  }

  public PrimitiveTypeImpl getChar() {
    if (charImpl == null) {
      charImpl = new PrimitiveTypeImpl();
      charImpl.isChar(true);
    }
    return charImpl;
  }

  public PrimitiveTypeImpl getByte() {
    if (byteImpl == null) {
      byteImpl = new PrimitiveTypeImpl();
      byteImpl.isByte(true);
    }
    return byteImpl;
  }

  public PrimitiveTypeImpl getShort() {
    if (shortImpl == null) {
      shortImpl = new PrimitiveTypeImpl();
      shortImpl.isShort(true);
    }
    return shortImpl;
  }

  public PrimitiveTypeImpl getInt() {
    if (intImpl == null) {
      intImpl = new PrimitiveTypeImpl();
      intImpl.isInt(true);
    }
    return intImpl;
  }

  public PrimitiveTypeImpl getLong() {
    if (longImpl == null) {
      longImpl = new PrimitiveTypeImpl();
      longImpl.isLong(true);
    }
    return longImpl;
  }

  public PrimitiveTypeImpl getFloat() {
    if (floatImpl == null) {
      floatImpl = new PrimitiveTypeImpl();
      floatImpl.isFloat(true);
    }
    return floatImpl;
  }

  public PrimitiveTypeImpl getDouble() {
    if (doubleImpl == null) {
      doubleImpl = new PrimitiveTypeImpl();
      doubleImpl.isDouble(true);
    }
    return doubleImpl;
  }

  public NullTypeImpl getNullType() {
    if (nullImpl == null) {
      nullImpl = new NullTypeImpl();
    }
    return nullImpl;
  }

  // creation methods

  public AbstractMethodImpl createAbstractMethod() {
    AbstractMethodImpl result = new AbstractMethodImpl();
    NameAnalysis.addNameAnalysis(result);
    addAttributes(result);
    return result;
  }

  public AnonymousAllocationImpl createAnonymousAllocation(List<Attribute<AExpression>> arguments, Attribute<Class> anonymousClass) {
    AnonymousAllocationImpl result = new AnonymousAllocationImpl(arguments, anonymousClass);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public ArrayImpl createArray (Attribute<AType> elementType) {
    return (ArrayImpl)elementType.value().getCorrespondingArray();
  }

  public ArrayAccessImpl createArrayAccess (Attribute<AExpression> array,
                                            Attribute<AExpression> index) {
    ArrayAccessImpl result = new ArrayAccessImpl (index, array);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public ArrayAllocationImpl createArrayAllocation() {
    ArrayAllocationImpl result = new ArrayAllocationImpl();
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  protected Attribute<AExpression> wrapArrayInitializer(final Attribute<AExpression> initializer) {
    return new Attribute<AExpression>() {
      protected AExpression calculate() {
	ArrayAllocationImpl a = createArrayAllocation();
	a.freeDimensions(1);
	a.setInitializer(new CastingAttribute<AExpression,ArrayInitializer> (initializer));
	Node _container = (Node)container;
	int levels = 0;
	while(!(_container instanceof ATyped)) {
	  if(_container instanceof ArrayAllocation) levels++;
	  _container = (Node)_container.container();
	}
	Array type = (Array) ((ATyped)_container).getType();
	for(int i=0; i<levels; i++) {
	  type = (Array) type.getElementType();
	}
	a.setElementType(new Constant<AType>(type.getElementType()));
	return a;
      }
    };
  }

  public ArrayInitializerImpl createArrayInitializer() {
    ArrayInitializerImpl result = new ArrayInitializerImpl();
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public ArrayLengthAccessImpl createArrayLengthAccess(Attribute<AExpression> exp) {
    ArrayLengthAccessImpl result = new ArrayLengthAccessImpl (exp);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public AssertImpl createAssert(Attribute<AExpression> exp1, Attribute<AExpression> exp2) {
    AssertImpl result = new AssertImpl (exp1, exp2);
    addAttributes(result);
    return result;
  }

  public AssignmentImpl createAssignment (final Attribute<AExpression> lhs,
                                          String                 operator,
                                          Attribute<AExpression> rhs) {
    Attribute<ALValue> real_lhs = new Attribute<ALValue>() {
        protected ALValue calculate() { return (ALValue)lhs.value(); }};
    AssignmentImpl result = new AssignmentImpl (rhs, real_lhs);
    result.operator(operator);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public BinaryOperationImpl createBinaryOperation (
                                   Attribute<AExpression> leftOperand,
                                   String                 operator,
                                   Attribute<AExpression> rightOperand) {
    BinaryOperationImpl result = new BinaryOperationImpl (leftOperand, rightOperand);
    result.operator(operator);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public BlockImpl createBlock() {
    BlockImpl result = new BlockImpl();
    NameAnalysis.addNameAnalysis(result);
    addAttributes(result);
    return result;
  }

  public BreakImpl createBreak() {
    BreakImpl result = new BreakImpl();
    addAttributes(result);
    return result;
  }

  public CaseBranchImpl createCaseBranch() {
    CaseBranchImpl result = new CaseBranchImpl();
    addAttributes(result);
    return result;
  }

  public CastImpl createCast (Attribute<AExpression> operand,
                              Attribute<AType>       castType) {
    CastImpl result = new CastImpl (operand, castType);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public CatchImpl createCatch() {
    CatchImpl result = new CatchImpl();
    NameAnalysis.addNameAnalysis(result);
    addAttributes(result);
    return result;
  }

  public ClassImpl createClass(String className, NodeImpl container) {
    ClassImpl result = new ClassImpl();
    NameAnalysis.addNameAnalysis(result);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    result.setName (className);
    if (container instanceof CompilationUnitImpl)
    {
      CompilationUnitImpl compilationUnit = (CompilationUnitImpl)container;
      compilationUnit.addClass_ (new Constant<Class>(result));
      PackageImpl pkg = (PackageImpl)compilationUnit.containing(PackageImpl.class);
      pkg.addClass_ (new Constant<Class>(result));
    }
    else if(container instanceof AUserTypeImpl)
    {
      AUserTypeImpl outerType = (AUserTypeImpl)container;
      outerType.addNestedClasse (new Constant<Class>(result));
    }
    else if(container instanceof UserTypeDeclarationImpl)
    {
      UserTypeDeclarationImpl decl = (UserTypeDeclarationImpl)container;
      decl.setUserType(new Constant<AUserType>(result));
    }
    else
    {
      if(container!=null) throw new RuntimeException("invalid container for class");
    }
    return result;
  }
  
  public ClassExpressionImpl createClassExpression(Attribute<barat.reflect.AType> t)
  {
    if(t==null) t=new Attribute<barat.reflect.AType>()
    {
      protected barat.reflect.AType calculate()
      {
        return null;
      }
    };
    ClassExpressionImpl result = new ClassExpressionImpl(t);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public CompilationUnitImpl createCompilationUnit(String filename, List<QualifiedName> classImports, List<barat.reflect.Package> packageImports) {
    CompilationUnitImpl result = new CompilationUnitImpl();
    result.filename(filename);
    packageImports.add(getPackage("java.lang"));
    result.setClassImports(classImports);
    result.setPackageImports(packageImports);
    NameAnalysis.addNameAnalysis(result, classImports, packageImports);
    addAttributes(result);
    return result;
  }

  public ConcreteMethodImpl createConcreteMethod() {
    ConcreteMethodImpl result = new ConcreteMethodImpl();
    NameAnalysis.addNameAnalysis(result);
    addAttributes(result);
    return result;
  }

  public ConditionalImpl createConditional (Attribute<AExpression> exp,
                                            Attribute<AExpression> ifTrue,
                                            Attribute<AExpression> ifFalse) {
    ConditionalImpl result = new ConditionalImpl (exp, ifTrue, ifFalse);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public LiteralImpl createLiteral (java.lang.Object value) {
    LiteralImpl result = new LiteralImpl ();
    result.constantValue(value);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public ConstructorImpl createConstructor() {
    ConstructorImpl result = new ConstructorImpl();
    NameAnalysis.addNameAnalysis (result);
    result.setName("<init>");
    addAttributes(result);
    return result;
  }

  public ContinueImpl createContinue() {
    ContinueImpl result = new ContinueImpl();
    addAttributes(result);
    return result;
  }

  public DefaultBranchImpl createDefaultBranch() {
    DefaultBranchImpl result = new DefaultBranchImpl();
    addAttributes(result);
    return result;
  }

  public DoImpl createDo (barat.reflect.AStatement body,
                          Attribute<AExpression>   expression) {
    DoImpl result = new DoImpl (expression, body);
    addAttributes(result);
    return result;
  }

  public EmptyStatementImpl createEmptyStatement() {
    EmptyStatementImpl result = new EmptyStatementImpl();
    addAttributes(result);
    return result;
  }

  public ExpressionStatementImpl createExpressionStatement(Attribute<AExpression> expr) {
    ExpressionStatementImpl result = new ExpressionStatementImpl(expr);
    addAttributes(result);
    return result;
  }

  public FieldImpl createField() {
    FieldImpl result = new FieldImpl();
    addAttributes(result);
    return result;
  }

  public FinallyImpl createFinally (barat.reflect.Block block) {
    FinallyImpl result = new FinallyImpl (block);
    addAttributes(result);
    return result;
  }

  public ForImpl createFor() {
    ForImpl result = new ForImpl();
    addAttributes(result);
    return result;
  }

  public ForInitDeclarationImpl createForInitDeclaration() {
    ForInitDeclarationImpl result = new ForInitDeclarationImpl();
    addAttributes(result);
    return result;
  }

  public ForInitExpressionImpl createForInitExpression() {
    ForInitExpressionImpl result = new ForInitExpressionImpl();
    addAttributes(result);
    return result;
  }

  public IfImpl createIf (Attribute<AExpression>   expression,
                          barat.reflect.AStatement thenBranch,
                          barat.reflect.AStatement elseBranch) {
    IfImpl result = new IfImpl (expression, thenBranch, elseBranch);
    addAttributes(result);
    return result;
  }

  public InstanceFieldAccessImpl createInstanceFieldAccess (
                                   Attribute<AExpression> instance,
                                   barat.reflect.Field    field) {
    InstanceFieldAccessImpl result = new InstanceFieldAccessImpl (field, instance);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public InstanceMethodCallImpl createInstanceMethodCall (
                          Attribute<AExpression>       instance,
                          barat.reflect.AMethod calledMethod,
                          List<Attribute<AExpression>> arguments) {
    InstanceMethodCallImpl result = new InstanceMethodCallImpl (arguments, calledMethod, instance);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public InstanceofImpl createInstanceof(Attribute<AExpression> operand, final Attribute<AType> type) {
    InstanceofImpl result = new InstanceofImpl(operand, new Attribute<AReferenceType>() {
        protected AReferenceType calculate() {
                return (AReferenceType)type.value(); }});
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public InterfaceImpl createInterface(String interfaceName, NodeImpl container) {
    InterfaceImpl result = new InterfaceImpl();
    NameAnalysis.addNameAnalysis(result);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    result.setName (interfaceName);
    if (container instanceof CompilationUnitImpl)
    {
      CompilationUnitImpl compilationUnit = (CompilationUnitImpl)container;
      compilationUnit.addInterface_ (new Constant<Interface>(result));
      PackageImpl pkg = (PackageImpl)compilationUnit.containing(PackageImpl.class);
      pkg.addInterface_ (new Constant<Interface>(result));
    }
    else if(container instanceof AUserTypeImpl)
    {
      AUserTypeImpl outerType = (AUserTypeImpl)container;
      outerType.addNestedInterface (new Constant<Interface>(result));
    }
    else if(container instanceof UserTypeDeclarationImpl)
    {
      UserTypeDeclarationImpl decl = (UserTypeDeclarationImpl)container;
      decl.setUserType(new Constant<AUserType>(result));
    }
    else
    {
      if(container!=null) throw new RuntimeException("invalid container for interface");
    }
    return result;
  }

  public LocalVariableImpl createLocalVariable() {
    LocalVariableImpl result = new LocalVariableImpl();
    addAttributes(result);
    return result;
  }

  public Modifiers createModifiers() {
    return new Modifiers();
  }

  public ObjectAllocationImpl createObjectAllocation (
                                List<Attribute<AExpression>> arguments) {
    ObjectAllocationImpl result = new ObjectAllocationImpl (arguments);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public ParameterImpl createParameter (Attribute<AType> type,
                                        String           id) {
    ParameterImpl result = new ParameterImpl (type, id);
    addAttributes(result);
    return result;
  }

  public ParenExpressionImpl createParenExpression(Attribute<AExpression> exp) {
    ParenExpressionImpl result = new ParenExpressionImpl (exp);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public ReturnImpl createReturn() {
    ReturnImpl result = new ReturnImpl();
    addAttributes(result);
    return result;
  }

  public StaticFieldAccessImpl createStaticFieldAccess (
                                 barat.reflect.Field field) {
    StaticFieldAccessImpl result = new StaticFieldAccessImpl (field);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public StaticMethodCallImpl createStaticMethodCall (
                                barat.reflect.AMethod        calledMethod,
                                List<Attribute<AExpression>> arguments) {
    StaticMethodCallImpl result = new StaticMethodCallImpl (arguments, calledMethod);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public SwitchImpl createSwitch() {
    SwitchImpl result = new SwitchImpl();
    addAttributes(result);
    return result;
  }

  public SynchronizedImpl createSynchronized(Attribute<AExpression> expression,
                                             barat.reflect.Block    block) {
    SynchronizedImpl result = new SynchronizedImpl (expression, block);
    addAttributes(result);
    return result;
  }

  public ThisImpl createThis() {
    ThisImpl result = new ThisImpl();
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public ThrowImpl createThrow(Attribute<AExpression> expr) {
    ThrowImpl result = new ThrowImpl(expr);
    addAttributes(result);
    return result;
  }

  public TryImpl createTry() {
    TryImpl result = new TryImpl();
    addAttributes(result);
    return result;
  }

  public UnaryOperationImpl createUnaryOperation (
                                   Attribute<AExpression> operand,
                                   String                 operator,
                                   boolean                isPostfix) {
    UnaryOperationImpl result = new UnaryOperationImpl (operand);
    result.operator(operator);
    result.isPostfix(isPostfix);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public UserTypeDeclarationImpl createUserTypeDeclaration()
  {
    UserTypeDeclarationImpl result = new UserTypeDeclarationImpl();
    NameAnalysis.addNameAnalysis(result);
    addAttributes(result);
    return result;
  }

  public VariableAccessImpl createVariableAccess (
                              barat.reflect.AVariable variable) {
    VariableAccessImpl result = new VariableAccessImpl (variable);
    TypeAnalysis.addTypeAnalysis(result);
    addAttributes(result);
    return result;
  }

  public VariableDeclarationImpl createVariableDeclaration (
                              barat.reflect.LocalVariable variable) {
    VariableDeclarationImpl result = new VariableDeclarationImpl(variable);
    NameAnalysis.addNameAnalysis(result);
    addAttributes(result);
    return result;
  }

  public WhileImpl createWhile (Attribute<AExpression>   expression,
                                barat.reflect.AStatement body) {
    WhileImpl result = new WhileImpl (expression, body);
    addAttributes(result);
    return result;
  }
}

