package com.xiaoqq.antlr.practise.listen1.generated;// Generated from ShapePlacer.g4 by ANTLR 4.5

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ShapePlacerLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SPHERE_KEYWORD=1, CUBE_KEYWORD=2, NUMBER=3, WS=4;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"SPHERE_KEYWORD", "CUBE_KEYWORD", "NUMBER", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'sphere'", "'cube'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "SPHERE_KEYWORD", "CUBE_KEYWORD", "NUMBER", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public ShapePlacerLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ShapePlacer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\6#\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3"+
		"\3\4\6\4\31\n\4\r\4\16\4\32\3\5\6\5\36\n\5\r\5\16\5\37\3\5\3\5\2\2\6\3"+
		"\3\5\4\7\5\t\6\3\2\4\3\2\63;\5\2\13\f\17\17\"\"$\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\3\13\3\2\2\2\5\22\3\2\2\2\7\30\3\2\2\2\t\35"+
		"\3\2\2\2\13\f\7u\2\2\f\r\7r\2\2\r\16\7j\2\2\16\17\7g\2\2\17\20\7t\2\2"+
		"\20\21\7g\2\2\21\4\3\2\2\2\22\23\7e\2\2\23\24\7w\2\2\24\25\7d\2\2\25\26"+
		"\7g\2\2\26\6\3\2\2\2\27\31\t\2\2\2\30\27\3\2\2\2\31\32\3\2\2\2\32\30\3"+
		"\2\2\2\32\33\3\2\2\2\33\b\3\2\2\2\34\36\t\3\2\2\35\34\3\2\2\2\36\37\3"+
		"\2\2\2\37\35\3\2\2\2\37 \3\2\2\2 !\3\2\2\2!\"\b\5\2\2\"\n\3\2\2\2\5\2"+
		"\32\37\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}