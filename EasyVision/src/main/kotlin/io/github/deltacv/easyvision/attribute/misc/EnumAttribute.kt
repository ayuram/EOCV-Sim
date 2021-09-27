package io.github.deltacv.easyvision.attribute.misc

import imgui.ImGui
import imgui.type.ImInt
import io.github.deltacv.easyvision.attribute.Attribute
import io.github.deltacv.easyvision.attribute.AttributeMode
import io.github.deltacv.easyvision.attribute.Type
import io.github.deltacv.easyvision.attribute.TypedAttribute
import io.github.deltacv.easyvision.codegen.CodeGen
import io.github.deltacv.easyvision.codegen.GenValue

class EnumAttribute<T: Enum<T>>(
    override val mode: AttributeMode,
    val values: Array<T>,
    override var variableName: String?
) : TypedAttribute(Companion) {

    companion object: Type {
        override val name = "Enum"
        override val allowsNew = false
    }

    private val valuesStrings = values.map {
        it.name
    }.toTypedArray()

    val currentItem = ImInt()

    override fun drawAttribute() {
        super.drawAttribute()

        if(!hasLink) {
            ImGui.pushItemWidth(110.0f)
            ImGui.combo("", currentItem, valuesStrings)
            ImGui.popItemWidth()
        }
    }

    override fun acceptLink(other: Attribute) = other is EnumAttribute<*> && values[0]::class == other.values[0]::class

    @Suppress("UNCHECKED_CAST")
    override fun value(current: CodeGen.Current): GenValue.Enum<T> {
        val expectedClass = values[0]::class

        if(isInput) {
            if(hasLink) {
                val linkedAttrib = linkedAttribute()

                raiseAssert(
                    linkedAttrib != null,
                    "Enum attribute must have another attribute attached"
                )

                val value = linkedAttrib!!.value(current)
                raiseAssert(value is GenValue.Enum<*>, "Attribute attached is not a valid Enum")

                val valueEnum = value as GenValue.Enum<*>

                raiseAssert(
                    value.clazz == expectedClass,
                    "Enum attribute attached (${value.clazz}) is not the expected type of enum ($expectedClass)"
                )

                return valueEnum as GenValue.Enum<T>
            } else {
                val value = values[currentItem.get()]

                return GenValue.Enum(value, value::class.java)
            }
        } else {
            val value = getOutputValue(current)
            raiseAssert(value is GenValue.Enum<*>, "Value returned from the node is not an enum")

            val valueEnum = value as GenValue.Enum<T>
            raiseAssert(
                value.clazz == expectedClass,
                "Enum attribute returned from the node (${value.clazz}) is not the expected type of enum ($expectedClass)"
            )

            return valueEnum
        }
    }

}