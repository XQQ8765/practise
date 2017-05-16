package com.xiaoqq.antlr.practise;

import com.xiaoqq.antlr.practise.listen1.BasicDumpVisitor;
import com.xiaoqq.antlr.practise.listen1.generated.ShapePlacerBaseVisitor;
import com.xiaoqq.antlr.practise.listen1.generated.ShapePlacerLexer;
import com.xiaoqq.antlr.practise.listen1.generated.ShapePlacerParser;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.junit.Test;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.BitSet;

import static org.junit.Assert.assertFalse;

/**
 * Refer to following wiki page:
 * https://codevomit.wordpress.com/2015/03/15/antlr4-project-with-maven-tutorial-episode-1/
 * https://codevomit.wordpress.com/2015/03/15/antlr4-project-with-maven-tutorial-episode-2/
 */
public class TestStringRecognition
{
    @Test
    public void testExploratoryString() throws IOException {

        String simplestProgram = "sphere 12 12 12 cube 2 3 4 cube 4 4 4 sphere 3 3 3";
        //String simplestProgram = "sphere 0 0 0 cube 5 5 5 sphere 10 1 3";

        CharStream inputCharStream = new ANTLRInputStream(new StringReader(simplestProgram));
        TokenSource tokenSource = new ShapePlacerLexer(inputCharStream);
        TokenStream inputTokenStream = new CommonTokenStream(tokenSource);
        ShapePlacerParser parser = new ShapePlacerParser(inputTokenStream);

        parser.addErrorListener(new TestErrorListener());

        ShapePlacerParser.ProgramContext context = parser.program();

        System.out.println(context.toString());
    }

    @Test
    public void testJsonVisitor() throws IOException{
        //String program = "sphere 0 0 0 cube 5 5 5 sphere 10 1 3";
        String program = "sphere 12 12 12 cube 2 3 4 cube 4 4 4 sphere 3 3 3";
        TestErrorListener errorListener = new TestErrorListener();

        CharStream inputCharStream = new ANTLRInputStream(new StringReader(program));
        TokenSource tokenSource = new ShapePlacerLexer(inputCharStream);
        TokenStream inputTokenStream = new CommonTokenStream(tokenSource);
        ShapePlacerParser parser = new ShapePlacerParser(inputTokenStream);

        parser.addErrorListener(new TestErrorListener());

        ShapePlacerParser.ProgramContext context = parser.program();

        assertFalse(errorListener.isFail());

        BasicDumpVisitor visitor = new BasicDumpVisitor();

        String jsonRepresentation = context.accept(visitor);
        System.out.println("String returned by the visitor = " + jsonRepresentation);
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
