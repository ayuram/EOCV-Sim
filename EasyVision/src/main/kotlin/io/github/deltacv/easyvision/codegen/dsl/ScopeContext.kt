package io.github.deltacv.easyvision.codegen.dsl

import io.github.deltacv.easyvision.codegen.build.Scope
import io.github.deltacv.easyvision.codegen.build.Value
import io.github.deltacv.easyvision.codegen.Visibility

class ScopeContext(val scope: Scope) {

    var appendWhiteline: Boolean
        get() = scope.appendWhiteline
        set(value) { scope.appendWhiteline = value }

    operator fun String.invoke(vararg parameters: Value) {
        scope.methodCall(this, *parameters)
    }

    infix fun String.value(v: Value) =
        scope.instanceVariable(Visibility.PUBLIC, this, v)

    fun String.local(name: String, v: Value) =
        scope.localVariable(name, v)

    infix fun String.set(v: Value) =
        scope.variableSet(this, v)

    infix fun String.instanceSet(v: Value) =
        scope.instanceVariableSet(this, v)

    fun returnMethod(value: Value? = null) = scope.returnMethod(value)

}