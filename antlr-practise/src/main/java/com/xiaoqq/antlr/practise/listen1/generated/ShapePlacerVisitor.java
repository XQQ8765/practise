package com.xiaoqq.antlr.practise.listen1.generated;// Generated from ShapePlacer.g4 by ANTLR 4.5

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ShapePlacerParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ShapePlacerVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ShapePlacerParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(ShapePlacerParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapePlacerParser#shapeDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShapeDefinition(ShapePlacerParser.ShapeDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapePlacerParser#sphereDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSphereDefinition(ShapePlacerParser.SphereDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapePlacerParser#cubeDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCubeDefinition(ShapePlacerParser.CubeDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ShapePlacerParser#coordinates}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCoordinates(ShapePlacerParser.CoordinatesContext ctx);
}