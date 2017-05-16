package com.xiaoqq.antlr.practise.listen1.generated;// Generated from ShapePlacer.g4 by ANTLR 4.5

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ShapePlacerParser}.
 */
public interface ShapePlacerListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ShapePlacerParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(ShapePlacerParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapePlacerParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(ShapePlacerParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapePlacerParser#shapeDefinition}.
	 * @param ctx the parse tree
	 */
	void enterShapeDefinition(ShapePlacerParser.ShapeDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapePlacerParser#shapeDefinition}.
	 * @param ctx the parse tree
	 */
	void exitShapeDefinition(ShapePlacerParser.ShapeDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapePlacerParser#sphereDefinition}.
	 * @param ctx the parse tree
	 */
	void enterSphereDefinition(ShapePlacerParser.SphereDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapePlacerParser#sphereDefinition}.
	 * @param ctx the parse tree
	 */
	void exitSphereDefinition(ShapePlacerParser.SphereDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapePlacerParser#cubeDefinition}.
	 * @param ctx the parse tree
	 */
	void enterCubeDefinition(ShapePlacerParser.CubeDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapePlacerParser#cubeDefinition}.
	 * @param ctx the parse tree
	 */
	void exitCubeDefinition(ShapePlacerParser.CubeDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ShapePlacerParser#coordinates}.
	 * @param ctx the parse tree
	 */
	void enterCoordinates(ShapePlacerParser.CoordinatesContext ctx);
	/**
	 * Exit a parse tree produced by {@link ShapePlacerParser#coordinates}.
	 * @param ctx the parse tree
	 */
	void exitCoordinates(ShapePlacerParser.CoordinatesContext ctx);
}