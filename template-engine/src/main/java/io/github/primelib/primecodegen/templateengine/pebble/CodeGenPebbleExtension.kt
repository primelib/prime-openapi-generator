package io.github.primelib.primecodegen.templateengine.pebble;

import io.github.primelib.primecodegen.templateengine.pebble.filter.LinePrefixFilter;
import io.github.primelib.primecodegen.templateengine.pebble.filter.PadRightFilter;
import io.github.primelib.primecodegen.templateengine.pebble.filter.WrapInFilter;
import io.github.primelib.primecodegen.templateengine.pebble.function.GetOrDefaultFunction;
import io.github.primelib.primecodegen.templateengine.pebble.function.JavadocDescription
import io.github.primelib.primecodegen.templateengine.pebble.function.JavadocParamDescription
import io.github.primelib.primecodegen.templateengine.pebble.function.NewLineFunction;
import io.github.primelib.primecodegen.templateengine.pebble.operator.StartsWithOperator;
import io.pebbletemplates.pebble.attributes.AttributeResolver;
import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.extension.NodeVisitorFactory;
import io.pebbletemplates.pebble.extension.Test;
import io.pebbletemplates.pebble.operator.BinaryOperator;
import io.pebbletemplates.pebble.operator.UnaryOperator;
import io.pebbletemplates.pebble.tokenParser.TokenParser;

class CodeGenPebbleExtension : Extension {
    override fun getFilters(): Map<String, Filter> {
        return mapOf(
            "padright" to PadRightFilter(),
            "wrapin" to WrapInFilter(),
            "lineprefix" to LinePrefixFilter()
        )
    }

    override fun getTests(): Map<String, Test>? {
        return null
    }

    override fun getFunctions(): Map<String, Function> {
        return mapOf(
            "newline" to NewLineFunction(),
            "getOrDefault" to GetOrDefaultFunction(),
            "javadocParamDescription" to JavadocParamDescription(),
            "javadocDescription" to JavadocDescription(),
        )
    }

    override fun getTokenParsers(): List<TokenParser>? {
        return null
    }

    override fun getBinaryOperators(): List<BinaryOperator> {
        return listOf(StartsWithOperator())
    }

    override fun getUnaryOperators(): List<UnaryOperator>? {
        return null
    }

    override fun getGlobalVariables(): Map<String, Any>? {
        return null
    }

    override fun getNodeVisitors(): List<NodeVisitorFactory>? {
        return null
    }

    override fun getAttributeResolver(): List<AttributeResolver>? {
        return null
    }
}
