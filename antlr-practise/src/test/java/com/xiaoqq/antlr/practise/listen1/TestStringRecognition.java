package com.xiaoqq.antlr.practise.listen1;

import com.xiaoqq.antlr.practise.listen1.BasicDumpVisitor;
import com.xiaoqq.antlr.practise.listen1.TextTreeDumpVisitor;
import com.xiaoqq.antlr.practise.listen1.generated.ShapePlacerLexer;
import com.xiaoqq.antlr.practise.listen1.generated.ShapePlacerParser;
import com.xiaoqq.antlr.practise.listen1.generated.ShapePlacerParser.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.junit.Test;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.BitSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Refer to following wiki page:
 * https://codevomit.wordpress.com/2015/03/15/antlr4-project-with-maven-tutorial-episode-1/
 * https://codevomit.wordpress.com/2015/04/19/antlr4-project-with-maven-tutorial-episode-2/
 * https://codevomit.wordpress.com/2015/04/25/antlr4-project-with-maven-tutorial-episode-3/
 */
public class TestStringRecognition
{
    @Test
    public void testExploratoryString() throws IOException {

        String simplestProgram = "sphere 12 12 12 cube 2 3 4 cube 4 4 4 sphere 3 3 3";
        //String simplestProgram = "sphere 0 0 0 cube 5 5 5 sphere 10 1 3";
        ShapePlacerParser.ProgramContext context = parseProgram(simplestProgram, new TestErrorListener());
        System.out.println(context.toString());
    }

    @Test
    public void testJsonVisitor() throws IOException{
        String program_error = "sphere a 0 0 cube 5 5 5 sphere 10 1 3";
        TestErrorListener errorListener0 = new TestErrorListener();
        ProgramContext context = parseProgram(program_error, errorListener0);
        assertTrue(errorListener0.isFail());


        String program = "sphere 11 12 13 cube 2 3 4 cube 4 4 4 sphere 3 3 3";
        TestErrorListener errorListener1 = new TestErrorListener();
        context = parseProgram(program, errorListener1);
        assertFalse(errorListener1.isFail());
        BasicDumpVisitor visitor = new BasicDumpVisitor();
        String jsonRepresentation = context.accept(visitor);
        System.out.println("String returned by the visitor = " + jsonRepresentation);
    }

    @Test
    public void testTextTreeDumpVisitor() throws IOException{
		String program = "sphere 11 12 13 cube 2 3 4 cube 4 4 4 sphere 3 3 3";;
        TestErrorListener errorListener = new TestErrorListener();
        ProgramContext parseTree = parseProgram(program, errorListener);
        TextTreeDumpVisitor visitor = new TextTreeDumpVisitor();
        String output = visitor.visit(parseTree);
        System.out.println("\n\nTree:\n\n" + output);
    }

    private ProgramContext parseProgram(String program, ANTLRErrorListener errorListener) throws IOException {
        CharStream inputCharStream = new ANTLRInputStream(new StringReader(program));
        TokenSource tokenSource = new ShapePlacerLexer(inputCharStream);
        TokenStream inputTokenStream = new CommonTokenStream(tokenSource);
        ShapePlacerParser parser = new ShapePlacerParser(inputTokenStream);

        parser.addErrorListener(errorListener);

        ProgramContext context = parser.program();
        return context;
    }

    class TestErrorListener implements ANTLRErrorListener {

        private boolean fail = false;

        public boolean isFail() {
            return fail;
        }

        public void setFail(boolean fail) {
            this.fail = fail;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> arg0, Object arg1, int arg2,
                                int arg3, String arg4, RecognitionException arg5) {
            setFail(true);
        }

        @Override
        public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2,
                                             int arg3, int arg4, ATNConfigSet arg5) {
            setFail(true);
        }

        @Override
        public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2,
                                                int arg3, BitSet arg4, ATNConfigSet arg5) {
            setFail(true);
        }

        @Override
        public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3,
                                    boolean arg4, BitSet arg5, ATNConfigSet arg6) {
            setFail(true);
        }
    }
}
