package barat.codegen;

import org.apache.bcel.generic.*;
import org.apache.bcel.util.InstructionFinder;
import java.util.BitSet;
import java.util.Iterator;

/**
 * Perform peephole optimizations on byte code.
 *
 * @version $Id: PeepHole.java,v 1.2 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
class PeepHole implements org.apache.bcel.Constants {
  private MethodGen       mg;
  private InstructionList il;
  private boolean         print;
  private static int      count;

  private static final int pow2(int i) { return 1 << i; }

  PeepHole(MethodGen mg, boolean print) {
    this.mg = mg;
    this.print = print;
  }
  
  void optimize(BitSet level) {
    il = mg.getInstructionList();

    
    if(level.get(1)) {
      count = 0;
      optimize1();
    }

    if(level.get(2)) {
      count = 0;
      optimize2();
    }

    if(level.get(3)) {
      count = 0;
      optimize3();
    }

    if(level.get(4)) {
      count = 0;
      optimize4();
    }

    if(level.get(5)) {
      count = 0;
      optimize5();
    }

    if(level.get(6)) {
      count = 0;
      optimize6();
    }
    
    if(level.get(10)) {
      count = 0;
      optimizeX();
    }
  }

  private static final void handleLostTarget(InstructionHandle[] targets,
					     InstructionHandle new_target) {
    for(int i=0; i < targets.length; i++) {
      InstructionTargeter[] targeters = targets[i].getTargeters();
      
      for(int j=0; j < targeters.length; j++)
	targeters[j].updateTarget(targets[i], new_target);
    }
  }

  private static final InstructionFinder.CodeConstraint constraint1 =
    new InstructionFinder.CodeConstraint() {
      public boolean checkCode(InstructionHandle[] match) {
	IfInstruction if1 = (IfInstruction)match[0].getInstruction();
	GOTO          g   = (GOTO)match[2].getInstruction();
	return (if1.getTarget() == match[3]) && (g.getTarget() == match[4]);
      }  
    };

  /**
   * Optimize
   * <PRE>
   * 175(276):       if_icmpeq       #283
   * 176(279):       iconst_0           (false)
   * 177(280):       goto    #284
   * 178(283):       iconst_1           (true)
   * 179(284):       nop
   * 180(285):       ifne    #307       (iftrue)
   * </PRE>
   * to
   * <PRE>
   * 175(276):       if_icmpeq       #307
   * </PRE>
   */
  private void optimize1() {
    if(print) System.out.print("1 ");

    InstructionFinder f = new InstructionFinder(il);
    String      pat   = "IfInstruction ICONST_0 GOTO ICONST_1 NOP (IFEQ|IFNE)";
    
    for(Iterator e = f.search(pat, constraint1); e.hasNext(); ) {
      InstructionHandle[] match = (InstructionHandle[])e.next();
      
      // Everything ok, update code
      BranchHandle  if1      = (BranchHandle)match[0];
      IfInstruction if1_i    = (IfInstruction)if1.getInstruction();
      BranchHandle  ifeqne   = (BranchHandle)match[5];
      IfInstruction ifeqne_i = (IfInstruction)ifeqne.getInstruction();
      boolean       is_ifeq  = ifeqne_i instanceof IFEQ;

      if(is_ifeq) // IFFALSE -> Negate orig branch instruction
	if1.setInstruction(if1_i.negate());

      InstructionHandle target = ifeqne_i.getTarget();
      if1.setTarget(target); // Update target of branch

      /* Due to the implementation of || there may be more than just the if instruction
       * targetting the ICONST_1 and the ICONST_0 instruction, respectively.
       */
      InstructionTargeter[] targeters = match[1].getTargeters(); // FALSE
      
      if(targeters != null) {
	if(is_ifeq)
	  target = ifeqne_i.getTarget();
	else
	  target = ifeqne.getNext();

	for(int i=0; i < targeters.length; i++)
	  targeters[i].updateTarget(match[1], target);
      }

      targeters = match[3].getTargeters(); // TRUE
      
      if(targeters != null) {
	if(is_ifeq)
	  target = ifeqne.getNext();
	else
	  target = ifeqne_i.getTarget();

	for(int i=0; i < targeters.length; i++)
	  targeters[i].updateTarget(match[3], target);
      }

      /* Now deletion should be safe, except may be for local variables and 
       * exception handlers
       */
      try {
	il.delete(match[1], match[5]);
      } catch(TargetLostException ex) {
	handleLostTarget(ex.getTargets(), if1);
      }

      f.reread();
      count++;
    }

    if(print && (count > 0))
      System.out.print("(" + count + ") ");
  }

  /**
   * Optimize
   * <PRE>
   * 176(279):       aload		%4
   * 177(281):       aconst_null
   * 178(282):       if_acmpeq	#289
   * </PRE>
   * to
   * <PRE>
   * 176(279):       aload		%4
   * 178(282):       ifnull	#289
   * </PRE>
   */
  private void optimize2() {
    if(print) System.out.print("2 ");

    InstructionFinder f = new InstructionFinder(il);
    String      pat   = "ACONST_NULL if_acmp";
    
    for(Iterator e = f.search(pat); e.hasNext(); ) {
      InstructionHandle[] match = (InstructionHandle[])e.next();

      BranchHandle      if1    = (BranchHandle)match[1];
      BranchInstruction bi     = (BranchInstruction)if1.getInstruction();
      InstructionHandle target = bi.getTarget();

      if(bi instanceof IF_ACMPNE)
	if1.setInstruction(new IFNONNULL(target));
      else
	if1.setInstruction(new IFNULL(target));
	
      try {
	il.delete(match[0], match[0]);
      } catch(TargetLostException ex) {
	handleLostTarget(ex.getTargets(), if1);
      }

      count++;
    }

    if(print && (count > 0))
      System.out.print("(" + count + ") ");
  }

  private static final InstructionFinder.CodeConstraint constraint_not_target =
    new InstructionFinder.CodeConstraint() {
      public boolean checkCode(InstructionHandle[] match) {
	return match[1].getTargeters() == null;
      }
    };

  /**
   * Optimize
   * <PRE>
   * 42:   bipush		64
   * 44:   i2c
   * </PRE>
   * to
   * <PRE>
   * 42:   bipush		64
   * </PRE>
   */
  private void optimize3() {
    if(print) System.out.print("3 ");

    InstructionFinder f = new InstructionFinder(il);
    String      pat   = "(SIPUSH|BIPUSH|ICONST)(I2C|I2S|I2B)";
    
    for(Iterator e = f.search(pat, constraint_not_target); e.hasNext(); ) {
      InstructionHandle[] match = (InstructionHandle[])e.next();

      ConstantPushInstruction cpi = (ConstantPushInstruction)match[0].getInstruction();
      int value = ((Integer)cpi.getValue()).intValue();
      int min, max;

      switch(match[1].getInstruction().getOpcode()) {
      case I2B: min = Byte.MIN_VALUE; max = Byte.MAX_VALUE; break;
      case I2C: min = 0; max = Character.MAX_VALUE; break;
      case I2S: min = Short.MIN_VALUE; max = Short.MAX_VALUE; break;
      default: throw new RuntimeException("Ooops");
      }
      
      if((value >= min) && (value <= max)) {
	try {
	  il.delete(match[1]);
	} catch(TargetLostException ex) {
	  InstructionHandle[] targets = ex.getTargets();

	  for(int i=0; i < targets.length; i++) {
	    InstructionTargeter[] targeters = targets[i].getTargeters();
	    
	    for(int j=0; j < targeters.length; j++)
	      System.out.println(targeters[j]);
	  }

	  throw new RuntimeException("Ooops");
	}
	
	count++;
      }
    }

    if(print && (count > 0))
      System.out.print("(" + count + ") ");
  }

  /**
   * Optimize
   * <PRE>
   * 205(333):       iconst_0
   * 206(334):       if_icmpge       #343
   * </PRE>
   * to
   * <PRE>
   * 206(334):       ifge       #343
   * </PRE>
   */
  private void optimize4() {
    if(print) System.out.print("4 ");

    InstructionFinder f = new InstructionFinder(il);
    String      pat   = "ICONST_0 if_icmp";
    
    for(Iterator e = f.search(pat); e.hasNext(); ) {
System.out.println("match");
      InstructionHandle[] match = (InstructionHandle[])e.next();

      BranchHandle      if1    = (BranchHandle)match[1];
      BranchInstruction bi     = (BranchInstruction)if1.getInstruction();
      InstructionHandle target = bi.getTarget();

      switch(bi.getOpcode()) {
      case IF_ICMPEQ: if1.setInstruction(new IFEQ(target)); break;
      case IF_ICMPNE: if1.setInstruction(new IFNE(target)); break;
      case IF_ICMPLT: if1.setInstruction(new IFLT(target)); break;
      case IF_ICMPGT: if1.setInstruction(new IFGT(target)); break;
      case IF_ICMPLE: if1.setInstruction(new IFLE(target)); break;
      case IF_ICMPGE: if1.setInstruction(new IFGE(target)); break;
      }

      try {
	il.delete(match[0]);
      } catch(TargetLostException ex) {
	handleLostTarget(ex.getTargets(), if1);
      }

      count++;
    }

    if(print && (count > 0))
      System.out.print("(" + count + ") ");
  }

  private static final InstructionFinder.CodeConstraint constraint5 =
    new InstructionFinder.CodeConstraint() {
      public boolean checkCode(InstructionHandle[] match) {
	short b = match[1].getInstruction().getOpcode();
	return (match[1].getTargeters() == null) && ((b == IFEQ) || (b == IFNE));
      }
    };

  /**
   * Optimize
   * <PRE>
   * 50(145):        iconst_0
   * 51(146):        ifeq		#161
   * </PRE>
   * to
   * <PRE>
   * 206(334):       goto       #161
   * </PRE>
   */
  private void optimize5() {
    if(print) System.out.print("5 ");

    InstructionFinder f = new InstructionFinder(il);
    String      pat = "(ICONST_0|ICONST_1)if";
    
    for(Iterator e = f.search(pat, constraint5); e.hasNext(); ) {
      InstructionHandle[] match = (InstructionHandle[])e.next();

      Instruction       const1 = match[0].getInstruction();
      BranchHandle      if1    = (BranchHandle)match[1];
      BranchInstruction bi     = (BranchInstruction)if1.getInstruction();
      InstructionHandle target = bi.getTarget();
      short             c      = const1.getOpcode();
      short             b      = bi.getOpcode();
      InstructionHandle new_target = null;

      InstructionHandle next = match[1].getNext();

      try {
	if(((c == ICONST_0) && (b == IFEQ)) || ((c == ICONST_1) && (b == IFNE))) {
	  if1.setInstruction(new GOTO(target));
	  new_target = if1; // redirect references to ICONST_X
	  il.delete(match[0]);
	} else if(((c == ICONST_0) && (b == IFNE)) || ((c == ICONST_1) && (b == IFEQ))) {
	  new_target = if1.getNext(); // Can never be true, so delete both
	  il.delete(match[0], match[1]);
	}
      } catch(TargetLostException ex) {
	handleLostTarget(ex.getTargets(), new_target);
      }
      count++;
    }

    if(print && (count > 0))
      System.out.print("(" + count + ") ");
  }

  /**
   * Optimize
   * <PRE>
   * 236:  ireturn
   * 237:  goto		#242
   * </PRE>
   * to
   * <PRE>
   * 236:  ireturn
   * </PRE>
   */
  private void optimize6() {
    if(print) System.out.print("6 ");

    InstructionFinder f = new InstructionFinder(il);
    String      pat   = "(ReturnInstruction|ATHROW) NOP* GOTO";
    
    for(Iterator e = f.search(pat); e.hasNext(); f.reread()) {
      InstructionHandle[] match = (InstructionHandle[])e.next();
      InstructionHandle   last  = match[match.length - 1];
      InstructionHandle next = last.getNext();
      InstructionHandle target = ((GOTO)last.getInstruction()).getTarget();

      for(int i=1; i < match.length; i++) {
	InstructionTargeter[] targeters = match[i].getTargeters();

	if(targeters != null) {
	  for(int j=0; j < targeters.length; j++) {
	    if(targeters[j] instanceof BranchInstruction)
	      targeters[j].updateTarget(match[i], target);
	    else
	      targeters[j].updateTarget(match[i], next);
	  }
	}
      }

      try {
	il.delete(match[1], last);
      } catch(TargetLostException ex) { throw new RuntimeException("Oops."); }
	
      count++;
    }

    if(print && (count > 0))
      System.out.print("(" + count + ") ");
  }

  private int _optimizeX(InstructionFinder.CodeConstraint c) {
    InstructionHandle next = null;

    for(InstructionHandle ih = il.getStart(); ih != null; ih = next) {
      next = ih.getNext();

      if((next != null) && c.checkCode(new InstructionHandle[] { ih, next })) {
	try {
	  il.delete(ih);
	} catch(TargetLostException e) {
	  handleLostTarget(e.getTargets(), next);
	}
      }

      count++;
    }

    return count;
  }    

  private static final InstructionFinder.CodeConstraint nop_constraint =
    new InstructionFinder.CodeConstraint() {
      public boolean checkCode(InstructionHandle[] match) {
	return match[0].getInstruction() instanceof NOP;
      }
    };

  private static final InstructionFinder.CodeConstraint goto_constraint =
    new InstructionFinder.CodeConstraint() {
      public boolean checkCode(InstructionHandle[] match) {
	Instruction inst = match[0].getInstruction();
	return (inst instanceof GOTO) && (((GOTO)inst).getTarget() == match[1]);
      }
    };

  /**
   * Remove NOP instructions and GOTO instructions that branch to the following
   * instruction. All references to these instructions will be updated.
   */
  private void optimizeX() {
    if(print) System.out.print("X ");

    count += _optimizeX(nop_constraint);
    count += _optimizeX(goto_constraint);

    InstructionHandle end = il.getEnd(); // optimize very last nop, if any

    if(end.getInstruction() instanceof NOP) {
      InstructionHandle prev = end.getPrev();

      InstructionHandle next;
      for(InstructionHandle ih = il.getStart(); ih != null; ih = next) {
	next = ih.getNext();

	Instruction i = ih.getInstruction();

	if((i instanceof GOTO) && (((GOTO)i).getTarget() == end)) {
	  InstructionHandle p = ih.getPrev();
	  try {
	    il.delete(ih);
	  } catch(TargetLostException e) {
	    handleLostTarget(e.getTargets(), p);
	  }
	}
      }

      try {
	il.delete(end);
      } catch(TargetLostException e) {
	handleLostTarget(e.getTargets(), prev);
      }
    }

    if(print && (count > 0))
      System.out.print("(" + count + ") ");
  }
}


