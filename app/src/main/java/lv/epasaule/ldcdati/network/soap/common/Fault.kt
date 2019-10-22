package lv.epasaule.ldcdati.network.soap.common

import org.simpleframework.xml.Element

data class Fault(
        @field:Element(name = "Code") @param:Element(name = "Code")
        var code: Code,
        @field:Element(name = "Reason") @param:Element(name = "Reason")
        var reason: Reason
) {
    data class Code(
            @field:Element(name = "Value") @param:Element(name = "Value")
            var value: String,

            @field:Element(name = "Subcode", required = false)
            @param:Element(name = "Subcode", required = false)
            var subcode: Subcode?
    ) {
        data class Subcode(
                @field:Element(name = "Value") @param:Element(name = "Value")
                var value: String
        )
    }
    data class Reason(
            @field:Element(name = "Text") @param:Element(name = "Text")
            var text: String
    )
}