package com.xiaoqq.antlr.practise.listen1.generated;// Generated from ShapePlacer.g4 by ANTLR 4.5

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.*;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ShapePlacerParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SPHERE_KEYWORD=1, CUBE_KEYWORD=2, NUMBER=3, WS=4;
	public static final int
		RULE_program = 0, RULE_shapeDefinition = 1, RULE_sphereDefinition = 2, 
		RULE_cubeDefinition = 3, RULE_coordinates = 4;
	public static final String[] ruleNames = {
		"program", "shapeDefinition", "sphereDefinition", "cubeDefinition", "coordinates"
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

	@Override
	public String getGrammarFileName() { return "ShapePlacer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ShapePlacerParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public List<ShapeDefinitionContext> shapeDefinition() {
			return getRuleContexts(ShapeDefinitionContext.class);
		}
		public ShapeDefinitionContext shapeDefinition(int i) {
			return getRuleContext(ShapeDefinitionContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapePlacerListener) ((ShapePlacerListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapePlacerListener) ((ShapePlacerListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapePlacerVisitor) return ((ShapePlacerVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(11); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(10);
				shapeDefinition();
				}
				}
				setState(13); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SPHERE_KEYWORD || _la==CUBE_KEYWORD );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ShapeDefinitionContext extends ParserRuleContext {
		public SphereDefinitionContext sphereDefinition() {
			return getRuleContext(SphereDefinitionContext.class,0);
		}
		public CubeDefinitionContext cubeDefinition() {
			return getRuleContext(CubeDefinitionContext.class,0);
		}
		public ShapeDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shapeDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapePlacerListener) ((ShapePlacerListener)listener).enterShapeDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapePlacerListener) ((ShapePlacerListener)listener).exitShapeDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapePlacerVisitor) return ((ShapePlacerVisitor<? extends T>)visitor).visitShapeDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ShapeDefinitionContext shapeDefinition() throws RecognitionException {
		ShapeDefinitionContext _localctx = new ShapeDefinitionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_shapeDefinition);
		try {
			setState(17);
			switch (_input.LA(1)) {
			case SPHERE_KEYWORD:
				enterOuterAlt(_localctx, 1);
				{
				setState(15);
				sphereDefinition();
				}
				break;
			case CUBE_KEYWORD:
				enterOuterAlt(_localctx, 2);
				{
				setState(16);
				cubeDefinition();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SphereDefinitionContext extends ParserRuleContext {
		public TerminalNode SPHERE_KEYWORD() { return getToken(ShapePlacerParser.SPHERE_KEYWORD, 0); }
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public SphereDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sphereDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapePlacerListener) ((ShapePlacerListener)listener).enterSphereDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapePlacerListener) ((ShapePlacerListener)listener).exitSphereDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapePlacerVisitor) return ((ShapePlacerVisitor<? extends T>)visitor).visitSphereDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SphereDefinitionContext sphereDefinition() throws RecognitionException {
		SphereDefinitionContext _localctx = new SphereDefinitionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_sphereDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(19);
			match(SPHERE_KEYWORD);
			setState(20);
			coordinates();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CubeDefinitionContext extends ParserRuleContext {
		public TerminalNode CUBE_KEYWORD() { return getToken(ShapePlacerParser.CUBE_KEYWORD, 0); }
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public CubeDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cubeDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapePlacerListener) ((ShapePlacerListener)listener).enterCubeDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapePlacerListener) ((ShapePlacerListener)listener).exitCubeDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapePlacerVisitor) return ((ShapePlacerVisitor<? extends T>)visitor).visitCubeDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CubeDefinitionContext cubeDefinition() throws RecognitionException {
		CubeDefinitionContext _localctx = new CubeDefinitionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_cubeDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			match(CUBE_KEYWORD);
			setState(23);
			coordinates();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CoordinatesContext extends ParserRuleContext {
		public List<TerminalNode> NUMBER() { return getTokens(ShapePlacerParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(ShapePlacerParser.NUMBER, i);
		}
		public CoordinatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coordinates; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapePlacerListener) ((ShapePlacerListener)listener).enterCoordinates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapePlacerListener) ((ShapePlacerListener)listener).exitCoordinates(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapePlacerVisitor) return ((ShapePlacerVisitor<? extends T>)visitor).visitCoordinates(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CoordinatesContext coordinates() throws RecognitionException {
		CoordinatesContext _localctx = new CoordinatesContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_coordinates);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			match(NUMBER);
			setState(26);
			match(NUMBER);
			setState(27);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\6 \4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\6\2\16\n\2\r\2\16\2\17\3\3\3\3\5\3\24\n"+
		"\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\2\2\7\2\4\6\b\n\2\2\34"+
		"\2\r\3\2\2\2\4\23\3\2\2\2\6\25\3\2\2\2\b\30\3\2\2\2\n\33\3\2\2\2\f\16"+
		"\5\4\3\2\r\f\3\2\2\2\16\17\3\2\2\2\17\r\3\2\2\2\17\20\3\2\2\2\20\3\3\2"+
		"\2\2\21\24\5\6\4\2\22\24\5\b\5\2\23\21\3\2\2\2\23\22\3\2\2\2\24\5\3\2"+
		"\2\2\25\26\7\3\2\2\26\27\5\n\6\2\27\7\3\2\2\2\30\31\7\4\2\2\31\32\5\n"+
		"\6\2\32\t\3\2\2\2\33\34\7\5\2\2\34\35\7\5\2\2\35\36\7\5\2\2\36\13\3\2"+
		"\2\2\4\17\23";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}