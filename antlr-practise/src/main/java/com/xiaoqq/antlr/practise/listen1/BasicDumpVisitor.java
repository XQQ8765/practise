package com.xiaoqq.antlr.practise.listen1;

import com.xiaoqq.antlr.practise.listen1.generated.ShapePlacerParser;
import com.xiaoqq.antlr.practise.listen1.generated.ShapePlacerVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;


public class BasicDumpVisitor implements ShapePlacerVisitor<String> {


    public String visit(ParseTree arg0) {
        return arg0.accept(this);
    }

    public String visitChildren(RuleNode arg0) {

        return null;
    }

    public String visitErrorNode(ErrorNode arg0) {

        return null;
    }

    public String visitTerminal(TerminalNode arg0) {
        return arg0.getText();
    }

    @Override
    public String visitProgram(ShapePlacerParser.ProgramContext ctx) {
        StringBuilder builder = new StringBuilder();
        for(ParseTree tree : ctx.children){
            builder.append(tree.accept(this));
        }
        return builder.toString();
    }

    public String visitShapeDefinition(ShapePlacerParser.ShapeDefinitionContext ctx) {
        StringBuilder builder = new StringBuilder();
        for (ParseTree tree : ctx.children){
            builder.append(tree.accept(this) + " ");
        }
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public String visitSphereDefinition(ShapePlacerParser.SphereDefinitionContext ctx) {
        return getShapeDefinitionString(ctx, "sphere");
    }

    public String visitCubeDefinition(ShapePlacerParser.CubeDefinitionContext ctx) {
        return getShapeDefinitionString(ctx, "cube");
    }

    public String visitCoordinates(ShapePlacerParser.CoordinatesContext ctx) {
        StringBuilder builder = new StringBuilder();
        for (ParseTree tree : ctx.children){
            builder.append(tree.accept(this)).append(" ");
        }
        return builder.toString();
    }

    private String getShapeDefinitionString(ParserRuleContext context, String keyword){
        StringBuilder builder = new StringBuilder();
        builder.append(keyword).append(" ");

        builder.append(context.children.get(1).accept(this)).append(" ");

        return builder.toString();
    }

}
